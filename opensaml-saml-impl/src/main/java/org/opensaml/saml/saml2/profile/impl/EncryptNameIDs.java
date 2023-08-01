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

import java.util.Collection;
import java.util.Set;
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
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.ext.saml2delrestrict.Delegate;
import org.opensaml.saml.ext.saml2delrestrict.DelegationRestrictionType;
import org.opensaml.saml.saml2.core.ArtifactResponse;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.ManageNameIDRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDMappingRequest;
import org.opensaml.saml.saml2.core.NameIDMappingResponse;
import org.opensaml.saml.saml2.core.NewEncryptedID;
import org.opensaml.saml.saml2.core.NewID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectQuery;
import org.opensaml.saml.saml2.profile.context.EncryptionContext;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.xml.SerializeSupport;

/**
 * Action that encrypts all {@link NameID}s in a message obtained from a lookup strategy,
 * by default the outbound message context.
 * 
 * <p>Specific formats may be excluded from encryption, by default excluding the "entity" format.</p> 
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#UNABLE_TO_ENCRYPT}
 * 
 * @post All SAML {@link NameID}s in all locations have been replaced with encrypted versions.
 * It's possible for some to be replaced but others not if an error occurs.
 */
public class EncryptNameIDs extends AbstractEncryptAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EncryptNameIDs.class);
    
    /** Strategy used to locate the message to operate on. */
    @Nonnull private Function<ProfileRequestContext,SAMLObject> messageLookupStrategy;
    
    /** Formats to exclude from encryption. */
    @Nonnull private Set<String> excludedFormats;
    
    /** The message to operate on. */
    @NonnullBeforeExec private SAMLObject message;
    
    /** Constructor. */
    public EncryptNameIDs() {
        messageLookupStrategy = new MessageLookup<>(SAMLObject.class).compose(
                new OutboundMessageContextLookup());
        excludedFormats = CollectionSupport.singleton(NameID.ENTITY);
    }

    /**
     * Set the strategy used to locate the {@link Response} to operate on.
     * 
     * @param strategy strategy used to locate the {@link Response} to operate on
     */
    public void setMessageLookupStrategy(@Nonnull final Function<ProfileRequestContext,SAMLObject> strategy) {
        checkSetterPreconditions();

        messageLookupStrategy = Constraint.isNotNull(strategy, "Message lookup strategy cannot be null");
    }
    
    /**
     * Set the {@link NameID} formats to ignore and leave unencrypted.
     * 
     * @param formats   formats to exclude
     */
    public void setExcludedFormats(@Nonnull final Collection<String> formats) {
        excludedFormats = CollectionSupport.copyToSet(StringSupport.normalizeStringCollection(formats));
    }

    /** {@inheritDoc} */
    @Override
    @Nullable protected EncryptionParameters getApplicableParameters(@Nullable final EncryptionContext ctx) {
        if (ctx != null) {
            return ctx.getIdentifierEncryptionParameters();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        message = messageLookupStrategy.apply(profileRequestContext);

        if (message != null && message instanceof ArtifactResponse) {
            message = ((ArtifactResponse) message).getMessage();
        }
        
        if (message == null) {
            log.debug("{} Message was not present, nothing to do", getLogPrefix());
            return false;
        }
        
        return true;
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        try {
            
            if (message instanceof AuthnRequest m) {
                processSubject(m.getSubject());
            } else if (message instanceof SubjectQuery m) {
                processSubject(m.getSubject());
            } else if (message instanceof Response m) {
                for (final Assertion a : m.getAssertions()) {
                    assert a != null;
                    processAssertion(a);
                }
            } else if (message instanceof LogoutRequest m) {
                processLogoutRequest(m);
            } else if (message instanceof ManageNameIDRequest m) {
                processManageNameIDRequest(m);
            } else if (message instanceof NameIDMappingRequest m) {
                processNameIDMappingRequest(m);
            } else if (message instanceof NameIDMappingResponse m) {
                processNameIDMappingResponse(m);
            } else if (message instanceof Assertion m) {
                processAssertion(m);
            } else {
                log.debug("{} Message was of unrecognized type {}, nothing to do", getLogPrefix(),
                        message.getClass().getName());
                return;
            }
        } catch (final EncryptionException e) {
            log.warn("{} Error encrypting NameID", getLogPrefix(), e);
            ActionSupport.buildEvent(profileRequestContext, EventIds.UNABLE_TO_ENCRYPT);
        }
    }
// Checkstyle: CyclomaticComplexity ON
    
    /**
     * Return true iff the NameID should be encrypted.
     * 
     * @param name  NameID to check
     * 
     * @return  true iff encryption should happen
     */
    private boolean shouldEncrypt(@Nonnull final NameID name) {
        String format = name.getFormat();
        if (format == null) {
            format = NameID.UNSPECIFIED;
        }
        if (!excludedFormats.contains(format)) {
            if (log.isDebugEnabled()) {
                try {
                    final Element dom = XMLObjectSupport.marshall(name);
                    log.debug("{} NameID before encryption:\n{}", getLogPrefix(),
                            SerializeSupport.prettyPrintXML(dom));
                } catch (final MarshallingException e) {
                    log.error("{} Unable to marshall NameID for logging purposes", getLogPrefix(), e);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Encrypt any {@link NameID}s found in a subject and replace them with the result.
     * 
     * @param subject   subject to operate on
     * 
     * @throws EncryptionException if an error occurs
     */
    private void processSubject(@Nullable final Subject subject) throws EncryptionException {
        
        if (subject != null) {
            NameID nameID = subject.getNameID();
            if (nameID != null && shouldEncrypt(nameID)) {
                log.debug("{} Encrypt NameID in Subject", getLogPrefix());
                final EncryptedID encrypted = getEncrypter().encrypt(nameID);
                subject.setEncryptedID(encrypted);
                subject.setNameID(null);
            }
            
            for (final SubjectConfirmation sc : subject.getSubjectConfirmations()) {
                nameID = sc.getNameID();
                if (nameID != null && shouldEncrypt(nameID)) {
                    log.debug("{} Encrypt NameID in SubjectConfirmation", getLogPrefix());
                    final EncryptedID encrypted = getEncrypter().encrypt(nameID);
                    sc.setEncryptedID(encrypted);
                    sc.setNameID(null);
                }
            }
        }
    }
    
    /**
     * Encrypt a {@link NameID} found in a LogoutRequest and replace it with the result.
     * 
     * @param request   request to operate on
     * 
     * @throws EncryptionException if an error occurs
     */
    private void processLogoutRequest(@Nonnull final LogoutRequest request) throws EncryptionException {
        final NameID nameID = request.getNameID();
        if (nameID != null && shouldEncrypt(nameID)) {
            log.debug("{} Encrypting NameID in LogoutRequest", getLogPrefix());
            final EncryptedID encrypted = getEncrypter().encrypt(nameID);
            request.setEncryptedID(encrypted);
            request.setNameID(null);
        }
    }
    
    /**
     * Encrypt a {@link NameID} found in a ManageNameIDRequest and replace it with the result.
     * 
     * @param request   request to operate on
     * 
     * @throws EncryptionException if an error occurs
     */
    private void processManageNameIDRequest(@Nonnull final ManageNameIDRequest request) throws EncryptionException {
        
        final NameID nameID = request.getNameID();
        if (nameID != null && shouldEncrypt(nameID)) {
            log.debug("{} Encrypting NameID in ManageNameIDRequest", getLogPrefix());
            final EncryptedID encrypted = getEncrypter().encrypt(nameID);
            request.setEncryptedID(encrypted);
            request.setNameID(null);
        }
        
        final NewID newID = request.getNewID();
        if (newID != null && request.getNewID() != null) {
            log.debug("{} Encrypting NewID in ManageNameIDRequest", getLogPrefix());
            final NewEncryptedID encrypted = getEncrypter().encrypt(newID);
            request.setNewEncryptedID(encrypted);
            request.setNewID(null);
        }
    }

    /**
     * Encrypt a {@link NameID} found in a NameIDMappingRequest and replace it with the result.
     * 
     * @param request   request to operate on
     * 
     * @throws EncryptionException if an error occurs
     */
    private void processNameIDMappingRequest(@Nonnull final NameIDMappingRequest request) throws EncryptionException {
        
        final NameID nameID = request.getNameID();
        if (nameID != null && shouldEncrypt(nameID)) {
            log.debug("{} Encrypting NameID in NameIDMappingRequest", getLogPrefix());
            final EncryptedID encrypted = getEncrypter().encrypt(nameID);
            request.setEncryptedID(encrypted);
            request.setNameID(null);
        }
    }

    /**
     * Encrypt a {@link NameID} found in a NameIDMappingResponse and replace it with the result.
     * 
     * @param response   response to operate on
     * 
     * @throws EncryptionException if an error occurs
     */
    private void processNameIDMappingResponse(@Nonnull final NameIDMappingResponse response)
            throws EncryptionException {
        
        final NameID nameID = response.getNameID();
        if (nameID != null && shouldEncrypt(nameID)) {
            log.debug("{} Encrypting NameID in NameIDMappingResponse", getLogPrefix());
            final EncryptedID encrypted = getEncrypter().encrypt(nameID);
            response.setEncryptedID(encrypted);
            response.setNameID(null);
        }
    }

    /**
     * Decrypt any {@link EncryptedID} found in an assertion and replace it with the result.
     * 
     * @param assertion   assertion to operate on
     * 
     * @throws EncryptionException if an error occurs
     */
    private void processAssertion(@Nonnull final Assertion assertion) throws EncryptionException {

        processSubject(assertion.getSubject());            
        
        final Conditions conditions = assertion.getConditions();
        if (conditions != null) {
            for (final Condition c : conditions.getConditions()) {
                if (!(c instanceof DelegationRestrictionType)) {
                    continue;
                }
                for (final Delegate d : ((DelegationRestrictionType) c).getDelegates()) {
                    final NameID nameID = d.getNameID();
                    if (nameID != null && shouldEncrypt(nameID)) {
                        log.debug("{} Encrypting NameID in Delegate", getLogPrefix());
                        final EncryptedID encrypted = getEncrypter().encrypt(nameID);
                        d.setEncryptedID(encrypted);
                        d.setNameID(null);
                    }
                }
            }
        }
    }
    
}