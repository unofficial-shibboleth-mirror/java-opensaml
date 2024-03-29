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

package org.opensaml.soap.soap11.profile.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.context.EventContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.CurrentOrPreviousEventLookup;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.soap11.FaultString;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Action that resolves or builds a SOAP 1.1 {@link Fault} object, and stores it in the outbound message context.
 * 
 * <p>
 * An attempt will first be made to resolve a pre-existing fault instance from the {@link ProfileRequestContext}, via
 * an optionally configured lookup strategy. This is to accommodate SOAP-aware components which may choose to 
 * emit a specific, locally determined fault. The default strategy is {@link MessageContextFaultStrategy}. 
 * </p>
 * 
 * <p>
 * If no context fault instance is resolved, a new instance will be built using strategy functions which lookup 
 * the {@link FaultCode} {@link QName} and the {@link FaultString} {@link String} values. If no value is produced,
 * the former defaults to {@link FaultCode#SERVER}. The latter defaults to <code>null</code>.
 * </p>
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 */
public class AddSOAPFault extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AddSOAPFault.class);
    
    /** Strategy for resolving a fault instance directly from the request context. */
    @Nullable private Function<ProfileRequestContext,Fault> contextFaultStrategy;

    /** Predicate determining whether detailed error information is permitted. */
    @Nonnull private Predicate<ProfileRequestContext> detailedErrorsCondition;

    /** Optional method to obtain fault code. */
    @Nullable private Function<ProfileRequestContext,QName> faultCodeLookupStrategy;

    /** Optional method to obtain a fault string. */
    @Nullable private Function<ProfileRequestContext,String> faultStringLookupStrategy;
    
    /** Default fault codes to insert. */
    @Nonnull private QName defaultFaultCode;
    
    /** A default fault string to include. */
    @Nullable private String faultString;
    
    /** Whether to include detailed status information. */
    private boolean detailedErrors;
    
    /** Whether to set the outbound message context's message property to null. */
    private boolean nullifyOutboundMessage;
    
    /** Constructor. */
    public AddSOAPFault() {
        detailedErrorsCondition = PredicateSupport.alwaysFalse();
        defaultFaultCode = FaultCode.SERVER;
        detailedErrors = false;
        contextFaultStrategy = new MessageContextFaultStrategy();
        nullifyOutboundMessage = true;
    }
    
    /**
     * Set the flag indicating whether to set the outbound message context's message property to null.
     * 
     * <p>Default is: <code>true</code></p>
     * 
     * @param flag true if should nullify, false if not
     */
    public void setNullifyOutboundMessage(final boolean flag) {
        nullifyOutboundMessage = flag;
    }
    
    /**
     * Set the optional strategy used to resolve a {@link Fault} instance directly
     * from the request context.
     * 
     * @param strategy strategy used to resolve the fault instance
     */
    public void setContextFaultStrategy(@Nullable final Function<ProfileRequestContext,Fault> strategy) {
        checkSetterPreconditions();

        contextFaultStrategy = strategy;
    }

    /**
     * Set the predicate used to determine the detailed errors condition.
     * 
     * @param condition predicate for detailed errors condition
     */
    public void setDetailedErrorsCondition(@Nonnull final Predicate<ProfileRequestContext> condition) {
        checkSetterPreconditions();

        detailedErrorsCondition = Constraint.isNotNull(condition, "Detailed errors condition cannot be null");
    }

    /**
     * Set the optional strategy used to obtain a faultcode to include.
     * 
     * @param strategy strategy used to obtain faultcode
     */
    public void setFaultCodeLookupStrategy(@Nullable final Function<ProfileRequestContext,QName> strategy) {
        checkSetterPreconditions();

        faultCodeLookupStrategy = strategy;
    }
    
    /**
     * Set the optional strategy used to obtain a faultstring to include.
     * 
     * @param strategy strategy used to obtain a fault string
     */
    public void setFaultStringLookupStrategy(@Nullable final Function<ProfileRequestContext,String> strategy) {
        checkSetterPreconditions();

        faultStringLookupStrategy = strategy;
    }
    
    /**
     * Set the default faultcode to insert.
     * 
     * @param code faultcode
     */
    public void setFaultCode(@Nonnull final QName code) {
        checkSetterPreconditions();
        
        defaultFaultCode = Constraint.isNotNull(code, "Faultcode cannot be null");
    }
    
    /**
     * Set a default faultstring to use in the event that error detail is off,
     * or no specific message is obtained.
     * 
     * @param message default faultstring
     */
    public void setFaultString(@Nullable final String message) {
        checkSetterPreconditions();
        
        faultString = StringSupport.trimOrNull(message);
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        checkComponentActive();

        if (!super.doPreExecute(profileRequestContext) ) {
            return false;
        }
        
        detailedErrors = detailedErrorsCondition.test(profileRequestContext);
        
        log.debug("{} Detailed errors are {}", getLogPrefix(), detailedErrors ? "enabled" : "disabled");

        final MessageContext mc = profileRequestContext.getOutboundMessageContext();
        
        if (mc != null && nullifyOutboundMessage) {
            mc.setMessage(null);
        } else {
            profileRequestContext.setOutboundMessageContext(new MessageContext());
        }
        
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        Fault fault = resolveContextFault(profileRequestContext);
        
        if (fault == null) {
            fault = buildNewMappedFault(profileRequestContext);
        }
        
        SOAPMessagingSupport.registerSOAP11Fault(profileRequestContext.ensureOutboundMessageContext(), fault);
    }

    /**
     * Resolve a {@link Fault} instance stored in the {@link ProfileRequestContext}.
     * 
     * @param profileRequestContext the current request context
     * 
     * @return the fault instance resolved from the request context, or null
     */
    @Nullable private Fault resolveContextFault(final ProfileRequestContext profileRequestContext) {
        if (contextFaultStrategy == null) {
            return null;
        }
        
        assert contextFaultStrategy != null;
        final Fault fault = contextFaultStrategy.apply(profileRequestContext);
        
        if (fault != null) {
            log.debug("{} Resolved Fault instance via context strategy", getLogPrefix());
            if (fault.getCode() == null) {
                log.debug("{} Resolved Fault contained no FaultCode, using configured default", getLogPrefix());
                final XMLObjectBuilder<FaultCode> faultCodeBuilder =
                        XMLObjectProviderRegistrySupport.getBuilderFactory().<FaultCode>ensureBuilder(
                                FaultCode.DEFAULT_ELEMENT_NAME);
                final FaultCode code = faultCodeBuilder.buildObject(FaultCode.DEFAULT_ELEMENT_NAME);
                code.setValue(defaultFaultCode);
                fault.setCode(code);
            }
            if (!detailedErrors) {
                log.debug("{} Removing any detailed error info from context Fault instance", getLogPrefix());
                if (faultString != null) {
                    buildFaultString(fault, faultString);
                } else {
                    fault.setMessage(null);
                }
                fault.setDetail(null);
                fault.setActor(null);
            }
        } else {
            log.debug("{} Failed to resolve any Fault instance via context strategy", getLogPrefix());
        }
        return fault;
    }

    /**
     * Build and return a new {@link Fault} based on configured mapping strategy.
     * 
     * @param profileRequestContext the current request context
     * 
     * @return the new fault
     */
    @Nonnull private Fault buildNewMappedFault(final ProfileRequestContext profileRequestContext) {
        final XMLObjectBuilder<Fault> faultBuilder =
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Fault>ensureBuilder(
                        Fault.DEFAULT_ELEMENT_NAME);
        final XMLObjectBuilder<FaultCode> faultCodeBuilder =
                XMLObjectProviderRegistrySupport.getBuilderFactory().<FaultCode>ensureBuilder(
                        FaultCode.DEFAULT_ELEMENT_NAME);
        
        final Fault fault = faultBuilder.buildObject(Fault.DEFAULT_ELEMENT_NAME);
       
        final FaultCode code = faultCodeBuilder.buildObject(FaultCode.DEFAULT_ELEMENT_NAME);
        if (faultCodeLookupStrategy != null) {
            final QName fc = faultCodeLookupStrategy.apply(profileRequestContext);
            if (fc == null) {
                code.setValue(defaultFaultCode);
            } else {
                code.setValue(fc);
            }
        } else {
            code.setValue(defaultFaultCode);
        }
        fault.setCode(code);

        // faultstring processing.
        if (!detailedErrors || faultStringLookupStrategy == null) {
            if (faultString != null) {
                buildFaultString(fault, faultString);
                log.debug("{} Setting faultstring to defaulted value", getLogPrefix());
            }
        } else if (faultStringLookupStrategy != null) {
            final String message = faultStringLookupStrategy.apply(profileRequestContext);
            if (message != null) {
                log.debug("{} Current state of request was mappable, setting faultstring to mapped value",
                        getLogPrefix());
                buildFaultString(fault, message);
            } else if (faultString != null) {
                buildFaultString(fault, faultString);
                log.debug("{} Current state of request was not mappable, setting faultstring to defaulted value",
                        getLogPrefix());
            }
        }
        
        return fault;
    }
    
    /**
     * Build and attach {@link FaultString} element.
     * 
     * @param fault    the element to attach to
     * @param message   the message to set
     */
    private void buildFaultString(@Nonnull final Fault fault, @Nonnull @NotEmpty final String message) {
        final XMLObjectBuilder<FaultString> faultStringBuilder =
                XMLObjectProviderRegistrySupport.getBuilderFactory().<FaultString>ensureBuilder(
                        FaultString.DEFAULT_ELEMENT_NAME);

        final FaultString fs = faultStringBuilder.buildObject(FaultString.DEFAULT_ELEMENT_NAME);
        fs.setValue(message);
        fault.setMessage(fs);
    }
    
    /** A default method to map event IDs to faultcode QName based on {@link EventContext}. */
    public static class FaultCodeMappingFunction implements Function<ProfileRequestContext,QName> {

        /** Code mappings. */
        @Nonnull private Map<String,QName> codeMappings;
        
        /** Strategy function for access to {@link EventContext} to check. */
        @Nonnull private Function<ProfileRequestContext,EventContext> eventContextLookupStrategy;
        
        /**
         * Constructor.
         *
         * @param mappings the status code mappings to use
         */
        public FaultCodeMappingFunction(@Nonnull final Map<String,QName> mappings) {
            Constraint.isNotNull(mappings, "Faultcode mappings cannot be null");
            
            codeMappings = new HashMap<>(mappings.size());
            for (final Map.Entry<String,QName> entry : mappings.entrySet()) {
                final String event = StringSupport.trimOrNull(entry.getKey());
                if (event != null && entry.getValue() != null) {
                    codeMappings.put(event, entry.getValue());
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
        @Nullable public QName apply(@Nullable final ProfileRequestContext input) {
            final EventContext eventCtx = eventContextLookupStrategy.apply(input);
            final Object event = eventCtx != null ? eventCtx.getEvent() : null;
            if (event != null) {
                return codeMappings.get(event.toString());
            }
            return null;
        }
    }
    
    /**
     * Default strategy which returns a {@link Fault} instance already registered in the current request context.
     * 
     * <p>
     * The outbound message context is checked first, followed by the inbound message context.  Evaluation
     * is performed using {@link SOAPMessagingSupport#getSOAP11Fault(MessageContext)}.
     * </p>
     */
    public static class MessageContextFaultStrategy implements Function<ProfileRequestContext, Fault> {
        
        /** Logger. */
        @Nonnull private Logger log = LoggerFactory.getLogger(MessageContextFaultStrategy.class);

        /** {@inheritDoc} */
        @Nullable public Fault apply(@Nullable final ProfileRequestContext input) {
            if (input == null) {
                return null;
            }
            Fault fault = null;
            final MessageContext outbound = input.getOutboundMessageContext();
            if (outbound != null) {
                fault = SOAPMessagingSupport.getSOAP11Fault(outbound);
                if (fault != null) {
                    log.debug("Found registered SOAP fault in outbound message context");
                    return fault;
                }
            }
            
            final MessageContext inbound = input.getInboundMessageContext();
            if (inbound != null) {
                fault = SOAPMessagingSupport.getSOAP11Fault(inbound);
                if (fault != null) {
                    log.debug("Found registered SOAP fault in inbound message context");
                    return fault;
                }
            }
            return null;
        }
        
    }
    
}