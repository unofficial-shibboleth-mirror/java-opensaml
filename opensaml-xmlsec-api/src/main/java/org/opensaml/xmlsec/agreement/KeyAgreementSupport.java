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

package org.opensaml.xmlsec.agreement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.EncryptedType;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.KeySize;

/**
 * Support for key agreement operations.
 */
public final class KeyAgreementSupport {
    
    /** Constructor. */
    private KeyAgreementSupport() {}

    
    /**
     * Get the global {@link KeyAgreementProcessorRegistry} instance.
     * 
     * @return the global processor registry, or null if nothing registered
     */
    @Nullable public static KeyAgreementProcessorRegistry getGlobalProcessorRegistry() {
        return ConfigurationService.get(KeyAgreementProcessorRegistry.class);
    }
    
    /**
     * Look for an explicit key size via an {@link AgreementMethod}'s grandparent's {@link EncryptionMethod}
     * child's {@link KeySize} child element.
     * 
     * @param agreementMethod the AgreementMethod to process
     * 
     * @return the key size, or null if not present
     */
    @Nullable public static Integer getExplicitKeySize(@Nonnull final AgreementMethod agreementMethod) {
        if (agreementMethod.getParent() == null || agreementMethod.getParent().getParent() == null
                || ! EncryptedType.class.isInstance(agreementMethod.getParent().getParent())) {
            return null;
        }
        
        final EncryptedType et = EncryptedType.class.cast(agreementMethod.getParent().getParent());
        if (et.getEncryptionMethod() == null || et.getEncryptionMethod().getKeySize() == null) {
            return null;
        }
        
        return et.getEncryptionMethod().getKeySize().getValue();
    }
}
