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
import org.opensaml.soap.wstrust.UseKey;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for the wst:UseKey element.
 * 
 */
public class UseKeyUnmarshaller extends AbstractWSTrustObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final UseKey uk = (UseKey) xmlObject;
        
        if (UseKey.SIG_ATTRIB_NAME.equals(attribute.getLocalName())) {
            uk.setSig(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final UseKey uk = (UseKey) parentXMLObject;
        uk.setUnknownXMLObject(childXMLObject);
    }
}
