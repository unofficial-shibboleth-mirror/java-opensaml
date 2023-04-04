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

import java.time.Instant;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.ResponseAbstractType;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.AttributeSupport;

/**
 * A thread safe Marshaller for {@link ResponseAbstractType} objects.
 */
public abstract class ResponseAbstractTypeMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final ResponseAbstractType response = (ResponseAbstractType) xmlObject;

        if (response.getID() != null) {
            domElement.setAttributeNS(null, ResponseAbstractType.ID_ATTRIB_NAME, response.getID());
        }

        if (response.getInResponseTo() != null) {
            domElement.setAttributeNS(null, ResponseAbstractType.INRESPONSETO_ATTRIB_NAME, response.getInResponseTo());
        }

        final Instant i = response.getIssueInstant();
        if (i != null) {
            AttributeSupport.appendDateTimeAttribute(domElement, ResponseAbstractType.ISSUEINSTANT_ATTRIB_QNAME, i);
        }

        final SAMLVersion version = response.getVersion();
        if (version != null) {
            domElement.setAttributeNS(null, ResponseAbstractType.MAJORVERSION_ATTRIB_NAME,
                    Integer.toString(version.getMajorVersion()));
            domElement.setAttributeNS(null, ResponseAbstractType.MINORVERSION_ATTRIB_NAME,
                    Integer.toString(version.getMinorVersion()));
        }

        if (response.getRecipient() != null) {
            domElement.setAttributeNS(null, ResponseAbstractType.RECIPIENT_ATTRIB_NAME, response.getRecipient());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributeIDness(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {

        if (((ResponseAbstractType)xmlObject).getVersion() != SAMLVersion.VERSION_10) {
            XMLObjectSupport.marshallAttributeIDness(null, ResponseAbstractType.ID_ATTRIB_NAME, domElement, true);
        }
    }

}