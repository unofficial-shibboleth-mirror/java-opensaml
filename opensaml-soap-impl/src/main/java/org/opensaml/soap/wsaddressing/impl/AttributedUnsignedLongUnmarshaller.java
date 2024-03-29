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

package org.opensaml.soap.wsaddressing.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.soap.wsaddressing.AttributedUnsignedLong;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for instances of {@link AttributedUnsignedLong}.
 */
public class AttributedUnsignedLongUnmarshaller extends AbstractWSAddressingObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final AttributedUnsignedLong aul = (AttributedUnsignedLong) xmlObject;
        XMLObjectSupport.unmarshallToAttributeMap(aul.getUnknownAttributes(), attribute);
    }

    /** {@inheritDoc} */
    protected void processElementContent(@Nonnull final XMLObject xmlObject, @Nonnull final String elementContent) {
        final AttributedUnsignedLong aul = (AttributedUnsignedLong) xmlObject;
        if (elementContent != null) {
            aul.setValue(Long.valueOf(elementContent.trim()));
        }
    }

}
