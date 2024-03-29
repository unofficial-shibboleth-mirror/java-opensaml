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

package org.opensaml.xmlsec.keyinfo;

import javax.annotation.Nullable;

import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.KeyInfo;

/**
 * Interface for implementations which generate a {@link KeyInfo} based on keying material and other
 * information found within a {@link Credential}.
 */
public interface KeyInfoGenerator {
    
    /**
     * Generate a new KeyInfo object based on keying material and other information within a credential. 
     * 
     * @param credential the credential containing keying material and possibly other information
     * @return a new KeyInfo object or null if nothing was generated
     * @throws SecurityException thrown if there is any error generating the new KeyInfo from the credential
     */
    @Nullable KeyInfo generate(@Nullable final Credential credential) throws SecurityException;

}