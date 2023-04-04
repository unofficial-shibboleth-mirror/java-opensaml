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

package org.opensaml.saml.saml2.core.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.Conditions;
import org.w3c.dom.Attr;

import com.google.common.base.Strings;

import net.shibboleth.shared.xml.DOMTypeSupport;

/**
 * A thread-safe Unmarshaller for {@link Conditions} objects.
 */
public class ConditionsUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final Conditions conditions = (Conditions) parentObject;

        if (childObject instanceof Condition) {
            conditions.getConditions().add((Condition) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final Conditions conditions = (Conditions) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (attribute.getLocalName().equals(Conditions.NOT_BEFORE_ATTRIB_NAME)
                    && !Strings.isNullOrEmpty(attribute.getValue())) {
                conditions.setNotBefore(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else if (attribute.getLocalName().equals(Conditions.NOT_ON_OR_AFTER_ATTRIB_NAME)
                    && !Strings.isNullOrEmpty(attribute.getValue())) {
                conditions.setNotOnOrAfter(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}