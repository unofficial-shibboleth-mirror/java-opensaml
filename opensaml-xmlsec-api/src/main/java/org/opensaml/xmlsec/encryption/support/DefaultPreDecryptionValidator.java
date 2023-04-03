/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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

import javax.annotation.Nonnull;

import org.opensaml.xmlsec.encryption.CipherData;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.EncryptedType;

/**
 * Default implementation of {@link PreDecryptionValidator}.
 */
public class DefaultPreDecryptionValidator implements PreDecryptionValidator {

    /** {@inheritDoc} */
    @Override
    public void validate(@Nonnull final EncryptedData encryptedData) throws PreDecryptionValidationException {
        performCommonValidation(encryptedData);
    }

    /** {@inheritDoc} */
    @Override
    public void validate(@Nonnull final EncryptedKey encryptedKey) throws PreDecryptionValidationException {
        performCommonValidation(encryptedKey);
    }

    /**
     * Perform validation common to both {@link EncryptedData} and {@link EncryptedKey}.
     * 
     * @param encryptedType the target to validate
     * 
     * @throws PreDecryptionValidationException if the target fails validation
     */
    protected void performCommonValidation(@Nonnull final EncryptedType encryptedType)
            throws PreDecryptionValidationException {
        
        final CipherData data = encryptedType.getCipherData();
        if (data == null) {
            throw new PreDecryptionValidationException(
                    String.format("%s contains no CipherData child element, which is mandatory",
                            encryptedType.getClass().getSimpleName()));
        }

        if (data.getCipherReference() != null) {
            throw new PreDecryptionValidationException(
                    String.format("%s contains a CipherReference, which is not allowed",
                            encryptedType.getClass().getSimpleName()));
        }
    }

}