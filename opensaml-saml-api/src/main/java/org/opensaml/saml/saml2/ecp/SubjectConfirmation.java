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

package org.opensaml.saml.saml2.ecp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.soap11.MustUnderstandBearing;

import net.shibboleth.shared.annotation.constraint.NotEmpty;


/**
 * SAML 2.0 ECP SubjectConfirmation SOAP header.
 */
public interface SubjectConfirmation extends SAMLObject, MustUnderstandBearing, ActorBearing {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "SubjectConfirmation";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20ECP_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20ECP_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "SubjectConfirmationType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20ECP_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20ECP_PREFIX);

    /** Method attribute name. */
    @Nonnull @NotEmpty static final String METHOD_ATTRIB_NAME = "Method";
    
    /**
     * Get the method used to confirm this subject.
     * 
     * @return the method used to confirm this subject
     */
    @Nullable String getMethod();

    /**
     * Sets the method used to confirm this subject.
     * 
     * @param newMethod the method used to confirm this subject
     */
    void setMethod(@Nullable final String newMethod);

    /**
     * Gets the data about how this subject was confirmed or constraints on the confirmation.
     * 
     * @return the data about how this subject was confirmed or constraints on the confirmation
     */
    @Nullable SubjectConfirmationData getSubjectConfirmationData();

    /**
     * Sets the data about how this subject was confirmed or constraints on the confirmation.
     * 
     * @param newSubjectConfirmationData the data about how this subject was confirmed or constraints on the
     *            confirmation
     */
    void setSubjectConfirmationData(@Nullable final SubjectConfirmationData newSubjectConfirmationData);
}
