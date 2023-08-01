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

package org.opensaml.saml.ext.saml2mdrpi.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.ext.saml2mdrpi.RegistrationInfo;
import org.opensaml.saml.ext.saml2mdrpi.RegistrationPolicy;
import org.w3c.dom.Attr;

import net.shibboleth.shared.xml.DOMTypeSupport;

/**
 * An unmarshaller for {@link RegistrationInfo}.
 */
public class RegistrationInfoUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject,
            @Nonnull final XMLObject childObject) throws UnmarshallingException {
        final RegistrationInfo info = (RegistrationInfo) parentObject;
        
        if (childObject instanceof RegistrationPolicy) {
            info.getRegistrationPolicies().add((RegistrationPolicy)childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final RegistrationInfo info = (RegistrationInfo) xmlObject;
        
        if (attribute.getNamespaceURI() == null) {
            if (RegistrationInfo.REGISTRATION_AUTHORITY_ATTRIB_NAME.equals(attribute.getName())) {
                info.setRegistrationAuthority(attribute.getValue());
            } else if (RegistrationInfo.REGISTRATION_INSTANT_ATTRIB_NAME.equals(attribute.getName())) {
                info.setRegistrationInstant(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

}