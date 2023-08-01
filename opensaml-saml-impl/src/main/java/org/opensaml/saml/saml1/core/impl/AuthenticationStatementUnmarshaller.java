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

package org.opensaml.saml.saml1.core.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml1.core.AuthenticationStatement;
import org.opensaml.saml.saml1.core.AuthorityBinding;
import org.opensaml.saml.saml1.core.SubjectLocality;
import org.w3c.dom.Attr;

import com.google.common.base.Strings;

import net.shibboleth.shared.xml.DOMTypeSupport;

/**
 * A thread-safe Unmarshaller for {@link AuthenticationStatement} objects.
 */
public class AuthenticationStatementUnmarshaller extends SubjectStatementUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {

        final AuthenticationStatement authenticationStatement = (AuthenticationStatement) parentObject;

        if (childObject instanceof SubjectLocality) {
            authenticationStatement.setSubjectLocality((SubjectLocality) childObject);
        } else if (childObject instanceof AuthorityBinding) {
            authenticationStatement.getAuthorityBindings().add((AuthorityBinding) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final AuthenticationStatement authenticationStatement = (AuthenticationStatement) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (AuthenticationStatement.AUTHENTICATIONINSTANT_ATTRIB_NAME.equals(attribute.getLocalName())
                    && !Strings.isNullOrEmpty(attribute.getValue())) {
                authenticationStatement.setAuthenticationInstant(
                        DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else if (AuthenticationStatement.AUTHENTICATIONMETHOD_ATTRIB_NAME.equals(attribute.getLocalName())) {
                authenticationStatement.setAuthenticationMethod(attribute.getValue());
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

}