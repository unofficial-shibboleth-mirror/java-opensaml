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

package org.opensaml.xmlsec.agreement;

import javax.annotation.Nonnull;

import org.opensaml.security.credential.Credential;

/**
 * Component which performs a key agreement operation.
 */
public interface KeyAgreementProcessor {
    
    /**
     * The key agreement algorithm URI.
     * 
     * @return the algorithm
     */
    @Nonnull public String getAlgorithm();
    
    /**
     * Perform the key agreement operation and return a new credential representing the results.
     * 
     * @param publicCredential the public credential, which will belong either to the recipient or originator party,
     *                         depending on whether encryption or decryption is being performed, respectively
     * @param keyAlgorithm the algorithm URI for which the derived key will be used
     * @param parameters parameters to the agreement operation. Internally a copy will be created so this input instance
     *                   will not be modified.
     * 
     * @return the agreement credential
     * 
     * @throws KeyAgreementException if the key agreement operation is not completed successfully
     */
    @Nonnull public KeyAgreementCredential execute(@Nonnull final Credential publicCredential,
            @Nonnull final String keyAlgorithm, @Nonnull final KeyAgreementParameters parameters)
                    throws KeyAgreementException;

}
