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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.saml2.core.ArtifactResponse;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.profile.context.EncryptionContext;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.xml.SerializeSupport;

/**
 * Action that encrypts all assertions in a {@link Response} message obtained from a lookup
 * strategy, by default the outbound message context.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#UNABLE_TO_ENCRYPT}
 * 
 * @post All assertions in the response have been replaced with encrypted versions, or no changes are made.
 */
public class EncryptAssertions extends AbstractEncryptAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EncryptAssertions.class);

    /** Used to log protocol messages. */
    @Nonnull private Logger protocolMessageLog = LoggerFactory.getLogger("PROTOCOL_MESSAGE");

    /** Strategy used to locate the {@link Response} to operate on. */
    @Nonnull private Function<ProfileRequestContext,StatusResponseType> responseLookupStrategy;
    
    /** The message to operate on. */
    @NonnullBeforeExec private Response response;
    
    /** Constructor. */

    public EncryptAssertions() {
        responseLookupStrategy =
                new MessageLookup<>(StatusResponseType.class).compose(
                        new OutboundMessageContextLookup());
    }

    /**
     * Set the strategy used to locate the {@link Response} to operate on.
     * 
     * @param strategy strategy used to locate the {@link Response} to operate on
     */
    public void setResponseLookupStrategy(@Nonnull final Function<ProfileRequestContext,StatusResponseType> strategy) {
        checkSetterPreconditions();

        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    @Nullable protected EncryptionParameters getApplicableParameters(@Nullable final EncryptionContext ctx) {
        if (ctx != null) {
            return ctx.getAssertionEncryptionParameters();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        final StatusResponseType message = responseLookupStrategy.apply(profileRequestContext);
        if (message != null) {
            if (message instanceof Response) {
                response = (Response) message;
            } else if (message instanceof ArtifactResponse
                    && ((ArtifactResponse) message).getMessage() instanceof Response) {
                response = (Response) ((ArtifactResponse) message).getMessage();
            }
        }
        
        if (response == null || response.getAssertions().isEmpty()) {
            log.debug("{} Response was not present or contained no assertions, nothing to do", getLogPrefix());
            return false;
        }
        
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (protocolMessageLog.isDebugEnabled()) {
            try {
                final Element dom = XMLObjectSupport.marshall(response);
                protocolMessageLog.debug("{} Response before assertion encryption:\n{}", getLogPrefix(),
                        SerializeSupport.prettyPrintXML(dom));
            } catch (final MarshallingException e) {
                log.error("{} Unable to marshall message for logging purposes", getLogPrefix(), e);
            }
        }
        
        final List<EncryptedAssertion> accumulator = new ArrayList<>(response.getAssertions().size());
        
        for (final Assertion assertion : response.getAssertions()) {
            assert assertion != null;
            try {
                accumulator.add(getEncrypter().encrypt(assertion));
            } catch (final EncryptionException e) {
                log.warn("{} Error encrypting assertion", getLogPrefix(), e);
                ActionSupport.buildEvent(profileRequestContext, EventIds.UNABLE_TO_ENCRYPT);
                return;
            }
        }
        
        response.getEncryptedAssertions().addAll(accumulator);
        response.getAssertions().clear();
    }
    
}