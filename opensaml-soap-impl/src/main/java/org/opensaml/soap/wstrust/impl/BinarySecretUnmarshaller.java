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
import org.opensaml.core.xml.schema.impl.XSBase64BinaryUnmarshaller;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.soap.wstrust.BinarySecret;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for the &lt;wst:BinarySecret&gt; element.
 * 
 */
public class BinarySecretUnmarshaller extends XSBase64BinaryUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final BinarySecret bs = (BinarySecret) xmlObject;
        if (BinarySecret.TYPE_ATTRIB_NAME.equals(attribute.getLocalName())) {
            bs.setType(attribute.getValue());
        } else {
            XMLObjectSupport.unmarshallToAttributeMap(bs.getUnknownAttributes(), attribute);
        }
    }

}
