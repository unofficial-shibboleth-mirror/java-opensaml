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

import java.util.function.Function;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.messaging.context.SAMLSubjectNameIdentifierContext;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Statement;
import org.opensaml.saml.saml1.core.Subject;
import org.opensaml.saml.saml1.core.SubjectStatement;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Action that builds a {@link NameIdentifier} and adds it to the {@link Subject} of all the statements
 * in all the assertions found in a {@link Response}. The message to update is returned by a lookup
 * strategy, by default the message returned by {@link ProfileRequestContext#getOutboundMessageContext()}.
 * 
 * <p>No assertions or statements will be created by this action, but if no {@link Subject} exists in
 * the statements found, it will be created.</p>
 * 
 * <p>The source of the {@link NameIdentifier} is a {@link SAMLSubjectNameIdentifierContext} returned by a lookup
 * strategy.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public class CopyNameIdentifierFromRequest extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(CopyNameIdentifierFromRequest.class);
    
    /** Builder for Subject objects. */
    @Nonnull private final SAMLObjectBuilder<Subject> subjectBuilder;

    /** Builder for NameIdentifier objects. */
    @Nonnull private final SAMLObjectBuilder<NameIdentifier> nameIdentifierBuilder;

    /** Flag controlling whether to overwrite an existing NameIdentifier. */
    private boolean overwriteExisting;
        
    /** Strategy used to locate the name identifier context to copy from. */
    @Nonnull
    private Function<ProfileRequestContext,SAMLSubjectNameIdentifierContext> nameIdentifierContextLookupStrategy;
    
    /** Strategy used to locate the {@link Response} to operate on. */
    @Nonnull private Function<ProfileRequestContext,Response> responseLookupStrategy;

    /** NameIdentifier to copy. */
    @NonnullBeforeExec private NameIdentifier nameIdentifier; 
    
    /** Response to modify. */
    @NonnullBeforeExec private Response response;
    
    /** Constructor. */
    public CopyNameIdentifierFromRequest() {
        subjectBuilder = (SAMLObjectBuilder<Subject>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Subject>ensureBuilder(
                        Subject.DEFAULT_ELEMENT_NAME);
        nameIdentifierBuilder = (SAMLObjectBuilder<NameIdentifier>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<NameIdentifier>ensureBuilder(
                        NameIdentifier.DEFAULT_ELEMENT_NAME);
        
        overwriteExisting = true;
        
        nameIdentifierContextLookupStrategy =
                new ChildContextLookup<>(SAMLSubjectNameIdentifierContext.class, true).compose(
                        new InboundMessageContextLookup());
        
        responseLookupStrategy = new MessageLookup<>(Response.class).compose(new OutboundMessageContextLookup());
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
     * Set the strategy used to locate the {@link SAMLSubjectNameIdentifierContext} to copy from.
     * 
     * @param strategy lookup strategy
     */
    public void setNameIdentifierContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,SAMLSubjectNameIdentifierContext> strategy) {
        checkSetterPreconditions();
        nameIdentifierContextLookupStrategy = Constraint.isNotNull(strategy,
                "SAMLSubjectNameIdentifierContext lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to locate the {@link Response} to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setResponseLookupStrategy(@Nonnull final Function<ProfileRequestContext,Response> strategy) {
        checkSetterPreconditions();
        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        log.debug("{} Attempting to add NameIdentifier to statements in outgoing Response", getLogPrefix());

        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} No SAML response located", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        } else if (response.getAssertions().isEmpty()) {
            log.debug("{} No assertions in response message, nothing to do", getLogPrefix());
            return false;
        }
        
        final SAMLSubjectNameIdentifierContext idCtx = nameIdentifierContextLookupStrategy.apply(profileRequestContext);
        if (idCtx == null) {
            log.debug("{} No SAMLSubjectNameIdentifierContext located", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }
        
        nameIdentifier = idCtx.getSAML1SubjectNameIdentifier();
        if (nameIdentifier == null) {
            log.debug("{} No SAMLSubjectNameIdentifierContext located", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }
                
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        int count = 0;
        
        for (final Assertion assertion : response.getAssertions()) {
            for (final Statement statement : assertion.getStatements()) {
                if (statement instanceof SubjectStatement) {
                    final Subject subject = getStatementSubject((SubjectStatement) statement);
                    final NameIdentifier existing = subject.getNameIdentifier();
                    if (existing == null || overwriteExisting) {
                        subject.setNameIdentifier(cloneNameIdentifier());
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
     * @return the copy
     */
    @Nonnull private NameIdentifier cloneNameIdentifier() {
        final NameIdentifier clone = nameIdentifierBuilder.buildObject();
        
        clone.setFormat(nameIdentifier.getFormat());
        clone.setNameQualifier(nameIdentifier.getNameQualifier());
        clone.setValue(nameIdentifier.getValue());
        
        return clone;
    }
    
}