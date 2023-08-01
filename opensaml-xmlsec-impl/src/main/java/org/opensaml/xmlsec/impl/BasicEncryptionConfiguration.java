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

package org.opensaml.xmlsec.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.StringSupport;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.KeyTransportAlgorithmPredicate;
import org.opensaml.xmlsec.encryption.support.KeyAgreementEncryptionConfiguration;
import org.opensaml.xmlsec.encryption.support.RSAOAEPParameters;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;

/**
 * Basic implementation of {@link EncryptionConfiguration}.
 */
public class BasicEncryptionConfiguration extends BasicAlgorithmPolicyConfiguration 
        implements EncryptionConfiguration {
    
    /** Data encryption credentials. */
    @Nonnull private List<Credential> dataEncryptionCredentials;
    
    /** Data encryption algorithm URIs. */
    @Nonnull private List<String> dataEncryptionAlgorithms;

    /** Key transport encryption credentials. */
    @Nonnull private List<Credential> keyTransportEncryptionCredentials;
    
    /** Key transport encryption algorithm URIs. */
    @Nonnull private List<String> keyTransportEncryptionAlgorithms;
    
    /** Manager for named KeyInfoGenerator instances for encrypting data. */
    @Nullable private NamedKeyInfoGeneratorManager dataKeyInfoGeneratorManager;
    
    /** Manager for named KeyInfoGenerator instances for encrypting keys. */
    @Nullable private NamedKeyInfoGeneratorManager keyTransportKeyInfoGeneratorManager;
    
    /** RSA OAEP parameters. */
    @Nullable private RSAOAEPParameters rsaOAEPParameters;
    
    /** Flag whether to merge RSA OAEP parameters. */
    private boolean rsaOAEPParametersMerge;
    
    /** Key transport algorithm predicate. */
    @Nullable private KeyTransportAlgorithmPredicate keyTransportPredicate;
    
    /** Key agreement configurations. */
    @Nonnull private Map<String, KeyAgreementEncryptionConfiguration> keyAgreementConfigurations;
    
    //TODO chaining to parent config instance on getters? or use a wrapping proxy, etc?
    
    //TODO update for modern coding conventions, Guava, etc
    
    /** Constructor. */
    public BasicEncryptionConfiguration() {
        dataEncryptionCredentials = CollectionSupport.emptyList();
        dataEncryptionAlgorithms = CollectionSupport.emptyList();
        keyTransportEncryptionCredentials = CollectionSupport.emptyList();
        keyTransportEncryptionAlgorithms = CollectionSupport.emptyList();
        keyAgreementConfigurations = CollectionSupport.emptyMap();
        
        rsaOAEPParametersMerge = true;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @Unmodifiable @NotLive public List<Credential> getDataEncryptionCredentials() {
        return dataEncryptionCredentials;
    }
    
    /**
     * Set the data encryption credentials to use.
     * 
     * @param credentials the list of data encryption credentials
     * 
     * @return this object
     */
    @Nonnull public BasicEncryptionConfiguration setDataEncryptionCredentials(
            @Nullable final List<Credential> credentials) {
        if (credentials == null) {
            dataEncryptionCredentials  = CollectionSupport.emptyList();
        } else {
            dataEncryptionCredentials = CollectionSupport.copyToList(credentials);
        }
        
        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @Unmodifiable @NotLive public List<String> getDataEncryptionAlgorithms() {
        return dataEncryptionAlgorithms;
    }
    
    /**
     * Set the data encryption algorithms to use.
     * 
     * @param algorithms the list of algorithms
     * 
     * @return this object
     */
    @Nonnull public BasicEncryptionConfiguration setDataEncryptionAlgorithms(
            @Nullable final List<String> algorithms) {
        if (algorithms == null) {
            dataEncryptionAlgorithms = CollectionSupport.emptyList();
        } else {
            dataEncryptionAlgorithms =
                    CollectionSupport.copyToList(StringSupport.normalizeStringCollection(algorithms));
        }
        
        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @Unmodifiable @NotLive public List<Credential> getKeyTransportEncryptionCredentials() {
        return keyTransportEncryptionCredentials;
    }
    
    /**
     * Set the key transport encryption credentials to use.
     * 
     * @param credentials the list of key transport encryption credentials
     * 
     * @return this object
     */
    @Nonnull public BasicEncryptionConfiguration setKeyTransportEncryptionCredentials(
            @Nullable final List<Credential> credentials) {
        if (credentials == null) {
            keyTransportEncryptionCredentials  = CollectionSupport.emptyList();
        } else {
            keyTransportEncryptionCredentials = CollectionSupport.copyToList(credentials);
        }
        
        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @Unmodifiable @NotLive public List<String> getKeyTransportEncryptionAlgorithms() {
        return keyTransportEncryptionAlgorithms;
    }
    
    /**
     * Set the key transport encryption algorithms to use.
     * 
     * @param algorithms the list of algorithms
     * 
     * @return this object
     */
    @Nonnull public BasicEncryptionConfiguration setKeyTransportEncryptionAlgorithms(
            @Nullable final List<String> algorithms) {
        if (algorithms == null) {
            keyTransportEncryptionAlgorithms = CollectionSupport.emptyList();
        } else {
            keyTransportEncryptionAlgorithms =
                    CollectionSupport.copyToList(StringSupport.normalizeStringCollection(algorithms));
        }
        
        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable public NamedKeyInfoGeneratorManager getDataKeyInfoGeneratorManager() {
        return dataKeyInfoGeneratorManager;
    }
    
    /**
     * Set the manager for named KeyInfoGenerator instances encrypting data.
     * 
     * @param keyInfoManager the KeyInfoGenerator manager to use
     * 
     * @return this object
     */
    @Nonnull public BasicEncryptionConfiguration setDataKeyInfoGeneratorManager(
            @Nullable final NamedKeyInfoGeneratorManager keyInfoManager) {
        dataKeyInfoGeneratorManager = keyInfoManager;
        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable public NamedKeyInfoGeneratorManager getKeyTransportKeyInfoGeneratorManager() {
        return keyTransportKeyInfoGeneratorManager;
    }
    
    /**
     * Set the manager for named KeyInfoGenerator instances for encrypting keys.
     * 
     * @param keyInfoManager the KeyInfoGenerator manager to use
     * 
     * @return this object
     */
    @Nonnull public BasicEncryptionConfiguration setKeyTransportKeyInfoGeneratorManager(
            @Nullable final NamedKeyInfoGeneratorManager keyInfoManager) {
        keyTransportKeyInfoGeneratorManager = keyInfoManager;
        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable public RSAOAEPParameters getRSAOAEPParameters() {
        return rsaOAEPParameters;
    }

    /**
     * Set the instance of {@link RSAOAEPParameters}.
     * 
     * @param params the new parameters instance
     * 
     * @return this object
     */
    @Nonnull public BasicEncryptionConfiguration setRSAOAEPParameters(@Nullable final RSAOAEPParameters params) {
        rsaOAEPParameters = params;
        return this;
    }
    
    /** {@inheritDoc}.
     * 
     * <p>Defaults to: <code>true</code>
     * 
     * */
    public boolean isRSAOAEPParametersMerge() {
        return rsaOAEPParametersMerge;
    }
    
    /**
     * Set the flag indicating whether to merge this configuration's {@link RSAOAEPParameters} values with those of 
     * a lower order of precedence, or to treat this configuration's parameters set as authoritative.
     * 
     * <p>Defaults to: <code>true</code>
     * 
     * @param flag true if should merge, false otherwise
     * 
     * @return this object
     */
    @Nonnull public BasicEncryptionConfiguration setRSAOAEPParametersMerge(final boolean flag) {
        rsaOAEPParametersMerge = flag;
        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable public KeyTransportAlgorithmPredicate getKeyTransportAlgorithmPredicate() {
        return keyTransportPredicate;
    }
    
    /**
     * Set the instance of {@link KeyTransportAlgorithmPredicate}.
     * 
     * @param predicate the new predicate instance
     * 
     * @return this object
     */
    @Nonnull public BasicEncryptionConfiguration setKeyTransportAlgorithmPredicate(
            @Nullable final KeyTransportAlgorithmPredicate predicate) {
        keyTransportPredicate = predicate;
        return this;
    }

    /** {@inheritDoc} */
    @Nonnull @Unmodifiable @NotLive
    public Map<String, KeyAgreementEncryptionConfiguration> getKeyAgreementConfigurations() {
        return keyAgreementConfigurations;
    }
    
    /**
     * Set the map of {@link KeyAgreementEncryptionConfiguration} instances.
     * 
     * @param configs the new map of instances
     * 
     * @return this object
     */
    @Nonnull public BasicEncryptionConfiguration setKeyAgreementConfigurations(
            @Nullable final Map<String,KeyAgreementEncryptionConfiguration> configs) {
        if (configs == null) {
            keyAgreementConfigurations = CollectionSupport.emptyMap();
        } else {
            keyAgreementConfigurations = CollectionSupport.copyToMap(configs);
        }
        
        return this;
    }
    
}