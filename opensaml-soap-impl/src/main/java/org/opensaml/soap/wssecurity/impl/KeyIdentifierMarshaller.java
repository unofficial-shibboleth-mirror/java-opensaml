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
import org.opensaml.soap.wssecurity.KeyIdentifier;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

/**
 * KeyIdentifierMarshaller.
 * 
 */
public class KeyIdentifierMarshaller extends EncodedStringMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final KeyIdentifier keyIdentifier = (KeyIdentifier) xmlObject;
        if (!Strings.isNullOrEmpty(keyIdentifier.getValueType())) {
            domElement.setAttributeNS(null, KeyIdentifier.VALUE_TYPE_ATTRIB_NAME, keyIdentifier.getValueType());
        }
        super.marshallAttributes(xmlObject, domElement);
    }

}
