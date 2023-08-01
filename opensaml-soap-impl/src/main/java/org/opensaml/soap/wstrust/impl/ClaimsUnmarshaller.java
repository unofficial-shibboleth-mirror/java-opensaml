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
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.soap.wstrust.Claims;
import org.w3c.dom.Attr;



/**
 * Unmarshaller for the wst:Claims element.
 * 
 */
public class ClaimsUnmarshaller extends AbstractWSTrustObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final Claims claims = (Claims) xmlObject;
        if (Claims.DIALECT_ATTRIB_NAME.equals(attribute.getLocalName())) {
            claims.setDialect(attribute.getValue());
        } else {
            XMLObjectSupport.unmarshallToAttributeMap(claims.getUnknownAttributes(), attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final Claims claims = (Claims) parentXMLObject;
        claims.getUnknownXMLObjects().add(childXMLObject);
    }

}
