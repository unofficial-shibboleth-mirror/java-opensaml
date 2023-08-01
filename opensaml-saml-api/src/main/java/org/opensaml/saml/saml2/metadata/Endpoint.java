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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.ElementExtensibleXMLObject;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Metadata Endpoint data type interface.
 */
public interface Endpoint extends SAMLObject, ElementExtensibleXMLObject, AttributeExtensibleXMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "Endpoint";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "EndpointType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** "Binding" attribute name. */
    @Nonnull @NotEmpty static final String BINDING_ATTRIB_NAME = "Binding";

    /** "Location" attribute name. */
    @Nonnull @NotEmpty static final String LOCATION_ATTRIB_NAME = "Location";

    /** "ResponseLocation" attribute name. */
    @Nonnull @NotEmpty static final String RESPONSE_LOCATION_ATTRIB_NAME = "ResponseLocation";

    /**
     * Gets the URI identifier for the binding supported by this Endpoint.
     * 
     * @return the URI identifier for the binding supported by this Endpoint
     */
    @Nullable String getBinding();

    /**
     * Sets the URI identifier for the binding supported by this Endpoint.
     * 
     * @param binding the URI identifier for the binding supported by this Endpoint
     */
    void setBinding(@Nullable final String binding);

    /**
     * Gets the URI, usually a URL, for the location of this Endpoint.
     * 
     * @return the location of this Endpoint
     */
    @Nullable String getLocation();

    /**
     * Sets the URI, usually a URL, for the location of this Endpoint.
     * 
     * @param location the location of this Endpoint
     */
    void setLocation(@Nullable final String location);

    /**
     * Gets the URI, usually a URL, responses should be sent to this for this Endpoint.
     * 
     * @return the URI responses should be sent to this for this Endpoint
     */
    @Nullable String getResponseLocation();

    /**
     * Sets the URI, usually a URL, responses should be sent to this for this Endpoint.
     * 
     * @param location the URI responses should be sent to this for this Endpoint
     */
    void setResponseLocation(@Nullable final String location);
}
