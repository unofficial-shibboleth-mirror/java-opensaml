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
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.slf4j.Logger;

import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Action to decrypt an {@link EncryptedAssertion} element and replace it with the decrypted
 * {@link Assertion} in situ.
 * 
 * <p>All of the built-in SAML message types that may include an {@link EncryptedAssertion} are
 * potentially handled, but the actual message to handle is obtained via strategy function, by
 * default the inbound message.</p> 
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 * @event {@link SAMLEventIds#DECRYPT_ASSERTION_FAILED}
 */
public class DecryptAssertions extends AbstractDecryptAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(DecryptAssertions.class);
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        final SAMLObject message = getSAMLObject();
        
        try {
            if (message instanceof Response) {
                processResponse(profileRequestContext, (Response) message);
            } else {
                log.debug("{} Message was of unrecognized type {}, nothing to do", getLogPrefix(),
                        message.getClass().getName());
                return;
            }
        } catch (final DecryptionException e) {
            log.warn("{} Failure performing decryption", getLogPrefix(), e);
            if (isErrorFatal()) {
                ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.DECRYPT_ASSERTION_FAILED);
            }
        }
    }
    
    /**
     * Decrypt an {@link EncryptedAssertion} and return the result.
     * 
     * @param profileRequestContext current profile request context
     * @param encAssert the encrypted object
     * 
     * @return the decrypted assertion, or null if the object did not need decryption
     * @throws DecryptionException if an error occurs during decryption
     */
    @Nullable private Assertion processEncryptedAssertion(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final EncryptedAssertion encAssert) throws DecryptionException {
        
        if (!getDecryptionPredicate().test(new Pair<>(profileRequestContext, encAssert))) {
            return null;
        }
        
        final Decrypter decrypter = getDecrypter();
        if (decrypter == null) {
            throw new DecryptionException("No decryption parameters, unable to decrypt EncryptedAssertion");
        }
        
        return decrypter.decrypt(encAssert);
    }

    /**
     * Decrypt any {@link EncryptedAssertion} found in a response and replace it with the result.
     * 
     * @param profileRequestContext current profile request context
     * @param response   response to operate on
     * 
     * @throws DecryptionException if an error occurs
     */
    private void processResponse(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final Response response) throws DecryptionException {

        final Collection<Assertion> decrypteds = new ArrayList<>();
        final Collection<EncryptedAssertion> encrypteds = new ArrayList<>();
        
        final Iterator<EncryptedAssertion> i = response.getEncryptedAssertions().iterator();
        while (i.hasNext()) {
            log.debug("{} Decrypting EncryptedAssertion in Response", getLogPrefix());
            try {
                final EncryptedAssertion encrypted = i.next();
                assert encrypted != null;
                final Assertion decrypted = processEncryptedAssertion(profileRequestContext, encrypted);
                if (decrypted != null) {
                    encrypteds.add(encrypted);
                    decrypteds.add(decrypted);
                }
            } catch (final DecryptionException e) {
                if (isErrorFatal()) {
                    throw e;
                }
                log.warn("{} Trapped failure decrypting EncryptedAssertion in Response", getLogPrefix(), e);
            }
        }
        
        response.getEncryptedAssertions().removeAll(encrypteds);
        response.getAssertions().addAll(decrypteds); 

        // Re-marshall the response so that any ID attributes within the decrypted Assertions
        // will have their ID-ness re-established at the DOM level.
        if (!decrypteds.isEmpty()) {
            try {
                XMLObjectSupport.marshall(response);
            } catch (final MarshallingException e) {
                log.warn("Error re-marshalling Response after Assertion decryption", e);
                throw new DecryptionException(e);
            }
        }
    }
    
}