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
import org.opensaml.xmlsec.algorithm.KeyAgreementAlgorithm;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;

/**
 * Algorithm descriptor for key agreement: Elliptic Curve Diffie-Hellman Ephemeral-Static Mode.
 */
public final class KeyAgreementECDH implements KeyAgreementAlgorithm {

    /** {@inheritDoc} */
    @Nonnull public String getURI() {
        return EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES;
    }

    /** {@inheritDoc} */
    @Nonnull public AlgorithmType getType() {
        return AlgorithmType.KeyAgreement;
    }

    /** {@inheritDoc} */
    @Nonnull public String getJCAAlgorithmID() {
        return JCAConstants.KEY_AGREEMENT_ECDH;
    }

}
