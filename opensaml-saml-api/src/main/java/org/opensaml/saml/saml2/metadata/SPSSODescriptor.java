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

import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Metadata SPSSODescriptorType.
 */
public interface SPSSODescriptor extends SSODescriptor {
    
    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "SPSSODescriptor";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "SPSSODescriptorType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** "AuthnRequestsSigned" attribute's local name. */
    @Nonnull @NotEmpty static final String AUTH_REQUESTS_SIGNED_ATTRIB_NAME = "AuthnRequestsSigned";

    /** "WantAssertionsSigned" attribute's local name. */
    @Nonnull @NotEmpty static final String WANT_ASSERTIONS_SIGNED_ATTRIB_NAME = "WantAssertionsSigned";

    /**
     * Gets whether this service signs AuthN requests.
     * 
     * @return true of this service signs requests, false if not
     */
    @Nullable Boolean isAuthnRequestsSigned();

    /**
     * Gets whether this service signs AuthN requests.
     * 
     * @return true of this service signs requests, false if not
     */
    @Nullable XSBooleanValue isAuthnRequestsSignedXSBoolean();

    /**
     * Sets whether this service signs AuthN requests. Boolean values will be marshalled to either "true" or "false".
     * 
     * @param newIsSigned true of this service signs requests, false if not
     */
    void setAuthnRequestsSigned(@Nullable final Boolean newIsSigned);

    /**
     * Sets whether this service signs AuthN requests.
     * 
     * @param newIsSigned true of this service signs requests, false if not
     */
    void setAuthnRequestsSigned(@Nullable final XSBooleanValue newIsSigned);

    /**
     * Gets whether this service wants assertions signed.
     * 
     * @return true if this service wants assertions signed, false if not
     */
    @Nullable Boolean getWantAssertionsSigned();

    /**
     * Gets whether this service wants assertions signed.
     * 
     * @return true if this service wants assertions signed, false if not
     */
    @Nullable XSBooleanValue getWantAssertionsSignedXSBoolean();

    /**
     * Sets whether this service wants assertions signed. Boolean values will be marshalled to either "true" or "false".
     * 
     * @param newWantAssestionSigned true if this service wants assertions signed, false if not
     */
    void setWantAssertionsSigned(@Nullable final Boolean newWantAssestionSigned);

    /**
     * Sets whether this service wants assertions signed.
     * 
     * @param newWantAssestionSigned true if this service wants assertions signed, false if not
     */
    void setWantAssertionsSigned(@Nullable final XSBooleanValue newWantAssestionSigned);

    /**
     * Gets a list of assertion consumer service {@link Endpoint}s for this service.
     * 
     * @return list of assertion consumer service {@link Endpoint}s for this service
     */
    @Nonnull @Live List<AssertionConsumerService> getAssertionConsumerServices();

    /**
     * Gets the default assertion consumer service.
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
     * @return default assertion consumer service (or null if there are no assertion consumer services defined)
     */
    @Nullable AssertionConsumerService getDefaultAssertionConsumerService();

    /**
     * Gets a list of attribute consuming service descriptors for this service.
     * 
     * @return list of attribute consuming service descriptors for this service
     */
    @Nonnull @Live List<AttributeConsumingService> getAttributeConsumingServices();

    /**
     * Gets the default attribute consuming service.
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
     * @return default attribute consuming service (or null if there are no attribute consuming services defined)
     */
    @Nullable AttributeConsumingService getDefaultAttributeConsumingService();
}
