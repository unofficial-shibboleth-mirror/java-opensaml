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

package org.opensaml.saml.ext.saml2mdui.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.ext.saml2mdui.Logo;
import org.opensaml.saml.saml2.metadata.impl.LocalizedURIUnmarshaller;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link Logo} objects.
 */
public class LogoUnmarshaller extends LocalizedURIUnmarshaller {
    
    /**  {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final Logo logo = (Logo) xmlObject;

        if (attribute.getLocalName().equals(Logo.HEIGHT_ATTR_NAME) && attribute.getNamespaceURI() == null) {
            logo.setHeight(Integer.valueOf(attribute.getValue()));
        } else if (attribute.getLocalName().equals(Logo.WIDTH_ATTR_NAME) && attribute.getNamespaceURI() == null) {
            logo.setWidth(Integer.valueOf(attribute.getValue()));
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

}