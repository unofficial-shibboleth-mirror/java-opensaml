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

/**
 * 
 */

package org.opensaml.saml.saml1.core.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.RequestAbstractType;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.xml.AttributeSupport;

/**
 * A thread safe Marshaller for {@link org.opensaml.saml.saml1.core.RequestAbstractType} objects.
 */
public class RequestAbstractTypeMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(final XMLObject samlElement, final Element domElement)
            throws MarshallingException {
        final RequestAbstractType request = (RequestAbstractType) samlElement;

        if (request.getID() != null) {
            domElement.setAttributeNS(null, RequestAbstractType.ID_ATTRIB_NAME, request.getID());
        }

        if (request.getIssueInstant() != null) {
            AttributeSupport.appendDateTimeAttribute(domElement, RequestAbstractType.ISSUEINSTANT_ATTRIB_QNAME,
                    request.getIssueInstant());
        }

        domElement.setAttributeNS(null, RequestAbstractType.MAJORVERSION_ATTRIB_NAME,
                Integer.toString(request.getVersion().getMajorVersion()));
        domElement.setAttributeNS(null, RequestAbstractType.MINORVERSION_ATTRIB_NAME,
                Integer.toString(request.getVersion().getMinorVersion()));
    }

    /** {@inheritDoc} */
    protected void marshallAttributeIDness(final XMLObject xmlObject, final Element domElement)
            throws MarshallingException {

        if (((RequestAbstractType)xmlObject).getVersion() != SAMLVersion.VERSION_10) {
            XMLObjectSupport.marshallAttributeIDness(null, RequestAbstractType.ID_ATTRIB_NAME, domElement, true);
        }
    }

}