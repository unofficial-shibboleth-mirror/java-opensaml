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
import org.opensaml.core.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * An abstract marshaller implementation for XMLObjects from WS-Trust.
 * 
 */
public abstract class AbstractWSTrustObjectMarshaller extends AbstractXMLObjectMarshaller {

    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractWSTrustObjectMarshaller.class);

    /** {@inheritDoc} */
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        log.trace("{} has no more attribute to marshall.", xmlObject.getElementQName().getLocalPart());

    }

    /** {@inheritDoc} */
    protected void marshallElementContent(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        log.trace("{} has no content to marshall.", xmlObject.getElementQName().getLocalPart());
    }
}
