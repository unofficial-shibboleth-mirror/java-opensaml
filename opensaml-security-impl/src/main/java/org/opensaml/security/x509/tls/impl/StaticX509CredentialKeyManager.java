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

package org.opensaml.security.x509.tls.impl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.net.ssl.X509KeyManager;

import org.opensaml.security.x509.X509Credential;
import org.slf4j.Logger;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * An implementation of {@link X509KeyManager} based on a single statically configured
 * private key and certificate chain, supplied either directly or via an instance of 
 * {@link X509Credential}.
 */
public class StaticX509CredentialKeyManager implements X509KeyManager {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(StaticX509CredentialKeyManager.class);
    
    /** The private key instance. */
    @Nonnull private PrivateKey privateKey;
    
    /** The certificate chain instance. */
    @Nonnull private java.security.cert.X509Certificate[] certificateChain;
    
    /** The alias representing the supplied static credential. */
    @Nonnull private String internalAlias = "internalAlias-" + this.toString();

    /**
     * Constructor.
     *
     * @param credential the static credential managed by this key manager
     */
    public StaticX509CredentialKeyManager(final X509Credential credential) {
        Constraint.isNotNull(credential, "Credential may not be null");
        privateKey = Constraint.isNotNull(credential.getPrivateKey(), "Credential PrivateKey may not be null");
        certificateChain = Constraint.isNotNull(credential.getEntityCertificateChain(), 
                "Credential certificate chain may not be null").toArray(new X509Certificate[0]);
        log.trace("Generated static internal alias was: {}", internalAlias);
    }

    /**
     * Constructor.
     *
     * @param key the private key managed by this key manager
     * @param chain the certificate chain managed by this key manager
     */
    public StaticX509CredentialKeyManager(final PrivateKey key, final Collection<X509Certificate> chain) {
        privateKey = Constraint.isNotNull(key, "PrivateKey may not be null");
        certificateChain = Constraint.isNotNull(chain, 
                "Certificate chain may not be null").toArray(new X509Certificate[0]);
        log.trace("Generated static internal alias was: {}", internalAlias);
    }

    /** {@inheritDoc} */
    public String chooseClientAlias(final String[] arg0, final Principal[] arg1, final Socket arg2) {
        log.trace("In chooseClientAlias");
        return internalAlias;
    }

    /** {@inheritDoc} */
    public String[] getClientAliases(final String arg0, final Principal[] arg1) {
        log.trace("In getClientAliases");
        return new String[] {internalAlias};
    }

    /** {@inheritDoc} */
    public java.security.cert.X509Certificate[] getCertificateChain( final String arg0) {
        log.trace("In getCertificateChain");
        return internalAlias.equals(arg0) ? certificateChain : null;
    }

    /** {@inheritDoc} */
    public PrivateKey getPrivateKey(final String arg0) {
        log.trace("In getPrivateKey");
        return internalAlias.equals(arg0) ? privateKey : null;
    }
    
    /** {@inheritDoc} */
    public String chooseServerAlias(final String arg0, final Principal[] arg1, final Socket arg2) {
        log.trace("In chooseServerAlias");
        return internalAlias;
    }
    
    /** {@inheritDoc} */
    public String[] getServerAliases(final String arg0, final Principal[] arg1) {
        log.trace("In getServerAliases");
        return new String[] {internalAlias};
    }
    
}