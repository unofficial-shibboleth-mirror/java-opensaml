/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.agreement.KeyAgreementCredential;
import org.opensaml.xmlsec.agreement.KeyAgreementParameter;
import org.opensaml.xmlsec.agreement.XMLExpressableKeyAgreementParameter;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.KANonce;
import org.opensaml.xmlsec.encryption.OriginatorKeyInfo;
import org.opensaml.xmlsec.encryption.RecipientKeyInfo;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorManager;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory implementation which produces instances of {@link KeyInfoGenerator} capable of 
 * handling the information contained within an {@link KeyAgreementCredential}.
 */
public class KeyAgreementKeyInfoGeneratorFactory extends BasicKeyInfoGeneratorFactory {
    
    /** The set of options configured for the factory. */
    private final KeyAgreementOptions options;
    
    /** Constructor. */
    public KeyAgreementKeyInfoGeneratorFactory() {
        super();
        options = (KeyAgreementOptions) super.getOptions();
    }
    
    /** {@inheritDoc} */
    @Nonnull public Class<? extends Credential> getCredentialType() {
        return KeyAgreementCredential.class;
    }

    /** {@inheritDoc} */
    public boolean handles(@Nonnull final Credential credential) {
        return credential instanceof KeyAgreementCredential;
    }

    /** {@inheritDoc} */
    @Nonnull public KeyInfoGenerator newInstance() {
        return newInstance(null);
    }
    
    /** {@inheritDoc} */
    @Nonnull public KeyInfoGenerator newInstance(@Nullable final Class<? extends KeyInfo> type) {
        return new KeyAgreementKeyInfoGenerator(options.clone(), type);
    }
    
    /**
     * Get the option to emit the OriginatorKeyInfo element within the AgreementMethod element.
     * 
     * @return the option value
     */
    public boolean emitOriginatorKeyInfo() {
        return options.emitOriginatorKeyInfo;
    }

    /**
     * Set the option to emit the OriginatorKeyInfo element within the AgreementMethod element.
     * 
     * @param newValue the new option value
     */
    public void setEmitOriginatorKeyInfo(final boolean newValue) {
        options.emitOriginatorKeyInfo = newValue;
    }
    
    /**
     * Get the option to emit the RecipientKeyInfo element within the AgreementMethod element.
     * 
     * @return the option value
     */
    public boolean emitRecipientKeyInfo() {
        return options.emitRecipientKeyInfo;
    }

    /**
     * Set the option to emit the RecipientKeyInfo element within the AgreementMethod element.
     * 
     * @param newValue the new option value
     */
    public void setEmitRecipientKeyInfo(final boolean newValue) {
        options.emitRecipientKeyInfo = newValue;
    }
    
    /**
     * Get the {@link KeyInfoGeneratorManager} instance to use to emit {@link OriginatorKeyInfo}.
     * 
     * @return the manager
     */
    public KeyInfoGeneratorManager getOriginatorKeyInfoGeneratorManager() {
        return options.originatorKeyInfoGeneratorManager;
    }

    /**
     * Set the {@link KeyInfoGeneratorManager} instance to use to emit {@link OriginatorKeyInfo}.
     * 
     * @param manager the manager instance
     */
    public void setOriginatorKeyInfoGeneratorManager(@Nullable final KeyInfoGeneratorManager manager) {
        options.originatorKeyInfoGeneratorManager = manager;
    }

    /**
     * Get the {@link KeyInfoGeneratorManager} instance to use to emit {@link RecipientKeyInfo}.
     * 
     * @return the manager
     */
    public KeyInfoGeneratorManager getRecipientKeyInfoGeneratorManager() {
        return options.recipientKeyInfoGeneratorManager;
    }

    /**
     * Set the {@link KeyInfoGeneratorManager} instance to use to emit {@link RecipientKeyInfo}.
     * 
     * @param manager the manager instance
     */
    public void setRecipientKeyInfoGeneratorManager(@Nullable final KeyInfoGeneratorManager manager) {
        options.recipientKeyInfoGeneratorManager = manager;
    }

    /** {@inheritDoc} */
    @Nonnull protected KeyAgreementOptions getOptions() {
        return options;
    }

    /** {@inheritDoc} */
    @Nonnull protected KeyAgreementOptions newOptions() {
        return new KeyAgreementOptions();
    }

    /**
     * An implementation of {@link KeyInfoGenerator} capable of handling the information 
     * contained within a {@link KeyAgreementCredential}.
     */
    public class KeyAgreementKeyInfoGenerator extends BasicKeyInfoGenerator {

