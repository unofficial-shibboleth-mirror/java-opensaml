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

package org.opensaml.xmlsec.encryption.support;

import javax.annotation.Nullable;

import org.opensaml.xmlsec.EncryptionParameters;

import net.shibboleth.shared.logic.Constraint;


/**
 * Parameters for encrypting keys.
 */
public class KeyEncryptionParameters extends DataEncryptionParameters {

    /** Recipient of the key. */
    @Nullable private String recipient;
    
    /** RSA OAEP parameters. */
    @Nullable private RSAOAEPParameters rsaOAEPParameters;

    /**
     * Constructor.
     */
    public KeyEncryptionParameters() {
        super();
        // The default supplied by the super class doesn't make sense,
        // can't autogenerate a key encryption key, always needs to be derived
        // from the key in the (for KEK, mandatory) encryption credential.
        setAlgorithm(null);
    }
    
    /**
     * Convenience constructor which allows copying the relevant key encryption parameters from
     * an instance of {@link EncryptionParameters}.
     * 
     * @param params the encryption parameters instance
     * @param recipientId the recipient of the key
     */
    public KeyEncryptionParameters(final EncryptionParameters params, final String recipientId) {
        this();
        Constraint.isNotNull(params, "EncryptionParameters instance was null");
        setEncryptionCredential(params.getKeyTransportEncryptionCredential());
        setAlgorithm(params.getKeyTransportEncryptionAlgorithm());
        setKeyInfoGenerator(params.getKeyTransportKeyInfoGenerator());
        setRecipient(recipientId);
        setRSAOAEPParameters(params.getRSAOAEPParameters());
    }

    /**
     * Gets the recipient of the key.
     * 
     * When generating an EncryptedKey, this will be used as the value of the Recipient attribute.
     * 
     * @return the recipient of the key, or null
     */
    @Nullable public String getRecipient() {
        return recipient;
    }

    /**
     * Sets the recipient of the key.
     * 
     * When generating an EncryptedKey, this will be used as the value of the Recipient attribute.
     * 
     * @param newRecipient the recipient of the key
     */
    public void setRecipient(@Nullable final String newRecipient) {
        recipient = newRecipient;
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