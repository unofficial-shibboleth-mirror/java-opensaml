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

package org.opensaml.xmlsec.agreement.tests;

import java.security.KeyPair;
import java.security.spec.ECGenParameterSpec;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.agreement.KeyAgreementException;
import org.opensaml.xmlsec.agreement.KeyAgreementSupport;
import org.opensaml.xmlsec.agreement.impl.ECDHKeyAgreementProcessor;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.KeySize;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class KeyAgreementSupportTest extends XMLObjectBaseTestCase {
    
    @Test
    public void getGlobalProcessorRegistry() throws Exception {
        Assert.assertNotNull(KeyAgreementSupport.getGlobalProcessorRegistry());
    }
    
    @Test
    public void getProcessor() throws Exception {
        Assert.assertNotNull(KeyAgreementSupport.getProcessor(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES));
        Assert.assertTrue(ECDHKeyAgreementProcessor.class.isInstance(
                KeyAgreementSupport.getProcessor(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES)));
    }

    @Test
    public void getExplicitKeySize() throws Exception {
        AgreementMethod agreementMethod = buildXMLObject(AgreementMethod.DEFAULT_ELEMENT_NAME);
        Assert.assertNull(KeyAgreementSupport.getExplicitKeySize(agreementMethod));
        
        KeyInfo keyInfo = buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        keyInfo.getAgreementMethods().add(agreementMethod);
        Assert.assertNull(KeyAgreementSupport.getExplicitKeySize(agreementMethod));
        
        EncryptedData encryptedData = buildXMLObject(EncryptedData.DEFAULT_ELEMENT_NAME);
        encryptedData.setKeyInfo(keyInfo);
        Assert.assertNull(KeyAgreementSupport.getExplicitKeySize(agreementMethod));
        
        EncryptionMethod encryptionMethod = buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME);
        encryptedData.setEncryptionMethod(encryptionMethod);
        Assert.assertNull(KeyAgreementSupport.getExplicitKeySize(agreementMethod));
        
        KeySize keySize = buildXMLObject(KeySize.DEFAULT_ELEMENT_NAME);
        keySize.setValue(128);
        encryptionMethod.setKeySize(keySize);
        Assert.assertEquals(KeyAgreementSupport.getExplicitKeySize(agreementMethod), Integer.valueOf(128));
    }
    
    @Test
    public void supportsKeyAgreement() throws Exception {
        KeyPair ecKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, new ECGenParameterSpec("secp256r1"), null);
        Assert.assertTrue(KeyAgreementSupport.supportsKeyAgreement(
                CredentialSupport.getSimpleCredential(ecKeyPair.getPublic(), null)));
        
        Assert.assertFalse(KeyAgreementSupport.supportsKeyAgreement(
                AlgorithmSupport.generateKeyPairAndCredential(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11, 2048, false)));
        Assert.assertFalse(KeyAgreementSupport.supportsKeyAgreement(
                AlgorithmSupport.generateKeyPairAndCredential(SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA256, 1024, false)));
    }
    
    @Test
    public void validateKeyAlgorithmAndSize() throws Exception {
        KeyAgreementSupport.validateKeyAlgorithmAndSize(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM, null);
        KeyAgreementSupport.validateKeyAlgorithmAndSize(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM, 128);
        
        KeyAgreementSupport.validateKeyAlgorithmAndSize(EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES, null);
        KeyAgreementSupport.validateKeyAlgorithmAndSize(EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES, 192);
        
        KeyAgreementSupport.validateKeyAlgorithmAndSize(EncryptionConstants.ALGO_ID_KEYWRAP_AES128, null);
        KeyAgreementSupport.validateKeyAlgorithmAndSize(EncryptionConstants.ALGO_ID_KEYWRAP_AES128, 128);
        
        // Non-length algorithm with non-null specified length should succeed
        KeyAgreementSupport.validateKeyAlgorithmAndSize("SomeAlgo", 128);
        
        try {
            KeyAgreementSupport.validateKeyAlgorithmAndSize(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, 256);
            Assert.fail("Should have failed mismatched specified length");
        } catch (KeyAgreementException e) {
            // expected
        }
        
        try {
            KeyAgreementSupport.validateKeyAlgorithmAndSize("SomeAlgo", null);
            Assert.fail("Should have failed non-length URI and null specified length");
        } catch (KeyAgreementException e) {
            // expected
        }
    }
    
}
