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

package org.opensaml.xmlsec.config;

import javax.annotation.Nullable;

import org.opensaml.security.config.SecurityConfiguration;
import org.opensaml.xmlsec.DecryptionConfiguration;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.SignatureValidationConfiguration;

/**
 * Extends base interface with XML Security configuration objects.
 * 
 * @since 5.0.0
 */
public interface XMLSecurityConfiguration extends SecurityConfiguration {

    /**
     * Get the configuration used when validating protocol message signatures.
     * 
     * @return configuration used when validating protocol message signatures, or null
     */
    @Nullable SignatureValidationConfiguration getSignatureValidationConfiguration();

    /**
     * Get the configuration used when generating protocol message signatures.
     * 
     * @return configuration used when generating protocol message signatures, or null
     */
    @Nullable SignatureSigningConfiguration getSignatureSigningConfiguration();

    /**
     * Get the configuration used when decrypting protocol message information.
     * 
     * @return configuration used when decrypting protocol message information, or null
     */
    @Nullable DecryptionConfiguration getDecryptionConfiguration();

    /**
     * Get the configuration used when encrypting protocol message information.
     * 
     * @return configuration used when encrypting protocol message information, or null
     */
    @Nullable EncryptionConfiguration getEncryptionConfiguration();

}