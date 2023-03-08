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

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyException;
import java.security.PrivateKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.collection.LazyList;
import net.shibboleth.shared.primitive.LoggerFactory;

import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.x509.X509Support;
import org.slf4j.Logger;

import org.springframework.beans.FatalBeanException;
import org.springframework.core.io.Resource;

/**
 * Spring bean factory for producing a {@link org.opensaml.security.x509.BasicX509Credential} from {@link Resource}s.
 * 
 * <p>
 * This factory bean supports DER and PEM encoded certificate resources and encrypted and non-encrypted PKCS8, DER, or
 * PEM encoded private key resources.
 * </p>
 */
public class BasicX509CredentialFactoryBean extends AbstractX509CredentialFactoryBean {

    /** log. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(BasicX509CredentialFactoryBean.class);

    /** The specification of where the entity Resource is to be found. */
    @Nullable private Resource entityResource;

    /** Where the certificates are to be found. */
    @Nullable private List<Resource> certificateResources;

    /** Where the private key is to be found. */
    @Nullable private Resource privateKeyResource;

    /** Where the crls are to be found. */
    @Nullable private List<Resource> crlResources;

    /**
     * Set the Resource with the entity certificate.
     * 
     * @param what The Resource to set.
     */
    public void setEntity(@Nonnull final Resource what) {
        entityResource = what;
    }

    /**
     * Sets the Resources which contain the certificates.
     * 
     * @param what The values to set.
     */
    public void setCertificates(@Nullable @NotEmpty final List<Resource> what) {
        certificateResources = what;
    }

    /**
     * Set the Resource with the entity certificate.
     * 
     * @param what The resource to set.
     */
    public void setPrivateKey(@Nullable final Resource what) {
        privateKeyResource = what;
    }

    /**
     * Sets the Resources which contain the crls.
     * 
     * @param what The value to set.
     */
    public void setCRLs(@Nullable @NotEmpty final List<Resource> what) {
        crlResources = what;
    }
    
    //
    // BasicX509CredentialFactoryBean
    //
    /**
     * Set the resource containing the private key.
     * 
     * @param res private key resource, never <code>null</code>
     */
    public void setPrivateKeyResource(@Nonnull final Resource res) {
        setPrivateKey(res);
    }

    /**
     * Set the password for the private key.
     * 
     * @param password password for the private key, may be null if the key is not encrypted
     */
    public void setPrivateKeyPassword(@Nullable final String password) {
        if (password != null) {
            setPrivateKeyPassword(password.toCharArray());
        } else {
            setPrivateKeyPassword((char[]) null);
        }
    }

    /**
     * Set the certificate resource.
     * 
     * @param res certificate resource
     */
    public void setCertificateResource(@Nonnull final Resource res) {
        setCertificates(CollectionSupport.singletonList(res));
    }

    /**
     * Set the entityID for the credential.
     * 
     * @param id entityID
     */
    public void setEntityId(@Nullable final String id) {
        setEntityID(id);
    }

    /** {@inheritDoc} */
    @Override public boolean isSingleton() {
        return true;
    }

    /** {@inheritDoc}. */
    @Override @Nullable protected X509Certificate getEntityCertificate() {

        final Resource localEntityResource = entityResource;
        if (null == localEntityResource) {
            return null;
        }
        try {
            final Collection<X509Certificate> certs = X509Support.decodeCertificates(localEntityResource.getInputStream());
            if (certs.size() > 1) {
                log.error("{}: Configuration element indicated an entityCertificate,"
                        + " but multiple certificates were decoded", getConfigDescription());
                throw new FatalBeanException("Configuration element indicated an entityCertificate,"
                        + " but multiple certificates were decoded");
            }
            return certs.iterator().next();
        } catch (final CertificateException | IOException e) {
            log.error("{}: Could not decode provided Entity Certificate at {}: {}", getConfigDescription(),
                    localEntityResource.getDescription(), e.getMessage());
            throw new FatalBeanException("Could not decode provided Entity Certificate file "
                    + localEntityResource.getDescription(), e);
        }
    }

    /** {@inheritDoc} */
    @Override @Nonnull protected List<X509Certificate> getCertificates() {
        
        if (certificateResources == null) {
            return CollectionSupport.emptyList();
        }
        
        final List<X509Certificate> certificates = new LazyList<>();
        assert certificateResources != null;
        for (final Resource r : certificateResources) {
            try(InputStream is = r.getInputStream()) {
                certificates.addAll(X509Support.decodeCertificates(is));
            } catch (final CertificateException | IOException e) {
                log.error("{}: could not decode CertificateFile at {}: {}", getConfigDescription(),
                        r.getDescription(), e.getMessage());
                throw new FatalBeanException("Could not decode provided CertificateFile: " + r.getDescription(), e);
            }
        }
        return certificates;
    }

    /** {@inheritDoc} */
    @Override @Nullable protected PrivateKey getPrivateKey() {
        final Resource localResource = privateKeyResource; 
        if (null == localResource) {
            return null;
        }
        try (InputStream is = localResource.getInputStream()) {
            return KeySupport.decodePrivateKey(is, getPrivateKeyPassword());
        } catch (final KeyException | IOException e) {
            log.error("{}: Could not decode KeyFile at {}: {}", getConfigDescription(),
                    localResource.getDescription(), e.getMessage());
            throw new FatalBeanException("Could not decode provided KeyFile " + localResource.getDescription(), e);
        }
    }

    /** {@inheritDoc} */
    @Override @Nullable protected List<X509CRL> getCRLs() {
        if (null == crlResources) {
            return null;
        }
        final List<X509CRL> crls = new LazyList<>();
        assert crlResources != null;
        for (final Resource crl : crlResources) {
            try (InputStream is = crl.getInputStream()) {
                crls.addAll(X509Support.decodeCRLs(is));
            } catch (final CRLException | IOException e) {
                log.error("{}: Could not decode CRL file at {}: {}", getConfigDescription(), crl.getDescription(),
                        e.getMessage());
                throw new FatalBeanException("Could not decode provided CRL file " + crl.getDescription(), e);
            }
        }
        return crls;
    }
}
