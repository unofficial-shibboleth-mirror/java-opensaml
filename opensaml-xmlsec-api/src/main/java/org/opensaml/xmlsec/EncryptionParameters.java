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

import javax.annotation.Nullable;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.encryption.support.RSAOAEPParameters;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;


/**
 * The effective parameters to use when generating encrypted XML.
 */
public class EncryptionParameters {
    
    /** The EncryptedData encryption credential. */
    @Nullable private Credential dataEncryptionCredential;
    
    /** The EncryptedKey encryption credential. */
    @Nullable private Credential keyTransportEncryptionCredential;
    
    /** The EncryptedData encryption algorithm URI. */
    @Nullable private String dataEncryptionAlgorithmURI;
    
    /** The EncryptedKey encryption algorithm URI. */
    @Nullable private String keyTransportEncryptionAlgorithmURI;
    
    /** The EncryptedData KeyInfoGenerator. */
    @Nullable private KeyInfoGenerator dataKeyInfoGenerator;
    
    /** The EncryptedKey KeyInfoGenerator. */
    @Nullable private KeyInfoGenerator keyTransportKeyInfoGenerator;
    
    /** RSA OAEP parameters. */
    @Nullable private RSAOAEPParameters rsaOAEPParameters;

    /**
     * Get the encryption credential to use when encrypting the EncryptedData.
     * 
     * @return the encryption credential
     */
    @Nullable public Credential getDataEncryptionCredential() {
        return dataEncryptionCredential;
    }
    
    /**
     * Set the encryption credential to use when encrypting the EncryptedData.
     * 
     * @param credential the encryption credential
     */
    public void setDataEncryptionCredential(@Nullable final Credential credential) {
        dataEncryptionCredential = credential;
    }
    
    /**
     * Get the encryption credential to use when encrypting the EncryptedKey.
     * 
     * @return the encryption credential
     */
    @Nullable public Credential getKeyTransportEncryptionCredential() {
        return keyTransportEncryptionCredential;
    }
    
    /**
     * Set the encryption credential to use when encrypting the EncryptedKey.
     * 
     * @param credential the encryption credential
     */
    public void setKeyTransportEncryptionCredential(@Nullable final Credential credential) {
        keyTransportEncryptionCredential = credential;
    }
    
    /**
     * Get the encryption algorithm URI to use when encrypting the EncryptedData.
     * 
     * @return an encryption algorithm URI
     */
    @Nullable public String getDataEncryptionAlgorithm() {
        return dataEncryptionAlgorithmURI;
    }
    
    /**
     * Set the encryption algorithm URI to use when encrypting the EncryptedData.
     * 
     * @param uri an encryption algorithm URI
     */
    public void setDataEncryptionAlgorithm(@Nullable final String uri) {
        dataEncryptionAlgorithmURI = uri;
    }
    
    /**
     * Get the encryption algorithm URI to use when encrypting the EncryptedKey.
     * 
     * @return an encryption algorithm URI
     */
    @Nullable public String getKeyTransportEncryptionAlgorithm() {
        return keyTransportEncryptionAlgorithmURI;
    }

    /**
     * Set the encryption algorithm URI to use when encrypting the EncryptedKey.
     * 
     * @param uri an encryption algorithm URI
     */
    public void setKeyTransportEncryptionAlgorithm(@Nullable final String uri) {
        keyTransportEncryptionAlgorithmURI = uri;
    }

    /**
     * Get the KeyInfoGenerator to use when generating the EncryptedData/KeyInfo.
     * 
     * @return the KeyInfoGenerator instance
     */
    @Nullable public KeyInfoGenerator getDataKeyInfoGenerator() {
        return dataKeyInfoGenerator;
    }
    
    /**
     * Set the KeyInfoGenerator to use when generating the EncryptedData/KeyInfo.
     * 
     * @param generator the KeyInfoGenerator instance
     */
    public void setDataKeyInfoGenerator(@Nullable final KeyInfoGenerator generator) {
        dataKeyInfoGenerator = generator;
    }
    
    /**
     * Get the KeyInfoGenerator to use when generating the EncryptedKey/KeyInfo.
     * 
     * @return the KeyInfoGenerator instance
     */
    @Nullable public KeyInfoGenerator getKeyTransportKeyInfoGenerator() {
        return keyTransportKeyInfoGenerator;
    }
    
    /**
     * Set the KeyInfoGenerator to use when generating the EncryptedKey/KeyInfo.
     * 
     * @param generator the KeyInfoGenerator instance
     */
    public void setKeyTransportKeyInfoGenerator(@Nullable final KeyInfoGenerator generator) {
        keyTransportKeyInfoGenerator = generator;
    }
    
    /**
     * Get the instance of {@link RSAOAEPParameters}.
     * 
     * @return the parameters instance
     */
    @Nullable public RSAOAEPParameters getRSAOAEPParameters() {
        return rsaOAEPParameters;
    }

    /**
     * Set the instance of {@link RSAOAEPParameters}.
     * 
     * @param params the new parameters instance
     */
    public void setRSAOAEPParameters(@Nullable final RSAOAEPParameters params) {
        rsaOAEPParameters = params;
    }
    
}