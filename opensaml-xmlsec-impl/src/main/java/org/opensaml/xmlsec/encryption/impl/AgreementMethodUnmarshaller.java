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

package org.opensaml.xmlsec.encryption.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.KANonce;
import org.opensaml.xmlsec.encryption.OriginatorKeyInfo;
import org.opensaml.xmlsec.encryption.RecipientKeyInfo;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link org.opensaml.xmlsec.encryption.AgreementMethod} objects.
 */
public class AgreementMethodUnmarshaller extends AbstractXMLEncryptionUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final AgreementMethod am = (AgreementMethod) xmlObject;

        if (attribute.getLocalName().equals(AgreementMethod.ALGORITHM_ATTRIBUTE_NAME)) {
            am.setAlgorithm(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final AgreementMethod am = (AgreementMethod) parentXMLObject;

        if (childXMLObject instanceof KANonce) {
            am.setKANonce((KANonce) childXMLObject);
        } else if (childXMLObject instanceof OriginatorKeyInfo) {
            am.setOriginatorKeyInfo((OriginatorKeyInfo) childXMLObject);
        } else if (childXMLObject instanceof RecipientKeyInfo) {
            am.setRecipientKeyInfo((RecipientKeyInfo) childXMLObject);
        } else {
            am.getUnknownXMLObjects().add(childXMLObject);
        }
    }

}
