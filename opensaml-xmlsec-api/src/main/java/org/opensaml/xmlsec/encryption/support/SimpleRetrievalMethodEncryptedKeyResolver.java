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

package org.opensaml.xmlsec.encryption.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.RetrievalMethod;
import org.slf4j.Logger;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Implementation of {@link EncryptedKeyResolver} which finds {@link EncryptedKey} elements by dereferencing
 * {@link RetrievalMethod} children of the {@link org.opensaml.xmlsec.signature.KeyInfo} of the {@link EncryptedData}
 * context.
 * 
 * The RetrievalMethod must have a <code>Type</code> attribute with the value of
 * {@link EncryptionConstants#TYPE_ENCRYPTED_KEY}. The <code>URI</code> attribute value must be a same-document
 * fragment identifier (via ID attribute). Processing of transforms children of RetrievalMethod is not supported by this
 * implementation.
 */
public class SimpleRetrievalMethodEncryptedKeyResolver extends AbstractEncryptedKeyResolver {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SimpleRetrievalMethodEncryptedKeyResolver.class);
    
    /** Constructor. */
    public SimpleRetrievalMethodEncryptedKeyResolver() {
        
    }

    /** 
     * Constructor. 
     * 
     * @param recipients the set of recipients
     */
    @Deprecated
    public SimpleRetrievalMethodEncryptedKeyResolver(@Nullable final Set<String> recipients) {
        super(recipients);
    }

    /** 
     * Constructor. 
     * 
     * @param recipient the recipient
     */
    @Deprecated
    public SimpleRetrievalMethodEncryptedKeyResolver(@Nullable final String recipient) {
        this(recipient != null ? CollectionSupport.singleton(recipient) : null);
    }

    /** {@inheritDoc} */
    @Nonnull public Iterable<EncryptedKey> resolve(@Nonnull final EncryptedData encryptedData,
            @Nullable final Set<String> recipients) {
        Constraint.isNotNull(encryptedData, "EncryptedData cannot be null");
        
        final List<EncryptedKey> resolvedEncKeys = new ArrayList<>();

        final KeyInfo keyInfo = encryptedData.getKeyInfo();
        if (keyInfo == null) {
            return CollectionSupport.emptyList();
        }

        for (final RetrievalMethod rm : keyInfo.getRetrievalMethods()) {
            if (!Objects.equals(rm.getType(), EncryptionConstants.TYPE_ENCRYPTED_KEY)) {
                continue;
            } else if (rm.getTransforms() != null) {
                log.warn("EncryptedKey RetrievalMethod has transforms, cannot process");
                continue;
            }
            
            final Set<String> validRecipients = getEffectiveRecipients(recipients);

            final EncryptedKey encKey = dereferenceURI(rm);
            if (encKey == null) {
                continue;
            } else if (matchRecipient(encKey.getRecipient(), validRecipients)) {
                resolvedEncKeys.add(encKey);
            }
        }

        return resolvedEncKeys;
    }

    /**
     * Dereference the URI attribute of the specified retrieval method into an EncryptedKey.
     * 
     * @param rm the RetrievalMethod to process
     * @return the dereferenced EncryptedKey
     */
    @Nullable protected EncryptedKey dereferenceURI(@Nonnull final RetrievalMethod rm) {
        final String uri = rm.getURI();
        if (uri == null || !uri.startsWith("#")) {
            log.warn("EncryptedKey RetrievalMethod did not contain a same-document URI reference, cannot process");
            return null;
        }
        
        final XMLObject target = rm.resolveIDFromRoot(uri.substring(1));
        if (target == null) {
            log.warn("EncryptedKey RetrievalMethod URI could not be dereferenced");
            return null;
        } else if (!(target instanceof EncryptedKey)) {
            log.warn("The product of dereferencing the EncryptedKey RetrievalMethod was not an EncryptedKey");
            return null;
        }
        return (EncryptedKey) target;
    }

}