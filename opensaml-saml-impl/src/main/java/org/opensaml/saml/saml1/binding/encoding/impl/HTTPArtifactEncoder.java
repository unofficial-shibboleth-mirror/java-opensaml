/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.saml1.binding.encoding.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap;
import org.opensaml.saml.common.messaging.context.SAMLArtifactContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.config.SAMLConfigurationSupport;
import org.opensaml.saml.saml1.binding.artifact.SAML1Artifact;
import org.opensaml.saml.saml1.binding.artifact.SAML1ArtifactBuilder;
import org.opensaml.saml.saml1.binding.artifact.SAML1ArtifactBuilderFactory;
import org.opensaml.saml.saml1.binding.artifact.SAML1ArtifactType0001;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.Response;
import org.slf4j.Logger;

import jakarta.servlet.http.HttpServletResponse;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.EncodingException;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.net.URLBuilder;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * SAML 1.X HTTP Artifact message encoder.
 */
public class HTTPArtifactEncoder extends BaseSAML1MessageEncoder {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(HTTPArtifactEncoder.class);

    /** SAML artifact map used to store created artifacts for later retrival. */
    @NonnullAfterInit private SAMLArtifactMap artifactMap;

    /** Default artifact type to use when encoding messages. */
    @Nonnull @NotEmpty private byte[] defaultArtifactType;

