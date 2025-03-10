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
 * Basic property-based implementation of {@link AssertionValidationNetworkInformationSupplier}.
 */
public class BasicNetworkInformationSupplier implements AssertionValidationNetworkInformationSupplier {
    
    /** The attesting party's certificate. */
    @Nullable X509Certificate attesterCertificate;
    
    /** The attesting party's IP address. */
    @Nullable String attesterIPAddress;
    
    /** The endpoint URI at which the assertion for validation was received. */
    @Nullable String receiverEndpointURI;

    /** {@inheritDoc} */
    @Override
    @Nullable
    public X509Certificate getAttesterCertificate() {
        return attesterCertificate;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public String getAttesterIPAddress() {
        return attesterIPAddress;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public String getReceiverEndpointURI() {
        return receiverEndpointURI;
    }

    /**
     * Set the attesting party's certificate.
     * 
     * @param certificate the attesting party's certificate
     */
    public void setAttesterCertificate(@Nullable final X509Certificate certificate) {
        attesterCertificate = certificate;
    }

    /**
     * Set the attesting party's IP address.
     * 
     * @param address the attesting party's IP address
     */
    public void setAttesterIPAddress(@Nullable final String address) {
        attesterIPAddress = address;
    }

    /**
     * Set the endpoint URI at which the assertion being validated was received.
     * 
     * @param endpointURI the endpoint URI
     */
    public void setReceiverEndpointURI(@Nullable final String endpointURI) {
        receiverEndpointURI = endpointURI;
    }

}
