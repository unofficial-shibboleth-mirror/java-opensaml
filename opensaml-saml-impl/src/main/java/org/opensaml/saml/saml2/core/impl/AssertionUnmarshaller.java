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

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.core.Advice;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Statement;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.xmlsec.signature.Signature;
import org.w3c.dom.Attr;

import com.google.common.base.Strings;

import net.shibboleth.shared.xml.DOMTypeSupport;

/**
 * A thread-safe Unmarshaller for {@link Assertion}.
 */
public class AssertionUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final Assertion assertion = (Assertion) parentObject;

        if (childObject instanceof Issuer) {
            assertion.setIssuer((Issuer) childObject);
        } else if (childObject instanceof Signature) {
            assertion.setSignature((Signature) childObject);
        } else if (childObject instanceof Subject) {
            assertion.setSubject((Subject) childObject);
        } else if (childObject instanceof Conditions) {
            assertion.setConditions((Conditions) childObject);
        } else if (childObject instanceof Advice) {
            assertion.setAdvice((Advice) childObject);
        } else if (childObject instanceof Statement) {
            assertion.getStatements().add((Statement) childObject);
        } else if (Statement.DEFAULT_ELEMENT_NAME.equals(childObject.getElementQName())
                && XSAny.class.isInstance(childObject)) {
            assertion.getStatements().add(new StatementXSAnyAdapter(XSAny.class.cast(childObject)));
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final Assertion assertion = (Assertion) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (attribute.getLocalName().equals(Assertion.VERSION_ATTRIB_NAME)) {
                assertion.setVersion(parseSAMLVersion(attribute));
            } else if (attribute.getLocalName().equals(Assertion.ISSUE_INSTANT_ATTRIB_NAME)
                    && !Strings.isNullOrEmpty(attribute.getValue())) {
                assertion.setIssueInstant(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else if (attribute.getLocalName().equals(Assertion.ID_ATTRIB_NAME)) {
                assertion.setID(attribute.getValue());
                attribute.getOwnerElement().setIdAttributeNode(attribute, true);
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}