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

package org.opensaml.security.x509.impl;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.X509KeyManager;

import org.opensaml.security.credential.AbstractCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.X509Credential;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;

/** A class that wraps a {@link X509KeyManager} and exposes it as an {@link X509Credential}. */
public class X509KeyManagerX509CredentialAdapter extends AbstractCredential implements X509Credential {

    /** Alias used to reference the credential in the key manager. */
    @Nonnull private final String credentialAlias;

    /** Wrapped key manager. */
    @Nonnull private final X509KeyManager keyManager;

    /**
     * Constructor.
     * 
     * @param manager wrapped key manager
     * @param alias alias used to reference the credential in the key manager
     */
    public X509KeyManagerX509CredentialAdapter(@Nonnull @ParameterName(name="manager") final X509KeyManager manager,
            @Nonnull @ParameterName(name="alias") final String alias) {
        keyManager = Constraint.isNotNull(manager, "Key manager cannot be null");
        credentialAlias = Constraint.isNotNull(StringSupport.trimOrNull(alias), "Entity alias cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public Collection<X509CRL> getCRLs() {
        return CollectionSupport.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public X509Certificate getEntityCertificate() {
        final X509Certificate[] certs = keyManager.getCertificateChain(credentialAlias);
        if (certs != null && certs.length > 0) {
            return certs[0];
        }

        throw new IllegalStateException("Error accessing certificate in key manager");
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public Collection<X509Certificate> getEntityCertificateChain() {
        final X509Certificate[] certs = keyManager.getCertificateChain(credentialAlias);
        if (certs != null && certs.length > 0) {
            return Arrays.asList(certs);
        }

        return CollectionSupport.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public PrivateKey getPrivateKey() {
        return keyManager.getPrivateKey(credentialAlias);
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public PublicKey getPublicKey() {
        return getEntityCertificate().getPublicKey();
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public Class<? extends Credential> getCredentialType() {
        return X509Credential.class;
    }

    /** {@inheritDoc} */
    @Override
    public void setEntityId(@Nullable final String newEntityID) {
        super.setEntityId(newEntityID);
    }

    /** {@inheritDoc} */
    @Override
    public void setUsageType(@Nonnull final UsageType newUsageType) {
        super.setUsageType(newUsageType);
    }
    
}