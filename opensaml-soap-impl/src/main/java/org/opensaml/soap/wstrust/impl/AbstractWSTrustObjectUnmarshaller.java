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

package org.opensaml.soap.wstrust.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.slf4j.Logger;
import org.w3c.dom.Attr;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * An abstract unmarshaller implementation for XMLObjects from WS-Trust.
 * 
 */
public abstract class AbstractWSTrustObjectUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractWSTrustObjectUnmarshaller.class);

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        log.warn("{} ignoring unknown child element {}", parentXMLObject.getElementQName().getLocalPart(),
                childXMLObject.getElementQName().getLocalPart());
    }

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        log.warn("{} ignoring unknown attribute {}", xmlObject.getElementQName().getLocalPart(), attribute
                .getLocalName());
    }

    /** {@inheritDoc} */
    protected void processElementContent(@Nonnull final XMLObject xmlObject, @Nonnull final String elementContent) {
        log.warn("{} ignoring unknown element content: {}", xmlObject.getElementQName().getLocalPart(), elementContent);
    }
}