    /** Constructor. */
    public HTTPArtifactEncoder() {
        defaultArtifactType = SAML1ArtifactType0001.TYPE_CODE;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public String getBindingURI() {
        return SAMLConstants.SAML1_ARTIFACT_BINDING_URI;
    }
    
    /**
     * Get the SAML artifact map to use.
     * 
     * @return the artifactMap.
     */
    @NonnullAfterInit public SAMLArtifactMap getArtifactMap() {
        return artifactMap;
    }

    /**
     * Set the SAML artifact map to use.
     * 
     * @param newArtifactMap the new artifactMap 
     */
    public void setArtifactMap(@Nonnull final SAMLArtifactMap newArtifactMap) {
        checkSetterPreconditions();
        
        artifactMap = Constraint.isNotNull(newArtifactMap, "SAMLArtifactMap cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (artifactMap == null) {
            throw new ComponentInitializationException("SAMLArtifactMap cannot be null");
        }
    }

    // Checkstyle: CyclomaticComplexity|MethodLength OFF
    /** {@inheritDoc} */
    @Override
    protected void doEncode() throws MessageEncodingException {
        final MessageContext messageContext = getMessageContext();

        final Object outboundMessage = messageContext.getMessage();
        if (!(outboundMessage instanceof Response)) {
            throw new MessageEncodingException("Outbound message was not a SAML 1 Response");
        }
        final Response samlResponse = (Response) outboundMessage;
        
        final String requester = getInboundMessageIssuer(messageContext);
        final String issuer = getOutboundMessageIssuer(messageContext);
        if (requester == null || issuer == null) {
            throw new MessageEncodingException("Unable to obtain issuer or relying party for message encoding");
        }
        
        final String endpointUrl = getEndpointURL(messageContext).toString();
        
        final URLBuilder urlBuilder;
        try {
            urlBuilder = new URLBuilder(endpointUrl);
        } catch (final MalformedURLException e) {
            throw new MessageEncodingException("Endpoint URL " + endpointUrl + " is not a valid URL", e);
        }
        
        final List<Pair<String, String>> queryParams = urlBuilder.getQueryParams();
        queryParams.clear();

        final String relayState = SAMLBindingSupport.getRelayState(messageContext);
        if (SAMLBindingSupport.checkRelayState(relayState)) {
            queryParams.add(new Pair<>("TARGET", relayState));
        }

        final SAML1ArtifactBuilder<?> artifactBuilder;
        final byte[] artifactType = getSAMLArtifactType(messageContext);
        if (artifactType != null) {
            final SAML1ArtifactBuilderFactory artifactBuilderFactory =
                    SAMLConfigurationSupport.getSAML1ArtifactBuilderFactory();
            artifactBuilder = artifactBuilderFactory != null
                    ? artifactBuilderFactory.getArtifactBuilder(artifactType) : null;
        } else {
            final SAML1ArtifactBuilderFactory artifactBuilderFactory =
                    SAMLConfigurationSupport.getSAML1ArtifactBuilderFactory();
            artifactBuilder = artifactBuilderFactory != null
                    ? artifactBuilderFactory.getArtifactBuilder(defaultArtifactType) : null;
            storeSAMLArtifactType(messageContext, defaultArtifactType);
        }
        if (artifactBuilder == null) {
            throw new MessageEncodingException("Unable to obtain SAML1ArtifactBuilder");
        }
        
        for (final Assertion assertion : samlResponse.getAssertions()) {
            assert assertion != null;
            final SAML1Artifact artifact = artifactBuilder.buildArtifact(messageContext, assertion);
            if (artifact == null) {
                log.error("Unable to build artifact for message to relying party {}", requester);
                throw new MessageEncodingException("Unable to build artifact for message to relying party");
            }

           
            try {
                final String artifactString = Base64Support.encode(artifact.getArtifactBytes(), 
                        Base64Support.UNCHUNKED);
                artifactMap.put(artifactString, requester, issuer, assertion);
                queryParams.add(new Pair<>("SAMLart", artifactString));
            } catch (final IOException e) {
                log.error("Unable to store assertion mapping for artifact: {}", e.getMessage());
                throw new MessageEncodingException("Unable to store assertion mapping for artifact", e);
            } catch (final EncodingException e) {
                log.error("Unable to base64 encode artifact for message to relying party: {}", e.getMessage());
                throw new MessageEncodingException("Unable to base64 encode artifact for message to relying party", e);
            }
            
        }

        final String encodedEndpoint = urlBuilder.buildURL();
        log.debug("Sending redirect to URL {} for relying party {}", encodedEndpoint, requester);
        
        final HttpServletResponse response = getHttpServletResponse();
        if (response == null) {
            throw new MessageEncodingException("HttpServletResponse was null");
        }
        
        try {
            response.sendRedirect(encodedEndpoint);
        } catch (final IOException e) {
            throw new MessageEncodingException("Problem sending HTTP redirect", e);
        }
    }
// Checkstyle: CyclomaticComplexity|MethodLength ON
    
    /**
     * Get the outbound message issuer.
     * 
     * @param messageContext  the message context
     * @return the outbound message issuer
     */
    @Nullable private String getOutboundMessageIssuer(@Nonnull final MessageContext messageContext) {

        final SAMLSelfEntityContext selfCtx = messageContext.getSubcontext(SAMLSelfEntityContext.class);
        if (selfCtx == null) {
            return null;
        }
        
        return selfCtx.getEntityId();
    }

    /**
     * Get the requester.
     * 
     * @param messageContext the message context
     * @return the requester
     */
    @Nullable private String getInboundMessageIssuer(@Nonnull final MessageContext messageContext) {
        final SAMLPeerEntityContext peerCtx = messageContext.getSubcontext(SAMLPeerEntityContext.class);
        if (peerCtx == null) {
            return null;
        }
        
        return peerCtx.getEntityId();
    }

    /**
     * Store the SAML artifact type in the message context.
     * 
     * @param messageContext  the message context
     * 
     * @param artifactType the artifact type to store
     */
    private void storeSAMLArtifactType(@Nonnull final MessageContext messageContext,
            @Nonnull @NotEmpty final byte[] artifactType) {
        messageContext.ensureSubcontext(SAMLArtifactContext.class).setArtifactType(artifactType);
    }

    /**
     * Get the SAML artifact type from the message context.
     * 
     * @param messageContext the message context
     * 
     * @return the artifact type
     */
    @Nullable private byte[] getSAMLArtifactType(@Nonnull final MessageContext messageContext) {
        return messageContext.ensureSubcontext(SAMLArtifactContext.class).getArtifactType();
    }
    
}
