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

package org.opensaml.core.xml;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.w3c.dom.Element;

/**
 * AbstractExtensibleXMLObjectMarshaller marshalls element of type <code>xs:any</code> and with
 * <code>xs:anyAttribute</code> attributes.
 */
public abstract class AbstractExtensibleXMLObjectMarshaller extends AbstractElementExtensibleXMLObjectMarshaller {

    /**
     * Marshalls the <code>xs:anyAttribute</code> attributes.
     * 
     * {@inheritDoc}
     */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {

        final AttributeExtensibleXMLObject anyAttribute = (AttributeExtensibleXMLObject) xmlObject;

        XMLObjectSupport.marshallAttributeMap(anyAttribute.getUnknownAttributes(), domElement);
    }

    /** {@inheritDoc} */
    protected void marshallAttributeIDness(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {

        final AttributeExtensibleXMLObject anyAttribute = (AttributeExtensibleXMLObject) xmlObject;

        XMLObjectSupport.marshallAttributeMapIDness(anyAttribute.getUnknownAttributes(), domElement);
    }

}