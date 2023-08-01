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

package org.opensaml.xmlsec.agreement;

import javax.annotation.Nonnull;

import org.opensaml.security.credential.Credential;

/**
 * An entity credential which represents the result of a key agreement operation.
 * 
 * <p>
 * This will typically contain a secret key only, and no public or private key.  The information available
 * via this type's interface describes how the secret key was produced.
 * </p>
 */
public interface KeyAgreementCredential extends Credential {
    
    /**
     * The key agreement algorithm URI used.
     * 
     * @return the algorithm
     */
    @Nonnull String getAlgorithm();
    
    /**
     * The credential holding the originator key material.
     * 
     * @return the originator credential
     */
    @Nonnull Credential getOriginatorCredential();
    
    /**
     * The credential holding the recipient key material.
     * 
     * @return the recipient credential
     */
    @Nonnull Credential getRecipientCredential();
    
    /**
     * The parameters to the key agreement operation.
     * 
     * @return the parameters
     */
    @Nonnull KeyAgreementParameters getParameters();
    
}