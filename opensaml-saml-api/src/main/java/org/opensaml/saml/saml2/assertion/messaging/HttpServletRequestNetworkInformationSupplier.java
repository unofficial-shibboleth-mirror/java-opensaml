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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.SecurityException;
import org.opensaml.security.messaging.ServletRequestX509CredentialAdapter;
import org.opensaml.security.x509.X509Credential;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.servlet.HttpServletSupport;

/**
 * Implementation of {@link AssertionValidationNetworkInformationSupplier} which wraps an 
 * instance of {@link HttpServletRequest}.
 */
public class HttpServletRequestNetworkInformationSupplier implements AssertionValidationNetworkInformationSupplier {
    
    /** The wrapped servlet request. */
    @Nonnull private HttpServletRequest servletRequest;

    /**
     * Constructor.
     *
     * @param request the servlet request instanc3
     */
    public HttpServletRequestNetworkInformationSupplier(@Nonnull final HttpServletRequest request) {
        servletRequest = Constraint.isNotNull(request, "HttpServletRequest was null");
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public X509Certificate getAttesterCertificate() {
        try {
            final X509Credential credential = new ServletRequestX509CredentialAdapter(servletRequest);
            return credential.getEntityCertificate();
        } catch (final SecurityException e) {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public String getAttesterIPAddress() {
        return HttpServletSupport.getRemoteAddr(servletRequest);
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public String getReceiverEndpointURI() {
        return servletRequest.getRequestURL().toString();
    }

}
