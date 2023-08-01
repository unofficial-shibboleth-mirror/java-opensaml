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

package org.opensaml.saml.saml2.core;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Core EncryptedElementType.
 */
public interface EncryptedElementType extends SAMLObject {
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "EncryptedElementType"; 
        
    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = 
        new QName(SAMLConstants.SAML20_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
    
    /**
     * Get the EncryptedData child element.
     * 
     * @return the EncryptedData child element
     */
    @Nullable EncryptedData getEncryptedData();
    
    /**
     * Set the EncryptedData child element.
     * 
     * @param newEncryptedData the new EncryptedData child element
     */
    void setEncryptedData(@Nullable final EncryptedData newEncryptedData);
    
    /**
     * A list of EncryptedKey child elements.
     * 
     * @return a list of EncryptedKey child elements
     */
    @Nonnull @Live List<EncryptedKey> getEncryptedKeys();

}
