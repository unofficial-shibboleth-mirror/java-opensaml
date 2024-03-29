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

package org.opensaml.saml.saml2.wssecurity.messaging.impl;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.SAML20AssertionValidator;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.wssecurity.SAML20AssertionToken;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.wssecurity.Security;
import org.opensaml.soap.wssecurity.WSSecurityConstants;
import org.opensaml.soap.wssecurity.messaging.Token.ValidationStatus;
import org.opensaml.soap.wssecurity.messaging.WSSecurityContext;
import org.slf4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.collection.LazyList;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.FunctionSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.NonnullSupplier;

/**
 * A security handler which resolves SAML 2.0 Assertion tokens from a SOAP envelope's
 * wsse:Security header, validates them, and makes them available via via the
 * {@link WSSecurityContext}.
 */
public class WSSecuritySAML20AssertionTokenSecurityHandler extends AbstractMessageHandler {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(WSSecuritySAML20AssertionTokenSecurityHandler.class);
    
    /** Supplier for the Current HTTP request, if available. */
    @Nullable private NonnullSupplier<HttpServletRequest> httpServletRequestSupplier;
    
    /** Flag which indicates whether a failure of Assertion validation should be considered fatal. */
    private boolean invalidFatal;
    
    /** The SAML 2.0 Assertion validator lookup function.*/
    @Nonnull private Function<Pair<MessageContext, Assertion>, SAML20AssertionValidator> assertionValidatorLookup;
    
    /** Function that builds a {@link ValidationContext} instance based on a 
     * {@link SAML20AssertionTokenValidationInput} instance. */
    @NonnullAfterInit private Function<SAML20AssertionTokenValidationInput, ValidationContext> validationContextBuilder;
    
    
    /** Constructor. */
    public WSSecuritySAML20AssertionTokenSecurityHandler() {
        setInvalidFatal(true);
        setValidationContextBuilder(new DefaultSAML20AssertionValidationContextBuilder());
        assertionValidatorLookup = FunctionSupport.constant(null);
    }

    /**
     * Get the function that builds a {@link ValidationContext} instance based on a 
     * {@link SAML20AssertionTokenValidationInput} instance.
     * 
     * <p>
     * Defaults to an instance of {@link DefaultSAML20AssertionValidationContextBuilder}.
     * </p>
     * 
     * @return the builder function
     */
    @NonnullAfterInit
    public Function<SAML20AssertionTokenValidationInput, ValidationContext> getValidationContextBuilder() {
        return validationContextBuilder;
    }

    /**
     * Set the function that builds a {@link ValidationContext} instance based on a 
     * {@link SAML20AssertionTokenValidationInput} instance.
     * 
     * <p>
     * Defaults to an instance of {@link DefaultSAML20AssertionValidationContextBuilder}.
     * </p>
     * 
     * @param builder the builder function
     */
    public void setValidationContextBuilder(
            @Nonnull final Function<SAML20AssertionTokenValidationInput, ValidationContext> builder) {
        checkSetterPreconditions();
        validationContextBuilder = Constraint.isNotNull(builder, "Validation context builder may not be null");
    }

    /**
     * Get the current HTTP request if available.
     * 
     * @return current HTTP request
     */
    @Nullable public HttpServletRequest getHttpServletRequest() {
        if (httpServletRequestSupplier != null) {
            return httpServletRequestSupplier.get();
        }
        return null;
    }

    /**
     * Get the supplier for  HTTP request if available.
     *
     * @return current HTTP request
     */
    @Nullable public NonnullSupplier<HttpServletRequest> getHttpServletRequestSupplier() {
        return httpServletRequestSupplier;
    }

    /**
     * Set the current HTTP request Supplier.
     *
     * @param requestSupplier Supplier for the current HTTP request
     */
    public void setHttpServletRequestSupplier(@Nullable final NonnullSupplier<HttpServletRequest> requestSupplier) {
        checkSetterPreconditions();

        httpServletRequestSupplier = requestSupplier;
    }
    
    /**
     * Get flag which indicates whether a failure of Assertion validation should be considered a fatal processing error.
     * 
     * <p>
     * Defaults to: {@code true}.
     * </p>
     * 
     * @return Returns the invalidFatal.
     */
    public boolean isInvalidFatal() {
        return invalidFatal;
    }

    /**
     * Set flag which indicates whether a failure of Assertion validation should be considered a fatal processing error.
     * 
     * <p>
     * Defaults to: {@code true}.
     * </p>
     * 
     * @param flag The invalidFatal to set.
     */
    public void setInvalidFatal(final boolean flag) {
        checkSetterPreconditions();
        invalidFatal = flag;
    }
    
