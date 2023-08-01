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

package org.opensaml.saml.ext.saml2mdrpi;

import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Representation of the <code>&lt;mdrpi:RegistrationInfo&gt;</code> element.
 *
 * @see <a
 * href="http://docs.oasis-open.org/security/saml/Post2.0/saml-metadata-rpi/v1.0/">http://docs.oasis-open.org/security
 * /saml/Post2.0/saml-metadata-rpi/v1.0/</a>
 */
public interface RegistrationInfo extends SAMLObject {

    /** Name of the element inside the Extensions. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "RegistrationInfo";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MDRPI_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MDRPI_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "RegistrationInfoType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML20MDRPI_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20MDRPI_PREFIX);

    /** registrationAuthority attribute name. */
    @Nonnull @NotEmpty static String REGISTRATION_AUTHORITY_ATTRIB_NAME = "registrationAuthority";

    /** registrationInstant attribute name. */
    @Nonnull @NotEmpty static String REGISTRATION_INSTANT_ATTRIB_NAME = "registrationInstant";

    /** QName of the registrationInstant attribute. */
    @Nonnull static final QName REGISTRATION_INSTANT_ATTRIB_QNAME =
            new QName(null, REGISTRATION_INSTANT_ATTRIB_NAME, XMLConstants.DEFAULT_NS_PREFIX);
    
    /**
     * Get the registration authority.
     * 
     * @return the registration authority
     */
    @Nullable String getRegistrationAuthority();

    /**
     * Set the registration authority.
     * 
     * @param authority the registration authority
     */
    void setRegistrationAuthority(@Nullable final String authority);

    /**
     * Get the registration instant.
     * 
     * @return the registration instant
     */
    @Nullable Instant getRegistrationInstant();

    /**
     * Set the registration instant.
     * 
     * @param dateTime the instant
     */
    void setRegistrationInstant(@Nullable final Instant dateTime);

    /**
     * Get the {@link RegistrationPolicy}s.
     * 
     * @return the list of policies
     */
    @Nonnull @Live List<RegistrationPolicy> getRegistrationPolicies();

}