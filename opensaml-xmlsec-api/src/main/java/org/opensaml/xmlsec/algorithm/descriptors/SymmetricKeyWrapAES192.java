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

package org.opensaml.xmlsec.algorithm.descriptors;

import javax.annotation.Nonnull;

import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.xmlsec.algorithm.SymmetricKeyWrapAlgorithm;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;

/**
 * Algorithm descriptor for symmetric key wrap algorithm: AES-192.
 */
public final class SymmetricKeyWrapAES192 implements SymmetricKeyWrapAlgorithm {

    /** {@inheritDoc} */
    @Nonnull public String getKey() {
        return JCAConstants.KEY_ALGO_AES;
    }

    /** {@inheritDoc} */
    @Nonnull public String getURI() {
        return EncryptionConstants.ALGO_ID_KEYWRAP_AES192;
    }

    /** {@inheritDoc} */
    @Nonnull public AlgorithmType getType() {
        return AlgorithmType.SymmetricKeyWrap;
    }

    /** {@inheritDoc} */
    @Nonnull public String getJCAAlgorithmID() {
        return JCAConstants.KEYWRAP_ALGO_AES;
    }

    /** {@inheritDoc} */
    public int getKeyLength() {
        return 192;
    }

}
