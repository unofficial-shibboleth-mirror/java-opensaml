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

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.saml.common.SAMLException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.common.profile.logic.DefaultNameIDPolicyPredicate;
import org.opensaml.saml.common.profile.logic.MetadataNameIdentifierFormatStrategy;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.profile.SAML2ActionSupport;
import org.opensaml.saml.saml2.profile.SAML2NameIDGenerator;
import org.slf4j.Logger;

import com.google.common.base.Strings;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.security.IdentifierGenerationStrategy;
import net.shibboleth.shared.security.IdentifierGenerationStrategy.ProviderType;

/**
 * Action that builds a {@link NameID} and adds it to the {@link Subject} of all the assertions
 * found in a {@link Response}. The message to update is returned by a lookup strategy, by default
 * the message returned by {@link ProfileRequestContext#getOutboundMessageContext()}.
 * 
 * <p>If no {@link Response} exists, then an {@link Assertion} directly in the outbound message context will
 * be used or created by the default lookup strategy.</p>
 * 
 * <p>If no {@link Subject} exists in the assertions found, it will be cretaed.</p>
 * 
 * <p>The source of the {@link NameID} is one of a set of candidate {@link SAML2NameIDGenerator}
 * plugins injected into the action. The plugin(s) to attempt to use are derived from the Format value,
 * which is established by a lookup strategy.</p>
 * 
 * <p>In addition, the generation process is influenced by the requested {@link NameIDPolicy}, which
 * is evaluated using a pluggable predicate.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_PROFILE_CTX}
 * @event {@link SAMLEventIds#INVALID_NAMEID_POLICY}
 */
