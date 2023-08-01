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

package org.opensaml.xmlsec.keyinfo.impl.provider;

import java.security.KeyException;
import java.security.PublicKey;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialContext;
import org.opensaml.security.criteria.KeyAlgorithmCriterion;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.keyinfo.impl.KeyInfoResolutionContext;
import org.opensaml.xmlsec.signature.ECKeyValue;
import org.opensaml.xmlsec.signature.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.LazySet;
import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * Implementation of {@link org.opensaml.xmlsec.keyinfo.impl.KeyInfoProvider} which supports {@link ECKeyValue}.
 */
public class ECKeyValueProvider extends AbstractKeyInfoProvider {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ECKeyValueProvider.class);

    /** {@inheritDoc} */
    public boolean handles(@Nonnull final XMLObject keyInfoChild) {
        return getECKeyValue(keyInfoChild) != null;
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public Collection<Credential> process(
            @Nonnull final KeyInfoCredentialResolver resolver, @Nonnull final XMLObject keyInfoChild,
            @Nullable final CriteriaSet criteriaSet, @Nonnull final KeyInfoResolutionContext kiContext)
                    throws SecurityException {

        final ECKeyValue keyValue = getECKeyValue(keyInfoChild);
        if (keyValue == null) {
            return null;
        }

        if (criteriaSet != null) {
            final KeyAlgorithmCriterion algorithmCriteria = criteriaSet.get(KeyAlgorithmCriterion.class);
            if (algorithmCriteria != null && algorithmCriteria.getKeyAlgorithm() != null
                    && !"EC".equals(algorithmCriteria.getKeyAlgorithm())) {
                log.debug("Criterion specified non-EC key algorithm, skipping");
                return null;
            }
        }

        log.debug("Attempting to extract credential from an RSAKeyValue");

        PublicKey pubKey = null;
        try {
            pubKey = KeyInfoSupport.getECKey(keyValue);
        } catch (final KeyException e) {
            log.error("Error extracting EC key value: {}", e.getMessage());
            throw new SecurityException("Error extracting EC key value", e);
        }
        final BasicCredential cred = new BasicCredential(pubKey);
        cred.getKeyNames().addAll(kiContext.getKeyNames());

        final CredentialContext credContext = buildCredentialContext(kiContext);
        if (credContext != null) {
            cred.getCredentialContextSet().add(credContext);
        }

        log.debug("Credential successfully extracted from ECKeyValue");
        final LazySet<Credential> credentialSet = new LazySet<>();
        credentialSet.add(cred);
        return credentialSet;
    }

    /**
     * Get the ECKeyValue from the passed XML object.
     * 
     * @param xmlObject an XML object, presumably either a {@link KeyValue} or an {@link ECKeyValue}
     * @return the ECKeyValue which was found, or null if none
     */
    @Nullable protected ECKeyValue getECKeyValue(@Nonnull final XMLObject xmlObject) {

        if (xmlObject instanceof ECKeyValue) {
            return (ECKeyValue) xmlObject;
        } else if (xmlObject instanceof KeyValue) {
            return ((KeyValue) xmlObject).getECKeyValue();
        } else {
            return null;
        }
    }
}
