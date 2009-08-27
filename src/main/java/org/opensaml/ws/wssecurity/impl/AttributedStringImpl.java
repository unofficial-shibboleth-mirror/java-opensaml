/*
 * Copyright 2009 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.ws.wssecurity.impl;

import org.opensaml.ws.wssecurity.AttributedString;
import org.opensaml.xml.util.AttributeMap;

/**
 * Implementation of {@link AttributedString}.
 */
public class AttributedStringImpl extends AbstractWSSecurityObject implements AttributedString {
    
    /** The string value. */
    private String value;
    
    /** The wsu:Id attribute value. */
    private String id;
    
    /** The wildcard attributes. */
    private AttributeMap attributes;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public AttributedStringImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        attributes = new AttributeMap(this);
    }

    /** {@inheritDoc} */
    public String getValue() {
        return value;
    }

    /** {@inheritDoc} */
    public void setValue(String newValue) {
        value = prepareForAssignment(value, newValue);
    }

    /** {@inheritDoc} */
    public String getWSUId() {
        return id;
    }

    /** {@inheritDoc} */
    public void setWSUId(String newId) {
        String oldId = id;
        id = prepareForAssignment(id, newId);
        registerOwnID(oldId, id);
    }

    /** {@inheritDoc} */
    public AttributeMap getUnknownAttributes() {
        return attributes;
    }

}