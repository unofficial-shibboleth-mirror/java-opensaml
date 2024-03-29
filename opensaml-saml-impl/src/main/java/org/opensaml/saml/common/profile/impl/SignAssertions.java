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

package org.opensaml.saml.common.profile.impl;

import java.util.function.Function;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.ArtifactResponse;
import org.opensaml.security.SecurityException;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.xml.SerializeSupport;

/**
 * Action that signs assertions in a SAML 1/2 Response returned by a lookup strategy,
 * by default the message returned by {@link ProfileRequestContext#getOutboundMessageContext()}.
 * 
 * <p>The {@link SecurityParametersContext} governing the signing process is located by a lookup
 * strategy, by default a child of the profile request context.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public class SignAssertions extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SignAssertions.class);

    /** Strategy used to locate the response to operate on. */
    @Nonnull private Function<ProfileRequestContext,SAMLObject> responseLookupStrategy;
    
    /** Strategy used to locate the {@link SecurityParametersContext} to use for signing. */
    @Nonnull private Function<ProfileRequestContext,SecurityParametersContext> securityParametersLookupStrategy;
    
    /** The signature signing parameters. */
    @NonnullBeforeExec private SignatureSigningParameters signatureSigningParameters;

    /** The response containing the assertions to be signed. */
    @NonnullBeforeExec private SAMLObject response;

    /** Constructor. */
    public SignAssertions() {
        responseLookupStrategy = new MessageLookup<>(SAMLObject.class).compose(
                new OutboundMessageContextLookup());
        securityParametersLookupStrategy = new ChildContextLookup<>(SecurityParametersContext.class);
    }
    
    /**
     * Set the strategy used to locate the response to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setResponseLookupStrategy(@Nonnull final Function<ProfileRequestContext,SAMLObject> strategy) {
        checkSetterPreconditions();
        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to locate the {@link SecurityParametersContext} to use.
     * 
     * @param strategy lookup strategy
     */
    public void setSecurityParametersLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,SecurityParametersContext> strategy) {
        checkSetterPreconditions();
        securityParametersLookupStrategy = Constraint.isNotNull(strategy,
                "SecurityParameterContext lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} No SAML Response located in current profile request context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }

        // Step down into ArtifactResponses.
        if (response instanceof ArtifactResponse resp) {
            log.debug("{} Found ArtifactResponse, stepping down into enclosed message", getLogPrefix());
            response = resp.getMessage();
        }
        
        if (response instanceof org.opensaml.saml.saml1.core.Response saml1) {
            if (saml1.getAssertions().isEmpty()) {
                log.debug("{} No assertions available, nothing to do", getLogPrefix());
                return false;
            }
        } else if (response instanceof org.opensaml.saml.saml2.core.Response saml2) {
            if (saml2.getAssertions().isEmpty()) {
                log.debug("{} No assertions available, nothing to do", getLogPrefix());
                return false;
            }
        } else {
            log.debug("{} Message returned by lookup strategy was not a SAML Response", getLogPrefix());
            return false;
        }

        final SecurityParametersContext secParamCtx = securityParametersLookupStrategy.apply(profileRequestContext);
        if (secParamCtx == null) {
            log.debug("{} Will not sign assertions because no security parameters context is available",
                    getLogPrefix());
            return false;
        }

        signatureSigningParameters = secParamCtx.getSignatureSigningParameters();
        if (signatureSigningParameters == null) {
            log.debug("{} Will not sign assertions because no signature signing parameters available", getLogPrefix());
            return false;
        }
        
        return true;
    }

    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        try {
            // TODO Maybe the response should not be logged ?
            if (log.isTraceEnabled()) {
                logResponse("Response before signing:");
            }

            if (response instanceof org.opensaml.saml.saml1.core.Response saml1) {
                for (final var assertion : saml1.getAssertions()) {
                    assert assertion != null;
                    SignatureSupport.signObject(assertion, signatureSigningParameters);
                }
            } else if (response instanceof org.opensaml.saml.saml2.core.Response saml2) {
                for (final var assertion : saml2.getAssertions()) {
                    assert assertion != null;
                    SignatureSupport.signObject(assertion, signatureSigningParameters);
                }
            }

            // TODO Maybe the response should not be logged ?
            if (log.isTraceEnabled()) {
                logResponse("Response after signing:");
            }
        } catch (final SecurityException | MarshallingException | SignatureException e) {
            log.warn("{} Error encountered while signing assertions", getLogPrefix(), e);
            ActionSupport.buildEvent(profileRequestContext, EventIds.UNABLE_TO_SIGN);
        }
    }

    /**
     * Log the Response with the given message at trace level.
     * 
     * @param message the log message
     */
    private void logResponse(@Nonnull final String message) {
        if (message != null && response != null) {
            try {
                final Element dom = XMLObjectSupport.marshall(response);
                log.trace(message + "\n" + SerializeSupport.prettyPrintXML(dom));
            } catch (final MarshallingException e) {
                log.warn("Unable to marshall message for logging purposes", e);
            }
        }
    }

}
