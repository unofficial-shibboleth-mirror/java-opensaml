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
import org.opensaml.xmlsec.algorithm.BlockEncryptionAlgorithm;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Algorithm descriptor for block encryption algorithm: DESede-CBC.
 */
public final class BlockEncryptionDESede implements BlockEncryptionAlgorithm {

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getKey() {
        return JCAConstants.KEY_ALGO_DESEDE;
    }

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getURI() {
        return EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES;
    }

    /** {@inheritDoc} */
    @Nonnull public AlgorithmType getType() {
        return AlgorithmType.BlockEncryption;
    }

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getJCAAlgorithmID() {
        return String.format("%s/%s/%s", getKey(), getCipherMode(), getPadding());
    }

    /** {@inheritDoc} */
    public int getKeyLength() {
        return 192;
    }

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getCipherMode() {
        return JCAConstants.CIPHER_MODE_CBC;
    }

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getPadding() {
        return JCAConstants.CIPHER_PADDING_ISO10126;
    }

}
