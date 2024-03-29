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
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.impl.XSStringMarshaller;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.soap.wstrust.BinaryExchange;
import org.w3c.dom.Element;

import net.shibboleth.shared.primitive.StringSupport;

/**
 * Marshaller for the BinaryExchange element.
 * 
 * @see BinaryExchange
 * 
 */
public class BinaryExchangeMarshaller extends XSStringMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final BinaryExchange binaryExchange = (BinaryExchange) xmlObject;
        
        final String valueType = StringSupport.trimOrNull(binaryExchange.getValueType());
        if (valueType != null) {
            domElement.setAttributeNS(null, BinaryExchange.VALUE_TYPE_ATTRIB_NAME, valueType);
        }
        final String encodingType = StringSupport.trimOrNull(binaryExchange.getEncodingType());
        if (encodingType != null) {
            domElement.setAttributeNS(null, BinaryExchange.ENCODING_TYPE_ATTRIB_NAME, encodingType);
        }
        
        XMLObjectSupport.marshallAttributeMap(binaryExchange.getUnknownAttributes(), domElement);
    }

}
