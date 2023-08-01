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
import org.opensaml.soap.wssecurity.Reference;

/**
 * ReferenceImpl.
 * 
 */
public class ReferenceImpl extends AbstractWSSecurityObject implements Reference {
    
    /** wsse:Reference/@URI attribute. */
    @Nullable private String uri;

    /** wsse:Reference/@ValueType attribute. */
    @Nullable private String valueType;
    
    /** Wildcard attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public ReferenceImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getURI() {
        return uri;
    }

    /** {@inheritDoc} */
    public void setURI(@Nullable final String newURI) {
        uri = prepareForAssignment(uri, newURI);
    }

    /** {@inheritDoc} */
    @Nullable public String getValueType() {
        return valueType;
    }

    /** {@inheritDoc} */
    public void setValueType(@Nullable final String newValueType) {
        valueType = prepareForAssignment(valueType, newValueType);
    }

    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

}
