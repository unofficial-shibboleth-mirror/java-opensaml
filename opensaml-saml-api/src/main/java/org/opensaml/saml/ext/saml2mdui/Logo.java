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

package org.opensaml.saml.ext.saml2mdui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.LocalizedURI;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Localized logo type.
 * 
 * 
 * @author RDW 27/Aug/2010
 * 
 * See IdP Discovery and Login UI Metadata Extension Profile.
 *  
 */
public interface Logo extends LocalizedURI, SAMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "Logo";
    
    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MDUI_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MDUI_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "LogoType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML20MDUI_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20MDUI_PREFIX);
    
    /** Attribute label. */
    @Nonnull @NotEmpty static final String HEIGHT_ATTR_NAME = "height";

    /** Attribute label. */
    @Nonnull @NotEmpty static final String WIDTH_ATTR_NAME = "width";

    /**
     * Get the height of the logo.
     * @return the height of the logo
     */
    @Nullable Integer getHeight();
    
    /**
     * Sets the height of the logo.
     * @param newHeight the height of the logo
     */
    void setHeight(@Nullable final Integer newHeight);

    /**
     * Get the width of the logo.
     * @return the width of the logo
     */
    @Nullable Integer getWidth();
    
    /**
     * Sets the width of the logo.
     * @param newWidth the height of the logo
     */
    void setWidth(@Nullable final Integer newWidth);
}