    /**
     * Get the configured Assertion validator.
     * 
     * @param messageContext input message context 
     * @param assertion input assertion
     * 
     * @return the configured Assertion validator, or null
     */
    @Nullable public SAML20AssertionValidator getAssertionValidator(@Nullable final MessageContext messageContext,
            @Nullable final Assertion assertion) {
        return assertionValidatorLookup.apply(new Pair<>(messageContext, assertion));
    }

    /**
     * Set a locally-configured Assertion validator.
     * 
     * @param validator the local Assertion validator, may be null
     */
    public void setAssertionValidator(@Nullable final SAML20AssertionValidator validator) {
        checkSetterPreconditions();
        assertionValidatorLookup = FunctionSupport.constant(validator);
    }
    
    /**
     * Get the Assertion validator lookup function.
     * 
     * @return the Assertion validator lookup function, or null
     */
    @Nullable public Function<Pair<MessageContext, Assertion>, SAML20AssertionValidator> getAssertionValidatorLookup() {
        return assertionValidatorLookup;
    }

    /**
     * Set the Assertion validator lookup function.
     * 
     * @param function the Assertion validator lookup function, may be null
     */
    public void setAssertionValidatorLookup(
            @Nonnull final Function<Pair<MessageContext, Assertion>, SAML20AssertionValidator> function) {
        checkSetterPreconditions();
        assertionValidatorLookup = Constraint.isNotNull(function, "Assertion validator lookup function cannot be null");
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (getValidationContextBuilder() == null) {
            throw new ComponentInitializationException("ValidationContext builder cannot be null");
        }
        
        if (getHttpServletRequest() == null) {
            throw new ComponentInitializationException("HttpServletRequest cannot be null");
        }
    }

// Checkstyle: ReturnCount OFF
    /** {@inheritDoc} */
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        if (!SOAPMessagingSupport.isSOAPMessage(messageContext)) {
            log.info("Message context does not contain a SOAP envelope. Skipping rule...");
            return;
        }
        
        final List<Assertion> assertions = resolveAssertions(messageContext);
        if (assertions == null || assertions.isEmpty()) {
            log.info("Inbound SOAP envelope contained no Assertion tokens. Skipping further processing");
            return;
        }
        
        final WSSecurityContext wsContext = messageContext.ensureSubcontext(WSSecurityContext.class);
        
        for (final Assertion assertion : assertions) {
            assert assertion != null;
            final SAML20AssertionValidator validator = getAssertionValidator(messageContext, assertion);
            if (validator == null) {
                log.warn("No SAML20AssertionValidator was available, terminating");
                SOAPMessagingSupport.registerSOAP11Fault(messageContext, FaultCode.SERVER, 
                        "Internal processing error", null, null, null);
                throw new MessageHandlerException("No SAML20AssertionValidator was available");
            }
        
            final ValidationContext validationContext = buildValidationContext(messageContext, assertion);
            
            try { 
                final ValidationResult validationResult = validator.validate(assertion, validationContext);
                final SAML20AssertionToken token = new SAML20AssertionToken(assertion);
                processResult(validationContext, validationResult, token, messageContext);
                wsContext.getTokens().add(token);
            } catch (final AssertionValidationException e) {
                log.warn("There was a problem determining Assertion validity: {}", e.getMessage());
                SOAPMessagingSupport.registerSOAP11Fault(messageContext, FaultCode.SERVER, 
                        "Internal security token processing error", null, null, null);
                throw new MessageHandlerException("Error determining SAML 2.0 Assertion validity", e);
            }
        }
    }
