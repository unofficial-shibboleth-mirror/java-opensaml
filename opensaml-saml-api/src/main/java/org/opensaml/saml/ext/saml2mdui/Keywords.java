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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.LangBearing;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * DisplayName.
 *
 * See IdP Discovery and Login UI Metadata Extension Profile.
 *
 * @author Rod Widdowson August 2010
 * 
 * Reflects the Description in the IdP Discovery and Login UI Metadata Extension Profile.
 * 
 */
public interface Keywords extends SAMLObject, LangBearing  {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "Keywords";
    
    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MDUI_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MDUI_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "KeywordsType";
           
    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML20MDUI_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20MDUI_PREFIX);
    
    /**
     * Gets the keywords.
     * 
     * @return the keywords
     */
    @Nullable @NotLive @Unmodifiable List<String> getKeywords();
    
    /**
     * Sets the keywords.
     * 
     * @param val The keywords
     */
    void setKeywords(@Nullable final List<String> val);
   
}