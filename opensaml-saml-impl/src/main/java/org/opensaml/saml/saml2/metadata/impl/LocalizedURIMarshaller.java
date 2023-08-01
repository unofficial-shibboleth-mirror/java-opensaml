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

import net.shibboleth.shared.xml.AttributeSupport;
import net.shibboleth.shared.xml.ElementSupport;
import net.shibboleth.shared.xml.XMLConstants;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.LangBearing;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.impl.XSURIMarshaller;
import org.opensaml.saml.saml2.metadata.LocalizedURI;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * A thread safe Marshaller for {@link LocalizedURI} objects.
 */
public class LocalizedURIMarshaller extends XSURIMarshaller {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final LocalizedURI name = (LocalizedURI) xmlObject;

        if (name.getXMLLang() != null) {
            final Attr attribute = AttributeSupport.constructAttribute(domElement.getOwnerDocument(),
                    XMLConstants.XML_NS, LangBearing.XML_LANG_ATTR_LOCAL_NAME, XMLConstants.XML_PREFIX);
            attribute.setValue(name.getXMLLang());
            domElement.setAttributeNodeNS(attribute);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void marshallElementContent(@Nonnull final XMLObject samlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final LocalizedURI name = (LocalizedURI) samlObject;

        if (name.getURI() != null) {
            ElementSupport.appendTextContent(domElement, name.getURI());
        }
    }

}