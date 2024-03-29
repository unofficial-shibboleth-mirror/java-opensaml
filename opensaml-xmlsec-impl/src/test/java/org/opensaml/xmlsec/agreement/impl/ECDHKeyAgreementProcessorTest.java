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

package org.opensaml.xmlsec.agreement.impl;

import java.security.KeyPair;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Collection;

import javax.crypto.SecretKey;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.agreement.KeyAgreementCredential;
import org.opensaml.xmlsec.agreement.KeyAgreementException;
import org.opensaml.xmlsec.agreement.KeyAgreementParameter;
import org.opensaml.xmlsec.agreement.KeyAgreementParameters;
import org.opensaml.xmlsec.derivation.impl.MockKeyDerivation;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings({"javadoc", "null"})
public class ECDHKeyAgreementProcessorTest extends OpenSAMLInitBaseTestCase {
    
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
        params.addAll(getMockParams());
        
        KeyAgreementCredential keyAgreementCredential = processor.execute(recipientCredential,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128,
                params);
        
        Assert.assertNotNull(keyAgreementCredential);
        
        final SecretKey skey = keyAgreementCredential.getSecretKey();
        assert skey != null;
        Assert.assertEquals(skey.getAlgorithm(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(KeySupport.getKeyLength(skey), Integer.valueOf(128));
        
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
        final KANonce kanonce = keyAgreementCredential.getParameters().get(KANonce.class);
        assert kanonce != null;
        Assert.assertEquals(kanonce.getValue(), "AABBCCDD");
        
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
        params.addAll(getMockParams());
        
        KeyAgreementCredential keyAgreementCredential = processor.execute(originatorCredential,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128,
                params);
        
        Assert.assertNotNull(keyAgreementCredential);
        
        final SecretKey skey = keyAgreementCredential.getSecretKey();
        assert skey != null;
        Assert.assertEquals(skey.getAlgorithm(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(KeySupport.getKeyLength(skey), Integer.valueOf(128));
        
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
        final KANonce kanonce = keyAgreementCredential.getParameters().get(KANonce.class);
        assert kanonce != null;
        Assert.assertEquals(kanonce.getValue(), "AABBCCDD");
        
    }

    @Test(expectedExceptions = KeyAgreementException.class)
    public void nonECCred() throws Exception {
        KeyPair kp = KeySupport.generateKeyPair("RSA", 2048, null);
        Credential publicCredential = CredentialSupport.getSimpleCredential(kp.getPublic(), null);
        
        KeyAgreementParameters params = new KeyAgreementParameters();
        params.add(new MockKeyDerivation()); 
        params.addAll(getMockParams());
        
        processor.execute(publicCredential,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128,
                params);
    }
    
    @Test(expectedExceptions = KeyAgreementException.class)
    public void keyDerivationError() throws Exception {
        KeyPair kp = KeySupport.generateKeyPair("EC", new ECGenParameterSpec("secp256r1"), null);
        Credential publicCredential = CredentialSupport.getSimpleCredential(kp.getPublic(), null);
        
        KeyAgreementParameters params = new KeyAgreementParameters();
        params.add(new MockKeyDerivation()); 
        params.addAll(getMockParams());
        
        processor.execute(publicCredential,
                "urn:test:InvalidBlockEncryption",
                params);
    }
    
    @Test(expectedExceptions = KeyAgreementException.class)
    public void missingKeyDerivationParam() throws Exception {
        KeyPair kp = KeySupport.generateKeyPair("EC", new ECGenParameterSpec("secp256r1"), null);
        Credential publicCredential = CredentialSupport.getSimpleCredential(kp.getPublic(), null);
        
        KeyAgreementParameters params = new KeyAgreementParameters();
        params.addAll(getMockParams());
        
        processor.execute(publicCredential,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128,
                params);
    }
    
    @Test(expectedExceptions = KeyAgreementException.class)
    public void specifiedKeySizeMismatch() throws Exception {
        KeyPair kp = KeySupport.generateKeyPair("EC", new ECGenParameterSpec("secp256r1"), null);
        Credential publicCredential = CredentialSupport.getSimpleCredential(kp.getPublic(), null);
        
        KeyAgreementParameters params = new KeyAgreementParameters();
        params.add(new MockKeyDerivation()); 
        params.add(new KeySize(256));
        
        processor.execute(publicCredential,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128,
                params);
    }
    
    private Collection<KeyAgreementParameter> getMockParams() {
        ArrayList<KeyAgreementParameter> params = new ArrayList<>();
        KANonce nonce = new KANonce();
        nonce.setValue("AABBCCDD");
        params.add(nonce);
        return params;
    }
    
        
}
