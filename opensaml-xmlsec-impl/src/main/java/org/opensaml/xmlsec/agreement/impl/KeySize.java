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

package org.opensaml.xmlsec.agreement.impl;

import org.opensaml.xmlsec.agreement.KeyAgreementParameter;

/**
 * Key agreement parameter used to explicitly represent the size of the derived key.
 */
public class KeySize implements KeyAgreementParameter {
    
    /** Key size. */
    private final int size;
    
    /**
     * Constructor.
     *
     * @param keySize the key size, in bits
     */
    public KeySize(final int keySize) {
        size = keySize;
    }
    
    /**
     * Get the key size, in bits.
     * 
     * @return the key size in bits
     */
    public int getSize() {
       return size; 
    }

}
