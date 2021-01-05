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

package org.opensaml.xmlsec.keyinfo.impl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.ECGenParameterSpec;

import javax.crypto.SecretKey;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.agreement.KeyAgreementCredential;
import org.opensaml.xmlsec.agreement.impl.BasicKeyAgreementCredential;
import org.opensaml.xmlsec.derivation.impl.ConcatKDF;
import org.opensaml.xmlsec.derivation.impl.PBKDF2;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.ConcatKDFParams;
import org.opensaml.xmlsec.encryption.KeyDerivationMethod;
import org.opensaml.xmlsec.encryption.OriginatorKeyInfo;
import org.opensaml.xmlsec.encryption.PBKDF2Params;
import org.opensaml.xmlsec.encryption.RecipientKeyInfo;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class KeyAgreementKeyInfoGeneratorTest extends XMLObjectBaseTestCase {
    
    private KeyPair keyPairOriginatorECDH, keyPairRecipientECDH;
    
    private Credential credOriginatorECDH, credRecipientECDH;
    
    private SecretKey derivedKey;
    
    private KeyAgreementCredential credECDH;
    
    private KeyAgreementKeyInfoGeneratorFactory factory;
    
    @BeforeClass
    public void beforeClass() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        keyPairOriginatorECDH = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, new ECGenParameterSpec("secp256r1"), null);
        credOriginatorECDH = new BasicCredential(keyPairOriginatorECDH.getPublic(), keyPairOriginatorECDH.getPrivate());
        
        keyPairRecipientECDH = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, new ECGenParameterSpec("secp256r1"), null);
        credRecipientECDH = new BasicCredential(keyPairRecipientECDH.getPublic());
        
        derivedKey = KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 256, null);
    }
    
    @BeforeMethod
    public void beforeMethod() {
        factory = new KeyAgreementKeyInfoGeneratorFactory(); 
        
        credECDH = new BasicKeyAgreementCredential(derivedKey, EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES, credOriginatorECDH, credRecipientECDH);
    }
    
    
    @Test
    void ECDHWithConcatKDFWithDefaults() throws Exception {
        ConcatKDF kdf = new ConcatKDF();
        kdf.setDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        kdf.setAlgorithmID("AA");
        kdf.setPartyUInfo("BB");
        kdf.setPartyVInfo("CC");
        kdf.setSuppPubInfo("DD");
        kdf.setSuppPrivInfo("EE");
        kdf.initialize();
        
        credECDH.getParameters().add(kdf);
        
        KeyInfoGenerator generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credECDH);
        
        Assert.assertNotNull(keyInfo);
        Assert.assertNotNull(keyInfo.getOrderedChildren());
        Assert.assertEquals(keyInfo.getOrderedChildren().size(), 1);
        Assert.assertEquals(keyInfo.getAgreementMethods().size(), 1);
        
        AgreementMethod agreementMethod = keyInfo.getAgreementMethods().get(0);
        Assert.assertEquals(agreementMethod.getOrderedChildren().size(), 3);
        
        //Originator
        Assert.assertNotNull(agreementMethod.getOriginatorKeyInfo());
        OriginatorKeyInfo originatorKeyInfo = agreementMethod.getOriginatorKeyInfo();
        Assert.assertEquals(originatorKeyInfo.getOrderedChildren().size(), 1);
        Assert.assertEquals(originatorKeyInfo.getDEREncodedKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(originatorKeyInfo.getDEREncodedKeyValues().get(0)), keyPairOriginatorECDH.getPublic());
        //TODO Can't do these until have support for ECKeyValue <-> PublicKey.  Change KeyInfo children size above also 1 -> 2.
        //Assert.assertEquals(originatorKeyInfo.getKeyValues().size(), 1);
        //Assert.assertEquals(KeyInfoSupport.getKey(originatorKeyInfo.getKeyValues().get(0)), keyPairOriginatorECDH.getPublic());
        
        //Recipient
        Assert.assertNotNull(agreementMethod.getRecipientKeyInfo());
        RecipientKeyInfo recipientKeyInfo = agreementMethod.getRecipientKeyInfo();
        Assert.assertEquals(recipientKeyInfo.getOrderedChildren().size(), 1);
        Assert.assertEquals(recipientKeyInfo.getDEREncodedKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(recipientKeyInfo.getDEREncodedKeyValues().get(0)), keyPairRecipientECDH.getPublic());
        //TODO Can't do these until have support for ECKeyValue <-> PublicKey.  Change KeyInfo children size above also 1 -> 2.
        //Assert.assertEquals(recipientKeyInfo.getKeyValues().size(), 1);
        //Assert.assertEquals(KeyInfoSupport.getKey(recipientKeyInfo.getKeyValues().get(0)), keyPairRecipientECDH.getPublic());
        
        //Params
        Assert.assertEquals(agreementMethod.getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).size(), 1);
        KeyDerivationMethod kdm = (KeyDerivationMethod) agreementMethod.getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).get(0);
        Assert.assertEquals(kdm.getAlgorithm(), EncryptionConstants.ALGO_ID_KEYDERIVATION_CONCATKDF);
        Assert.assertEquals(kdm.getUnknownXMLObjects().size(), 1);
        Assert.assertEquals(kdm.getUnknownXMLObjects(ConcatKDFParams.DEFAULT_ELEMENT_NAME).size(), 1);
        ConcatKDFParams kdfParams = (ConcatKDFParams) kdm.getUnknownXMLObjects(ConcatKDFParams.DEFAULT_ELEMENT_NAME).get(0);
        Assert.assertNotNull(kdfParams.getDigestMethod());
        Assert.assertEquals(kdfParams.getDigestMethod().getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA512);
        Assert.assertEquals(kdfParams.getAlgorithmID(), "00AA");
        Assert.assertEquals(kdfParams.getPartyUInfo(), "00BB");
        Assert.assertEquals(kdfParams.getPartyVInfo(), "00CC");
        Assert.assertEquals(kdfParams.getSuppPubInfo(), "00DD");
        Assert.assertEquals(kdfParams.getSuppPrivInfo(), "00EE");
    }

    @Test
    void ECDHWithPBKDF2WithDefaults() throws Exception {
        PBKDF2 kdf = new PBKDF2();
        kdf.setIterationCount(1500);
        kdf.setKeyLength(256);
        kdf.setPRF(SignatureConstants.ALGO_ID_MAC_HMAC_SHA512);
        kdf.setSalt("ABCD");
        kdf.initialize();
        
        credECDH.getParameters().add(kdf);
        
        KeyInfoGenerator generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credECDH);
        
        Assert.assertNotNull(keyInfo);
        Assert.assertNotNull(keyInfo.getOrderedChildren());
        Assert.assertEquals(keyInfo.getOrderedChildren().size(), 1);
        Assert.assertEquals(keyInfo.getAgreementMethods().size(), 1);
        
        AgreementMethod agreementMethod = keyInfo.getAgreementMethods().get(0);
        Assert.assertEquals(agreementMethod.getOrderedChildren().size(), 3);
        
        //Originator
        Assert.assertNotNull(agreementMethod.getOriginatorKeyInfo());
        OriginatorKeyInfo originatorKeyInfo = agreementMethod.getOriginatorKeyInfo();
        Assert.assertEquals(originatorKeyInfo.getOrderedChildren().size(), 1);
        Assert.assertEquals(originatorKeyInfo.getDEREncodedKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(originatorKeyInfo.getDEREncodedKeyValues().get(0)), keyPairOriginatorECDH.getPublic());
        //TODO Can't do these until have support for ECKeyValue <-> PublicKey.  Change KeyInfo children size above also 1 -> 2.
        //Assert.assertEquals(originatorKeyInfo.getKeyValues().size(), 1);
        //Assert.assertEquals(KeyInfoSupport.getKey(originatorKeyInfo.getKeyValues().get(0)), keyPairOriginatorECDH.getPublic());
        
        //Recipient
        Assert.assertNotNull(agreementMethod.getRecipientKeyInfo());
        RecipientKeyInfo recipientKeyInfo = agreementMethod.getRecipientKeyInfo();
        Assert.assertEquals(recipientKeyInfo.getOrderedChildren().size(), 1);
        Assert.assertEquals(recipientKeyInfo.getDEREncodedKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(recipientKeyInfo.getDEREncodedKeyValues().get(0)), keyPairRecipientECDH.getPublic());
        //TODO Can't do these until have support for ECKeyValue <-> PublicKey.  Change KeyInfo children size above also 1 -> 2.
        //Assert.assertEquals(recipientKeyInfo.getKeyValues().size(), 1);
        //Assert.assertEquals(KeyInfoSupport.getKey(recipientKeyInfo.getKeyValues().get(0)), keyPairRecipientECDH.getPublic());
        
        //Params
        Assert.assertEquals(agreementMethod.getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).size(), 1);
        KeyDerivationMethod kdm = (KeyDerivationMethod) agreementMethod.getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).get(0);
        Assert.assertEquals(kdm.getAlgorithm(), EncryptionConstants.ALGO_ID_KEYDERIVATION_PBKDF2);
        Assert.assertEquals(kdm.getUnknownXMLObjects().size(), 1);
        Assert.assertEquals(kdm.getUnknownXMLObjects(PBKDF2Params.DEFAULT_ELEMENT_NAME).size(), 1);
        PBKDF2Params kdfParams = (PBKDF2Params) kdm.getUnknownXMLObjects(PBKDF2Params.DEFAULT_ELEMENT_NAME).get(0);
        Assert.assertNotNull(kdfParams.getIterationCount());
        Assert.assertEquals(kdfParams.getIterationCount().getValue().intValue(), 1500);
        Assert.assertNotNull(kdfParams.getKeyLength());
        Assert.assertEquals(kdfParams.getKeyLength().getValue().intValue(), 256/8); // bytes
        Assert.assertNotNull(kdfParams.getPRF());
        Assert.assertEquals(kdfParams.getPRF().getAlgorithm(), SignatureConstants.ALGO_ID_MAC_HMAC_SHA512);
        Assert.assertNotNull(kdfParams.getSalt());
        Assert.assertNotNull(kdfParams.getSalt().getSpecified());
        Assert.assertEquals(kdfParams.getSalt().getSpecified().getValue(), "ABCD");
    }
    
    @Test
    public void noEmitKeyinfos() throws Exception {
        factory.setEmitOriginatorKeyInfo(false);
        factory.setEmitRecipientKeyInfo(false);
        
        ConcatKDF kdf = new ConcatKDF();
        kdf.setDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        kdf.setAlgorithmID("AA");
        kdf.setPartyUInfo("BB");
        kdf.setPartyVInfo("CC");
        kdf.setSuppPubInfo("DD");
        kdf.setSuppPrivInfo("EE");
        kdf.initialize();
        
        credECDH.getParameters().add(kdf);
        
        KeyInfoGenerator generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credECDH);
        
        Assert.assertNotNull(keyInfo);
        Assert.assertNotNull(keyInfo.getOrderedChildren());
        Assert.assertEquals(keyInfo.getOrderedChildren().size(), 1);
        Assert.assertEquals(keyInfo.getAgreementMethods().size(), 1);
        
        AgreementMethod agreementMethod = keyInfo.getAgreementMethods().get(0);
        Assert.assertEquals(agreementMethod.getOrderedChildren().size(), 1);
        
        Assert.assertEquals(agreementMethod.getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).size(), 1);
        Assert.assertNull(agreementMethod.getOriginatorKeyInfo());
        Assert.assertNull(agreementMethod.getRecipientKeyInfo());
    }
     
    @Test
    public void noKeyInfoManagers() throws Exception {
        factory.setOriginatorKeyInfoGeneratorManager(null);
        factory.setRecipientKeyInfoGeneratorManager(null);
        
        ConcatKDF kdf = new ConcatKDF();
        kdf.setDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        kdf.setAlgorithmID("AA");
        kdf.setPartyUInfo("BB");
        kdf.setPartyVInfo("CC");
        kdf.setSuppPubInfo("DD");
        kdf.setSuppPrivInfo("EE");
        kdf.initialize();
        
        credECDH.getParameters().add(kdf);
        
        KeyInfoGenerator generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credECDH);
        
        Assert.assertNotNull(keyInfo);
        Assert.assertNotNull(keyInfo.getOrderedChildren());
        Assert.assertEquals(keyInfo.getOrderedChildren().size(), 1);
        Assert.assertEquals(keyInfo.getAgreementMethods().size(), 1);
        
        AgreementMethod agreementMethod = keyInfo.getAgreementMethods().get(0);
        Assert.assertEquals(agreementMethod.getOrderedChildren().size(), 1);
        
        Assert.assertEquals(agreementMethod.getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).size(), 1);
        Assert.assertNull(agreementMethod.getOriginatorKeyInfo());
        Assert.assertNull(agreementMethod.getRecipientKeyInfo());
    }
     

}
