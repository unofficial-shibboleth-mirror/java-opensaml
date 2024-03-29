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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.EventContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.CurrentOrPreviousEventLookup;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusMessage;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Action that sets {@link Status} content in a {@link StatusResponseType} obtained from
 * a lookup strategy, typically from the outbound message context.
 * 
 * <p>If the message already contains status information, this action will overwrite it.</p>
 * 
 * <p>Options allows for the creation of a {@link StatusMessage} either explicitly,
 * or via lookup strategy.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public class AddStatusToResponse extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AddStatusToResponse.class);

    /** Strategy used to locate the {@link StatusResponseType} to operate on. */
    @Nonnull private Function<ProfileRequestContext,StatusResponseType> responseLookupStrategy;

    /** Predicate determining whether detailed error information is permitted. */
    @Nonnull private Predicate<ProfileRequestContext> detailedErrorsCondition;

    /** Optional method to obtain status codes. */
    @Nullable private Function<ProfileRequestContext,List<String>> statusCodesLookupStrategy;
    
    /** Optional method to obtain a status message. */
    @Nullable private Function<ProfileRequestContext,String> statusMessageLookupStrategy;
    
    /** One or more status codes to insert. */
    @Nonnull private List<String> defaultStatusCodes;
    
    /** A default status message to include. */
    @Nullable private String statusMessage;
    
    /** Whether to include detailed status information. */
    private boolean detailedErrors;
    
    /** Response to modify. */
    @NonnullBeforeExec private StatusResponseType response;
    
    /** Constructor. */
    public AddStatusToResponse() {
        responseLookupStrategy =
                new MessageLookup<>(StatusResponseType.class).compose(new OutboundMessageContextLookup());
        detailedErrorsCondition = PredicateSupport.alwaysFalse();
        defaultStatusCodes = CollectionSupport.emptyList();
        detailedErrors = false;
    }

    /**
     * Set the strategy used to locate the {@link StatusResponseType} to operate on.
     * 
     * @param strategy strategy used to locate the {@link StatusResponseType} to operate on
     */
    public void setResponseLookupStrategy(@Nonnull final Function<ProfileRequestContext,StatusResponseType> strategy) {
        checkSetterPreconditions();

        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }


    /**
     * Set the predicate used to determine the detailed errors condition.
     * 
     * @param condition predicate for detailed errors condition
     */
    public void setDetailedErrorsCondition(@Nonnull final Predicate<ProfileRequestContext> condition) {
        checkSetterPreconditions();

        detailedErrorsCondition =
                Constraint.isNotNull(condition, "Detailed errors condition cannot be null");
    }
    
    /**
     * Set the optional strategy used to obtain status codes to include.
     * 
     * @param strategy strategy used to obtain status codes
     */
    public void setStatusCodesLookupStrategy(@Nullable final Function<ProfileRequestContext,List<String>> strategy) {
        checkSetterPreconditions();

        statusCodesLookupStrategy = strategy;
    }

    /**
     * Set the optional strategy used to obtain a status message to include.
     * 
     * @param strategy strategy used to obtain a status message
     */
    public void setStatusMessageLookupStrategy(@Nullable final Function<ProfileRequestContext, String> strategy) {
        checkSetterPreconditions();

        statusMessageLookupStrategy = strategy;
    }
    
    /**
     * Set the list of status code values to insert, ordered such that the top level code is first
     * and every other code will be nested inside the previous one.
     * 
     * @param codes list of status code values to insert
     */
    public void setStatusCodes(@Nonnull final List<String> codes) {
        checkSetterPreconditions();
        
        defaultStatusCodes = CollectionSupport.copyToList(
                Constraint.isNotNull(codes, "Status code list cannot be null"));
    }
    
    /**
     * Set a default status message to use in the event that error detail is off,
     * or no specific message is obtained.
     * 
     * @param message default status message
     */
    public void setStatusMessage(@Nullable final String message) {
        checkSetterPreconditions();
        
        statusMessage = StringSupport.trimOrNull(message);
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} Response message was not returned by lookup strategy", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }

        detailedErrors = detailedErrorsCondition.test(profileRequestContext);
        
        log.debug("{} Detailed errors are {}", getLogPrefix(), detailedErrors ? "enabled" : "disabled");
        
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        final SAMLObjectBuilder<Status> statusBuilder = (SAMLObjectBuilder<Status>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Status>ensureBuilder(Status.TYPE_NAME);

        final Status status = statusBuilder.buildObject();
        response.setStatus(status);
        
        if (statusCodesLookupStrategy != null) {
            final List<String> codes = statusCodesLookupStrategy.apply(profileRequestContext);
            if (codes == null || codes.isEmpty()) {
                buildStatusCode(status, defaultStatusCodes);
            } else {
                buildStatusCode(status, codes);
            }
        } else {
            buildStatusCode(status, defaultStatusCodes);
        }
                
        // StatusMessage processing.
        if (!detailedErrors || statusMessageLookupStrategy == null) {
            if (statusMessage != null) {
                log.debug("{} Setting StatusMessage to defaulted value", getLogPrefix());
                assert statusMessage != null;
                buildStatusMessage(status, statusMessage);
            }
        } else if (statusMessageLookupStrategy != null) {
            final String message = statusMessageLookupStrategy.apply(profileRequestContext);
            if (message != null) {
                log.debug("{} Current state of request was mappable, setting StatusMessage to mapped value",
                        getLogPrefix());
                buildStatusMessage(status, message);
            } else if (statusMessage != null) {
                log.debug("{} Current state of request was not mappable, setting StatusMessage to defaulted value",
                        getLogPrefix());
                assert statusMessage != null;
                buildStatusMessage(status, statusMessage);
            }
        }
    }
    
    /**
     * Build and attach {@link StatusCode} element.
     * 
     * @param status    the element to attach to
     * @param codes     the status codes to use
     */
    private void buildStatusCode(@Nonnull final Status status, @Nonnull final List<String> codes) {
        final SAMLObjectBuilder<StatusCode> statusCodeBuilder = (SAMLObjectBuilder<StatusCode>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<StatusCode>ensureBuilder(
                        StatusCode.TYPE_NAME);

        // Build nested StatusCodes.
        StatusCode statusCode = statusCodeBuilder.buildObject();
        status.setStatusCode(statusCode);
        if (codes.isEmpty()) {
            statusCode.setValue(StatusCode.RESPONDER);
        } else {
            statusCode.setValue(codes.get(0));
            final Iterator<String> i = codes.iterator();
            i.next();
            while (i.hasNext()) {
                final StatusCode subcode = statusCodeBuilder.buildObject();
                subcode.setValue(i.next());
                statusCode.setStatusCode(subcode);
                statusCode = subcode;
            }
        }
    }
    
    /**
     * Build and attach {@link StatusMessage} element.
     * 
     * @param status    the element to attach to
     * @param message   the message to set
     */
    private void buildStatusMessage(@Nonnull final Status status, @Nonnull @NotEmpty final String message) {
        final SAMLObjectBuilder<StatusMessage> statusMessageBuilder = (SAMLObjectBuilder<StatusMessage>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<StatusMessage>ensureBuilder(
                        StatusMessage.DEFAULT_ELEMENT_NAME);
        final StatusMessage sm = statusMessageBuilder.buildObject();
        sm.setValue(message);
        status.setStatusMessage(sm);
    }
    
    /** A default method to map event IDs to SAML 2 StatusCode URIs based on {@link EventContext}. */
    public static class StatusCodeMappingFunction implements Function<ProfileRequestContext,List<String>> {

        /** Code mappings. */
        @Nonnull private Map<String,List<String>> codeMappings;
        
        /** Strategy function for access to {@link EventContext} to check. */
        @Nonnull private Function<ProfileRequestContext,EventContext> eventContextLookupStrategy;
        
        /**
         * Constructor.
         *
         * @param mappings the status code mappings to use
         */
        public StatusCodeMappingFunction(@Nonnull final Map<String,List<String>> mappings) {
            Constraint.isNotNull(mappings, "Status code mappings cannot be null");
            
            codeMappings = new HashMap<>(mappings.size());
            for (final Map.Entry<String,List<String>> entry : mappings.entrySet()) {
                final String event = StringSupport.trimOrNull(entry.getKey());
                if (event != null && entry.getValue() != null) {
                    codeMappings.put(event, CollectionSupport.copyToList(entry.getValue()));
                }
            }
            
            eventContextLookupStrategy = new CurrentOrPreviousEventLookup();
        }
        
        /**
         * Set lookup strategy for {@link EventContext} to check.
         * 
         * @param strategy  lookup strategy
         */
        public void setEventContextLookupStrategy(
                @Nonnull final Function<ProfileRequestContext,EventContext> strategy) {
            eventContextLookupStrategy = Constraint.isNotNull(strategy, "EventContext lookup strategy cannot be null");
        }
        
        /** {@inheritDoc} */
        @Override
        @Nullable public List<String> apply(@Nullable final ProfileRequestContext input) {
            
            final EventContext eventCtx = eventContextLookupStrategy.apply(input);
            final Object event = eventCtx != null ? eventCtx.getEvent() : null;
            if (event != null) {
                return codeMappings.get(event.toString());
            }
            return CollectionSupport.emptyList();
        }
    }

}