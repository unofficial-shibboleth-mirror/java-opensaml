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
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xmlsec.encryption.DerivedKey;
import org.opensaml.xmlsec.encryption.DerivedKeyName;
import org.opensaml.xmlsec.encryption.KeyDerivationMethod;
import org.opensaml.xmlsec.encryption.MasterKeyName;
import org.opensaml.xmlsec.encryption.ReferenceList;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link org.opensaml.xmlsec.encryption.DerivedKey} objects.
 */
public class DerivedKeyUnmarshaller extends AbstractXMLEncryptionUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final DerivedKey dk = (DerivedKey) xmlObject;

        if (attribute.getLocalName().equals(DerivedKey.ID_ATTRIBUTE_NAME)) {
            dk.setId(attribute.getValue());
            attribute.getOwnerElement().setIdAttributeNode(attribute, true);
        } else if (attribute.getLocalName().equals(DerivedKey.RECIPIENT_ATTRIBUTE_NAME)) {
            dk.setRecipient(attribute.getValue());
        } else if (attribute.getLocalName().equals(DerivedKey.TYPE_ATTRIBUTE_NAME)) {
            dk.setType(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final DerivedKey dk = (DerivedKey) parentXMLObject;

        if (childXMLObject instanceof KeyDerivationMethod) {
            dk.setKeyDerivationMethod((KeyDerivationMethod) childXMLObject);
        } else if (childXMLObject instanceof ReferenceList) {
            dk.setReferenceList((ReferenceList) childXMLObject);
        } else if (childXMLObject instanceof DerivedKeyName) {
            dk.setDerivedKeyName((DerivedKeyName) childXMLObject);
        } else if (childXMLObject instanceof MasterKeyName) {
            dk.setMasterKeyName((MasterKeyName) childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}
