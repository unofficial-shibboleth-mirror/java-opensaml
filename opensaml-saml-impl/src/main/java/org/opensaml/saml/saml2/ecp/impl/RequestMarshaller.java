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

package org.opensaml.saml.saml2.ecp.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.saml2.ecp.Request;
import org.w3c.dom.Element;

/**
 * Marshaller for instances of {@link Request}.
 */
public class RequestMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final Request request = (Request) xmlObject;
        
        if (request.getProviderName() != null) {
            domElement.setAttributeNS(null, Request.PROVIDER_NAME_ATTRIB_NAME, request.getProviderName());
        }
        
        XSBooleanValue flag = request.isPassiveXSBoolean();
        if (flag != null) {
            domElement.setAttributeNS(null, Request.IS_PASSIVE_NAME_ATTRIB_NAME, flag.toString());
        }
        
        flag = request.isSOAP11MustUnderstandXSBoolean();
        if (flag != null) {
            XMLObjectSupport.marshallAttribute(Request.SOAP11_MUST_UNDERSTAND_ATTR_NAME, flag.toString(), domElement,
                    false);
        }
        
        if (request.getSOAP11Actor() != null) {
            XMLObjectSupport.marshallAttribute(Request.SOAP11_ACTOR_ATTR_NAME, 
                    request.getSOAP11Actor(), domElement, false);
        }
        
    }

}