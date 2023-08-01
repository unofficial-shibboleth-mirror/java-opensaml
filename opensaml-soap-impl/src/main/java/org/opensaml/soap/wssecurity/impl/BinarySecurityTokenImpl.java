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

import org.opensaml.soap.wssecurity.BinarySecurityToken;

/**
 * BinarySecurityTokenImpl.
 */
public class BinarySecurityTokenImpl extends EncodedStringImpl implements BinarySecurityToken {

    /** wsse:BinarySecurityToken/@ValueType attribute. */
    @Nullable private String valueType;

    /**
     * Constructor. Default EncodingType is <code>BinarySecurityToken.ENCODINGTYPE_BASE64_BINARY</code>
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public BinarySecurityTokenImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        // default encoding type
        setEncodingType(BinarySecurityToken.ENCODING_TYPE_BASE64_BINARY);
    }

    /** {@inheritDoc} */
    @Nullable public String getValueType() {
        return valueType;
    }

    /** {@inheritDoc} */
    public void setValueType(@Nullable final String newValueType) {
        valueType = prepareForAssignment(valueType, newValueType);
    }

}
