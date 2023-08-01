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

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.encryption.OriginatorKeyInfo;
import org.opensaml.xmlsec.encryption.RecipientKeyInfo;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.KeyInfo;

import com.google.common.base.Strings;

import net.shibboleth.shared.codec.EncodingException;


/**
 * A factory implementation which produces instances of {@link KeyInfoGenerator} capable of 
 * handling the information contained within a {@link Credential}.
 * 
 * All boolean options default to false.
 */
public class BasicKeyInfoGeneratorFactory implements KeyInfoGeneratorFactory {
    
    /** Mappings from KeyInfo Class to QNames.*/
    @Nonnull private static final Map<Class<? extends KeyInfo>, QName> CLASS_TO_NAME;
    
    static {
        CLASS_TO_NAME = new HashMap<>();
        CLASS_TO_NAME.put(KeyInfo.class, KeyInfo.DEFAULT_ELEMENT_NAME);
        CLASS_TO_NAME.put(OriginatorKeyInfo.class, OriginatorKeyInfo.DEFAULT_ELEMENT_NAME);
        CLASS_TO_NAME.put(RecipientKeyInfo.class, RecipientKeyInfo.DEFAULT_ELEMENT_NAME);
    }
    
    /** The set of options configured for the factory. */
    @Nonnull private final BasicOptions options;
    
    /**
     * Constructor.
     * 
     * All boolean options are initialzed as false;
     */
    public BasicKeyInfoGeneratorFactory() {
        options = newOptions();
    }
    
    /** {@inheritDoc} */
    @Nonnull public Class<? extends Credential> getCredentialType() {
        return Credential.class;
    }

    /** {@inheritDoc} */
    public boolean handles(@Nonnull final Credential credential) {
        // This top-level class can handle any Credential type, with output limited to basic Credential information
        return true;
    }

    /** {@inheritDoc} */
    @Nonnull public KeyInfoGenerator newInstance() {
        return newInstance(null);
    }
    
    /** {@inheritDoc} */
    @Nonnull public KeyInfoGenerator newInstance(@Nullable final Class<? extends KeyInfo> type) {
        return new BasicKeyInfoGenerator(options.clone(), type);
    }
    
    /**
     * Get the option to emit the entity ID value in a Credential as a KeyName element.
     * 
     * @return return the option value
     */
    public boolean emitEntityIDAsKeyName() {
        return options.emitEntityIDAsKeyName;
    }

    /**
     * Set the option to emit the entity ID value in a Credential as a KeyName element.
     * 
     * @param newValue the new option value to set
     */
    public void setEmitEntityIDAsKeyName(final boolean newValue) {
        options.emitEntityIDAsKeyName = newValue;
    }

    /**
     * Get the option to emit key names found in a Credential as KeyName elements.
     * 
     * @return the option value
     */
    public boolean emitKeyNames() {
        return options.emitKeyNames;
    }

    /**
     * Set the option to emit key names found in a Credential as KeyName elements.
     * 
     * @param newValue the new option value to set
     */
    public void setEmitKeyNames(final boolean newValue) {
        options.emitKeyNames = newValue;
    }

    /**
     * Get the option to emit the value of {@link Credential#getPublicKey()} as a KeyValue element.
     * 
     * @return the option value
     */
    public boolean emitPublicKeyValue() {
        return options.emitPublicKeyValue;
    }

    /**
     * Set the option to emit the value of {@link Credential#getPublicKey()} as a KeyValue element.
     * 
     * @param newValue the new option value to set
     */
    public void setEmitPublicKeyValue(final boolean newValue) {
        options.emitPublicKeyValue = newValue;
    }
    
    /**
     * Get the option to emit the value of {@link Credential#getPublicKey()} as a DEREncodedKeyValue element.
     *
     * @return the option value
     */
    public boolean emitPublicDEREncodedKeyValue() {
        return options.emitPublicDEREncodedKeyValue;
    }
          
    /**
     * Set the option to emit the value of {@link Credential#getPublicKey()} as a DEREncodedKeyValue element.
     *
     * @param newValue the new option value to set
     */
    public void setEmitPublicDEREncodedKeyValue(final boolean newValue) {
        options.emitPublicDEREncodedKeyValue = newValue;
    }
    
    /**
     * Get a new instance to hold options.  Used by the top-level superclass constructor.
     * Subclasses <strong>MUST</strong> override to produce an instance of the appropriate 
     * subclass of {@link BasicOptions}.
     * 
     * @return a new instance of factory/generator options
     */
    @Nonnull protected BasicOptions newOptions() {
        return new BasicOptions();
    }
    
    /**
     * Get the options of this instance. Used by subclass constructors to get the options built by 
     * the top-level class constructor with {@link #newOptions()}.
     * 
     * @return the options instance
     */
    @Nonnull protected BasicOptions getOptions() {
        return options;
    }
    
    /**
     * An implementation of {@link KeyInfoGenerator} capable of  handling the information 
     * contained within a {@link Credential}.
    */
    public class BasicKeyInfoGenerator implements KeyInfoGenerator {
        
        /** The set of options to be used by the generator.*/
        @Nonnull private final BasicOptions options;
        
        /** The specific type of KeyInfo to generate. */
        @Nonnull private final Class<? extends KeyInfo> keyInfoType;
       
        /** Builder factory for KeyInfo objects. */
        @Nonnull private final XMLObjectBuilderFactory builderFactory;
       
