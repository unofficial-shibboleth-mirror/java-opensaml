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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.soap.wssecurity.BinarySecurityToken;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

/**
 * BinarySecurityTokenMarshaller.
 */
public class BinarySecurityTokenMarshaller extends EncodedStringMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final BinarySecurityToken token = (BinarySecurityToken) xmlObject;
        if (!Strings.isNullOrEmpty(token.getValueType())) {
            domElement.setAttributeNS(null, BinarySecurityToken.ENCODING_TYPE_ATTRIB_NAME, token.getValueType());
        }
        super.marshallAttributes(xmlObject, domElement);
    }

}
