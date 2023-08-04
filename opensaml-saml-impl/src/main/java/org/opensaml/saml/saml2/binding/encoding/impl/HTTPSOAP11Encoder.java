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

package org.opensaml.saml.saml2.binding.encoding.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.common.binding.encoding.SAMLMessageEncoder;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 SOAP 1.1 over HTTP binding encoder.
 */
public class HTTPSOAP11Encoder extends org.opensaml.soap.soap11.encoder.http.impl.HTTPSOAP11Encoder
        implements SAMLMessageEncoder {

    /** Constructor. */
    public HTTPSOAP11Encoder() {
        super();
        setProtocolMessageLoggerSubCategory("SAML");
    }

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getBindingURI() {
        return SAMLConstants.SAML2_SOAP11_BINDING_URI;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable protected String getSOAPAction() {
        return "http://www.oasis-open.org/committees/security";
    }
    
}