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

package org.opensaml.saml.ext.saml2delrestrict.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.ext.saml2delrestrict.Delegate;
import org.opensaml.saml.saml2.core.BaseID;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NameID;
import org.w3c.dom.Attr;

import net.shibboleth.shared.xml.DOMTypeSupport;

/**
 * Unmarshaller for instances of {@link Delegate}.
 */
public class DelegateUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute) 
            throws UnmarshallingException {
        final Delegate delegate = (Delegate) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            final String attrName = attribute.getLocalName();
            if (Delegate.CONFIRMATION_METHOD_ATTRIB_NAME.equals(attrName)) {
                delegate.setConfirmationMethod(attribute.getValue());
            } else if (Delegate.DELEGATION_INSTANT_ATTRIB_NAME.equals(attrName)) {
                delegate.setDelegationInstant(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final Delegate delegate = (Delegate) parentObject;
        
        if (childObject instanceof BaseID) {
            delegate.setBaseID((BaseID) childObject);
        } else if (childObject instanceof NameID) {
            delegate.setNameID((NameID) childObject);
        } else if (childObject instanceof EncryptedID) {
            delegate.setEncryptedID((EncryptedID) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

}