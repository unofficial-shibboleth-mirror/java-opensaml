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

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Metadata AuthnAuthorityDescriptor.
 */
public interface AuthnAuthorityDescriptor extends SAMLObject, RoleDescriptor {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "AuthnAuthorityDescriptor";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "AuthnAuthorityDescriptorType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /**
     * Gets the list of authentication query services for this authority.
     * 
     * @return list of authentication query services
     */
    @Nonnull @Live List<AuthnQueryService> getAuthnQueryServices();

    /**
     * Gets the list of assertion ID request services for this authority.
     * 
     * @return assertion ID request services for this authority
     */
    @Nonnull @Live List<AssertionIDRequestService> getAssertionIDRequestServices();

    /**
     * Gets the list of supported name ID formats for this authority.
     * 
     * @return supported name ID formats for this authority
     */
    @Nonnull @Live List<NameIDFormat> getNameIDFormats();
}
