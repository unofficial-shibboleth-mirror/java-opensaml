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
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.xmlsec.encryption.ConcatKDFParams;
import org.w3c.dom.Element;

/**
 * A thread-safe Marshaller for {@link org.opensaml.xmlsec.encryption.ConcatKDFParams} objects.
 */
public class ConcatKDFParamsMarshaller extends AbstractXMLEncryptionMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final ConcatKDFParams params = (ConcatKDFParams) xmlObject;

        if (params.getAlgorithmID() != null) {
            domElement.setAttributeNS(null, ConcatKDFParams.ALGORITHM_ID_ATTRIBUTE_NAME, params.getAlgorithmID());
        }
        if (params.getPartyUInfo() != null) {
            domElement.setAttributeNS(null, ConcatKDFParams.PARTY_U_INFO_ATTRIBUTE_NAME, params.getPartyUInfo());
        }
        if (params.getPartyVInfo() != null) {
            domElement.setAttributeNS(null, ConcatKDFParams.PARTY_V_INFO_ATTRIBUTE_NAME, params.getPartyVInfo());
        }
        if (params.getSuppPubInfo() != null) {
            domElement.setAttributeNS(null, ConcatKDFParams.SUPP_PUB_INFO_ATTRIBUTE_NAME, params.getSuppPubInfo());
        }
        if (params.getSuppPrivInfo() != null) {
            domElement.setAttributeNS(null, ConcatKDFParams.SUPP_PRIV_INFO_ATTRIBUTE_NAME, params.getSuppPrivInfo());
        }
        
        super.marshallAttributes(xmlObject, domElement);
    }

}
