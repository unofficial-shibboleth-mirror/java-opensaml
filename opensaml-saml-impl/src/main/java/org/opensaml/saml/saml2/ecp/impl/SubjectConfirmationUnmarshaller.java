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

package org.opensaml.saml.saml2.ecp.impl;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.ecp.SubjectConfirmation;
import org.w3c.dom.Attr;

import net.shibboleth.shared.xml.QNameSupport;

/**
 * A thread-safe Unmarshaller for {@link SubjectConfirmation} objects.
 */
public class SubjectConfirmationUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final SubjectConfirmation sc = (SubjectConfirmation) parentObject;

        if (childObject instanceof SubjectConfirmationData) {
            sc.setSubjectConfirmationData((SubjectConfirmationData) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final SubjectConfirmation sc = (SubjectConfirmation) xmlObject;

        final QName attrName = QNameSupport.getNodeQName(attribute);
        if (SubjectConfirmation.SOAP11_MUST_UNDERSTAND_ATTR_NAME.equals(attrName)) {
            sc.setSOAP11MustUnderstand(XSBooleanValue.valueOf(attribute.getValue()));
        } else if (SubjectConfirmation.SOAP11_ACTOR_ATTR_NAME.equals(attrName)) {
            sc.setSOAP11Actor(attribute.getValue()); 
        } else if (attribute.getLocalName().equals(SubjectConfirmation.METHOD_ATTRIB_NAME)) {
            sc.setMethod(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}