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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.ext.saml2delrestrict.Delegate;
import org.opensaml.saml.ext.saml2delrestrict.DelegationRestrictionType;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.EncryptedElementType;
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
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.slf4j.Logger;

import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Action to decrypt an {@link EncryptedID} element and replace it with the decrypted {@link NameID}
 * in situ.
 * 
 * <p>All of the built-in SAML message types that may include an {@link EncryptedID} are potentially
 * handled, but the actual message to handle is obtained via strategy function, by default the inbound
 * message.</p> 
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 * @event {@link SAMLEventIds#DECRYPT_NAMEID_FAILED}
 */
public class DecryptNameIDs extends AbstractDecryptAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(DecryptNameIDs.class);
    
// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        final SAMLObject message = getSAMLObject();
        
        try {
            if (message instanceof AuthnRequest r) {
                processSubject(profileRequestContext, r.getSubject());
            } else if (message instanceof SubjectQuery r) {
                processSubject(profileRequestContext, r.getSubject());
            } else if (message instanceof Response r) {
                for (final Assertion a : r.getAssertions()) {
                    assert a != null;
                    processAssertion(profileRequestContext, a);
                }
            } else if (message instanceof LogoutRequest r) {
                processLogoutRequest(profileRequestContext, r);
            } else if (message instanceof ManageNameIDRequest r) {
                processManageNameIDRequest(profileRequestContext, r);
            } else if (message instanceof NameIDMappingRequest r) {
                processNameIDMappingRequest(profileRequestContext, r);
            } else if (message instanceof NameIDMappingResponse r) {
                processNameIDMappingResponse(profileRequestContext, r);
            } else if (message instanceof Assertion a) {
                processAssertion(profileRequestContext, a);
            } else {
                log.debug("{} Message was of unrecognized type {}, nothing to do", getLogPrefix(),
                        message.getClass().getName());
                return;
            }
        } catch (final DecryptionException e) {
            log.warn("{} Failure performing decryption", getLogPrefix(), e);
            if (isErrorFatal()) {
                ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.DECRYPT_NAMEID_FAILED);
            }
        }
        
    }
