/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.soap.wssecurity.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.soap.wssecurity.EncryptedHeader;
import org.w3c.dom.Element;

/**
 * Marshaller for instances of {@link EncryptedHeaderMarshaller}.
 */
public class EncryptedHeaderMarshaller extends AbstractWSSecurityObjectMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final EncryptedHeader eh = (EncryptedHeader) xmlObject;
        
        if (eh.getWSUId() != null) {
            XMLObjectSupport.marshallAttribute(EncryptedHeader.WSU_ID_ATTR_NAME, 
                    eh.getWSUId(), domElement, true);
        }
        
        XSBooleanValue xsbool = eh.isSOAP11MustUnderstandXSBoolean();
        if (xsbool != null) {
            XMLObjectSupport.marshallAttribute(EncryptedHeader.SOAP11_MUST_UNDERSTAND_ATTR_NAME, 
                    xsbool.toString(), domElement, false);
        }
        
        if (eh.getSOAP11Actor() != null) {
            XMLObjectSupport.marshallAttribute(EncryptedHeader.SOAP11_ACTOR_ATTR_NAME, 
                    eh.getSOAP11Actor(), domElement, false);
        }
        
        xsbool = eh.isSOAP12MustUnderstandXSBoolean();
        if (xsbool != null) {
            XMLObjectSupport.marshallAttribute(EncryptedHeader.SOAP12_MUST_UNDERSTAND_ATTR_NAME, 
                    xsbool.toString(), domElement, false);
        }
        
        if (eh.getSOAP12Role() != null) {
            XMLObjectSupport.marshallAttribute(EncryptedHeader.SOAP12_ROLE_ATTR_NAME,
                    eh.getSOAP12Role(), domElement, false);
        }
        
        xsbool = eh.isSOAP12RelayXSBoolean();
        if (xsbool != null) {
            XMLObjectSupport.marshallAttribute(EncryptedHeader.SOAP12_RELAY_ATTR_NAME, 
                    xsbool.toString(), domElement, false);
        }
        
        super.marshallAttributes(xmlObject, domElement);
    }
    
    /** {@inheritDoc} */
    protected void marshallAttributeIDness(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        XMLObjectSupport.marshallAttributeIDness(EncryptedHeader.WSU_ID_ATTR_NAME, domElement, true);
        
        super.marshallAttributeIDness(xmlObject, domElement);
    }

}