        /**
         * Constructor.
         * 
         * @param newOptions the options to be used by the generator
         * @param type the type of element to produce
         */
        protected BasicKeyInfoGenerator(@Nonnull final BasicOptions newOptions,
                @Nullable final Class<? extends KeyInfo> type) {
            options = newOptions;
            keyInfoType = type != null ? type : KeyInfo.class;
            builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
        }

        /** {@inheritDoc} */
        @Nullable public KeyInfo generate(@Nullable final Credential credential) throws SecurityException {
            if (credential == null) {
                return null;
            }
            
            final KeyInfo keyInfo = buildKeyInfo();
            
            processKeyNames(keyInfo, credential);
            processEntityID(keyInfo, credential);
            processPublicKey(keyInfo, credential);
            
            final List<XMLObject> children = keyInfo.getOrderedChildren();
            if (children != null && children.size() > 0) {
                return keyInfo;
            }
            return null;
        }
        
        /**
         * Build a new KeyInfo instance.
         * 
         * <p>
         * The exact element type is determined by {@link #keyInfoType} which was supplied at factory construction.
         * </p>
         * 
         * @return a new KeyInfo instance
         * 
         * @throws SecurityException if class type can not be mapped to an element {@link QName}
         */
        @Nonnull protected KeyInfo buildKeyInfo() throws SecurityException {
            final QName elementName = classToElementName(keyInfoType);
            if (elementName == null) { 
                throw new SecurityException("KeyInfo type not mapped to an element QName: "
                        + keyInfoType.getClass().getName());
            }
            
            final XMLObject xmlObject = builderFactory.ensureBuilder(elementName).buildObject(elementName);
            return KeyInfo.class.cast(xmlObject);
        }
        
        /**
         * Map the specified KeyInfo type to an element {@link QName}.
         * 
         * <p>
         * Subclasses may override to implement new types or custom mappings.
         * </p>
         * 
         * @param type the KeyInfo element type
         * 
         * @return the mapped element name
         */
        @Nullable protected QName classToElementName(@Nonnull final Class<? extends KeyInfo> type) {
            return CLASS_TO_NAME.get(type);
        }
        
        /** Process the values of {@link Credential#getKeyNames()}.
         * 
         * @param keyInfo the KeyInfo that is being built
         * @param credential the Credential that is geing processed
         */
        protected void processKeyNames(@Nonnull final KeyInfo keyInfo, @Nonnull final Credential credential) {
            if (options.emitKeyNames) {
                for (final String keyNameValue : credential.getKeyNames()) {
                    if (!Strings.isNullOrEmpty(keyNameValue)) {
                        KeyInfoSupport.addKeyName(keyInfo, keyNameValue);
                    }
                }
            }
        }
        
        /** Process the value of {@link Credential#getEntityId()}.
         * 
         * @param keyInfo the KeyInfo that is being built
         * @param credential the Credential that is geing processed
         */
        protected void processEntityID(@Nonnull final KeyInfo keyInfo, @Nonnull final Credential credential) {
            if (options.emitEntityIDAsKeyName) {
                final String keyNameValue = credential.getEntityId();
                if (!Strings.isNullOrEmpty(keyNameValue)) {
                    KeyInfoSupport.addKeyName(keyInfo, keyNameValue);
                }
            }
        }
        
        /** Process the value of {@link Credential#getPublicKey()}.
         * 
         * @param keyInfo the KeyInfo that is being built
         * @param credential the Credential that is being processed
         * @throws SecurityException if the public key can't be encoded properly
         */
        protected void processPublicKey(@Nonnull final KeyInfo keyInfo, @Nonnull final Credential credential)
            throws SecurityException {
            
            final PublicKey key = credential.getPublicKey();
            if (key != null) {
                if (options.emitPublicKeyValue) {
                    try {
                        KeyInfoSupport.addPublicKey(keyInfo, key);
                    } catch (final EncodingException e) {
                        throw new SecurityException("Can't add public key to key info",e);
                    }
                }
                if (options.emitPublicDEREncodedKeyValue) {
                    try {
                        KeyInfoSupport.addDEREncodedPublicKey(keyInfo, key);
                    } catch (final NoSuchAlgorithmException e) {
                        throw new SecurityException("Can't DER-encode key, unsupported key algorithm", e);
                    } catch (final InvalidKeySpecException e) {
                        throw new SecurityException("Can't DER-encode key, invalid key specification", e);
                    }
                }
            }
        }
    }
    
    /**
     * Options to be used in the production of a {@link KeyInfo} from a {@link Credential}.
     */
    protected class BasicOptions implements Cloneable {
        
        /** Emit key names found in a Credential as KeyName elements. */
        private boolean emitKeyNames;
        
        /** Emit the entity ID value in a Credential as a KeyName element. */
        private boolean emitEntityIDAsKeyName;
        
        /** Emit the value of {@link Credential#getPublicKey()} as a KeyValue element. */
        private boolean emitPublicKeyValue;
        
        /** Emit the value of {@link Credential#getPublicKey()} as a DEREncodedKeyValue element. */
        private boolean emitPublicDEREncodedKeyValue;
        
        /** {@inheritDoc} */
        protected BasicOptions clone() {
            try {
                return (BasicOptions) super.clone();
            } catch (final CloneNotSupportedException e) {
                // we know we're cloneable, so this will never happen
                return null;
            }
        }
        
    }
    
}