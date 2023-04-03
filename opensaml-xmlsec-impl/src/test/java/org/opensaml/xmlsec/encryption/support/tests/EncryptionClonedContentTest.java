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

package org.opensaml.xmlsec.encryption.support.tests;

import javax.annotation.Nonnull;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.encryption.support.Decrypter;
import org.opensaml.xmlsec.encryption.support.Encrypter;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

@SuppressWarnings("javadoc")
public class EncryptionClonedContentTest extends XMLObjectBaseTestCase {
    
    private String targetFile;
    
    @Nonnull private final String algoURI = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
    
    public EncryptionClonedContentTest() {
        targetFile = "/org/opensaml/xmlsec/encryption/support/SimpleEncryptionTest.xml";
    }

    @Test
    public void testEncryptDecrypt() throws Exception {
            final SignableSimpleXMLObject origXMLObject = (SignableSimpleXMLObject) unmarshallElement(targetFile);
            assert origXMLObject != null;
            final SignableSimpleXMLObject clonedXMLObject = XMLObjectSupport.cloneXMLObject(origXMLObject);
            
            final Credential encCred = AlgorithmSupport.generateSymmetricKeyAndCredential(algoURI);
            
            final DataEncryptionParameters encParams = new DataEncryptionParameters();
            encParams.setAlgorithm(algoURI);
            encParams.setEncryptionCredential(encCred);
            
            Encrypter encrypter = new Encrypter();
            
            final EncryptedData encryptedData = encrypter.encryptElement(clonedXMLObject, encParams);
            Assert.assertNotNull(encryptedData);
            
            final EncryptionMethod method = encryptedData.getEncryptionMethod();
            assert method != null;
            Assert.assertEquals(method.getAlgorithm(), algoURI);
            
            final StaticKeyInfoCredentialResolver dataKeyInfoResolver = new StaticKeyInfoCredentialResolver(encCred);
            
            final Decrypter decrypter = new Decrypter(dataKeyInfoResolver, null, null);
            
            final XMLObject decryptedXMLObject = decrypter.decryptData(encryptedData);
            
            Assert.assertNotNull(decryptedXMLObject);
            Assert.assertTrue(decryptedXMLObject instanceof SignableSimpleXMLObject);
            
            final Element origDOM = origXMLObject.getDOM();
            assert origDOM != null;
            assertXMLEquals(origDOM.getOwnerDocument(), decryptedXMLObject);
    }
}
