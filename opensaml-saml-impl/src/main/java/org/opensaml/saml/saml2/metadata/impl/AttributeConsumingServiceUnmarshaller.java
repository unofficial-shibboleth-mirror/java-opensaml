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

package org.opensaml.saml.saml2.metadata.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;
import org.opensaml.saml.saml2.metadata.ServiceDescription;
import org.opensaml.saml.saml2.metadata.ServiceName;
import org.w3c.dom.Attr;

/**
 * A thread safe Unmarshaller for {@link AttributeConsumingService} objects.
 */
public class AttributeConsumingServiceUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final AttributeConsumingService service = (AttributeConsumingService) parentObject;

        if (childObject instanceof ServiceName) {
            service.getNames().add((ServiceName) childObject);
        } else if (childObject instanceof ServiceDescription) {
            service.getDescriptions().add((ServiceDescription) childObject);
        } else if (childObject instanceof RequestedAttribute) {
            service.getRequestedAttributes().add((RequestedAttribute) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final AttributeConsumingService service = (AttributeConsumingService) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (attribute.getLocalName().equals(AttributeConsumingService.INDEX_ATTRIB_NAME)) {
                service.setIndex(Integer.valueOf(attribute.getValue()));
            } else if (attribute.getLocalName().equals(AttributeConsumingService.IS_DEFAULT_ATTRIB_NAME)) {
                service.setIsDefault(XSBooleanValue.valueOf(attribute.getValue()));
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

}