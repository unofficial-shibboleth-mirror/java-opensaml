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

package org.opensaml.saml.saml1.core.impl;

import java.time.Instant;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.saml1.core.Assertion;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.AttributeSupport;

/**
 * A thread safe Marshaller for {@link Assertion} objects.
 */
public class AssertionMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {

        final Assertion assertion = (Assertion) xmlObject;

        if (assertion.getID() != null) {
            domElement.setAttributeNS(null, Assertion.ID_ATTRIB_NAME, assertion.getID());
        }

        if (assertion.getIssuer() != null) {
            domElement.setAttributeNS(null, Assertion.ISSUER_ATTRIB_NAME, assertion.getIssuer());
        }

        final Instant i = assertion.getIssueInstant();
        if (i != null) {
            AttributeSupport.appendDateTimeAttribute(domElement, Assertion.ISSUEINSTANT_ATTRIB_QNAME, i);
        }

        domElement.setAttributeNS(null, Assertion.MAJORVERSION_ATTRIB_NAME, "1");
        if (assertion.getMinorVersion() == 0) {
            domElement.setAttributeNS(null, Assertion.MINORVERSION_ATTRIB_NAME, "0");
        } else {
            domElement.setAttributeNS(null, Assertion.MINORVERSION_ATTRIB_NAME, "1");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributeIDness(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {

        if (((Assertion)xmlObject).getMinorVersion() != 0) {
            XMLObjectSupport.marshallAttributeIDness(null, Assertion.ID_ATTRIB_NAME, domElement, true);
        }
    }

}