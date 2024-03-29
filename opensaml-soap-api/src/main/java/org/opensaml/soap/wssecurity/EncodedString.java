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

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface for elements of complex type EncodedString.
 */
public interface EncodedString extends AttributedString {
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "EncodedString"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = 
        new QName(WSSecurityConstants.WSSE_NS, TYPE_LOCAL_NAME, WSSecurityConstants.WSSE_PREFIX);
    
    /** The EncodingType attribute name. */
    @Nonnull @NotEmpty public static final String ENCODING_TYPE_ATTRIB_NAME = "EncodingType";
    
    /** The EncodingType attribute value <code>#Base64Binary</code>. */
    @Nonnull @NotEmpty
    public static final String ENCODING_TYPE_BASE64_BINARY = WSSecurityConstants.WS_SECURITY_NS + "#Base64Binary";
    
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
