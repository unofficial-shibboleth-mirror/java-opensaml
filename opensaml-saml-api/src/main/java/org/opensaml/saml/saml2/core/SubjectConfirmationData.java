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

import java.time.Instant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.ElementExtensibleXMLObject;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Core SubjectConfirmationData.
 */
public interface SubjectConfirmationData extends SAMLObject, ElementExtensibleXMLObject, AttributeExtensibleXMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "SubjectConfirmationData";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "SubjectConfirmationDataType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** NotBefore attribute name. */
    @Nonnull @NotEmpty static final String NOT_BEFORE_ATTRIB_NAME = "NotBefore";

    /** QName for the NotBefore attribute. */
    @Nonnull static final QName NOT_BEFORE_ATTRIB_QNAME = new QName(null, "NotBefore", XMLConstants.DEFAULT_NS_PREFIX);

    /** Name for the NotOnOrAfter attribute. */
    @Nonnull @NotEmpty static final String NOT_ON_OR_AFTER_ATTRIB_NAME = "NotOnOrAfter";

    /** QName for the NotOnOrAfter attribute. */
    @Nonnull static final QName NOT_ON_OR_AFTER_ATTRIB_QNAME =
            new QName(null, "NotOnOrAfter", XMLConstants.DEFAULT_NS_PREFIX);

    /** Recipient attribute name. */
    @Nonnull @NotEmpty static final String RECIPIENT_ATTRIB_NAME = "Recipient";

    /** InResponseTo attribute name. */
    @Nonnull @NotEmpty static final String IN_RESPONSE_TO_ATTRIB_NAME = "InResponseTo";

    /** Address attribute name. */
    @Nonnull @NotEmpty static final String ADDRESS_ATTRIB_NAME = "Address";

    /**
     * Gets the time before which this subject is not valid.
     * 
     * @return the time before which this subject is not valid
     */
    @Nullable Instant getNotBefore();

    /**
     * Sets the time before which this subject is not valid.
     * 
     * @param newNotBefore the time before which this subject is not valid
     */
    void setNotBefore(@Nullable final Instant newNotBefore);

    /**
     * Gets the time at, or after, which this subject is not valid.
     * 
     * @return the time at, or after, which this subject is not valid
     */
    @Nullable Instant getNotOnOrAfter();

    /**
     * Sets the time at, or after, which this subject is not valid.
     * 
     * @param newNotOnOrAfter the time at, or after, which this subject is not valid
     */
    void setNotOnOrAfter(@Nullable final Instant newNotOnOrAfter);

    /**
     * Gets the recipient of this subject.
     * 
     * @return the recipient of this subject
     */
    @Nullable String getRecipient();

    /**
     * Sets the recipient of this subject.
     * 
     * @param newRecipient the recipient of this subject
     */
    void setRecipient(@Nullable final String newRecipient);

    /**
     * Gets the message ID this is in response to.
     * 
     * @return the message ID this is in response to
     */
    @Nullable String getInResponseTo();

    /**
     * Sets the message ID this is in response to.
     * 
     * @param newInResponseTo the message ID this is in response to
     */
    void setInResponseTo(@Nullable final String newInResponseTo);

    /**
     * Gets the IP address to which this information may be pressented.
     * 
     * @return the IP address to which this information may be pressented
     */
    @Nullable String getAddress();

    /**
     * Sets the IP address to which this information may be pressented.
     * 
     * @param newAddress the IP address to which this information may be pressented
     */
    void setAddress(@Nullable final String newAddress);
    
}