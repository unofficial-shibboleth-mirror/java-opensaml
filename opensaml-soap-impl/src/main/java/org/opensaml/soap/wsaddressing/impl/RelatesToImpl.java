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

package org.opensaml.soap.wsaddressing.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.schema.impl.XSURIImpl;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.soap.wsaddressing.RelatesTo;

/**
 * Implementation of {@link RelatesTo}.
 */
public class RelatesToImpl extends XSURIImpl implements RelatesTo {
    
    /** RelationshipType attribute value. */
    @Nullable private String relationshipType;
    
    /** Wildcard attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public RelatesToImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
        // Default attribute value per the schema.
        setRelationshipType(RELATIONSHIP_TYPE_REPLY);
    }

    /** {@inheritDoc} */
    @Nullable public String getRelationshipType() {
        return relationshipType;
    }

    /** {@inheritDoc} */
    public void setRelationshipType(@Nullable final String newRelationshipType) {
        relationshipType = prepareForAssignment(relationshipType, newRelationshipType);
    }

    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

}
