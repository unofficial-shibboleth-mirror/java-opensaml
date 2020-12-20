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

package org.opensaml.xmlsec.agreement.impl;

import java.security.KeyPair;
import java.security.spec.ECGenParameterSpec;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.agreement.KeyAgreementCredential;
import org.opensaml.xmlsec.agreement.KeyAgreementException;
import org.opensaml.xmlsec.agreement.KeyAgreementParameters;
import org.opensaml.xmlsec.derivation.impl.MockKeyDerivation;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class ECDHKeyAgreementProcessorTest {
    
    private ECDHKeyAgreementProcessor processor;
    
    @BeforeMethod
    public void setUp() {
        processor = new ECDHKeyAgreementProcessor();
    }
    
    @Test
    public void encryptingCase() throws Exception {
        KeyPair recipientKeyPair = KeySupport.generateKeyPair("EC", new ECGenParameterSpec("secp256r1"), null);
        Credential recipientCredential = CredentialSupport.getSimpleCredential(recipientKeyPair.getPublic(), null);
        
        KeyAgreementParameters params = new KeyAgreementParameters();
        params.add(new MockKeyDerivation()); 
        params.add(new KANonce("someBase64")); 
        
        KeyAgreementCredential keyAgreementCredential = processor.execute(recipientCredential,
                JCAConstants.KEY_ALGO_AES,
                128,
                params);
        
        Assert.assertNotNull(keyAgreementCredential);
        
        Assert.assertNotNull(keyAgreementCredential.getSecretKey());
        Assert.assertEquals(keyAgreementCredential.getSecretKey().getAlgorithm(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(KeySupport.getKeyLength(keyAgreementCredential.getSecretKey()), Integer.valueOf(128));
        
        Assert.assertNull(keyAgreementCredential.getPublicKey());
        Assert.assertNull(keyAgreementCredential.getPrivateKey());
        
        Assert.assertNotNull(keyAgreementCredential.getRecipientCredential());
        Assert.assertNotNull(keyAgreementCredential.getRecipientCredential().getPublicKey());
        Assert.assertNull(keyAgreementCredential.getRecipientCredential().getPrivateKey());
        Assert.assertNull(keyAgreementCredential.getRecipientCredential().getSecretKey());
        
        Assert.assertNotNull(keyAgreementCredential.getOriginatorCredential());
        Assert.assertNotNull(keyAgreementCredential.getOriginatorCredential().getPublicKey());
        Assert.assertNotNull(keyAgreementCredential.getOriginatorCredential().getPrivateKey());
        Assert.assertNull(keyAgreementCredential.getOriginatorCredential().getSecretKey());
        
        Assert.assertEquals(keyAgreementCredential.getAlgorithm(), EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        
        Assert.assertEquals(keyAgreementCredential.getParameters().size(), 2);
        Assert.assertTrue(keyAgreementCredential.getParameters().contains(MockKeyDerivation.class));
        Assert.assertTrue(keyAgreementCredential.getParameters().contains(KANonce.class));
        Assert.assertEquals(keyAgreementCredential.getParameters().get(KANonce.class).getValue(), "someBase64");
        
    }
    
    @Test
    public void decryptingCase() throws Exception {
        KeyPair originatorKeyPair = KeySupport.generateKeyPair("EC", new ECGenParameterSpec("secp256r1"), null);
        Credential originatorCredential = CredentialSupport.getSimpleCredential(originatorKeyPair.getPublic(), null);
        
        KeyPair recipientKeyPair = KeySupport.generateKeyPair("EC", new ECGenParameterSpec("secp256r1"), null);
        Credential recipientCredential = CredentialSupport.getSimpleCredential(recipientKeyPair.getPublic(), recipientKeyPair.getPrivate());
        
        KeyAgreementParameters params = new KeyAgreementParameters();
        params.add(new PrivateCredential(recipientCredential));
        params.add(new MockKeyDerivation()); 
        params.add(new KANonce("someBase64")); 
        
        KeyAgreementCredential keyAgreementCredential = processor.execute(originatorCredential,
                JCAConstants.KEY_ALGO_AES,
                128,
                params);
        
        Assert.assertNotNull(keyAgreementCredential);
        
        Assert.assertNotNull(keyAgreementCredential.getSecretKey());
        Assert.assertEquals(keyAgreementCredential.getSecretKey().getAlgorithm(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(KeySupport.getKeyLength(keyAgreementCredential.getSecretKey()), Integer.valueOf(128));
        
        Assert.assertNull(keyAgreementCredential.getPublicKey());
        Assert.assertNull(keyAgreementCredential.getPrivateKey());
        
        Assert.assertNotNull(keyAgreementCredential.getRecipientCredential());
        Assert.assertNotNull(keyAgreementCredential.getRecipientCredential().getPublicKey());
        Assert.assertNotNull(keyAgreementCredential.getRecipientCredential().getPrivateKey());
        Assert.assertNull(keyAgreementCredential.getRecipientCredential().getSecretKey());
        
        Assert.assertNotNull(keyAgreementCredential.getOriginatorCredential());
        Assert.assertNotNull(keyAgreementCredential.getOriginatorCredential().getPublicKey());
        Assert.assertNull(keyAgreementCredential.getOriginatorCredential().getPrivateKey());
        Assert.assertNull(keyAgreementCredential.getOriginatorCredential().getSecretKey());
        
        Assert.assertEquals(keyAgreementCredential.getAlgorithm(), EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        
        Assert.assertEquals(keyAgreementCredential.getParameters().size(), 3);
        Assert.assertTrue(keyAgreementCredential.getParameters().contains(PrivateCredential.class));
        Assert.assertTrue(keyAgreementCredential.getParameters().contains(MockKeyDerivation.class));
        Assert.assertTrue(keyAgreementCredential.getParameters().contains(KANonce.class));
        Assert.assertEquals(keyAgreementCredential.getParameters().get(KANonce.class).getValue(), "someBase64");
        
    }

    @Test(expectedExceptions = KeyAgreementException.class)
    public void nonECCred() throws Exception {
        KeyPair kp = KeySupport.generateKeyPair("RSA", 2048, null);
        Credential publicCredential = CredentialSupport.getSimpleCredential(kp.getPublic(), null);
        
        KeyAgreementParameters params = new KeyAgreementParameters();
        params.add(new MockKeyDerivation()); 
        params.add(new KANonce("someBase64")); 
        
        processor.execute(publicCredential,
                JCAConstants.KEY_ALGO_AES,
                128,
                params);
    }
    
    @Test(expectedExceptions = KeyAgreementException.class)
    public void keyDerivationError() throws Exception {
        KeyPair kp = KeySupport.generateKeyPair("EC", new ECGenParameterSpec("secp256r1"), null);
        Credential publicCredential = CredentialSupport.getSimpleCredential(kp.getPublic(), null);
        
        KeyAgreementParameters params = new KeyAgreementParameters();
        params.add(new MockKeyDerivation()); 
        params.add(new KANonce("someBase64")); 
        
        processor.execute(publicCredential,
                "INVALID",
                128,
                params);
    }
    
    @Test(expectedExceptions = KeyAgreementException.class)
    public void missingKeyDerivationParam() throws Exception {
        KeyPair kp = KeySupport.generateKeyPair("EC", new ECGenParameterSpec("secp256r1"), null);
        Credential publicCredential = CredentialSupport.getSimpleCredential(kp.getPublic(), null);
        
        KeyAgreementParameters params = new KeyAgreementParameters();
        params.add(new KANonce("someBase64")); 
        
        processor.execute(publicCredential,
                JCAConstants.KEY_ALGO_AES,
                128,
                params);
    }
        
}
