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
import org.opensaml.xmlsec.algorithm.MACAlgorithm;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

/**
 * Algorithm descriptor for MAC algorithm: HMAC SHA-384.
 */
public final class HMACSHA384 implements MACAlgorithm {

    /** {@inheritDoc} */
    @Nonnull public String getURI() {
        return SignatureConstants.ALGO_ID_MAC_HMAC_SHA384;
    }

    /** {@inheritDoc} */
    @Nonnull public AlgorithmType getType() {
        return AlgorithmType.Mac;
    }

    /** {@inheritDoc} */
    @Nonnull public String getJCAAlgorithmID() {
        return JCAConstants.HMAC_SHA384;
    }

    /** {@inheritDoc} */
    @Nonnull public String getDigest() {
        return JCAConstants.DIGEST_SHA384;
    }

}
