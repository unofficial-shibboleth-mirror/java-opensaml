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
import org.opensaml.saml.saml2.core.Attribute;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Metadata RequestedAttribute.
 */
public interface RequestedAttribute extends Attribute {

    /** Local name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "RequestedAttribute";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "RequestedAttributeType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** "isRequired" attribute's local name. */
    @Nonnull @NotEmpty static final String IS_REQUIRED_ATTRIB_NAME = "isRequired";

    /**
     * Checks to see if this requested attribute is also required.
     * 
     * @return true if this attribute is required
     */
    @Nullable Boolean isRequired();

    /**
     * Checks to see if this requested attribute is also required.
     * 
     * @return true if this attribute is required
     */
    @Nullable XSBooleanValue isRequiredXSBoolean();

    /**
     * Sets if this requested attribute is also required. Boolean values will be marshalled to either "true" or "false".
     * 
     * @param newIsRequire true if this attribute is required
     */
    void setIsRequired(@Nullable final Boolean newIsRequire);

    /**
     * Sets if this requested attribute is also required.
     * 
     * @param newIsRequire true if this attribute is required
     */
    void setIsRequired(@Nullable final XSBooleanValue newIsRequire);
}