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

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Algorithm descriptor for MAC algorithm: HMAC MD5.
 */
public final class HMACMD5 implements MACAlgorithm {

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getURI() {
        return SignatureConstants.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5;
    }

    /** {@inheritDoc} */
    @Nonnull public AlgorithmType getType() {
        return AlgorithmType.Mac;
    }

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getJCAAlgorithmID() {
        return JCAConstants.HMAC_MD5;
    }

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getDigest() {
        return JCAConstants.DIGEST_MD5;
    }

}
