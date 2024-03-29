/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.soap.wssecurity.AttributedString;
import org.opensaml.soap.wssecurity.IdBearing;

/**
 * Implementation of {@link AttributedString}.
 */
public class AttributedStringImpl extends AbstractWSSecurityObject implements AttributedString {
    
    /** The string value. */
    @Nullable private String value;
    
    /** The wsu:Id attribute value. */
    @Nullable private String id;
    
    /** The wildcard attributes. */
    @Nonnull private final AttributeMap attributes;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public AttributedStringImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        attributes = new AttributeMap(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getValue() {
        return value;
    }

    /** {@inheritDoc} */
    public void setValue(@Nullable final String newValue) {
        value = prepareForAssignment(value, newValue);
    }

    /** {@inheritDoc} */
    @Nullable public String getWSUId() {
        return id;
    }

    /** {@inheritDoc} */
    public void setWSUId(@Nullable final String newId) {
        final String oldId = id;
        id = prepareForAssignment(id, newId);
        registerOwnID(oldId, id);
        manageQualifiedAttributeNamespace(IdBearing.WSU_ID_ATTR_NAME, id != null);
    }

    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return attributes;
    }

}
