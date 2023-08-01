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
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.SubjectLocality;
import org.w3c.dom.Attr;

import com.google.common.base.Strings;

import net.shibboleth.shared.xml.DOMTypeSupport;

/**
 * A thread-safe Unmarshaller for {@link AuthnStatement}.
 */
public class AuthnStatementUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final AuthnStatement authnStatement = (AuthnStatement) parentObject;
        if (childObject instanceof SubjectLocality) {
            authnStatement.setSubjectLocality((SubjectLocality) childObject);
        } else if (childObject instanceof AuthnContext) {
            authnStatement.setAuthnContext((AuthnContext) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final AuthnStatement authnStatement = (AuthnStatement) xmlObject;
        
        if (attribute.getNamespaceURI() == null) {
            if (attribute.getLocalName().equals(AuthnStatement.AUTHN_INSTANT_ATTRIB_NAME)
                    && !Strings.isNullOrEmpty(attribute.getValue())) {
                authnStatement.setAuthnInstant(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else if (attribute.getLocalName().equals(AuthnStatement.SESSION_INDEX_ATTRIB_NAME)) {
                authnStatement.setSessionIndex(attribute.getValue());
            } else if (attribute.getLocalName().equals(AuthnStatement.SESSION_NOT_ON_OR_AFTER_ATTRIB_NAME)
                    && !Strings.isNullOrEmpty(attribute.getValue())) {
                authnStatement.setSessionNotOnOrAfter(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}