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

package org.opensaml.xmlsec.encryption.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.xmlsec.encryption.DerivedKey;
import org.w3c.dom.Element;

/**
 * A thread-safe Marshaller for {@link org.opensaml.xmlsec.encryption.DerivedKey} objects.
 */
public class DerivedKeyMarshaller extends AbstractXMLEncryptionMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final DerivedKey dk = (DerivedKey) xmlObject;

        if (dk.getRecipient() != null) {
            domElement.setAttributeNS(null, DerivedKey.RECIPIENT_ATTRIBUTE_NAME, dk.getRecipient());
        }
        if (dk.getId() != null) {
            domElement.setAttributeNS(null, DerivedKey.ID_ATTRIBUTE_NAME, dk.getId());
        }
        if (dk.getType() != null) {
            domElement.setAttributeNS(null, DerivedKey.TYPE_ATTRIBUTE_NAME, dk.getType());
        }
        
        super.marshallAttributes(xmlObject, domElement);
    }

    /** {@inheritDoc} */
    protected void marshallAttributeIDness(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {

        XMLObjectSupport.marshallAttributeIDness(null, DerivedKey.ID_ATTRIBUTE_NAME, domElement, true);
    }

}
