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

package org.opensaml.saml.saml1.profile.impl;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLException;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.profile.logic.MetadataNameIdentifierFormatStrategy;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Statement;
import org.opensaml.saml.saml1.core.Subject;
import org.opensaml.saml.saml1.core.SubjectStatement;
import org.opensaml.saml.saml1.profile.SAML1NameIdentifierGenerator;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Action that builds a {@link NameIdentifier} and adds it to the {@link Subject} of all the statements
 * in all the assertions found via a lookup strategy, by default from the outbound message context.
 * 
 * <p>No assertions or statements will be created by this action, but if no {@link Subject} exists in
 * the statements found, it will be created.</p>
 * 
 * <p>The source of the {@link NameIdentifier} is one of a set of candidate {@link SAML1NameIdentifierGenerator}
 * plugins injected into the action. The plugin(s) to attempt to use are derived from the Format value,
 * which is established by a lookup strategy.</p>
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 */
public class AddNameIdentifierToSubjects extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AddNameIdentifierToSubjects.class);
    
    /** Builder for Subject objects. */
    @Nonnull private final SAMLObjectBuilder<Subject> subjectBuilder;

    /** Builder for NameIdentifier objects. */
    @Nonnull private final SAMLObjectBuilder<NameIdentifier> nameIdentifierBuilder;
    
    /** Flag controlling whether to overwrite an existing NameIdentifier. */
    private boolean overwriteExisting;
    
    /** Strategy used to locate the {@link Assertion}s to operate on. */
    @Nonnull private Function<ProfileRequestContext,List<Assertion>> assertionsLookupStrategy;

    /** Strategy used to determine the formats to try. */
    @Nonnull private Function<ProfileRequestContext,List<String>> formatLookupStrategy;

    /** Generator to use. */
    @NonnullAfterInit private SAML1NameIdentifierGenerator generator;
    
    /** Formats to try. */
    @NonnullBeforeExec private List<String> formats;
    
    /** Assertions to modify. */
    @NonnullBeforeExec private List<Assertion> assertions;
    
    /** Constructor. */
    public AddNameIdentifierToSubjects() {
        subjectBuilder = (SAMLObjectBuilder<Subject>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Subject>ensureBuilder(
                        Subject.DEFAULT_ELEMENT_NAME);
        nameIdentifierBuilder = (SAMLObjectBuilder<NameIdentifier>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<NameIdentifier>ensureBuilder(
                        NameIdentifier.DEFAULT_ELEMENT_NAME);
        
        overwriteExisting = true;
        
        assertionsLookupStrategy = new AssertionStrategy();
        formatLookupStrategy = new MetadataNameIdentifierFormatStrategy();
    }
    
    /**
     * Set whether to overwrite any existing {@link NameIdentifier} objects found.
     * 
     * @param flag  true iff the action should overwrite any existing objects
     */
    public void setOverwriteExisting(final boolean flag) {
        checkSetterPreconditions();
        overwriteExisting = flag;
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
    public void setNameIdentifierGenerator(@Nonnull final SAML1NameIdentifierGenerator theGenerator) {
        checkSetterPreconditions();
        generator = Constraint.isNotNull(theGenerator, "SAML1NameIdentifierGenerator cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (generator == null) {
            throw new ComponentInitializationException("SAML1NameIdentifierGenerator cannot be null");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        log.debug("{} Attempting to add NameIdentifier to statements in outgoing Assertions", getLogPrefix());

        assertions = assertionsLookupStrategy.apply(profileRequestContext);
        if (assertions == null || assertions.isEmpty()) {
            log.debug("{} No assertions returned, nothing to do", getLogPrefix());
            return false;
        }
        
        formats = formatLookupStrategy.apply(profileRequestContext);
        if (formats == null || formats.isEmpty()) {
            log.debug("{} No candidate NameIdentifier formats, nothing to do", getLogPrefix());
            return false;
        }
        log.debug("{} Candidate NameIdentifier formats: {}", getLogPrefix(), formats);
        
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        final NameIdentifier nameIdentifier = generateNameIdentifier(profileRequestContext);
        if (nameIdentifier == null) {
            log.debug("{} Unable to generate a NameIdentifier, leaving empty", getLogPrefix());
            return;
        }
        
        int count = 0;
        
        for (final Assertion assertion : assertions) {
            for (final Statement statement : assertion.getStatements()) {
                if (statement instanceof SubjectStatement) {
                    final Subject subject = getStatementSubject((SubjectStatement) statement);
                    final NameIdentifier existing = subject.getNameIdentifier();
                    if (existing == null || overwriteExisting) {
                        subject.setNameIdentifier(count > 0 ? cloneNameIdentifier(nameIdentifier) : nameIdentifier);
                        count ++;
                    }
                }
            }
        }
        
        if (count > 0) {
            log.debug("{} Added NameIdentifier to {} statement subject(s)", getLogPrefix(), count);
        }
    }

    /**
     * Attempt to generate a {@link NameIdentifier} using each of the candidate Formats and plugins.
     * 
     * @param profileRequestContext current profile request context
     * 
     * @return a generated {@link NameIdentifier} or null
     */
    @Nullable private NameIdentifier generateNameIdentifier(
            @Nonnull final ProfileRequestContext profileRequestContext) {
        
        // See if we can generate one.
        for (final String format : formats) {
            log.debug("{} Trying to generate NameIdentifier with Format {}", getLogPrefix(), format);
            try {
                final NameIdentifier nameIdentifier = generator.generate(profileRequestContext, format);
                if (nameIdentifier != null) {
                    log.debug("{} Successfully generated NameIdentifier with Format {}", getLogPrefix(),
                            format);
                    return nameIdentifier;
                }
            } catch (final SAMLException e) {
                log.error("{} Error while generating NameIdentifier", getLogPrefix(), e);
            }
        }
        
        return null;
    }
    
    /**
     * Get the subject to which the name identifier will be added.
     * 
     * @param statement the statement being modified
     * 
     * @return the subject to which the name identifier will be added
     */
    @Nonnull private Subject getStatementSubject(@Nonnull final SubjectStatement statement) {
        Subject subject = statement.getSubject();
        if (subject != null) {
            return subject;
        }
        
        subject = subjectBuilder.buildObject();
        statement.setSubject(subject);
        return subject;
    }
    
    /**
     * Create an efficient field-wise copy of a {@link NameIdentifier}.
     * 
     * @param nameIdentifier    the object to clone
     * 
     * @return the copy
     */
    @Nonnull private NameIdentifier cloneNameIdentifier(@Nonnull final NameIdentifier nameIdentifier) {
        final NameIdentifier clone = nameIdentifierBuilder.buildObject();
        
        clone.setFormat(nameIdentifier.getFormat());
        clone.setNameQualifier(nameIdentifier.getNameQualifier());
        clone.setValue(nameIdentifier.getValue());
        
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
        @Nullable public List<Assertion> apply(@Nullable final ProfileRequestContext input) {
            if (input != null && input.getOutboundMessageContext() != null) {
                final Object outboundMessage = input.ensureOutboundMessageContext().getMessage();
                if (outboundMessage == null) {
                    return null;
                } else if (outboundMessage instanceof Assertion) {
                    return CollectionSupport.singletonList((Assertion) outboundMessage);
                } else if (outboundMessage instanceof Response) {
                    return ((Response) outboundMessage).getAssertions();
                }
            }
            
            return null;
        }
    }

}