        /** Class logger. */
        private final Logger log = LoggerFactory.getLogger(KeyAgreementKeyInfoGenerator.class);
        
        /** The set of options to be used by the generator.*/
        private KeyAgreementOptions options;
       
        /** Builder for AgreementMethod objects. */
        private final XMLObjectBuilder<AgreementMethod> agreementMethodBuilder;
       
        /**
         * Constructor.
         * 
         * @param newOptions the options to be used by the generator
         * @param type the KeyInfo element type
         */
        protected KeyAgreementKeyInfoGenerator(final KeyAgreementOptions newOptions,
                final Class<? extends KeyInfo> type) {
            super(newOptions, type);
            options = newOptions;
            
            agreementMethodBuilder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilderOrThrow(
                    AgreementMethod.DEFAULT_ELEMENT_NAME);
        }

        /** {@inheritDoc} */
        @Nullable public KeyInfo generate(@Nullable final Credential credential) throws SecurityException {
            if (credential == null) {
                log.warn("KeyAgreementKeyInfoGenerator was passed a null credential");
                return null;
            } else if (!(credential instanceof KeyAgreementCredential)) {
                log.warn("KeyAgreementKeyInfoGenerator was passed a credential that was not an instance of " 
                        + "KeyAgreementCredential: {}", credential.getClass().getName());
                return null;
            }
            final KeyAgreementCredential keyAgreementCredential = (KeyAgreementCredential) credential;
            
            KeyInfo keyInfo =  super.generate(credential);
            if (keyInfo == null) {
                keyInfo = buildKeyInfo();
            }
            
            final AgreementMethod agreementMethod =
                    agreementMethodBuilder.buildObject(AgreementMethod.DEFAULT_ELEMENT_NAME);
            
            agreementMethod.setAlgorithm(keyAgreementCredential.getAlgorithm());
            
            processAgreementParameters(keyInfo, agreementMethod, keyAgreementCredential);
            processOriginatorKeyInfo(keyInfo, agreementMethod, keyAgreementCredential);
            processRecipientKeyInfo(keyInfo, agreementMethod, keyAgreementCredential);
            
            keyInfo.getAgreementMethods().add(agreementMethod);
            
            return keyInfo;
        }

        /**
         * Process {@link KeyAgreementCredential#getParameters()}.
         * 
         * @param keyInfo the KeyInfo that is being built
         * @param agreementMethod the AgreementMethod that is being built
         * @param credential the Credential that is being processed
         * 
         * @throws SecurityException
         */
        private void processAgreementParameters(@Nonnull final KeyInfo keyInfo,
                @Nonnull final AgreementMethod agreementMethod, @Nonnull final KeyAgreementCredential credential)
                        throws SecurityException {
            
            // We emit these unconditionally, because key agreement on the recipient side 
            // realistically isn't possible without them
            
            for (final KeyAgreementParameter param : credential.getParameters()) {
                if (XMLExpressableKeyAgreementParameter.class.isInstance(param)) {
                    final XMLObject xmlParam = XMLExpressableKeyAgreementParameter.class.cast(param).buildXMLObject();
                    if (KANonce.class.isInstance(xmlParam)) {
                        agreementMethod.setKANonce(KANonce.class.cast(xmlParam));
                    } else if (xmlParam != null){
                        agreementMethod.getUnknownXMLObjects().add(xmlParam);
                    }
                }
            }
            
        }

        /**
         * Process the {@link KeyAgreementCredential#getOriginatorCredential()}.
         * 
         * @param keyInfo the KeyInfo that is being built
         * @param agreementMethod the AgreementMethod that is being built
         * @param credential the Credential that is being processed
         * 
         * @throws SecurityException
         */
        private void processOriginatorKeyInfo(@Nonnull final KeyInfo keyInfo,
                @Nonnull final AgreementMethod agreementMethod, @Nonnull final KeyAgreementCredential credential)
                        throws SecurityException {
            
            if (options.emitOriginatorKeyInfo) {
                if (options.originatorKeyInfoGeneratorManager == null) {
                    log.warn("KeyInfoGeneratorManager for OriginatorKeyInfo is null, can not process");
                    return;
                }
                if (credential.getOriginatorCredential() == null) {
                    log.warn("KeyAgreementCredential originator credential is null, can not process");
                    return;
                }

                final KeyInfo originatorKeyInfo = options.originatorKeyInfoGeneratorManager
                        .getFactory(credential.getOriginatorCredential())
                        .newInstance(OriginatorKeyInfo.class)
                        .generate(credential.getOriginatorCredential());
                if (originatorKeyInfo == null) {
                    log.warn("Failed to generate KeyInfo from KeyAgreementCredential originator Credential");
                    return;
                }
                if (!OriginatorKeyInfo.class.isInstance(originatorKeyInfo)) {
                    log.warn("KeyInfo generated from KeyAgreementCredential was not OriginatorKeyInfo");
                    return;
                }

                agreementMethod.setOriginatorKeyInfo(OriginatorKeyInfo.class.cast(originatorKeyInfo));
            }
        }

