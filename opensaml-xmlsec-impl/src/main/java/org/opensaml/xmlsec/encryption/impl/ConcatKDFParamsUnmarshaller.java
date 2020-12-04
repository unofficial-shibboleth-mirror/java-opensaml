/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xmlsec.encryption.ConcatKDFParams;
import org.opensaml.xmlsec.signature.DigestMethod;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link org.opensaml.xmlsec.encryption.ConcatKDFParams} objects.
 */
public class ConcatKDFParamsUnmarshaller extends AbstractXMLEncryptionUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(final XMLObject xmlObject, final Attr attribute) throws UnmarshallingException {
        final ConcatKDFParams params = (ConcatKDFParams) xmlObject;

        if (attribute.getLocalName().equals(ConcatKDFParams.ALGORITHM_ID_ATTRIBUTE_NAME)) {
            params.setAlgorithmID(attribute.getValue());
        } else if (attribute.getLocalName().equals(ConcatKDFParams.PARTY_U_INFO_ATTRIBUTE_NAME)) {
            params.setPartyUInfo(attribute.getValue());
        } else if (attribute.getLocalName().equals(ConcatKDFParams.PARTY_V_INFO_ATTRIBUTE_NAME)) {
            params.setPartyVInfo(attribute.getValue());
        } else if (attribute.getLocalName().equals(ConcatKDFParams.SUPP_PUB_INFO_ATTRIBUTE_NAME)) {
            params.setSuppPubInfo(attribute.getValue());
        } else if (attribute.getLocalName().equals(ConcatKDFParams.SUPP_PRIV_INFO_ATTRIBUTE_NAME)) {
            params.setSuppPrivInfo(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processChildElement(final XMLObject parentXMLObject, final XMLObject childXMLObject)
            throws UnmarshallingException {
        final ConcatKDFParams params = (ConcatKDFParams) parentXMLObject;

        if (childXMLObject instanceof DigestMethod) {
            params.setDigestMethod((DigestMethod) childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}
