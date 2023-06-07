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

package org.opensaml.saml.security;

import javax.annotation.Nullable;

import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.xmlsec.encryption.support.KeyAgreementEncryptionConfiguration;

/**
 * A specialization of {@link KeyAgreementEncryptionConfiguration} that can hold configuration
 * specific to the user of SAML metadata.
 */
public class SAMLMetadataKeyAgreementEncryptionConfiguration extends KeyAgreementEncryptionConfiguration {
    
    /** Options for whether to use symmetric key wrap with credentials from SAML metadata. */
    public enum KeyWrap {
        
        /** Always use key wrap for metadata credentials. */
        Always,
        
        /** Never use key wrap for metadata credentials. */
        Never,
        
        /** Use key wrap if no indication is given via {@link EncryptionMethod} elements within
         * the associated {@link KeyDescriptor} element. See also {@link #Default}. */
        IfNotIndicated,
        
        /** Default behavior, which is to enable key wrap or not based on the presence or absence respectively of
         * {@link EncryptionMethod} elements in the associated {@link KeyDescriptor} containing 
         * symmetric key wrap algorithms.
         * The presence of any symmetric key wrap algorithms (after runtime support and include/exclude filtering)
         * will enable key wrap. Otherwise, key wrap will be disabled.
         */
        Default;
    }
    
    /** Option which determines whether symmetric key wrap is to be used with metadata credentials. */
    @Nullable private KeyWrap metadataUseKeyWrap;
    
    /**
     * Get the option which determines whether symmetric key wrap is to be used with metadata credentials.
     * 
     * @return the configured optiona value, or null if not explicitly configured
     */
    @Nullable public KeyWrap getMetadataUseKeyWrap() {
        return metadataUseKeyWrap;
    }
    
    /**
     * Set the option which determines whether symmetric key wrap is to be used with metadata credentials.
     * 
     * @param option the new option value
     */
    public void setMetadataUseKeyWrap(@Nullable final KeyWrap option) {
        metadataUseKeyWrap = option;
    }
    
}