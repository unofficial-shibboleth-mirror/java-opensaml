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

package org.opensaml.saml.saml2.assertion.messaging;

import java.security.cert.X509Certificate;

import javax.annotation.Nullable;

/**
 * Interface for a component that supplies network-related information for assertion validation.
 */
public interface AssertionValidationNetworkInformationSupplier {
    
    /**
     * The X.509 certificate presented by the attesting party, may be null.
     * 
     * @return the attesting party's certificate, or null
     */
    @Nullable X509Certificate getAttesterCertificate();
    
    /**
     * The attesting party's IP address, may be null.
     * 
     * <p>
     * For IPv6 addresses this should be in "sanitized" form, without enclosing square brackets.
     * </p>
     * 
     * @return the attesting party's IP address, or null if not known
     */
    @Nullable String getAttesterIPAddress();
    
    /**
     * The endpoint URI at which the assertion being validated was received, may be null.
     * 
     * @return the endpoint URI, or null if not applicable
     */
    @Nullable String getReceiverEndpointURI();

}
