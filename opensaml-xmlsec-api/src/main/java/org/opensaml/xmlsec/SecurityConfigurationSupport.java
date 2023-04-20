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

package org.opensaml.xmlsec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.config.ConfigurationService;

/**
 * Helper methods for working with security configuration.
 */
public final class SecurityConfigurationSupport {
    
    /** Constructor. */
    private SecurityConfigurationSupport() { }
    
    /**
     * Get the global {@link DecryptionConfiguration} instance.
     * 
     * @return the global decryption configuration
     */
    @Nullable public static DecryptionConfiguration getGlobalDecryptionConfiguration() {
        return ConfigurationService.get(DecryptionConfiguration.class);
    }

    /**
     * Get the global {@link DecryptionConfiguration} instance, raising an exception if absent.
     * 
     * @return the global decryption configuration
     * 
     * @since 5.0.0
     */
    @Nonnull public static DecryptionConfiguration ensureGlobalDecryptionConfiguration() {
        return ConfigurationService.ensure(DecryptionConfiguration.class);
    }
    
    /**
     * Get the global {@link EncryptionConfiguration} instance.
     * 
     * @return the global encryption configuration
     */
    @Nullable public static EncryptionConfiguration getGlobalEncryptionConfiguration() {
        return ConfigurationService.get(EncryptionConfiguration.class);
    }
    
    /**
     * Get the global {@link EncryptionConfiguration} instance, raising an exception if absent.
     * 
     * @return the global encryption configuration
     * 
     * @since 5.0.0
     */
    @Nonnull public static EncryptionConfiguration ensureGlobalEncryptionConfiguration() {
        return ConfigurationService.ensure(EncryptionConfiguration.class);
    }
    
    /**
     * Get the global {@link SignatureSigningConfiguration} instance.
     * 
     * @return the global signature signing configuration
     */
    @Nullable public static SignatureSigningConfiguration getGlobalSignatureSigningConfiguration() {
        return ConfigurationService.get(SignatureSigningConfiguration.class);
    }

    /**
     * Get the global {@link SignatureSigningConfiguration} instance, raising an exception if absent.
     * 
     * @return the global signature signing configuration
     * 
     * @since 5.0.0
     */
    @Nonnull public static SignatureSigningConfiguration ensureGlobalSignatureSigningConfiguration() {
        return ConfigurationService.ensure(SignatureSigningConfiguration.class);
    }

    /**
     * Get the global {@link SignatureValidationConfiguration} instance.
     * 
     * @return the global signature validation configuration
     */
    @Nullable public static SignatureValidationConfiguration getGlobalSignatureValidationConfiguration() {
        return ConfigurationService.get(SignatureValidationConfiguration.class);
    }
    
    /**
     * Get the global {@link SignatureValidationConfiguration} instance, raising an exception if absent.
     * 
     * @return the global signature validation configuration
     * 
     * @since 5.0.0
     */
    @Nonnull public static SignatureValidationConfiguration ensureGlobalSignatureValidationConfiguration() {
        return ConfigurationService.ensure(SignatureValidationConfiguration.class);
    }

}