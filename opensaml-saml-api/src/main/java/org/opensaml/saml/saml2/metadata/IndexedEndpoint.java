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

import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Metadata IndexedEndpoint.
 */
public interface IndexedEndpoint extends Endpoint {

    /** Local name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "IndexedEndpoint";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "IndexedEndpointType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** index attribute name. */
    @Nonnull @NotEmpty static final String INDEX_ATTRIB_NAME = "index";

    /** isDeault attribute name. */
    @Nonnull @NotEmpty static final String IS_DEFAULT_ATTRIB_NAME = "isDefault";

    /**
     * Gets the index of the endpoint.
     * 
     * @return index of the endpoint
     */
    @Nullable Integer getIndex();

    /**
     * Sets the index of the endpoint.
     * 
     * @param index index of the endpoint
     */
    void setIndex(@Nullable final Integer index);

    /**
     * Gets whether this is the default endpoint in a list.
     * 
     * @return whether this is the default endpoint in a list
     */
    @Nullable Boolean isDefault();

    /**
     * Gets whether this is the default endpoint in a list.
     * 
     * @return whether this is the default endpoint in a list
     */
    @Nullable XSBooleanValue isDefaultXSBoolean();

    /**
     * Sets whether this is the default endpoint in a list. Boolean values will be marshalled to either "true" or
     * "false".
     * 
     * @param newIsDefault whether this is the default endpoint in a list
     */
    void setIsDefault(@Nullable final Boolean newIsDefault);

    /**
     * Sets whether this is the default endpoint in a list.
     * 
     * @param newIsDefault whether this is the default endpoint in a list
     */
    void setIsDefault(@Nullable final XSBooleanValue newIsDefault);
}
