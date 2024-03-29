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

package org.opensaml.saml.saml1.binding.encoding.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.common.binding.encoding.SAMLMessageEncoder;
import org.opensaml.saml.common.xml.SAMLConstants;

/**
 * SAML 1.X HTTP SOAP 1.1 binding message encoder for HttpClient HttpRequest.
 */
public class HttpClientRequestSOAP11Encoder
        extends org.opensaml.soap.client.soap11.encoder.http.impl.HttpClientRequestSOAP11Encoder
        implements SAMLMessageEncoder {
    
    /** Constructor. */
    public HttpClientRequestSOAP11Encoder() {
        super();
        setProtocolMessageLoggerSubCategory("SAML");
    }

    /** {@inheritDoc} */
    @Override
    @Nullable protected String getSOAPAction() {
        return "http://www.oasis-open.org/committees/security";
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public String getBindingURI() {
        return SAMLConstants.SAML1_SOAP11_BINDING_URI;
    }

}