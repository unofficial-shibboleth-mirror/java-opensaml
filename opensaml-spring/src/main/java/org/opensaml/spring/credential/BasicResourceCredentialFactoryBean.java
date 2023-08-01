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

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;

import org.cryptacular.util.KeyPairUtil;
import org.opensaml.security.crypto.KeySupport;
import org.slf4j.Logger;

import org.springframework.beans.factory.BeanCreationException;

import com.google.common.io.ByteStreams;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resource.Resource;

/**
 * Spring bean factory for producing a {@link org.opensaml.security.credential.BasicCredential} from {@link Resource}s.
 */
public class BasicResourceCredentialFactoryBean extends AbstractBasicCredentialFactoryBean {

    /** log. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(BasicResourceCredentialFactoryBean.class);

    /** Configured public key Info. */
    @Nullable private Resource publicKeyInfo;

    /** Configured private key Info. */
    @Nullable private Resource privateKeyInfo;

    /** Configured secret key Info. */
    @Nullable private Resource secretKeyInfo;

    /**
     * Get the information used to generate the public key.
     * 
     * @return Returns the info.
     */
    @Nullable public Resource getPublicKeyInfo() {
        return publicKeyInfo;
    }

    /**
     * Set the information used to generate the public key.
     * 
     * @param info The info to set.
     */
    public void setPublicKeyInfo(@Nullable final Resource info) {
        publicKeyInfo = info;
    }

    /**
     * Get the information used to generate the private key.
     * 
     * @return Returns the info.
     */
    @Nullable public Resource getPrivateKeyInfo() {
        return privateKeyInfo;
    }

    /**
     * Set the information used to generate the private key.
     * 
     * @param info The info to set.
     */
    public void setPrivateKeyInfo(@Nullable final Resource info) {
        privateKeyInfo = info;
    }

    /**
     * Get the information used to generate the secret key.
     * 
     * @return Returns the info.
     */
    @Nullable public Resource getSecretKeyInfo() {
        return secretKeyInfo;
    }

    /**
     * Set the information used to generate the secret key.
     * 
     * @param info The info to set.
     */
    public void setSecretKeyInfo(@Nullable final Resource info) {
        secretKeyInfo = info;
    }

    /** {@inheritDoc} */
    @Override @Nullable protected PublicKey getPublicKey() {
        final Resource pkinfo = getPublicKeyInfo();
        if (null == pkinfo) {
            return null;
        }
        try (final InputStream is = pkinfo.getInputStream()) {
            return KeyPairUtil.readPublicKey(is);
        } catch (final IOException e) {
            log.error("{}: Could not decode public key: {}", getConfigDescription(), e.getMessage());
            throw new BeanCreationException("Could not decode public key", e);
        }
    }

    /** {@inheritDoc} */
    @Override @Nullable protected PrivateKey getPrivateKey() {
        final Resource pkinfo = getPrivateKeyInfo();
        if (null == pkinfo) {
            return null;
        }
        try (final InputStream is = pkinfo.getInputStream()) {
            return KeySupport.decodePrivateKey(is, getPrivateKeyPassword());
        } catch (final KeyException | IOException e) {
            log.error("{}: Could not decode private key: {}", getConfigDescription(), e.getMessage());
            throw new BeanCreationException("Could not decode private key", e);
        }
    }

    /** {@inheritDoc} */
    @Override @Nullable protected SecretKey getSecretKey() {
        final Resource skinfo = getSecretKeyInfo(); 
        if (null == skinfo) {
            return null;
        }
        
        try (final InputStream is = skinfo.getInputStream()) {
            final String alg = getSecretKeyAlgorithm();
            if (alg == null) {
                throw new KeyException("Key algorithm was null");
            }
            return KeySupport.decodeSecretKey(decodeSecretKey(ByteStreams.toByteArray(is)), alg);
        } catch (final KeyException | IOException e) {
            log.error("{}: Could not decode secret key: {}", getConfigDescription(), e.getMessage());
            throw new BeanCreationException("Could not decode secret key", e);
        }
    }
}
