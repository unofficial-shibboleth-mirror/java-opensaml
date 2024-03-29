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
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.ext.saml2mdquery.QueryDescriptorType;
import org.opensaml.saml.saml2.metadata.impl.RoleDescriptorMarshaller;
import org.w3c.dom.Element;

/**
 * Marshaller for {@link QueryDescriptorType} objects.
 */
public abstract class QueryDescriptorTypeMarshaller extends RoleDescriptorMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final QueryDescriptorType descriptor = (QueryDescriptorType) xmlObject;

        final XSBooleanValue flag = descriptor.getWantAssertionsSignedXSBoolean();
        if (flag != null) {
            domElement.setAttributeNS(null, QueryDescriptorType.WANT_ASSERTIONS_SIGNED_ATTRIB_NAME, flag.toString());
        }

        super.marshallAttributes(xmlObject, domElement);
    }

}