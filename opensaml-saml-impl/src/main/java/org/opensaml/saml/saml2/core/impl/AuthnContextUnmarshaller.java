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
import org.opensaml.saml.saml2.core.AuthenticatingAuthority;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextDecl;
import org.opensaml.saml.saml2.core.AuthnContextDeclRef;

/**
 * A thread-safe Unmarshaller for {@link AuthnContext}.
 */
public class AuthnContextUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final AuthnContext authnContext = (AuthnContext) parentObject;
        if (childObject instanceof AuthnContextClassRef) {
            authnContext.setAuthnContextClassRef((AuthnContextClassRef) childObject);
        } else if (childObject instanceof AuthnContextDecl) {
            authnContext.setAuthnContextDecl((AuthnContextDecl) childObject);
        } else if (childObject instanceof AuthnContextDeclRef) {
            authnContext.setAuthnContextDeclRef((AuthnContextDeclRef) childObject);
        } else if (childObject instanceof AuthenticatingAuthority) {
            authnContext.getAuthenticatingAuthorities().add((AuthenticatingAuthority) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }
    
}