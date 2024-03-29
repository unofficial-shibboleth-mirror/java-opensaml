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

package org.opensaml.saml.saml2.metadata;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.signature.KeyInfo;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Metadata KeyDescriptor.
 */
public interface KeyDescriptor extends SAMLObject {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "KeyDescriptor";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "KeyDescriptorType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** "use" attribute's local name. */
    @Nonnull @NotEmpty static final String USE_ATTRIB_NAME = "use";

    /**
     * Gets the use of this key.
     * 
     * @return the use of this key
     */
    @Nullable UsageType getUse();

    /**
     * Sets the use of this key.
     * 
     * @param newType the use of this key
     */
    void setUse(@Nullable final UsageType newType);

    /**
     * Gets information about the key, including the key itself.
     * 
     * @return information about the key, including the key itself
     */
    @Nullable KeyInfo getKeyInfo();

    /**
     * Sets information about the key, including the key itself.
     * 
     * @param newKeyInfo information about the key, including the key itself
     */
    void setKeyInfo(@Nullable final KeyInfo newKeyInfo);

    /**
     * Gets the encryption methods that are supported by the entity.
     * 
     * @return the encryption methods that are supported by the entity
     */
    @Nonnull @Live List<EncryptionMethod> getEncryptionMethods();
}
