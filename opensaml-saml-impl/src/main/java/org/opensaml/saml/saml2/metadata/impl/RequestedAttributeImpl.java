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
import org.opensaml.saml.saml2.core.impl.AttributeImpl;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;

/**
 * Concrete implementation of {@link RequestedAttribute}.
 */
public class RequestedAttributeImpl extends AttributeImpl implements RequestedAttribute {

    /** isRequired attribute. */
    @Nullable private XSBooleanValue isRequired;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected RequestedAttributeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
    
    /** {@inheritDoc} */
    @Nullable public Boolean isRequired(){
        if(isRequired != null){
            return isRequired.getValue();
        }
        
        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue isRequiredXSBoolean() {
        return isRequired;
    }
    
    /** {@inheritDoc} */
    public void setIsRequired(@Nullable final Boolean newIsRequired){
        if(newIsRequired != null){
            isRequired = prepareForAssignment(isRequired, new XSBooleanValue(newIsRequired, false));
        }else{
            isRequired = prepareForAssignment(isRequired, null);
        }
    }

    /** {@inheritDoc} */
    public void setIsRequired(@Nullable final XSBooleanValue newIsRequired) {
        isRequired = prepareForAssignment(isRequired, newIsRequired);

    }

}