public class AddNameIDToSubjects extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AddNameIDToSubjects.class);
    
    /** Builder for Subject objects. */
    @Nonnull private SAMLObjectBuilder<Subject> subjectBuilder;

    /** Builder for NameID objects. */
    @Nonnull private SAMLObjectBuilder<NameID> nameIdBuilder;
    
    /** Flag controlling whether to overwrite an existing NameID. */
    private boolean overwriteExisting;

    /** Strategy used to locate the {@link AuthnRequest} to operate on, if any. */
    @Nonnull private Function<ProfileRequestContext,AuthnRequest> requestLookupStrategy;
    
    /** Strategy used to locate the {@link Response} to operate on. */
    @Nonnull private Function<ProfileRequestContext,List<Assertion>> assertionsLookupStrategy;

    /** Strategy used to locate the {@link IdentifierGenerationStrategy} to use. */
    @Nonnull private Function<ProfileRequestContext,IdentifierGenerationStrategy> idGeneratorLookupStrategy;

    /** Strategy used to obtain the response issuer value. */
    @Nullable private Function<ProfileRequestContext,String> issuerLookupStrategy;
    
    /** Predicate to validate {@link NameIDPolicy}. */
    @Nonnull private Predicate<ProfileRequestContext> nameIDPolicyPredicate;
    
    /** Strategy used to determine the formats to try. */
    @Nonnull private Function<ProfileRequestContext,List<String>> formatLookupStrategy;
    
    /** Generator to use. */
    @NonnullAfterInit private SAML2NameIDGenerator generator;

    /** Formats to try. */
    @NonnullBeforeExec private List<String> formats;
    
    /** Format required by requested {@link NameIDPolicy}. */
    @Nullable private String requiredFormat;

    /** Request to examine. */
    @Nullable private AuthnRequest request;
    
    /** Response to modify. */
    @NonnullBeforeExec private List<Assertion> assertions;
    
    /** The generator to use. */
    @NonnullBeforeExec private IdentifierGenerationStrategy idGenerator;

    /** EntityID to populate into Issuer element. */
    @Nullable private String issuerId;

    /** Constructor.
     *  
     * @throws ComponentInitializationException if an error occurs initializing default predicate.
     */
    public AddNameIDToSubjects() throws ComponentInitializationException {
        subjectBuilder = (SAMLObjectBuilder<Subject>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Subject>ensureBuilder(
                        Subject.DEFAULT_ELEMENT_NAME);
        nameIdBuilder = (SAMLObjectBuilder<NameID>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<NameID>ensureBuilder(
                        NameID.DEFAULT_ELEMENT_NAME);
        
        overwriteExisting = true;
        
        requestLookupStrategy = new MessageLookup<>(AuthnRequest.class).compose(new InboundMessageContextLookup());
        assertionsLookupStrategy = new AssertionStrategy();

        // Default strategy is a 16-byte secure random source.
        idGeneratorLookupStrategy = prc ->IdentifierGenerationStrategy.getInstance(ProviderType.SECURE);
        
        // Default predicate pulls SPNameQualifier from NameIDPolicy and does a direct match
        // against issuer. Handles simple cases, overridden for complex ones.
        nameIDPolicyPredicate = new DefaultNameIDPolicyPredicate();
        ((DefaultNameIDPolicyPredicate) nameIDPolicyPredicate).setRequesterIdLookupStrategy(
                new RequesterIdFromIssuerFunction());
        ((DefaultNameIDPolicyPredicate) nameIDPolicyPredicate).setObjectLookupStrategy(
                new NameIDPolicyLookupFunction());
        ((DefaultNameIDPolicyPredicate) nameIDPolicyPredicate).initialize();
        
        formatLookupStrategy = new MetadataNameIdentifierFormatStrategy();
        formats = CollectionSupport.emptyList();
    }
    
    /**
     * Set whether to overwrite any existing {@link NameID} objects found.
     * 
     * @param flag  true iff the action should overwrite any existing objects
     */
    public void setOverwriteExisting(final boolean flag) {
        checkSetterPreconditions();
        
        overwriteExisting = flag;
    }

    /**
     * Set the strategy used to locate the {@link AuthnRequest} to examine, if any.
     * 
     * @param strategy strategy used to locate the {@link AuthnRequest}
     */
    public void setRequestLookupStrategy(@Nonnull final Function<ProfileRequestContext,AuthnRequest> strategy) {
        checkSetterPreconditions();

        requestLookupStrategy = Constraint.isNotNull(strategy, "AuthnRequest lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to locate the {@link Assertion}s to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setAssertionsLookupStrategy(@Nonnull final Function<ProfileRequestContext,List<Assertion>> strategy) {
        checkSetterPreconditions();

        assertionsLookupStrategy = Constraint.isNotNull(strategy, "Assertions lookup strategy cannot be null");
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
    
    /**
     * Set the predicate used to evaluate the {@link NameIDPolicy}.
     * 
     * @param predicate predicate used to evaluate the {@link NameIDPolicy}
     */
    public void setNameIDPolicyPredicate(@Nonnull final Predicate<ProfileRequestContext> predicate) {
        checkSetterPreconditions();
        
        nameIDPolicyPredicate = Constraint.isNotNull(predicate, "NameIDPolicy predicate cannot be null");
    }

    /**
     * Set the strategy function to use to obtain the formats to try.
     * 
     * @param strategy  format lookup strategy
     */
    public void setFormatLookupStrategy(@Nonnull final Function<ProfileRequestContext,List<String>> strategy) {
        checkSetterPreconditions();
        
        formatLookupStrategy = Constraint.isNotNull(strategy, "Format lookup strategy cannot be null");
    }
    
    /**
     * Set the generator to use.
     * 
     * @param theGenerator the generator to use
     */
    public void setNameIDGenerator(@Nullable final SAML2NameIDGenerator theGenerator) {
        checkSetterPreconditions();
        
        generator = Constraint.isNotNull(theGenerator, "SAML2NameIDGenerator cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (generator == null) {
            throw new ComponentInitializationException("SAML2NameIDGenerator cannot be null");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        log.debug("{} Attempting to add NameID to outgoing Assertion Subjects", getLogPrefix());
        
        idGenerator = idGeneratorLookupStrategy.apply(profileRequestContext);
        if (idGenerator == null) {
            log.debug("{} No identifier generation strategy", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return false;
        }
        
        if (issuerLookupStrategy != null) {
            issuerId = issuerLookupStrategy.apply(profileRequestContext);
        }
        
        assertions = assertionsLookupStrategy.apply(profileRequestContext);
        if (assertions == null || assertions.isEmpty()) {
            log.debug("{} No assertions returned, nothing to do", getLogPrefix());
            return false;
        }
        
        if (!nameIDPolicyPredicate.test(profileRequestContext)) {
            log.debug("{} NameIDPolicy was unacceptable", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.INVALID_NAMEID_POLICY);
            return false;
        }
        
        request = requestLookupStrategy.apply(profileRequestContext);
        
        requiredFormat = getRequiredFormat(profileRequestContext);
        if (requiredFormat != null) {
            formats = CollectionSupport.singletonList(requiredFormat);
            log.debug("{} Request specified NameID format: {}", getLogPrefix(), requiredFormat);
        } else {
            formats = formatLookupStrategy.apply(profileRequestContext);
            if (formats == null || formats.isEmpty()) {
                log.debug("{} No candidate NameID formats, nothing to do", getLogPrefix());
                return false;
            }
            log.debug("{} Candidate NameID formats: {}", getLogPrefix(), formats);
        }
        
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        final NameID nameId = generateNameID(profileRequestContext);
        if (nameId == null) {
            if (requiredFormat != null) {
                log.warn("{} Request specified use of an unsupportable identifier format: {}", getLogPrefix(),
                        requiredFormat);
                ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.INVALID_NAMEID_POLICY);
            } else {
                log.debug("{} Unable to generate a NameID, leaving empty", getLogPrefix());
            }
            return;
        }
        
        int count = 0;
        
        for (final Assertion assertion : assertions) {
            assert assertion != null;
            final Subject subject = getAssertionSubject(assertion);
            final NameID existing = subject.getNameID();
            if (existing == null || overwriteExisting) {
                subject.setNameID(count > 0 ? cloneNameID(nameId) : nameId);
            }
            count ++;
        }
        
        if (count > 0) {
            log.debug("{} Added NameID to {} assertion subject(s)", getLogPrefix(), count);
        }
    }

    /**
     * Extract a format required by the inbound request, if present.
     * 
     * @param profileRequestContext current profile request context
     * 
     * @return a format dictated by the request, or null 
     */
    @Nullable private String getRequiredFormat(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (request != null) {
            final NameIDPolicy policy = request.getNameIDPolicy();
            if (policy != null) {
                final String format = policy.getFormat();
                if (!Strings.isNullOrEmpty(format) && !NameID.UNSPECIFIED.equals(format)
                        && !NameID.ENCRYPTED.equals(format)) {
                    return format;
                }
            }
        }
        
        return null;
    }

    /**
     * Attempt to generate a {@link NameID} using each of the candidate Formats and plugins.
     * 
     * @param profileRequestContext current profile request context
     * 
     * @return a generated {@link NameID} or null
     */
    @Nullable private NameID generateNameID(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        // See if we can generate one.
        for (final String format : formats) {
            assert format != null;
            log.debug("{} Trying to generate NameID with Format {}", getLogPrefix(), format);
            try {
                final NameID nameId = generator.generate(profileRequestContext, format);
                if (nameId != null) {
                    log.debug("{} Successfully generated NameID with Format {}", getLogPrefix(), format);
                    return nameId;
                }
            } catch (final SAMLException e) {
                log.error("{} Error while generating NameID", getLogPrefix(), e);
            }
        }
        
        return null;
    }
    
    /**
     * Get the subject to which the name identifier will be added.
     * 
     * @param assertion the assertion being modified
     * 
     * @return the assertion to which the name identifier will be added
     */
    @Nonnull private Subject getAssertionSubject(@Nonnull final Assertion assertion) {
        Subject subject = assertion.getSubject();
        if (subject != null) {
            return subject;
        }
        
        subject = subjectBuilder.buildObject();
        assertion.setSubject(subject);
        return subject;
    }
    
    /**
     * Create an efficient field-wise copy of a {@link NameID}.
     * 
     * @param nameId    the object to clone
     * 
     * @return the copy
     */
    @Nonnull private NameID cloneNameID(@Nonnull final NameID nameId) {
        final NameID clone = nameIdBuilder.buildObject();
        
        clone.setFormat(nameId.getFormat());
        clone.setNameQualifier(nameId.getNameQualifier());
        clone.setSPNameQualifier(nameId.getSPNameQualifier());
        clone.setSPProvidedID(nameId.getSPProvidedID());
        clone.setValue(nameId.getValue());
        
        return clone;
    }

    /**
     * Default strategy for obtaining assertions to modify.
     * 
     * <p>If the outbound context is empty, a null is returned. If the outbound
     * message is already an assertion, it's returned. If the outbound message is a response,
     * then its contents are returned. If the outbound message is anything else, null is returned.</p>
     */
    private class AssertionStrategy implements Function<ProfileRequestContext,List<Assertion>> {

        /** {@inheritDoc} */
        @Override
        @Nullable @Unmodifiable @NotLive public List<Assertion> apply(@Nullable final ProfileRequestContext input) {
            final MessageContext outboundContext = input != null ? input.getOutboundMessageContext() : null;
            if (outboundContext != null) {
                final Object outboundMessage = outboundContext.getMessage();
                if (outboundMessage == null) {
                    final Assertion ret = SAML2ActionSupport.buildAssertion(AddNameIDToSubjects.this,
                            idGenerator, issuerId);
                    outboundContext.setMessage(ret);
                    return CollectionSupport.singletonList(ret);
                } else if (outboundMessage instanceof Assertion) {
                    return CollectionSupport.singletonList((Assertion) outboundMessage);
                } else if (outboundMessage instanceof Response) {
                    return ((Response) outboundMessage).getAssertions();
                }
            }
            
            return null;
        }
    }
    
    /**
     * Lookup function that returns the {@link NameIDPolicy} from an {@link AuthnRequest} message returned
     * from a lookup function, by default the inbound message.
     */
    public static class NameIDPolicyLookupFunction implements Function<ProfileRequestContext,SAMLObject> {

        /** Strategy used to locate the {@link AuthnRequest} to operate on. */
        @Nonnull private Function<ProfileRequestContext,AuthnRequest> requestLookupStrategy;
        
        /** Constructor. */
        public NameIDPolicyLookupFunction() {
            requestLookupStrategy = new MessageLookup<>(AuthnRequest.class).compose(new InboundMessageContextLookup());
        }

        /**
         * Set the strategy used to locate the {@link AuthnRequest} to examine.
         * 
         * @param strategy strategy used to locate the {@link AuthnRequest}
         */
        public void setRequestLookupStrategy(@Nonnull final Function<ProfileRequestContext,AuthnRequest> strategy) {
            requestLookupStrategy = Constraint.isNotNull(strategy, "AuthnRequest lookup strategy cannot be null");
        }
        
        /** {@inheritDoc} */
        @Nullable public SAMLObject apply(@Nullable final ProfileRequestContext profileRequestContext) {
            
            final AuthnRequest request = requestLookupStrategy.apply(profileRequestContext);

            if (request != null) {
                return request.getNameIDPolicy();
            }
            
            return null;
        }
        
    }

    /**
     * Lookup function that returns {@link org.opensaml.saml.saml2.core.RequestAbstractType#getIssuer()}
     * from a request message returned from a lookup function, by default the inbound message.
     */
    public static class RequesterIdFromIssuerFunction implements Function<ProfileRequestContext,String> {

        /** Strategy used to locate the {@link AuthnRequest} to operate on. */
        @Nonnull private Function<ProfileRequestContext,RequestAbstractType> requestLookupStrategy;
        
        /** Constructor. */
        public RequesterIdFromIssuerFunction() {
            requestLookupStrategy =
                    new MessageLookup<>(RequestAbstractType.class).compose(
                            new InboundMessageContextLookup());
        }

        /**
         * Set the strategy used to locate the {@link RequestAbstractType} to examine.
         * 
         * @param strategy strategy used to locate the {@link RequestAbstractType}
         */
        public void setRequestLookupStrategy(
                @Nonnull final Function<ProfileRequestContext,RequestAbstractType> strategy) {
            requestLookupStrategy = Constraint.isNotNull(strategy, "Request lookup strategy cannot be null");
        }
        
        /** {@inheritDoc} */
        @Nullable public String apply(@Nullable final ProfileRequestContext profileRequestContext) {
            
            final RequestAbstractType request = requestLookupStrategy.apply(profileRequestContext);
            if (request != null) {
                final Issuer issuer = request.getIssuer();
                if (issuer != null && (issuer.getFormat() == null || NameID.ENTITY.equals(issuer.getFormat()))) {
                    return issuer.getValue();
                }
            }
            
            return null;
        }
        
    }
    
}