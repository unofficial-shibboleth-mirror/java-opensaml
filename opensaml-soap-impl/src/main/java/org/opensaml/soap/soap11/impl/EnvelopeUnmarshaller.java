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

package org.opensaml.soap.soap11.impl;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Header;
import org.w3c.dom.Attr;

import net.shibboleth.shared.xml.QNameSupport;

/**
 * A thread-safe unmarshaller for {@link org.opensaml.soap.soap11.Envelope}s.
 */
public class EnvelopeUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final Envelope envelope = (Envelope) parentXMLObject;

        if (childXMLObject instanceof Header) {
            envelope.setHeader((Header) childXMLObject);
        } else if (childXMLObject instanceof Body) {
            envelope.setBody((Body) childXMLObject);
        } else {
            envelope.getUnknownXMLObjects().add(childXMLObject);
        }
    }

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final Envelope envelope = (Envelope) xmlObject;
        final QName attribQName = QNameSupport.constructQName(attribute.getNamespaceURI(), attribute.getLocalName(),
                attribute.getPrefix());
        if (attribute.isId()) {
            envelope.getUnknownAttributes().registerID(attribQName);
        }
        envelope.getUnknownAttributes().put(attribQName, attribute.getValue());
    }

}