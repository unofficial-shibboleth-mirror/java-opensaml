/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;

import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;

/**
 * A basic implementation of {@link X509Credential}.
 */
public class BasicX509Credential extends BasicCredential implements X509Credential {

    /** Entity certificate. */
    @Nonnull private X509Certificate entityCert;

    /** Entity certificate chain, must include entity certificate. */
    @Nullable @NonnullElements private Collection<X509Certificate> entityCertChain;

    /** CRLs for this credential. */
    @Nullable @NonnullElements private Collection<X509CRL> crls;
    
    /**
     * Constructor.
     *
     * @param entityCertificate the credential entity certificate
     */
    public BasicX509Credential(
            @Nonnull @ParameterName(name="entityCertificate") final X509Certificate entityCertificate) {
        entityCert = Constraint.isNotNull(entityCertificate, "Credential certificate cannot be null");
    }
    
    /**
     * Constructor.
     *
     * @param entityCertificate the credential entity certificate
     * @param privateKey the credential private key
     */
    public BasicX509Credential(
            @Nonnull @ParameterName(name="entityCertificate") final X509Certificate entityCertificate,
            @ParameterName(name="privateKey") @Nonnull final PrivateKey privateKey) {
        entityCert = Constraint.isNotNull(entityCertificate, "Credential certificate cannot be null");
        setPrivateKey(privateKey);
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public Class<? extends Credential> getCredentialType() {
        return X509Credential.class;
    }

    /** {@inheritDoc} */
    @Nullable @NonnullElements @Unmodifiable @NotLive public Collection<X509CRL> getCRLs() {
        return crls;
    }

    /**
     * Sets the CRLs for this credential.
     * 
     * @param newCRLs CRLs for this credential
     */
    public void setCRLs(@Nullable @NonnullElements final Collection<X509CRL> newCRLs) {
        if (newCRLs != null) {
            crls = CollectionSupport.copyToList(newCRLs);
        } else {
            crls = CollectionSupport.emptyList();
        }
    }

    /** {@inheritDoc} */
    @Nonnull public X509Certificate getEntityCertificate() {
        return entityCert;
    }

    /**
     * Sets the entity certificate for this credential.
     * 
     * @param newEntityCertificate entity certificate for this credential
     */
    public void setEntityCertificate(@Nonnull final X509Certificate newEntityCertificate) {
        Constraint.isNotNull(newEntityCertificate, "Credential certificate cannot be null");
        entityCert = newEntityCertificate;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable public PublicKey getPublicKey() {
        return getEntityCertificate().getPublicKey();
    }
    
    /**
     * This operation is unsupported for X.509 credentials. The public key will be retrieved
     * automatically from the entity certificate.
     * 
     * @param newPublicKey not supported
     */
    @Override
    public void setPublicKey(@Nullable final PublicKey newPublicKey) {
        throw new UnsupportedOperationException("Public key may not be set explicitly on an X509 credential");
    }

    /** {@inheritDoc} */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public Collection<X509Certificate> getEntityCertificateChain() {
        synchronized(this) {
            if (entityCertChain == null) {
                return CollectionSupport.singletonList(entityCert);
            }
            assert entityCertChain != null;
            return entityCertChain;
        }
    }

    /**
     * Sets the entity certificate chain for this credential. This <strong>MUST</strong> include the entity
     * certificate.
     * 
     * @param newCertificateChain entity certificate chain for this credential
     */
    public void setEntityCertificateChain(
            @Nonnull @NotEmpty @NonnullElements final Collection<X509Certificate> newCertificateChain) {
        Constraint.isNotNull(newCertificateChain, "Certificate chain collection cannot be null");
        Constraint.isNotEmpty(newCertificateChain, "Certificate chain collection cannot be empty");
        
        synchronized(this) {
            entityCertChain = CollectionSupport.copyToList(newCertificateChain);
        }
    }
    
    /**
     *  This operation is unsupported for X.509 credentials.
     *  
     *  @return null
     */
    @Override
    @Nullable public SecretKey getSecretKey() {
        return null;
    }
    
    /**
     *  This operation is unsupported for X.509 credentials.
     *  
     *  @param newSecretKey unsupported
     */
    @Override
    public void setSecretKey(@Nullable final SecretKey newSecretKey) {
        throw new UnsupportedOperationException("An X509Credential may not contain a secret key");
    }

}
