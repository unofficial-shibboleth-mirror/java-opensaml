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

import javax.annotation.Nonnull;
import javax.net.ssl.X509KeyManager;

import org.opensaml.security.x509.X509Credential;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * An implementation of {@link X509KeyManager} which returns data based on the thread-local credential 
 * instance obtained via {@link ThreadLocalX509CredentialContext}.
 */
public class ThreadLocalX509CredentialKeyManager implements X509KeyManager {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(ThreadLocalX509CredentialKeyManager.class);
    
    /** The alias representing the supplied static credential. */
    @Nonnull private String internalAlias = "internalAlias-ThreadLocal";

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
    public X509Certificate[] getCertificateChain(final String arg0) {
        log.trace("In getCertificateChain");
        
        final X509Credential cred = ThreadLocalX509CredentialContext.getCredential();
        
        return internalAlias.equals(arg0) &&
                cred != null ? cred.getEntityCertificateChain().toArray(new X509Certificate[0]) : null;
    }

    /** {@inheritDoc} */
    public PrivateKey getPrivateKey(final String arg0) {
        log.trace("In getPrivateKey");
        
        final X509Credential cred = ThreadLocalX509CredentialContext.getCredential();
        
        return internalAlias.equals(arg0) && cred != null ? cred.getPrivateKey() : null;
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