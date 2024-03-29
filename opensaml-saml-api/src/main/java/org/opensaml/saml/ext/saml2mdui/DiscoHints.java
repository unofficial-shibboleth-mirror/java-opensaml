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
 * @author RDW 27/Aug/2010
 * 
 * Reflects the DiscoHints 
 */
public interface DiscoHints extends SAMLObject {

    /** Name of the element inside the Extensions. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "DiscoHints";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MDUI_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MDUI_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "DiscoHintsType";
           
    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML20MDUI_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20MDUI_PREFIX);
    
    /** 
     * The &lt;IPHint&gt; element specifies a set of [CIDR] blocks associated with, 
     * or serviced by, the entity.  Both IPv4 and IPv6 CIDR blocks MUST be supported.
     * 
     * @return hints
     */
    @Nonnull @Live List <IPHint> getIPHints();
    
    /**
     * The &lt;DomainHint&gt; element specifies a set of DNS domains associated with, 
     * or serviced by, the entity.
     *
     * @return hints.
     */
    @Nonnull @Live List <DomainHint> getDomainHints();
    
    /**
     * The &lt;GeolocationHint&gt; element specifies the geographic coordinates associated 
     * with, or serviced by, the entity.  Coordinates are given in decimal form using
     * the World Geodetic System (2d) coordinate system.
     * 
     * @return hints
     */
    @Nonnull @Live List <GeolocationHint> getGeolocationHints();
    
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
