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

package org.opensaml.saml.ext.saml2mdquery.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.ext.saml2mdquery.QueryDescriptorType;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.impl.RoleDescriptorUnmarshaller;
import org.w3c.dom.Attr;

/** Unmarshaller for {@link QueryDescriptorType} objects. */
public class QueryDescriptorTypeUnmarshaller extends RoleDescriptorUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject,
            @Nonnull final XMLObject childObject) throws UnmarshallingException {
        final QueryDescriptorType descriptor = (QueryDescriptorType) parentObject;

        if (childObject instanceof NameIDFormat) {
            descriptor.getNameIDFormat().add((NameIDFormat) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject samlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final QueryDescriptorType descriptor = (QueryDescriptorType) samlObject;

        if (attribute.getLocalName().equals(QueryDescriptorType.WANT_ASSERTIONS_SIGNED_ATTRIB_NAME)
                && attribute.getNamespaceURI() == null) {
            descriptor.setWantAssertionsSigned(XSBooleanValue.valueOf(attribute.getValue()));
        } else {
            super.processAttribute(samlObject, attribute);
        }
    }

}