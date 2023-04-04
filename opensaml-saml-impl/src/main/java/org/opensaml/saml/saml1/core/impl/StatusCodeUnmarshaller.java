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

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml1.core.StatusCode;
import org.w3c.dom.Attr;

import net.shibboleth.shared.xml.AttributeSupport;

/**
 * A thread-safe Unmarshaller for {@link StatusCode} objects.
 */
public class StatusCodeUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {

        final StatusCode statusCode = (StatusCode) parentObject;

        if (childObject instanceof StatusCode) {
            statusCode.setStatusCode((StatusCode) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }

    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {

        final StatusCode statusCode = (StatusCode) xmlObject;

        if (attribute.getName().equals(StatusCode.VALUE_ATTRIB_NAME) && attribute.getNamespaceURI() == null) {
            statusCode.setValue(AttributeSupport.getAttributeValueAsQName(attribute));
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

}