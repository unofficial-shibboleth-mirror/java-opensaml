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

/**
 * 
 */

package org.opensaml.saml.saml2.core.impl;

import java.time.Instant;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.AttributeSupport;

/**
 * A thread-safe Marshaller for {@link SubjectConfirmationData} objects.
 */
public class SubjectConfirmationDataMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final SubjectConfirmationData subjectCD = (SubjectConfirmationData) xmlObject;

        Instant i = subjectCD.getNotBefore();
        if (i != null) {
            AttributeSupport.appendDateTimeAttribute(domElement, SubjectConfirmationData.NOT_BEFORE_ATTRIB_QNAME, i);
        }

        i = subjectCD.getNotOnOrAfter();
        if (i != null) {
            AttributeSupport.appendDateTimeAttribute(domElement, SubjectConfirmationData.NOT_ON_OR_AFTER_ATTRIB_QNAME,
                    i);
        }

        if (subjectCD.getRecipient() != null) {
            domElement.setAttributeNS(null, SubjectConfirmationData.RECIPIENT_ATTRIB_NAME, subjectCD.getRecipient());
        }

        if (subjectCD.getInResponseTo() != null) {
            domElement.setAttributeNS(null, SubjectConfirmationData.IN_RESPONSE_TO_ATTRIB_NAME, subjectCD
                    .getInResponseTo());
        }

        if (subjectCD.getAddress() != null) {
            domElement.setAttributeNS(null, SubjectConfirmationData.ADDRESS_ATTRIB_NAME, subjectCD.getAddress());
        }

        marshallUnknownAttributes(subjectCD, domElement);
    }
    
}