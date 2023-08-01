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

package org.opensaml.saml.saml2.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Core IDPEntry.
 */
public interface IDPEntry extends SAMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "IDPEntry";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20P_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20P_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "IDPEntryType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20P_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20P_PREFIX);

    /** ProviderID attribute name. */
    @Nonnull @NotEmpty static final String PROVIDER_ID_ATTRIB_NAME = "ProviderID";

    /** Name attribute name. */
    @Nonnull @NotEmpty static final String NAME_ATTRIB_NAME = "Name";

    /** Loc attribute name. */
    @Nonnull @NotEmpty static final String LOC_ATTRIB_NAME = "Loc";

    /**
     * Gets ProviderID URI.
     * 
     * @return the ProviderID URI
     */
    @Nullable String getProviderID();

    /**
     * Sets the ProviderID URI.
     * 
     * @param newProviderID the new ProviderID URI
     */
    void setProviderID(@Nullable final String newProviderID);

    /**
     * Gets the Name value.
     * 
     * @return the Name value
     */
    @Nullable String getName();

    /**
     * Sets the Name value.
     * 
     * @param newName the Name value
     */
    void setName(@Nullable final String newName);

    /**
     * Gets the Loc value.
     * 
     * @return the Loc value
     */
    @Nullable String getLoc();

    /**
     * Sets the Loc value.
     * 
     * @param newLoc the new Loc value
     */
    void setLoc(@Nullable final String newLoc);

}
