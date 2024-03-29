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

package org.opensaml.saml.ext.reqattr;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML V2.0 Protocol Extension For Requesting Attributes Per Request.
 */
public interface RequestedAttributes extends SAMLObject {

    /** Name of the element inside the Extensions. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "RequestedAttributes";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20PREQ_ATTR_NS, 
                    DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20PREQ_ATTRR_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "RequestedAttributesType";
    
    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML20PREQ_ATTR_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20PREQ_ATTRR_PREFIX);
    
    /** Local name of the supportsRequestedAttributes attribute. */
    @Nonnull @NotEmpty static final String SUPPORTS_REQUESTED_ATTRIBUTES_LOCAL_NAME = "supportsRequestedAttributes";

    /** QName of the XSI type. */
    @Nonnull static final QName SUPPORTS_REQUESTED_ATTRIBUTES = new QName(SAMLConstants.SAML20PREQ_ATTR_NS, 
            SUPPORTS_REQUESTED_ATTRIBUTES_LOCAL_NAME, SAMLConstants.SAML20PREQ_ATTRR_PREFIX);

    /**
     * Gets a list of child {@link RequestedAttribute}s.
     * 
     * @return list of child RequestedAttribute s
     */
    @Nonnull @Live List<RequestedAttribute> getRequestedAttributes();

}