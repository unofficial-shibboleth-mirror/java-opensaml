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

package org.opensaml.xmlsec;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.encryption.support.KeyAgreementEncryptionConfiguration;
import org.opensaml.xmlsec.encryption.support.RSAOAEPParameters;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * The configuration information to use when generating encrypted XML.
 */
public interface EncryptionConfiguration extends AlgorithmPolicyConfiguration {
    
    /**
     * Get the list of data encryption credentials to use, in preference order.
     * 
     * @return the list of encryption credentials, may be empty
     */
    @Nonnull @Unmodifiable @NotLive List<Credential> getDataEncryptionCredentials();
    
    /**
     * Get the list of preferred data encryption algorithm URIs, in preference order.
     * 
     * @return the list of algorithm URIs, may be empty
     */
    @Nonnull @Unmodifiable @NotLive List<String> getDataEncryptionAlgorithms();
    
    /**
     * Get the list of key transport encryption credentials to use, in preference order.
     * 
     * @return the list of encryption credentials, may be empty
     */
    @Nonnull @Unmodifiable @NotLive List<Credential> getKeyTransportEncryptionCredentials();
    
    /**
     * Get the list of preferred key transport encryption algorithm URIs, in preference order.
     * 
     * @return the list of algorithm URIs, may be empty
     */
    @Nonnull @Unmodifiable @NotLive List<String> getKeyTransportEncryptionAlgorithms();

    /**
     * Get the KeyInfoGenerator manager to use when generating the EncryptedData/KeyInfo.
     * 
     * @return the KeyInfoGenerator manager instance
     */
    @Nullable NamedKeyInfoGeneratorManager getDataKeyInfoGeneratorManager();
    
    /**
     * Get the KeyInfoGenerator manager to use when generating the EncryptedKey/KeyInfo.
     * 
     * @return the KeyInfoGenerator manager instance
     */
    @Nullable NamedKeyInfoGeneratorManager getKeyTransportKeyInfoGeneratorManager();
    
    /**
     * Get the instance of {@link RSAOAEPParameters}.
     * 
     * @return the parameters instance
     */
    @Nullable RSAOAEPParameters getRSAOAEPParameters();
    
    /**
     * Flag indicating whether to merge this configuration's {@link RSAOAEPParameters} values with those of 
     * a lower order of precedence, or to treat this configuration's parameters set as authoritative.
     * 
     * @return true if should merge, false otherwise
     */
    boolean isRSAOAEPParametersMerge();
    
    /**
     * Get the instance of {@link KeyTransportAlgorithmPredicate}.
     * 
     * @return the predicate instance
     */
    @Nullable KeyTransportAlgorithmPredicate getKeyTransportAlgorithmPredicate();
    
    /**
     * Get the map of {@link KeyAgreementEncryptionConfiguration} instances.
     * 
     * @return the 
     */
    @Nonnull @Unmodifiable @NotLive Map<String, KeyAgreementEncryptionConfiguration> getKeyAgreementConfigurations();
    
}