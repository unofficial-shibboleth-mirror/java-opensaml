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

package org.opensaml.security.x509;

import javax.annotation.Nonnull;

import org.opensaml.security.SecurityException;

/**
 * An interface for classes which evaluate an {@link X509Credential} against a set of trusted
 * {@link PKIXValidationInformation}, using PKIX validation rules.
 */
public interface PKIXTrustEvaluator {
    
    /**
     * Validate the specified credential against the specified set of trusted validation information.
     * 
     * @param validationInfo the set of trusted validation information
     * @param untrustedCredential the credential being evaluated
     * @return true if the credential can be successfully evaluated, false otherwise
     * @throws SecurityException thrown if there is an error evaluating the credential
     */
    boolean validate(@Nonnull final PKIXValidationInformation validationInfo,
            @Nonnull final X509Credential untrustedCredential) throws SecurityException;
    
    /**
     * Get the {@link PKIXValidationOptions} instance that is in use.
     * 
     * @return the PKIXValidationOptions instance
     */
    @Nonnull PKIXValidationOptions getPKIXValidationOptions();
    
}