        /**
         * Process {@link KeyAgreementCredential#getRecipientCredential()}.
         * 
         * @param keyInfo the KeyInfo that is being built
         * @param agreementMethod the AgreementMethod that is being built
         * @param credential the Credential that is being processed
         * 
         * @throws SecurityException
         */
        private void processRecipientKeyInfo(@Nonnull final KeyInfo keyInfo,
                @Nonnull final AgreementMethod agreementMethod, @Nonnull final KeyAgreementCredential credential)
                        throws SecurityException {
            
            if (options.emitRecipientKeyInfo) {
                if (options.recipientKeyInfoGeneratorManager == null) {
                    log.warn("KeyInfoGeneratorManager for RecipientKeyInfo is null, can not process");
                    return;
                }
                if (credential.getRecipientCredential() == null) {
                    log.warn("KeyAgreementCredential recipient credential is null, can not process");
                    return;
                }
                
                final KeyInfo recipientKeyInfo = options.recipientKeyInfoGeneratorManager
                        .getFactory(credential.getRecipientCredential())
                        .newInstance(RecipientKeyInfo.class)
                        .generate(credential.getRecipientCredential());
                if (recipientKeyInfo == null) {
                    log.warn("Failed to generate KeyInfo from KeyAgreementCredential recipient Credential");
                    return;
                }
                if (!RecipientKeyInfo.class.isInstance(recipientKeyInfo)) {
                    log.warn("KeyInfo generated from KeyAgreementCredential was not RecipientKeyInfo");
                    return;
                }
                
                agreementMethod.setRecipientKeyInfo(RecipientKeyInfo.class.cast(recipientKeyInfo));
            }
            
        }
        
    }
    
    /**
    * Options to be used in the production of a {@link KeyInfo} from an {@link KeyAgreementCredential}.
    */
   protected class KeyAgreementOptions extends BasicOptions {
       
       /** Emit the OriginatorKeyInfo element within AgreementMethod. */
       private boolean emitOriginatorKeyInfo;
       
       /** Emit the RecipientKeyInfo element within AgreementMethod. */
       private boolean emitRecipientKeyInfo;
       
       /** KeyInfo generator manager for OriginatorKeyInfo elements. */
       private KeyInfoGeneratorManager  originatorKeyInfoGeneratorManager;
       
       /** KeyInfo generator manager for RecipientKeyInfo elements. */
       private KeyInfoGeneratorManager recipientKeyInfoGeneratorManager;
       
       /** Constructor. */
       protected KeyAgreementOptions() {
           emitOriginatorKeyInfo = true;
           emitRecipientKeyInfo = true;
           
           // TODO We can't default the general ones below until KeyInfoSupport supports PublicKey -> ECKeyValue
           // For now limit defaults to emit DEREncodedKeyValue only
           final KeyInfoGeneratorManager managerDEROnly = new KeyInfoGeneratorManager();
           final BasicKeyInfoGeneratorFactory basicFactoryDEROnly = new BasicKeyInfoGeneratorFactory();
           basicFactoryDEROnly.setEmitPublicDEREncodedKeyValue(true);
           managerDEROnly.registerFactory(basicFactoryDEROnly);
           originatorKeyInfoGeneratorManager = managerDEROnly;
           recipientKeyInfoGeneratorManager = managerDEROnly;
           /*
           originatorKeyInfoGeneratorManager =
                   DefaultSecurityConfigurationBootstrap.buildBasicKeyInfoGeneratorManager().getDefaultManager();
           
           recipientKeyInfoGeneratorManager =
                   DefaultSecurityConfigurationBootstrap.buildBasicKeyInfoGeneratorManager().getDefaultManager();
            */
       }
       
       /** {@inheritDoc} */
       protected KeyAgreementOptions clone() {
           final KeyAgreementOptions clonedOptions = (KeyAgreementOptions) super.clone();
           
           return clonedOptions;
       }
       
   }

}