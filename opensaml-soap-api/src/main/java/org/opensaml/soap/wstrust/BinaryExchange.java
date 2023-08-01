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

package org.opensaml.soap.wstrust;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.schema.XSString;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * The wst:BinaryExchange element.
 * 
 * @see "WS-Trust 1.3, Chapter 8.3 Binary Exchanges and Negotiations."
 * 
 */
public interface BinaryExchange extends XSString, AttributeExtensibleXMLObject, WSTrustObject {

    /** Element local name. */
    @Nonnull @NotEmpty public static final String ELEMENT_LOCAL_NAME = "BinaryExchange";

    /** Default element name. */
    @Nonnull public static final QName ELEMENT_NAME =
        new QName(WSTrustConstants.WST_NS, ELEMENT_LOCAL_NAME, WSTrustConstants.WST_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "BinaryExchangeType"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = 
        new QName(WSTrustConstants.WST_NS, TYPE_LOCAL_NAME, WSTrustConstants.WST_PREFIX);
    
    /** The ValueType attribute name. */
    @Nonnull @NotEmpty public static final String VALUE_TYPE_ATTRIB_NAME = "ValueType";
    
    /** The EncodingType attribute name. */
    @Nonnull @NotEmpty public static final String ENCODING_TYPE_ATTRIB_NAME = "EncodingType";
    
    /**
     * Returns the ValueType attribute URI value.
     * 
     * @return the ValueType attribute value or <code>null</code>.
     */
    @Nullable public String getValueType();

    /**
     * Sets the ValueType attribute URI value.
     * 
     * @param newValueType the ValueType attribute value.
     */
    public void setValueType(@Nullable final String newValueType);
    
    /**
     * Returns the EncodingType attribute value.
     * 
     * @return the EncodingType attribute value.
     */
    @Nullable public String getEncodingType();

    /**
     * Sets the EncodingType attribute value.
     * 
     * @param newEncodingType the EncodingType attribute value.
     */
    public void setEncodingType(@Nullable final String newEncodingType);

}
