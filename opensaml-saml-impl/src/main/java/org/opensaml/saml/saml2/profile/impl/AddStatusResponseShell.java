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

package org.opensaml.saml.saml2.profile.impl;

import java.time.Instant;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.security.IdentifierGenerationStrategy;
import net.shibboleth.shared.security.IdentifierGenerationStrategy.ProviderType;

/**
 * Action that creates an empty object derived from {@link StatusResponseType},
 * and sets it as the message returned by {@link ProfileRequestContext#getOutboundMessageContext()}.
 * 
 * <p>The {@link Status} is set to {@link StatusCode#SUCCESS} as a default assumption,
 * and this can be overridden by subsequent actions.</p>
 * 
 * <p>If an issuer value is returned via a lookup strategy, then it's set as the Issuer of the message.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 * 
 * @post ProfileRequestContext.getOutboundMessageContext().getMessage() != null
 */
public class AddStatusResponseShell extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AbstractResponseShellAction.class);

    /** Message type to create. */
    @NonnullAfterInit private QName messageType;
    
    /** Overwrite an existing message? */
    private boolean overwriteExisting;
    
    /** Strategy used to locate the {@link IdentifierGenerationStrategy} to use. */
    @Nonnull private Function<ProfileRequestContext,IdentifierGenerationStrategy> idGeneratorLookupStrategy;

    /** Strategy used to obtain the response issuer value. */
    @Nullable private Function<ProfileRequestContext,String> issuerLookupStrategy;
    
    /** The generator to use. */
    @NonnullBeforeExec private IdentifierGenerationStrategy idGenerator;

    /** EntityID to populate into Issuer element. */
    @Nullable private String issuerId;
    
    /** Constructor. */
    public AddStatusResponseShell() {
        // Default strategy is a 16-byte secure random source.
        idGeneratorLookupStrategy = prc ->IdentifierGenerationStrategy.getInstance(ProviderType.SECURE);
    }
    
    /**
     * Set the type of message to create.
     * 
     * @param type  message type
     */
    public void setMessageType(@Nonnull final QName type) {
        messageType = Constraint.isNotNull(type, "Message type cannot be null");
    }
    
    /**
     * Set whether to overwrite an existing message.
     * 
     * @param flag flag to set
     */
    public void setOverwriteExisting(final boolean flag) {
        checkSetterPreconditions();
        
        overwriteExisting = flag;
    }
    
    /**
     * Set the strategy used to locate the {@link IdentifierGenerationStrategy} to use.
     * 
     * @param strategy lookup strategy
     */
    public void setIdentifierGeneratorLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,IdentifierGenerationStrategy> strategy) {
        checkSetterPreconditions();

        idGeneratorLookupStrategy =
                Constraint.isNotNull(strategy, "IdentifierGenerationStrategy lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to locate the issuer value to use.
     * 
     * @param strategy lookup strategy
     */
    public void setIssuerLookupStrategy(@Nullable final Function<ProfileRequestContext,String> strategy) {
        checkSetterPreconditions();

        issuerLookupStrategy = strategy;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (messageType == null) {
            throw new ComponentInitializationException("Message type cannot be null");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }

        final MessageContext outboundMessageCtx = profileRequestContext.getOutboundMessageContext();
        if (outboundMessageCtx == null) {
            log.debug("{} No outbound message context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        } else if (!overwriteExisting && outboundMessageCtx.getMessage() != null) {
            log.debug("{} Outbound message context already contains a response", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }

        idGenerator = idGeneratorLookupStrategy.apply(profileRequestContext);
        if (idGenerator == null) {
            log.debug("{} No identifier generation strategy", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return false;
        }
        
        if (issuerLookupStrategy != null) {
            issuerId = issuerLookupStrategy.apply(profileRequestContext);
        }

        outboundMessageCtx.setMessage(null);
        
        return true;
    }

    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        final XMLObjectBuilderFactory bf = XMLObjectProviderRegistrySupport.getBuilderFactory();
        final SAMLObjectBuilder<StatusCode> statusCodeBuilder =
                (SAMLObjectBuilder<StatusCode>) bf.<StatusCode>ensureBuilder(StatusCode.TYPE_NAME);
        final SAMLObjectBuilder<Status> statusBuilder =
                (SAMLObjectBuilder<Status>) bf.<Status>ensureBuilder(Status.TYPE_NAME);
        final SAMLObjectBuilder<?> responseBuilder = (SAMLObjectBuilder<?>) bf.ensureBuilder(messageType);

        final StatusCode statusCode = statusCodeBuilder.buildObject();
        statusCode.setValue(StatusCode.SUCCESS);

        final Status status = statusBuilder.buildObject();
        status.setStatusCode(statusCode);

        final SAMLObject object = responseBuilder.buildObject();
        if (!(object instanceof StatusResponseType)) {
            log.error("{} Message was not derived from StatusResponseType, not compatible with this action",
                    getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.MESSAGE_PROC_ERROR);
            return;
        }

        final StatusResponseType response = (StatusResponseType) object;
        
        response.setID(idGenerator.generateIdentifier());
        response.setIssueInstant(Instant.now());
        response.setStatus(status);
        response.setVersion(SAMLVersion.VERSION_20);

        if (issuerId != null) {
            log.debug("{} Setting Issuer to {}", getLogPrefix(), issuerId);
            final SAMLObjectBuilder<Issuer> issuerBuilder =
                    (SAMLObjectBuilder<Issuer>) bf.<Issuer>ensureBuilder(Issuer.DEFAULT_ELEMENT_NAME);
            final Issuer issuer = issuerBuilder.buildObject();
            issuer.setValue(issuerId);
            response.setIssuer(issuer);
        } else {
            log.debug("{} No issuer value available, leaving Issuer unset", getLogPrefix());
        }

        profileRequestContext.ensureOutboundMessageContext().setMessage(response);
    }
    
}