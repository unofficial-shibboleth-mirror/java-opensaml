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

import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.EncryptedAttribute;
import org.opensaml.saml.saml2.core.EncryptedElementType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.slf4j.Logger;

import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Action to decrypt an {@link EncryptedAttribute} element and replace it with the decrypted
 * {@link Attribute} in situ.
 * 
 * <p>All of the built-in SAML message types that may include an {@link EncryptedAttribute} are
 * potentially handled, but the actual message to handle is obtained via strategy function, by
 * default the inbound message.</p> 
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 * @event {@link SAMLEventIds#DECRYPT_ATTRIBUTE_FAILED}
 */
public class DecryptAttributes extends AbstractDecryptAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(DecryptAttributes.class);
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        final SAMLObject message = getSAMLObject();
        
        try {
            if (message instanceof Response resp) {
                for (final Assertion a : resp.getAssertions()) {
                    assert a != null;
                    processAssertion(profileRequestContext, a);
                }
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
                ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.DECRYPT_ATTRIBUTE_FAILED);
            }
        }
    }
    
    /**
     * Decrypt an {@link EncryptedAttribute} and return the result.
     * 
     * @param profileRequestContext current profile request context
     * @param encAttr the encrypted object
     * 
     * @return the decrypted attribute, or null if the object did not need decryption
     * @throws DecryptionException if an error occurs during decryption
     */
    @Nullable private Attribute processEncryptedAttribute(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final EncryptedAttribute encAttr) throws DecryptionException {
        
        if (!getDecryptionPredicate().test(
                new Pair<ProfileRequestContext,EncryptedElementType>(profileRequestContext, encAttr))) {
            return null;
        }
        
        final Decrypter decrypter = getDecrypter();
        if (decrypter == null) {
            throw new DecryptionException("No decryption parameters, unable to decrypt EncryptedAttribute");
        }
        
        return decrypter.decrypt(encAttr);
    }

    /**
     * Decrypt any {@link EncryptedAttribute} found in an assertion and replace it with the result.
     * 
     * @param profileRequestContext current profile request context
     * @param assertion   assertion to operate on
     * 
     * @throws DecryptionException if an error occurs
     */
    private void processAssertion(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final Assertion assertion) throws DecryptionException {

        for (final AttributeStatement s : assertion.getAttributeStatements()) {

            final Collection<Attribute> decrypteds = new ArrayList<>();
            final Collection<EncryptedAttribute> encrypteds = new ArrayList<>();
            
            final Iterator<EncryptedAttribute> i = s.getEncryptedAttributes().iterator();
            while (i.hasNext()) {
                log.debug("{} Decrypting EncryptedAttribute in AttributeStatement", getLogPrefix());
                
                try {
                    final EncryptedAttribute encrypted = i.next();
                    assert encrypted != null;
                    final Attribute decrypted = processEncryptedAttribute(profileRequestContext, encrypted);
                    if (decrypted != null) {
                        encrypteds.add(encrypted);
                        decrypteds.add(decrypted);
                    }
                } catch (final DecryptionException e) {
                    if (isErrorFatal()) {
                        throw e;
                    }
                    log.warn("{} Trapped failure decrypting EncryptedAttribute in AttributeStatement", getLogPrefix(),
                            e);
                }
            }
            
            s.getEncryptedAttributes().removeAll(encrypteds);
            s.getAttributes().addAll(decrypteds); 
        }
    }
    
}