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

/**
 * 
 */

package org.opensaml.saml.ext.samlec.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.ext.samlec.SessionKey;
import org.w3c.dom.Element;

/**
 * A thread-safe Marshaller for {@link SessionKey} objects.
 */
public class SessionKeyMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final SessionKey key = (SessionKey) xmlObject;

        final XSBooleanValue mustUnderstand = key.isSOAP11MustUnderstandXSBoolean();
        if (mustUnderstand != null) {
            XMLObjectSupport.marshallAttribute(SessionKey.SOAP11_MUST_UNDERSTAND_ATTR_NAME, 
                    mustUnderstand.toString(), domElement, false);
        }
        
        if (key.getSOAP11Actor() != null) {
            XMLObjectSupport.marshallAttribute(SessionKey.SOAP11_ACTOR_ATTR_NAME, 
                    key.getSOAP11Actor(), domElement, false);
        }
        
        if (key.getAlgorithm() != null) {
            domElement.setAttributeNS(null, SessionKey.ALGORITHM_ATTRIB_NAME, key.getAlgorithm());
        }
    }

}