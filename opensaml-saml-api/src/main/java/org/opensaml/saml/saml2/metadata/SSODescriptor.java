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

import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Metadata SSODescriptor.
 */
public interface SSODescriptor extends RoleDescriptor {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "SSODescriptor";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "SSODescriptorType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /**
     * Gets a list of artifact resolution services for this service.
     * 
     * @return list of artifact resolution services for this service
     */
    @Nonnull @Live List<ArtifactResolutionService> getArtifactResolutionServices();

    /**
     * Gets the default artifact resolution service.
     * 
     * <p>
     * The selection algorithm used is:
     * </p>
     * <ol>
     * <li>Select the first service with an explicit <code>isDefault=true</code></li>
     * <li>Select the first service with no explicit <code>isDefault</code></li>
     * <li>Select the first service</li>
     * </ol>
     * 
     * @return default artifact resolution service (or null if there are no artifact resolution services defined)
     * 
     */
    @Nullable ArtifactResolutionService getDefaultArtifactResolutionService();

    /**
     * Gets a list of single logout services for this service.
     * 
     * @return list of single logout services for this service
     */
    @Nonnull @Live List<SingleLogoutService> getSingleLogoutServices();

    /**
     * Gets a list of manage NameId services for this service.
     * 
     * @return list of manage NameId services for this service
     */
    @Nonnull @Live List<ManageNameIDService> getManageNameIDServices();

    /**
     * Gets the list of NameID formats this service supports.
     * 
     * @return NameID formats this service supports
     */
    @Nonnull @Live List<NameIDFormat> getNameIDFormats();
}
