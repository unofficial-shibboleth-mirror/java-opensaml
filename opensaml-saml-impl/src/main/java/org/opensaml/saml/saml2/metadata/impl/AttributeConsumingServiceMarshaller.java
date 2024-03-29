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

package org.opensaml.saml.saml2.metadata.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.w3c.dom.Element;

/**
 * A thread safe Marshaller for {@link AttributeConsumingService} objects.
 */
public class AttributeConsumingServiceMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final AttributeConsumingService service = (AttributeConsumingService) xmlObject;

        final Integer i = service.getIndex();
        if (i != null) {
            domElement.setAttributeNS(null, AttributeConsumingService.INDEX_ATTRIB_NAME, i.toString());
        }

        final XSBooleanValue flag = service.isDefaultXSBoolean();
        if (flag != null) {
            domElement.setAttributeNS(null, AttributeConsumingService.IS_DEFAULT_ATTRIB_NAME, flag.toString());
        }
    }

}