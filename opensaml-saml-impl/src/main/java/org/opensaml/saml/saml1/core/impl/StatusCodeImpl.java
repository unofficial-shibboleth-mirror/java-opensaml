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

package org.opensaml.saml.saml1.core.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml1.core.StatusCode;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link StatusCode}.
 */
public class StatusCodeImpl extends AbstractXMLObject implements StatusCode {

    /** Contents of the Value attribute. */
    @Nullable private QName value;

    /** The child StatusCode sub element. */
    @Nullable private StatusCode childStatusCode;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected StatusCodeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public QName getValue() {
        return value;
    }

    /** {@inheritDoc} */
    public void setValue(@Nullable final QName newValue) {
        value = prepareAttributeValueForAssignment(StatusCode.VALUE_ATTRIB_NAME, value, newValue);
    }

    /** {@inheritDoc} */
    @Nullable public StatusCode getStatusCode() {
        return childStatusCode;
    }

    /** {@inheritDoc} */
    public void setStatusCode(@Nullable final StatusCode statusCode) {
        childStatusCode = prepareForAssignment(childStatusCode, statusCode);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        
        if (childStatusCode != null) {
            return CollectionSupport.singletonList(childStatusCode);
        }
        
        return null;
    }

}