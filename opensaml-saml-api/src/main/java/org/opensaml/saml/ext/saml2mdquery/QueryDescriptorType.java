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

package org.opensaml.saml.ext.saml2mdquery;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Metadata extension QueryDescriptorType.
 */
public interface QueryDescriptorType extends RoleDescriptor {
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "QueryDescriptorType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MDQUERY_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MDQUERY_PREFIX);
    
    /** "WantAssertionSigned" attribute's local name. */
    @Nonnull @NotEmpty static final String WANT_ASSERTIONS_SIGNED_ATTRIB_NAME = "WantAssertionsSigned";
    
    /**
     * Gets whether assertions to this endpoint should be signed.
     * 
     * @return whether assertions to this endpoint should be signed
     */
    @Nullable Boolean getWantAssertionsSigned();
    
    /**
     * Gets whether assertions to this endpoint should be signed.
     * 
     * @return whether assertions to this endpoint should be signed
     */
    @Nullable XSBooleanValue getWantAssertionsSignedXSBoolean();
    
    /**
     * Sets whether assertions to this endpoint should be signed.
     * 
     * @param newWantAssertionsSigned whether assertions to this endpoint should be signed
     */
    void setWantAssertionsSigned(@Nullable final Boolean newWantAssertionsSigned);
    
    /**
     * Sets whether assertions to this endpoint should be signed.
     * 
     * @param newWantAssertionsSigned whether assertions to this endpoint should be signed
     */
    void setWantAssertionsSigned(@Nullable final XSBooleanValue newWantAssertionsSigned);
    
    /**
     * Gets the list of name ID formats supported by this query service.
     * 
     * @return the list of name ID formats supported by this query service
     */
    @Nonnull @Live List<NameIDFormat> getNameIDFormat();
}