// Checkstyle: CyclomaticComplexity ON
    
    /**
     * Decrypt an {@link EncryptedID} and return the result.
     * 
     * @param profileRequestContext current profile request context
     * @param encID the encrypted object
     * 
     * @return the decrypted name, or null if the object did not need decryption
     * @throws DecryptionException if an error occurs during decryption
     */
    @Nullable private NameID processEncryptedID(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final EncryptedID encID) throws DecryptionException {
        
        if (!getDecryptionPredicate().test(
                new Pair<ProfileRequestContext,EncryptedElementType>(profileRequestContext, encID))) {
            return null;
        }
        
        final Decrypter decrypter = getDecrypter();
        if (decrypter == null) {
            throw new DecryptionException("No decryption parameters, unable to decrypt EncryptedID");
        }
        
        final SAMLObject object = decrypter.decrypt(encID);
        if (object instanceof NameID) {
            return (NameID) object;
        }
        throw new DecryptionException("Decrypted EncryptedID was not a NameID, was a "
                + object.getElementQName().toString());
    }

    /**
     * Decrypt a {@link NewEncryptedID} and return the result.
     * 
     * @param profileRequestContext current profile request context
     * @param encID the encrypted object
     * 
     * @return the decrypted name, or null if the object did not need decryption
     * @throws DecryptionException if an error occurs during decryption
     */
    @Nullable private NewID processNewEncryptedID(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final NewEncryptedID encID) throws DecryptionException {
        
        if (!getDecryptionPredicate().test(
                new Pair<ProfileRequestContext,EncryptedElementType>(profileRequestContext, encID))) {
            return null;
        }

        final Decrypter decrypter = getDecrypter();
        if (decrypter == null) {
            throw new DecryptionException("No decryption parameters, unable to decrypt NewEncryptedID");
        }

        return decrypter.decrypt(encID);
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /**
     * Decrypt any {@link EncryptedID} found in a subject and replace it with the result.
     * 
     * @param profileRequestContext current profile request context
     * @param subject   subject to operate on
     * 
     * @throws DecryptionException if an error occurs
     */
    private void processSubject(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nullable final Subject subject) throws DecryptionException {
        
        if (subject != null) {
            EncryptedID encID = subject.getEncryptedID();
            if (encID != null) {
                log.debug("{} Decrypting EncryptedID in Subject", getLogPrefix());
                try {
                    final NameID decrypted = processEncryptedID(profileRequestContext, encID);
                    if (decrypted != null) {
                        subject.setNameID(decrypted);
                        subject.setEncryptedID(null);
                    }
                } catch (final DecryptionException e) {
                    if (isErrorFatal()) {
                        throw e;
                    }
                    log.warn("{} Trapped failure decrypting EncryptedID in Subject", getLogPrefix(), e);
                }
            }
            
            for (final SubjectConfirmation sc : subject.getSubjectConfirmations()) {
                encID = sc.getEncryptedID();
                if (encID != null) {
                    log.debug("{} Decrypting EncryptedID in SubjectConfirmation", getLogPrefix());
                    try {
                        final NameID decrypted = processEncryptedID(profileRequestContext, encID);
                        if (decrypted != null) {
                            sc.setNameID(decrypted);
                            sc.setEncryptedID(null);
                        }
                    } catch (final DecryptionException e) {
                        if (isErrorFatal()) {
                            throw e;
                        }
                        log.warn("{} Trapped failure decrypting EncryptedID in SubjectConfirmation", getLogPrefix(), e);
                    }
                }
            }
        }
    }
// Checkstyle: CyclomaticComplexity ON

    /**
     * Decrypt any {@link EncryptedID} found in a LogoutRequest and replace it with the result.
     * 
     * @param profileRequestContext current profile request context
     * @param request   request to operate on
     * 
     * @throws DecryptionException if an error occurs
     */
    private void processLogoutRequest(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final LogoutRequest request) throws DecryptionException {
        
        final EncryptedID encID = request.getEncryptedID();
        if (encID != null) {
            log.debug("{} Decrypting EncryptedID in LogoutRequest", getLogPrefix());
            final NameID decrypted = processEncryptedID(profileRequestContext, encID);
            if (decrypted != null) {
                request.setNameID(decrypted);
                request.setEncryptedID(null);
            }
        }
    }

    /**
     * Decrypt any {@link EncryptedID} found in a ManageNameIDRequest and replace it with the result.
     * 
     * @param profileRequestContext current profile request context
     * @param request   request to operate on
     * 
     * @throws DecryptionException if an error occurs
     */
    private void processManageNameIDRequest(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final ManageNameIDRequest request) throws DecryptionException {
        
        final EncryptedID encID = request.getEncryptedID();
        if (encID != null) {
            log.debug("{} Decrypting EncryptedID in ManageNameIDRequest", getLogPrefix());
            final NameID decrypted = processEncryptedID(profileRequestContext, encID);
            if (decrypted != null) {
                request.setNameID(decrypted);
                request.setEncryptedID(null);
            }
        }
        
        final NewEncryptedID newID = request.getNewEncryptedID();
        if (newID != null) {
            log.debug("{} Decrypting NewEncryptedID in ManageNameIDRequest", getLogPrefix());
            final NewID decrypted = processNewEncryptedID(profileRequestContext, newID);
            if (decrypted != null) {
                request.setNewID(decrypted);
                request.setNewEncryptedID(null);
            }
        }
    }

    /**
     * Decrypt any {@link EncryptedID} found in a NameIDMappingRequest and replace it with the result.
     * 
     * @param profileRequestContext current profile request context
     * @param request   request to operate on
     * 
     * @throws DecryptionException if an error occurs
     */
    private void processNameIDMappingRequest(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final NameIDMappingRequest request) throws DecryptionException {
        
        final EncryptedID encID = request.getEncryptedID();
        if (encID != null) {
            log.debug("{} Decrypting EncryptedID in NameIDMappingRequest", getLogPrefix());
            final NameID decrypted = processEncryptedID(profileRequestContext, encID);
            if (decrypted != null) {
                request.setNameID(decrypted);
                request.setEncryptedID(null);
            }
        }
    }

    /**
     * Decrypt any {@link EncryptedID} found in a NameIDMappingResponse and replace it with the result.
     * 
     * @param profileRequestContext current profile request context
     * @param response   response to operate on
     * 
     * @throws DecryptionException if an error occurs 
     */
    private void processNameIDMappingResponse(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final NameIDMappingResponse response) throws DecryptionException {
        
        final EncryptedID encID = response.getEncryptedID();
        if (encID != null) {
            log.debug("{} Decrypting EncryptedID in NameIDMappingRequest", getLogPrefix());
            final NameID decrypted = processEncryptedID(profileRequestContext, encID);
            if (decrypted != null) {
                response.setNameID(decrypted);
                response.setEncryptedID(null);
            }
        }
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /**
     * Decrypt any {@link EncryptedID} found in an assertion and replace it with the result.
     * 
     * @param profileRequestContext current profile request context
     * @param assertion   assertion to operate on
     * 
     * @throws DecryptionException if an error occurs
     */
    private void processAssertion(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final Assertion assertion) throws DecryptionException {

        try {
            processSubject(profileRequestContext, assertion.getSubject());
        } catch (final DecryptionException e) {
            if (isErrorFatal()) {
                throw e;
            }
            log.warn("{} Trapped failure decrypting EncryptedIDs in Subject", getLogPrefix(), e);
        }
            
        final Conditions conditions = assertion.getConditions();
        if (conditions != null) {
            for (final Condition c : conditions.getConditions()) {
                if (!(c instanceof DelegationRestrictionType)) {
                    continue;
                }
                for (final Delegate d : ((DelegationRestrictionType) c).getDelegates()) {
                    final EncryptedID encID = d.getEncryptedID();
                    if (encID != null) {
                        log.debug("{} Decrypting EncryptedID in Delegate", getLogPrefix());
                        try {
                            final NameID decrypted = processEncryptedID(profileRequestContext, encID);
                            if (decrypted != null) {
                                d.setNameID(decrypted);
                                d.setEncryptedID(null);
                            }
                        } catch (final DecryptionException e) {
                            if (isErrorFatal()) {
                                throw e;
                            }
                            log.warn("{} Trapped failure decrypting EncryptedID in Delegate", getLogPrefix(), e);
                        }
                    }
                }
            }
        }
    }
// Checkstyle: CyclomaticComplexity OFF
    
}