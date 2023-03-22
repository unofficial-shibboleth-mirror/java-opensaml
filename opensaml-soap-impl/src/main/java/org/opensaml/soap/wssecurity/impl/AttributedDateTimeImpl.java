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

import java.time.Instant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.soap.wssecurity.AttributedDateTime;
import org.opensaml.soap.wssecurity.IdBearing;

import net.shibboleth.shared.xml.DOMTypeSupport;

/**
 * Implementation of {@link AttributedDateTime}.
 * 
 */
public class AttributedDateTimeImpl extends AbstractWSSecurityObject implements AttributedDateTime {

    /** DateTime object. */
    @Nullable private Instant dateTimeValue;

    /** String dateTime representation. */
    @Nullable private String stringValue;
    
    /** wsu:id attribute value. */
    @Nullable private String id;
    
    /** Wildcard attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public AttributedDateTimeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
    }

    /** {@inheritDoc} */
    @Nullable public Instant getDateTime() {
        return dateTimeValue;
    }

    /** {@inheritDoc} */
    public void setDateTime(@Nullable final Instant newDateTime) {
        dateTimeValue = newDateTime;
        stringValue = prepareForAssignment(stringValue,
                dateTimeValue != null ? DOMTypeSupport.instantToString(dateTimeValue) : null);
    }

    /** {@inheritDoc} */
    @Nullable public String getValue() {
        return stringValue;
    }

    /** {@inheritDoc} */
    public void setValue(@Nullable final String newValue) {
        dateTimeValue = newValue != null ? DOMTypeSupport.stringToInstant(newValue) : null;
        stringValue = prepareForAssignment(stringValue, newValue);
    }

    /** {@inheritDoc} */
    @Nullable public String getWSUId() {
        return id;
    }

    /** {@inheritDoc} */
    public void setWSUId(@Nullable final String newId) {
        final String oldID = id;
        id = prepareForAssignment(id, newId);
        registerOwnID(oldID, id);
        manageQualifiedAttributeNamespace(IdBearing.WSU_ID_ATTR_NAME, id != null);
    }

    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }
    
}