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

package org.opensaml.spring.credential;

import java.security.PrivateKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.credential.UsageType;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.x509.BasicX509Credential;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanCreationException;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A factory bean to collect information to do with an X509 backed {@link BasicX509Credential}.
 */
public abstract class AbstractX509CredentialFactoryBean extends AbstractCredentialFactoryBean<BasicX509Credential> {

    /** Log. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractX509CredentialFactoryBean.class);

    /** {@inheritDoc} */
    // Checkstyle: CyclomaticComplexity OFF
    @Override
    @Nonnull protected BasicX509Credential doCreateInstance() throws Exception {

        final List<X509Certificate> certificates = getCertificates();
        if (null == certificates || certificates.isEmpty()) {
            log.error("{}: No Certificates provided", getConfigDescription());
            throw new BeanCreationException("No Certificates provided");
        }

        X509Certificate entityCertificate = getEntityCertificate();
        if (null == entityCertificate) {
            entityCertificate = certificates.get(0);
        }
        
        assert entityCertificate != null;

        final PrivateKey privateKey = getPrivateKey();

        final BasicX509Credential credential;
        if (null == privateKey) {
            credential = new BasicX509Credential(entityCertificate);
        } else {
            credential = new BasicX509Credential(entityCertificate, privateKey);

            if (!KeySupport.matchKeyPair(entityCertificate.getPublicKey(), privateKey)) {
                log.error("{}: Public and private keys do not match", getConfigDescription());
                throw new BeanCreationException("Public and private keys do not match");
            }
        }

        credential.setEntityCertificateChain(certificates);

        final List<X509CRL> crls = getCRLs();
        if (null != crls && !crls.isEmpty()) {
            credential.setCRLs(crls);
        }

        if (null != getUsageType()) {
            credential.setUsageType(UsageType.valueOf(getUsageType()));
        }

        if (null != getEntityID()) {
            credential.setEntityId(getEntityID());
        }

        final List<String> keyNames = getKeyNames();
        if (null != keyNames) {
            credential.getKeyNames().addAll(keyNames);
        }

        return credential;
    }

    // Checkstyle: CyclomaticComplexity ON

    /** {@inheritDoc} */
    @Override public Class<BasicX509Credential> getObjectType() {
        return BasicX509Credential.class;
    }

    /**
     * return the explicitly configured entity certificate.
     * 
     * @return the certificate, or none if not configured.
     */
    @Nullable protected abstract X509Certificate getEntityCertificate();

    /**
     * Get the configured certificates. This <strong>MUST</strong> include the entity certificate if it was configured.
     * 
     * @return the certificates.
     */

    @Nonnull @NotEmpty protected abstract List<X509Certificate> getCertificates();

    /**
     * Get the configured private key.
     * 
     * @return the key or null if non configured
     */
    @Nullable protected abstract PrivateKey getPrivateKey();

    /**
     * Get the configured CRL list.
     * 
     * @return the crls or null
     */
    @Nullable protected abstract List<X509CRL> getCRLs();

}
