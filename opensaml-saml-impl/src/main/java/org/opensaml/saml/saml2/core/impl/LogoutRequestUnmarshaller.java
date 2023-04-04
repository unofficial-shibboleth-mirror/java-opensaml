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

package org.opensaml.saml.saml2.core.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.BaseID;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.SessionIndex;
import org.w3c.dom.Attr;

import com.google.common.base.Strings;

import net.shibboleth.shared.xml.DOMTypeSupport;

/**
 * A thread-safe Unmarshaller for {@link LogoutRequest} objects.
 */
public class LogoutRequestUnmarshaller extends RequestAbstractTypeUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final LogoutRequest req = (LogoutRequest) parentObject;
    
        if (childObject instanceof BaseID) {
            req.setBaseID((BaseID) childObject);
        } else if (childObject instanceof NameID) {
            req.setNameID((NameID) childObject);
        } else if (childObject instanceof EncryptedID) {
            req.setEncryptedID((EncryptedID) childObject);
        } else if (childObject instanceof SessionIndex) {
            req.getSessionIndexes().add((SessionIndex) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final LogoutRequest req = (LogoutRequest) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (attribute.getLocalName().equals(LogoutRequest.REASON_ATTRIB_NAME)) {
                req.setReason(attribute.getValue());
            } else if (attribute.getLocalName().equals(LogoutRequest.NOT_ON_OR_AFTER_ATTRIB_NAME)
                    && !Strings.isNullOrEmpty(attribute.getValue())) {
                req.setNotOnOrAfter(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}