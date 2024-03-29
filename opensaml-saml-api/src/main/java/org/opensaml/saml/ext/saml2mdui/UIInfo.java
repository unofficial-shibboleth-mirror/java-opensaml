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
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * See IdP Discovery and Login UI Metadata Extension Profile.
 *
 * @author Rod Widdowson August 2010
 * 
 * Reflects the UINFO in the IdP Discovery and Login UI Metadata Extension Profile,
 *
 */
public interface UIInfo extends SAMLObject {

    /** Name of the element inside the Extensions. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "UIInfo";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MDUI_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MDUI_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "UIInfoType";
    
    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML20MDUI_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20MDUI_PREFIX);
    
    /** 
     * Get the Display Names
     * 
     * <p>
     * The &lt;DisplayName&gt; element specifies a set of localized names fit for 
     * display to users.  Such names are meant to allow a user to distinguish 
     * and identify the entity acting in a particular role.
     * </p>
     *
     * @return the names
     */
    @Nonnull @Live List <DisplayName> getDisplayNames();
    
    /** 
     * Get the keywords.
     *
     * <p>
     * The &lt;Keywords&gt; element specifies a set of keywords associated with the entity.
     * </p>
     *  
     * @return a list of keywords
     */
    @Nonnull @Live List <Keywords> getKeywords();
    
    /**
     * Return the descriptions.
     *
     * <p>
     * The &lt;Description&gt; element specifies a set of brief, localized descriptions 
     * fit for display to users. In the case of service providers this SHOULD be a 
     * description of the service being offered.  In the case of an identity provider 
     * this SHOULD be a description of the community serviced.  In all cases this text 
     * SHOULD be standalone, meaning it is not meant to be filled in to some template 
     * text (e.g. 'This service offers $description').
     * </p>
     *
     * @return descriptions
     */
    @Nonnull @Live List <Description> getDescriptions();
    
    /** 
     * Get the logos.
     * 
     * <p>The &lt;Logo&gt; element specifies a set of localized logos fit for display to users.</p>
     *  
     * @return a list of logos
     */
    @Nonnull @Live List <Logo> getLogos();
    
    /** 
     * Get the URLs.
     *
     * <p>
     * The &lt;InformationURL&gt; specifies URLs to localized information, about the entity 
     * acting in a given role, meant to be viewed by users.  The contents found at 
     * these URLs SHOULD give a more complete set of information about than what is 
     * provided by the &lt;Description&gt; element. 
     * </p>
     *
     * @return the URLs
     */
    @Nonnull @Live List <InformationURL> getInformationURLs();
    
    /**
     * Get the Privacy Statement URLs.
     *
     * <p>
     * The &lt;PrivacyStatementURL&gt; specifies URLs to localized privacy statements.  
     * Such statements are meant to provide a user with information about how 
     * information will be used and managed by the entity.
     * </p>
     *
     * @return the URLs
     */
    @Nonnull @Live List <PrivacyStatementURL> getPrivacyStatementURLs(); 
    
    /**
     * Get the list of all children of this element.
     * 
     * @return the list of all XMLObject children
     */
    @Nonnull @Live List <XMLObject> getXMLObjects(); 
    
    /**
     * Get the list of all children of this element which have the specified name or type.
     * 
     * @param typeOrName the element name or type of the desired list of elements
     * 
     * @return the list of all XMLObject children
     */
    @Nonnull @Live List <XMLObject> getXMLObjects(@Nonnull final QName typeOrName); 

}
