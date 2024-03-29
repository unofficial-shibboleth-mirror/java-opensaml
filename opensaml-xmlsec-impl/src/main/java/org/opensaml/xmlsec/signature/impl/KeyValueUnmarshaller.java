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

package org.opensaml.xmlsec.signature.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xmlsec.encryption.DHKeyValue;
import org.opensaml.xmlsec.signature.DSAKeyValue;
import org.opensaml.xmlsec.signature.ECKeyValue;
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.RSAKeyValue;

/**
 * A thread-safe Unmarshaller for {@link KeyValue} objects.
 */
public class KeyValueUnmarshaller extends AbstractXMLSignatureUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final KeyValue keyValue = (KeyValue) parentXMLObject;

        if (childXMLObject instanceof DSAKeyValue) {
            keyValue.setDSAKeyValue((DSAKeyValue) childXMLObject);
        } else if (childXMLObject instanceof RSAKeyValue) {
            keyValue.setRSAKeyValue((RSAKeyValue) childXMLObject);
        } else if (childXMLObject instanceof ECKeyValue) {
            keyValue.setECKeyValue((ECKeyValue) childXMLObject);
        } else if (childXMLObject instanceof DHKeyValue) {
            keyValue.setDHKeyValue((DHKeyValue) childXMLObject);
        } else {
            // There can be only one...
            if (keyValue.getUnknownXMLObject() == null) {
                keyValue.setUnknownXMLObject(childXMLObject);
            } else {
                // If this happens, throw the others up to the parent class to be logged/handled.
                super.processChildElement(parentXMLObject, childXMLObject);
            }
        }
    }

}