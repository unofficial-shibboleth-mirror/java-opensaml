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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.w3c.dom.Element;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.logic.Constraint;

/**
 * Implementation of {@link KeyInfoGenerator} which always returns static {@link KeyInfo} data.
 * 
 * Note that the argument to {@link #generate(Credential)} is not used in generating the new KeyInfo,
 * and so may be null.
 * 
 * If the original KeyInfo that was passed to this instance is already the child of some other
 * XMLObject at the time {@link #generate(Credential)} is called, then a newly constructed KeyInfo
 * object will be returned that contains the same data as the original. Otherwise, the original
 * KeyInfo instance is returned directly.
 * 
 */
public class StaticKeyInfoGenerator implements KeyInfoGenerator {
    
    /** The KeyInfo object held by this generator instance. */
    @Nonnull private KeyInfo keyInfo;
    
    /** Unmarshaller used in cloning operation. */
    @Nullable private Unmarshaller keyInfoUnmarshaller;
    
    /** Marshaller used in cloning operation. */
    @Nullable private Marshaller keyInfoMarshaller;
    
    /**
     * Constructor.
     *
     * @param newKeyInfo the KeyInfo used as the basis to return new KeyInfo objects from this generator
     */
    public StaticKeyInfoGenerator(@Nonnull @ParameterName(name="newKeyInfo") final KeyInfo newKeyInfo) {
        keyInfo = Constraint.isNotNull(newKeyInfo, "KeyInfo cannot be null");
    }

    /** {@inheritDoc} */
    @Nonnull public KeyInfo generate(@Nullable final Credential credential) throws SecurityException {
        if (keyInfo.getParent() == null) {
            return keyInfo;
        }
        return clone(keyInfo);
    }
    
    /**
     * Get the static KeyInfo object held by this generator.
     * 
     * @return the currently held KeyInfo object
     */
    @Nonnull public KeyInfo getKeyInfo() {
        return keyInfo;
    }
    
    /**
     * Set the static KeyInfo object held by this generator.
     * 
     * @param newKeyInfo the new KeyInfo object
     */
    public void setKeyInfo(@Nonnull final KeyInfo newKeyInfo) {
        keyInfo = Constraint.isNotNull(newKeyInfo, "KeyInfo cannot be null");
    }
    
    /**
     * Clone a KeyInfo and return the new object.
     * 
     * @param origKeyInfo the KeyInfo object to clone
     * @return a new KeyInfo object cloned from the original
     * @throws SecurityException thrown in there are marshalling or unmarshalling errors during cloning
     */
    @Nonnull private KeyInfo clone(@Nonnull final KeyInfo origKeyInfo) throws SecurityException {
        // A brute force approach to cloning:
        //   1) marshall the original (if necessary)
        //   2) unmarshall a new object around the cached or newly marshalled DOM.
        //   3) ensure only one of them caches the DOM (original or marshalled)
        final Element origDOM = origKeyInfo.getDOM();
        if (origDOM == null) {
            try {
                getMarshaller().marshall(origKeyInfo);
            } catch (final MarshallingException e) {
                throw new SecurityException("Error marshalling the original KeyInfo during cloning", e);
            }
        }
        
        KeyInfo newKeyInfo = null;
        try {
            final Element newDOM = origKeyInfo.getDOM();
            assert newDOM != null;
            newKeyInfo = (KeyInfo) getUnmarshaller().unmarshall(newDOM);
        } catch (final UnmarshallingException e) {
            throw new SecurityException("Error unmarshalling the new KeyInfo during cloning", e);
        }
        
        // If the original had no cached DOM, go ahead and drop so this operation doesn't have any side effects.
        // If it did have, then drop it on the new one, so isn't cached by two objects.
        if (origDOM == null) {
            origKeyInfo.releaseChildrenDOM(true);
            origKeyInfo.releaseDOM();
        } else {
            newKeyInfo.releaseChildrenDOM(true);
            newKeyInfo.releaseDOM();
        }
        
        return newKeyInfo;
    }
    
    /**
     * Get a KeyInfo marshaller.
     * 
     * @return a KeyInfo marshaller
     * @throws SecurityException thrown if there is an error obtaining the marshaller from the configuration
     */
    @Nonnull private Marshaller getMarshaller() throws SecurityException {
        if (keyInfoMarshaller == null) {
            keyInfoMarshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(
                    KeyInfo.DEFAULT_ELEMENT_NAME);
            if (keyInfoMarshaller == null) {
                throw new SecurityException("Could not obtain KeyInfo marshaller from the configuration");
            }
        }
        
        assert keyInfoMarshaller != null;
        return keyInfoMarshaller;
    }

    /**
     * Get a KeyInfo unmarshaller.
     * 
     * @return a KeyInfo unmarshaller
     * @throws SecurityException thrown if there is an error obtaining the unmarshaller from the configuration
     */
    @Nonnull private Unmarshaller getUnmarshaller() throws SecurityException {
        if (keyInfoUnmarshaller == null) {
            keyInfoUnmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(
                    KeyInfo.DEFAULT_ELEMENT_NAME);
            if (keyInfoUnmarshaller == null) {
                throw new SecurityException("Could not obtain KeyInfo unmarshaller from the configuration");
            }
        }
        
        assert keyInfoUnmarshaller != null;
        return keyInfoUnmarshaller;
    }
    
}
