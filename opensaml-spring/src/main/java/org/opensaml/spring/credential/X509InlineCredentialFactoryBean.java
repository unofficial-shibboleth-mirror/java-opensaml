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

package org.opensaml.spring.credential;

import java.security.PrivateKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.collection.LazyList;
import net.shibboleth.shared.primitive.LoggerFactory;

import org.cryptacular.util.KeyPairUtil;
import org.opensaml.security.x509.X509Support;
import org.slf4j.Logger;

import org.springframework.beans.FatalBeanException;

/**
 * A factory bean to understand X509Inline credentials.
 */
public class X509InlineCredentialFactoryBean extends AbstractX509CredentialFactoryBean {

    /** log. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(X509InlineCredentialFactoryBean.class);

    /** The entity certificate. */
    @Nullable private String entityCertificate;

    /** The certificates. */
    @Nullable private List<String> certificates;

    /** The private key. */
    @Nullable private byte[] privateKey;

    /** The crls. */
    @Nullable private List<String> crls;
    
    /**
     * Set the file with the entity certificate.
     * 
     * @param entityCert The file to set.
     */
    public void setEntity(@Nonnull final String entityCert) {
        entityCertificate = entityCert;
    }

    /**
     * Sets the certificates.
     * 
     * @param certs The value to set.
     */
    public void setCertificates(@Nullable @NotEmpty final List<String> certs) {
        certificates = certs;
    }

    /**
     * Set the private key.
     * 
     * @param key The file to set.
     */
    public void setPrivateKey(@Nullable final byte[] key) {
        privateKey = key;
    }

    /**
     * Sets the files which contain the crls.
     * 
     * @param list The value to set.
     */
    public void setCRLs(@Nullable @NotEmpty final List<String> list) {
        crls = list;
    }

    /** {@inheritDoc}. */
    @Override @Nullable protected X509Certificate getEntityCertificate() {

        if (null == entityCertificate) {
            return null;
        }
        try {
            assert entityCertificate != null;
            return X509Support.decodeCertificate(entityCertificate);
        } catch (final CertificateException e) {
            log.error("{}: Could not decode provided Entity Certificate: {}", getConfigDescription(), e.getMessage());
            throw new FatalBeanException("Could not decode provided Entity Certificate", e);
        }
    }

    /** {@inheritDoc} */
    @Override @Nonnull protected List<X509Certificate> getCertificates() {
        
        if (certificates == null) {
            return CollectionSupport.emptyList();
        }
        
        final List<X509Certificate> certs = new LazyList<>();
        assert certificates != null;
        for (final String cert : certificates) {
            try {
                certs.add(X509Support.decodeCertificate(cert.trim()));
            } catch (final CertificateException e) {
                log.error("{}: Could not decode provided Certificate: {}", getConfigDescription(), e.getMessage());
                throw new FatalBeanException("Could not decode provided Certificate", e);
            }
        }
        return certs;
    }

    /** {@inheritDoc} */
    @Override @Nullable protected PrivateKey getPrivateKey() {
        if (null == privateKey) {
            return null;
        }
        return KeyPairUtil.decodePrivateKey(privateKey, getPrivateKeyPassword());
    }

    /** {@inheritDoc} */
    @Override @Nullable protected List<X509CRL> getCRLs() {
        if (null == crls) {
            return null;
        }
        final List<X509CRL> result = new LazyList<>();
        assert crls != null;
        for (final String crl : crls) {
            try {
                assert crl != null;
                result.add(X509Support.decodeCRL(crl));
            } catch (final CRLException | CertificateException e) {
                log.error("{}: Could not decode provided CRL: {}", getConfigDescription(), e.getMessage());
                throw new FatalBeanException("Could not decode provided CRL", e);
            }
        }
        return result;
    }
}
