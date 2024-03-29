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

package org.opensaml.security.x509.impl;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.SecurityException;
import org.opensaml.security.x509.X509Credential;

/**
 * Interface for classes which evaluate an {@link X509Credential} against a 
 * supplied set of trusted names.
 */
public interface X509CredentialNameEvaluator {

    /**
     * Evaluate the specified credential against the specified set of trusted names.
     * 
     * <p>The types of names supported, and the manner in which they are evaluated, is
     * implementation-specific.</p>
     * 
     * @param credential the X.509 credential to evaluate
     * @param trustedNames trusted names against which the credential will be evaluated
     * 
     * @return true if the name evaluation succeeds, false otherwise
     * 
     * @throws SecurityException thrown if there is an error during name evaluation
     */
    boolean evaluate(@Nonnull final X509Credential credential, @Nullable final Set<String> trustedNames)
            throws SecurityException;
    
}