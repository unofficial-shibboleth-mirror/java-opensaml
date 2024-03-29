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
import org.opensaml.core.xml.schema.impl.XSURIUnmarshaller;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.soap.wsaddressing.RelatesTo;
import org.w3c.dom.Attr;

import net.shibboleth.shared.primitive.StringSupport;

/**
 *  Unmarshaller for instances of {@link RelatesTo}.
 */
public class RelatesToUnmarshaller extends XSURIUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final RelatesTo relatesTo = (RelatesTo) xmlObject;
        
        if (RelatesTo.RELATIONSHIP_TYPE_ATTRIB_NAME.equals(attribute.getLocalName())) {
            relatesTo.setRelationshipType(StringSupport.trimOrNull(attribute.getValue()));
        } else {
            XMLObjectSupport.unmarshallToAttributeMap(relatesTo.getUnknownAttributes(), attribute);
        }
    }

}
