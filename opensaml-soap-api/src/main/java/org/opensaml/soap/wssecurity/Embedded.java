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

package org.opensaml.soap.wssecurity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.ElementExtensibleXMLObject;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * the &lt;wsse:Embedded&gt; element.
 * 
 */
public interface Embedded extends AttributeExtensibleXMLObject, ElementExtensibleXMLObject, WSSecurityObject {
    
    /** Element local name. */
    @Nonnull @NotEmpty public static final String ELEMENT_LOCAL_NAME = "Embedded";

    /** Qualified element name. */
    @Nonnull public static final QName ELEMENT_NAME =
        new QName(WSSecurityConstants.WSSE_NS, ELEMENT_LOCAL_NAME, WSSecurityConstants.WSSE_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "EmbeddedType"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = 
        new QName(WSSecurityConstants.WSSE_NS, TYPE_LOCAL_NAME, WSSecurityConstants.WSSE_PREFIX);

    /** The wsse:Embedded/@ValueType attribute local name. */
    @Nonnull @NotEmpty public static final String VALUE_TYPE_ATTRIB_NAME= "ValueType";

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
    
}
