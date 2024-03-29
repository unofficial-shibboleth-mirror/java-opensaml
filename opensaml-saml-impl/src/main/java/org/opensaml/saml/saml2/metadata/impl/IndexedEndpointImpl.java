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

/**
 * 
 */

package org.opensaml.saml.saml2.metadata.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.saml2.metadata.IndexedEndpoint;

/**
 * Concrete implementation of {@link IndexedEndpoint}.
 */
public abstract class IndexedEndpointImpl extends EndpointImpl implements IndexedEndpoint {

    /** Index of this endpoint. */
    @Nullable private Integer index;

    /** isDefault attribute. */
    @Nullable private XSBooleanValue isDefault;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected IndexedEndpointImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public Integer getIndex() {
        return index;
    }

    /** {@inheritDoc} */
    public void setIndex(@Nullable final Integer theIndex) {
        index = prepareForAssignment(index, theIndex);
    }
    
    /** {@inheritDoc} */
    @Nullable public Boolean isDefault() {
        if (isDefault != null) {
            return isDefault.getValue();
        }
        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue isDefaultXSBoolean() {
        return isDefault;
    }
    
    /** {@inheritDoc} */
    public void setIsDefault(@Nullable final Boolean newIsDefault){
        if(newIsDefault != null){
            isDefault = prepareForAssignment(isDefault, new XSBooleanValue(newIsDefault, false));
        }else{
            isDefault = prepareForAssignment(isDefault, null);
        }
    }

    /** {@inheritDoc} */
    public void setIsDefault(@Nullable final XSBooleanValue theIsDefault) {
        isDefault = prepareForAssignment(isDefault, theIsDefault);
    }

}