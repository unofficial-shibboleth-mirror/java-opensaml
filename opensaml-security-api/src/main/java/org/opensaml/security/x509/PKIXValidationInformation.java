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

package org.opensaml.security.x509;

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * Source of PKIX validation information such as trust anchors and CRLs.
 */
public interface PKIXValidationInformation {

    /**
     * Gets the maximum allowable trust chain verification depth.
     * 
     * @return maximum allowable trust chain verification depth
     */
    @Nullable Integer getVerificationDepth();

    /**
     * Gets the certificate trust anchors used during PKIX validation.
     * 
     * @return trust anchors used during PKIX validation
     */
    @Nullable @Unmodifiable @NotLive Collection<X509Certificate> getCertificates();

    /**
     * Gets the CRLs used during PKIX validation.
     * 
     * @return CRLs used during PKIX validation
     */
    @Nullable @Unmodifiable @NotLive Collection<X509CRL> getCRLs();

}