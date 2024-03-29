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

package org.opensaml.saml.common.messaging.context;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml1.core.AttributeQuery;
import org.opensaml.saml.saml1.core.AuthorizationDecisionQuery;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDType;
import org.slf4j.Logger;


/**
 * Subcontext that carries information about a SAML peer entity.
 * 
 * <p>
 * This context will often contain subcontexts, whose data is construed to be scoped to that peer entity.
 * </p>
 * 
 * <p>
 * The method {@link #getEntityId()} will attempt to dynamically resolve the appropriate data 
 * from the SAML message held in the message context if the data has not been set statically 
 * by the corresponding setter method. This evaluation will be attempted only if the this 
 * context instance is an immediate child of the message context, as returned by {@link #getParent()}.
 * </p>
 */
public final class SAMLPeerEntityContext extends AbstractAuthenticatableSAMLEntityContext {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(SAMLPeerEntityContext.class);
    
    /** Whether to use the resource of SAML 1 queries to resolve the entity ID. */
    private boolean useSAML1QueryResourceAsEntityId;
    
    /** Constructor. */
    public SAMLPeerEntityContext() {
        useSAML1QueryResourceAsEntityId = true;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable @NotEmpty public String getEntityId() {
        if (super.getEntityId() == null) {
            setEntityId(resolveEntityId());
        }
        return super.getEntityId();
    }
    
    /**
     * Gets whether to use the Resource attribute of some SAML 1 queries to resolve the entity 
     * ID.
     * 
     * @return whether to use the Resource attribute of some SAML 1 queries to resolve the entity ID 
     */
    public boolean getUseSAML1QueryResourceAsEntityId() {
        return useSAML1QueryResourceAsEntityId;
    }

    /**
     * Sets whether to use the Resource attribute of some SAML 1 queries to resolve the entity ID.
     * 
     * @param useResource whether to use the Resource attribute of some SAML 1 queries to resolve the entity ID
     */
    public void setUseSAML1QueryResourceAsEntityId(final boolean useResource) {
        useSAML1QueryResourceAsEntityId = useResource;
    }

    /**
     * Dynamically resolve the SAML peer entity ID from the SAML protocol message held in 
     * {@link MessageContext#getMessage()}.
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String resolveEntityId() {
        final SAMLObject samlMessage = resolveSAMLMessage();
        //SAML 2 Request
        if (samlMessage instanceof org.opensaml.saml.saml2.core.RequestAbstractType msg) {
            return processSaml2Request(msg);
        //SAML 2 Response
        } else if (samlMessage instanceof org.opensaml.saml.saml2.core.StatusResponseType msg) {
            return processSaml2Response(msg);
        //SAML 1 Response
        } else if (samlMessage instanceof org.opensaml.saml.saml1.core.Response msg) {
            return processSaml1Response(msg);
        //SAML 1 Request
        } else if (samlMessage instanceof org.opensaml.saml.saml1.core.Request msg) {
            return processSaml1Request(msg);
        }
        
        return null;
    }
    
    /**
     * Resolve the SAML entity ID from a SAML 2 request.
     * 
     * @param request the request
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml2Request(
            @Nonnull final org.opensaml.saml.saml2.core.RequestAbstractType request) {

        return processSaml2Issuer(request.getIssuer());
    }

    /**
     * Resolve the SAML entity ID from a SAML 2 response.
     * 
     * @param statusResponse the response
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml2Response(
            @Nonnull final org.opensaml.saml.saml2.core.StatusResponseType statusResponse) {
        if (statusResponse.getIssuer() != null) {
            return processSaml2Issuer(statusResponse.getIssuer());
        }

        if (statusResponse instanceof org.opensaml.saml.saml2.core.Response msg) {
            processSaml2ResponseAssertions(msg);

        }

        return null;
    }

    /**
     * Resolve the SAML entity ID from the Assertions of a SAML 2 response.
     *
     * @param response the response
     *
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml2ResponseAssertions(
            @Nonnull final org.opensaml.saml.saml2.core.Response response) {

        String issuer = null;
        final List<org.opensaml.saml.saml2.core.Assertion> assertions = response.getAssertions();
        if (assertions != null && assertions.size() > 0) {
            log.info("Attempting to extract issuer from enclosed SAML 2.x Assertion(s)");

            if (response.getEncryptedAssertions() != null && response.getEncryptedAssertions().size() > 0) {
                log.warn("SAML 2.x Response '{}' contained both Assertions and EncryptedAssertions, "
                        + "can not currently dynamically resolve SAML peer entity ID on that basis",
                        response.getID());
                return null;
            }

            for (final org.opensaml.saml.saml2.core.Assertion assertion : assertions) {
                if (assertion != null) {
                    final String current = processSaml2Issuer(assertion.getIssuer());
                    if (issuer != null && !issuer.equals(current)) {
                        log.warn("SAML 2.x assertions within response '{}' contain different issuer IDs, "
                                + "can not dynamically resolve SAML peer entity ID", response.getID());
                        return null;
                    }
                    issuer = current;
                }
            }
        }

        if (issuer == null) {
            log.warn("Issuer could not be extracted from standard SAML 2.x Response message via Assertions");
        }

        return issuer;
    }
    
    /**
     * Resolve the SAML entity ID from a SAML 2 Issuer.
     * 
     * @param issuer the issuer
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml2Issuer(@Nullable final Issuer issuer) {
        if (issuer == null) {
            return null;
        }

        final String format = issuer.getFormat();
        if (format == null || format.equals(NameIDType.ENTITY)) {
            return issuer.getValue();
        }
        log.warn("Couldn't dynamically resolve SAML 2 peer entity ID due to unsupported NameID format: {}", format);
        return null;
    }

    /**
     * Resolve the SAML entity ID from a SAML 1 response.
     * 
     * @param response the response
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml1Response(@Nonnull final org.opensaml.saml.saml1.core.Response response) {
        String issuer = null;
        final List<org.opensaml.saml.saml1.core.Assertion> assertions = response.getAssertions();
        if (assertions != null && assertions.size() > 0) {
            log.info("Attempting to extract issuer from enclosed SAML 1.x Assertion(s)");
            for (final org.opensaml.saml.saml1.core.Assertion assertion : assertions) {
                if (assertion != null && assertion.getIssuer() != null) {
                    if (issuer != null && !issuer.equals(assertion.getIssuer())) {
                        log.warn("SAML 1.x assertions within response '{}' contain different issuer IDs, "
                                + "can not dynamically resolve SAML peer entity ID", response.getID());
                        return null;
                    }
                    issuer = assertion.getIssuer();
                }
            }
        }

        if (issuer == null) {
            log.warn("Issuer could not be extracted from standard SAML 1.x response message");
        }

        return issuer;
    }

    /**
     * Resolve the SAML entity ID from a SAML 1 request.
     * 
     * @param request the request
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml1Request(@Nonnull final org.opensaml.saml.saml1.core.Request request) {
        String entityId = null;
        final AttributeQuery q = request.getAttributeQuery();
        if (q != null) {
            entityId = processSaml1AttributeQuery(q);
            if (entityId != null) {
                return entityId;
            }
        }

        final AuthorizationDecisionQuery aq = request.getAuthorizationDecisionQuery();
        if (aq != null) {
            entityId = processSaml1AuthorizationDecisionQuery(aq);
            if (entityId != null) {
                return entityId;
            }
        }
        
        return null;
    }

    /**
     * Resolve the SAML entity ID from a SAML 1 AttributeQuery.
     * 
     * @param query the query
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml1AttributeQuery(@Nonnull final AttributeQuery query) {
        if (getUseSAML1QueryResourceAsEntityId()) {
            log.debug("Attempting to extract entity ID from SAML 1 AttributeQuery Resource attribute");
            final String resource = StringSupport.trimOrNull(query.getResource());

            if (resource != null) {
                log.debug("Extracted entity ID from SAML 1.x AttributeQuery: {}", resource);
                return resource;
            }
        }
        return null;
    }

    /**
     * Resolve the SAML entityID from a SAML 1 AuthorizationDecisionQuery.
     * 
     * @param query the query
     * 
     * @return the entity ID, or null if it could not be resolved
     */
    @Nullable protected String processSaml1AuthorizationDecisionQuery(@Nonnull final AuthorizationDecisionQuery query) {
        if (getUseSAML1QueryResourceAsEntityId()) {
            log.debug("Attempting to extract entity ID from SAML 1 AuthorizationDecisionQuery Resource attribute");
            final String resource = StringSupport.trimOrNull(query.getResource());

            if (resource != null) {
                log.debug("Extracted entity ID from SAML 1.x AuthorizationDecisionQuery: {}", resource);
                return resource;
            }
        }
        return null;
    }

    /**
     * Resolve the SAML message from the message context.
     * 
     * @return the SAML message, or null if it can not be resolved
     */
    @Nullable protected SAMLObject resolveSAMLMessage() {
        if (getParent() instanceof MessageContext mc) {
            if (mc.getMessage() instanceof SAMLObject msg) {
                return msg;
            } 
        }
        return null;
    }

}