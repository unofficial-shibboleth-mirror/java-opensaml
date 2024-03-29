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

package org.opensaml.xmlsec.keyinfo.impl;

import java.security.Key;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.KeyInfo;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.collection.LazyMap;
import net.shibboleth.shared.collection.LazySet;

/**
 *  Resolution context class that is used to supply state information to, and to share information
 *  amongst, {@link KeyInfoProvider}s.
 *  
 *  <p>
 *  The extensible properties map available from {@link #getProperties()} may for example used to communicate
 *  state between two or more providers, or between a provider and custom logic in a particular implementation
 *  of {@link org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver}. It is recommended that providers and/or
 *  resolvers define and use property names in such a way as to avoid collisions with those used by other providers
 *  and resolvers, and to also clearly define the data type stored for each propery name.
 *  </p>
 *  
 */
public class KeyInfoResolutionContext {
    
    /** The KeyInfo being processed. */
    @Nullable private KeyInfo keyInfo;
    
    /** Key names which are known to be associated with the KeyInfo being processed.
     * These may have for example been extracted from KeyName elements present,
     * or may have been inferred from the context in which the KeyInfo exists or
     * is being used. */
    @Nonnull @Live private final Set<String> keyNames;
    
    /** Get the key currently known to be represented by the KeyInfo. */
    @Nullable private Key key;
    
    /** This list provides KeyInfo resolvers and providers in a particular processing
     * environment access to credentials that may have already been previously resolved. */
    @Nonnull private final Collection<Credential> resolvedCredentials;
    
    /** Extensible map of properties used to share state amongst providers and/or resolver logic. */
    @Nonnull @Live private final Map<String, Object> properties;
    
    /**
     * Constructor.
     * 
     * @param credentials a reference to the collection in which credentials previously
     *          resolved in a processing flow are being stored
     */
    public KeyInfoResolutionContext(
            @Nonnull @ParameterName(name="credentials") final Collection<Credential> credentials) {
        resolvedCredentials = CollectionSupport.copyToList(credentials);
        properties = new LazyMap<>();
        keyNames = new LazySet<>();
    }
    
    /**
     * Gets the KeyInfo being processed.
     * 
     * @return Returns the keyInfo.
     */
    @Nullable public KeyInfo getKeyInfo() {
        return keyInfo;
    }
    
    /**
     * Sets the KeyInfo being processed.
     * 
     * @param newKeyInfo The keyInfo to set.
     */
    public void setKeyInfo(@Nullable final KeyInfo newKeyInfo) {
        keyInfo = newKeyInfo;
    }
    
    /**
     * The key names which are currently known.
     * 
     * These key names should be those which are known to be associated with the
     * key represented by the KeyInfo being processed. These may have for example
     * been directly extracted from KeyName elements present, or may have been
     * inferred from the context in which the KeyInfo exists or is being used. 
     * 
     * @return the set of key names
     * 
     * */
    @Nonnull @Live public Set<String> getKeyNames() {
        return keyNames;
    }
    
    /**
     * Get the key currently known to be represented by the KeyInfo.
     * 
     * @return the key currently known to be represented by the KeyInfo
     *          or null if not currently known
     */
    @Nullable public Key getKey() {
        return key;
    }
    
    /**
     * Set the key currently known to be represented by the KeyInfo.
     * 
     * @param newKey the new Key
     */
    public void setKey(@Nullable final Key newKey) {
        key = newKey;
    }
    
    /**
     * Get the set of credentials previously resolved.
     * 
     * @return Returns the keyValueCredential.
     */
    @Nonnull @Unmodifiable @NotLive public Collection<Credential> getResolvedCredentials() {
        return resolvedCredentials;
    }
    
    /**
     * Get the extensible properties map.
     * 
     * @return Returns the properties.
     */
    @Nonnull @Live public Map<String, Object> getProperties() {
        return properties;
    }
}