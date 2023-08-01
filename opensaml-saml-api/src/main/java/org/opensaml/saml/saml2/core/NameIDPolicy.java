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

import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Core NameIDPolicy.
 */
public interface NameIDPolicy extends SAMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "NameIDPolicy";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20P_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20P_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "NameIDPolicyType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20P_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20P_PREFIX);

    /** Format attribute name. */
    @Nonnull @NotEmpty static final String FORMAT_ATTRIB_NAME = "Format";

    /** SPNameQualifier attribute name. */
    @Nonnull @NotEmpty static final String SP_NAME_QUALIFIER_ATTRIB_NAME = "SPNameQualifier";

    /** AllowCreate attribute name. */
    @Nonnull @NotEmpty static final String ALLOW_CREATE_ATTRIB_NAME = "AllowCreate";

    /**
     * Gets the format of the NameIDPolicy.
     * 
     * @return the format of the NameIDPolicy
     */
    @Nullable String getFormat();

    /**
     * Sets the format of the NameIDPolicy.
     * 
     * @param newFormat the format of the NameIDPolicy
     */
    void setFormat(@Nullable final String newFormat);

    /**
     * Gets the SPNameQualifier value.
     * 
     * @return the SPNameQualifier value
     */
    @Nullable String getSPNameQualifier();

    /**
     * Sets the SPNameQualifier value.
     * 
     * @param newSPNameQualifier the SPNameQualifier value
     */
    void setSPNameQualifier(@Nullable final String newSPNameQualifier);

    /**
     * Gets the AllowCreate value.
     * 
     * @return the AllowCreate value
     */
    @Nullable Boolean getAllowCreate();

    /**
     * Gets the AllowCreate value.
     * 
     * @return the AllowCreate value
     */
    @Nullable XSBooleanValue getAllowCreateXSBoolean();

    /**
     * Sets the AllowCreate value. Boolean values will be marshalled to either "true" or "false".
     * 
     * @param newAllowCreate the AllowCreate value
     */
    void setAllowCreate(@Nullable final Boolean newAllowCreate);

    /**
     * Sets the AllowCreate value.
     * 
     * @param newAllowCreate the AllowCreate value
     */
    void setAllowCreate(@Nullable final XSBooleanValue newAllowCreate);

}