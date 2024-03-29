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
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Attribute;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Metadata AttributeAuthorityDescriptor.
 */
public interface AttributeAuthorityDescriptor extends SAMLObject, RoleDescriptor {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "AttributeAuthorityDescriptor";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "AttributeAuthorityDescriptorType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /**
     * Gets a list of attribute service {@link Endpoint}s for this authority.
     * 
     * @return list of attributes services
     */
    @Nonnull @Live List<AttributeService> getAttributeServices();

    /**
     * Gets a list of Assertion ID request services.
     * 
     * @return list of Assertion ID request services
     */
    @Nonnull @Live List<AssertionIDRequestService> getAssertionIDRequestServices();

    /**
     * Gets a list of NameID formats supported by this authority.
     * 
     * @return list of NameID formats supported by this authority
     */
    @Nonnull @Live List<NameIDFormat> getNameIDFormats();

    /**
     * Gets a list of Attribute profiles supported by this authority.
     * 
     * @return list of Attribute profiles supported by this authority
     */
    @Nonnull @Live List<AttributeProfile> getAttributeProfiles();

    /**
     * Gets the list of attribute available from this authority.
     * 
     * @return list of attribute available from this authority
     */
    @Nonnull @Live List<Attribute> getAttributes();
}
