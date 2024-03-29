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
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.impl.XSStringUnmarshaller;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.soap.wstrust.BinaryExchange;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for the &lt;wst:BinaryExchange&gt; element.
 * 
 * @see BinaryExchange
 * 
 */
public class BinaryExchangeUnmarshaller extends XSStringUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final BinaryExchange binaryExchange = (BinaryExchange) xmlObject;
        
        final String attrName = attribute.getLocalName();
        if (BinaryExchange.VALUE_TYPE_ATTRIB_NAME.equals(attrName)) {
            binaryExchange.setValueType(attribute.getValue());
        } else if (BinaryExchange.ENCODING_TYPE_ATTRIB_NAME.equals(attrName)) {
            binaryExchange.setEncodingType(attribute.getValue());
        } else {
            XMLObjectSupport.unmarshallToAttributeMap(binaryExchange.getUnknownAttributes(), attribute);
        }
    }

}
