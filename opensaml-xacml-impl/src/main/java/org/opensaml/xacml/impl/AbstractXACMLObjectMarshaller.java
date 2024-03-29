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

package org.opensaml.xacml.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.w3c.dom.Element;

/**
 * A thread safe, abstract implementation of the {@link org.opensaml.core.xml.io.Marshaller} interface that handles
 * most of the boilerplate code for Marshallers.
 */
public abstract class AbstractXACMLObjectMarshaller extends AbstractXMLObjectMarshaller {

    /**
     * No-op method. Extending implementations should override this method if they have attributes to marshall into the
     * Element.
     * 
     * {@inheritDoc}
     */
    protected void marshallAttributes(final XMLObject xmlObject, final Element domElement) throws MarshallingException {

    }

    /**
     * No-op method. Extending implementations should override this method if they have text content to marshall into
     * the Element.
     * 
     * {@inheritDoc}
     */
    protected void marshallElementContent(final XMLObject xmlObject, final Element domElement)
            throws MarshallingException {

    }
}