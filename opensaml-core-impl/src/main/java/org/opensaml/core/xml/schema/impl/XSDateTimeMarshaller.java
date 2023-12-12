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

package org.opensaml.core.xml.schema.impl;

import java.time.Instant;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSDateTime;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.DOMTypeSupport;
import net.shibboleth.shared.xml.ElementSupport;

/**
 * Thread-safe marshaller of {@link XSDateTime} objects.
 */
public class XSDateTimeMarshaller extends AbstractXMLObjectMarshaller {

    /** {@inheritDoc} */
    protected void marshallElementContent(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        
        final Instant i = ((XSDateTime) xmlObject).getValue();
        if (i != null) {
            ElementSupport.appendTextContent(domElement, DOMTypeSupport.instantToString(i));
        }
    }

}