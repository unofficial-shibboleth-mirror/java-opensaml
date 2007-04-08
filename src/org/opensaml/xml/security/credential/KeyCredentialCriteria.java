/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.security.credential;

import java.security.PublicKey;

import org.opensaml.xml.util.DatatypeHelper;

/**
 * An implementation of {@link CredentialCriteria} which specifies criteria pertaining 
 * to characteristics of a key to be resolved.
 */
public final class KeyCredentialCriteria implements CredentialCriteria {
    
    /** Key algorithm type of resolved credentials. */
    private String keyAlgorithm;
    
    /** Key length of resolved credentials. */
    private int keyLength;
    
    /** Key name of resolved credentials.  */
    private String keyName;
    
    /** Specifier of public key associated with resolved credentials. */
    private PublicKey publicKey;
    
    /**
     * Constructor.
     *
     * @param name key name
     * @param algorithm key algorithm
     * @param length key length 
     * @param pubKey public key
     */
    public KeyCredentialCriteria(String name, String algorithm, int length, PublicKey pubKey) {
        setKeyName(name);
        setKeyAlgorithm(algorithm);
        setKeyLength(length);
        setPublicKey(pubKey);
    }
 
    /**
     * Get the key algorithm criteria.
     * 
     * @return returns the keyAlgorithm.
     */
    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    /**
     * Set the key algorithm criteria.
     * 
     * @param algorithm The keyAlgorithm to set.
     */
    public void setKeyAlgorithm(String algorithm) {
        keyAlgorithm = DatatypeHelper.safeTrimOrNullString(algorithm);
    }

    /**
     * Get the key name criteria.
     * 
     * @return Returns the keyName.
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * Set the key name criteria.
     * 
     * @param name The keyName to set.
     */
    public void setKeyName(String name) {
        keyName = DatatypeHelper.safeTrimOrNullString(keyName);
    }

    /**
     * Get the public key criteria.
     * 
     * @return Returns the publicKey.
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Set the public key criteria. 
     * 
     * @param key The publicKey to set.
     */
    public void setPublicKey(PublicKey key) {
        publicKey = key;
    }

    /**
     * Get the key length.
     * 
     * @return Returns the keyLength.
     */
    public int getKeyLength() {
        return keyLength;
    }

    /**
     * Set the key length.
     * 
     * @param length The keyLength to set.
     */
    public void setKeyLength(int length) {
        keyLength = length;
    }

}
