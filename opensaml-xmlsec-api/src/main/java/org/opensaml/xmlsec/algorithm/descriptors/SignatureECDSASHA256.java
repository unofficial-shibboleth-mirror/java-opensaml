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
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

/**
 * Algorithm descriptor for signature algorithm: ECDSA SHA-256.
 */
public final class SignatureECDSASHA256 implements SignatureAlgorithm {

    /** {@inheritDoc} */
    @Nonnull public String getKey() {
        return JCAConstants.KEY_ALGO_EC;
    }

    /** {@inheritDoc} */
    @Nonnull public String getURI() {
        return SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256;
    }

    /** {@inheritDoc} */
    @Nonnull public AlgorithmType getType() {
        return AlgorithmType.Signature;
    }

    /** {@inheritDoc} */
    @Nonnull public String getJCAAlgorithmID() {
        return JCAConstants.SIGNATURE_ECDSA_SHA256;
    }

    /** {@inheritDoc} */
    @Nonnull public String getDigest() {
        return JCAConstants.DIGEST_SHA256;
    }

}