// Checkstyle: ReturnCount ON

    /**
     * Process the result of the token validation.
     * 
     * @param validationContext the Assertion validation context
     * @param validationResult the Assertion validation result
     * @param token the token being produced
     * @param messageContext the current message context
     * 
     * @throws MessageHandlerException if the Assertion was invalid or indeterminate and idInvalidFatal is true
     */
    protected void processResult(@Nonnull final ValidationContext validationContext, 
            @Nonnull final ValidationResult validationResult, @Nonnull final SAML20AssertionToken token, 
            @Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        log.debug("Assertion token validation result was: {}", validationResult);
        
        switch (validationResult) {
            case VALID:
                token.setValidationStatus(ValidationStatus.VALID);
                token.setSubjectConfirmation((SubjectConfirmation) validationContext.getDynamicParameters()
                        .get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION));
                break;
            case INVALID:
                log.warn("Assertion token validation was INVALID. Reason(s): {}",
                        validationContext.getValidationFailureMessages());
                if (isInvalidFatal()) {
                    SOAPMessagingSupport.registerSOAP11Fault(messageContext,
                            WSSecurityConstants.SOAP_FAULT_INVALID_SECURITY_TOKEN, 
                            "The SAML 2.0 Assertion token was invalid", null, null, null);
                    throw new MessageHandlerException("Assertion token validation result was INVALID"); 
                }
                token.setValidationStatus(ValidationStatus.INVALID);
                token.setSubjectConfirmation((SubjectConfirmation) validationContext.getDynamicParameters()
                        .get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION));
                break;
            case INDETERMINATE:
                log.warn("Assertion token validation was INDETERMINATE. Reason(s): {}",
                        validationContext.getValidationFailureMessages());
                if (isInvalidFatal()) {
                    SOAPMessagingSupport.registerSOAP11Fault(messageContext,
                            WSSecurityConstants.SOAP_FAULT_INVALID_SECURITY_TOKEN, 
                            "The SAML 2.0 Assertion token's validity could not be determined", null, null, null);
                    throw new MessageHandlerException("Assertion token validation result was INDETERMINATE"); 
                }
                token.setValidationStatus(ValidationStatus.INDETERMINATE);
                token.setSubjectConfirmation((SubjectConfirmation) validationContext.getDynamicParameters()
                        .get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION));
                break;
            default:
                log.warn("Assertion validation result indicated an unknown value: {}", validationResult);
                SOAPMessagingSupport.registerSOAP11Fault(messageContext, FaultCode.SERVER, 
                        "Internal processing error", null, null, null);
                throw new IllegalArgumentException("Assertion validation result indicated an unknown value: "
                        + validationResult);
        }
        
    }

    /**
     * Build the Assertion ValidationContext.
     * 
     * @param messageContext the current message context
     * @param assertion the assertion which is to be validated
     * 
     * @return the new Assertion validation context to use
     * 
     * @throws MessageHandlerException if no validation context instance could be built
     */
    @Nonnull protected ValidationContext buildValidationContext(@Nonnull final MessageContext messageContext, 
            @Nonnull final Assertion assertion) throws MessageHandlerException {
        
        final HttpServletRequest servletRequest = getHttpServletRequest();
        if (servletRequest == null) {
            log.warn("HttpServletRequest was null");
            SOAPMessagingSupport.registerSOAP11Fault(messageContext, FaultCode.SERVER, 
                    "Internal processing error", null, null, null);
            throw new MessageHandlerException("HttpServletRequest was null");
        }
        
        final ValidationContext validationContext = getValidationContextBuilder().apply(
                new SAML20AssertionTokenValidationInput(messageContext, servletRequest, assertion));
        
        if (validationContext == null) {
            log.warn("ValidationContext produced was null");
            SOAPMessagingSupport.registerSOAP11Fault(messageContext, FaultCode.SERVER, 
                    "Internal processing error", null, null, null);
            throw new MessageHandlerException("No ValidationContext was produced");
        }
        
        return validationContext;
    }

    /**
     * Resolve the SAML 2.0 Assertions token from the SOAP envelope.
     * 
     * @param messageContext the current message context
     * 
     * @return the list of resolved Assertions, or an empty list
     */
    @Nonnull @Unmodifiable @NotLive protected List<Assertion> resolveAssertions(
            @Nonnull final MessageContext messageContext) {
        final List<XMLObject> securityHeaders = SOAPMessagingSupport.getInboundHeaderBlock(messageContext,
                Security.ELEMENT_NAME);
        if (securityHeaders == null || securityHeaders.isEmpty()) {
            log.debug("No WS-Security Security header found in inbound SOAP message. Skipping further processing.");
            return CollectionSupport.emptyList();
        }
        
        final LazyList<Assertion> assertions = new LazyList<>();
        
        // There could be multiple Security headers targeted to this node, so process all of them
        for (final XMLObject header : securityHeaders) {
            final Security securityHeader = (Security) header;
            final List<XMLObject> xmlObjects = securityHeader.getUnknownXMLObjects(Assertion.DEFAULT_ELEMENT_NAME);
            if (!xmlObjects.isEmpty()) {
                for (final XMLObject xmlObject : xmlObjects) {
                    assertions.add((Assertion) xmlObject);
                }
            }
        }
        
        return assertions;
    }

}
