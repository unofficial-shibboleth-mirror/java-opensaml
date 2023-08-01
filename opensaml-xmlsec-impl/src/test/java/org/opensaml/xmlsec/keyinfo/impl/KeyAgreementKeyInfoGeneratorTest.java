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

package org.opensaml.xmlsec.keyinfo.impl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.ECGenParameterSpec;
import java.util.List;

import javax.crypto.SecretKey;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.agreement.KeyAgreementCredential;
import org.opensaml.xmlsec.agreement.impl.BasicKeyAgreementCredential;
import org.opensaml.xmlsec.agreement.impl.DigestMethod;
import org.opensaml.xmlsec.agreement.impl.KANonce;
import org.opensaml.xmlsec.derivation.impl.ConcatKDF;
import org.opensaml.xmlsec.derivation.impl.PBKDF2;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.ConcatKDFParams;
import org.opensaml.xmlsec.encryption.IterationCount;
import org.opensaml.xmlsec.encryption.KeyDerivationMethod;
import org.opensaml.xmlsec.encryption.KeyLength;
import org.opensaml.xmlsec.encryption.OriginatorKeyInfo;
import org.opensaml.xmlsec.encryption.PBKDF2Params;
import org.opensaml.xmlsec.encryption.PRF;
import org.opensaml.xmlsec.encryption.RecipientKeyInfo;
import org.opensaml.xmlsec.encryption.Salt;
import org.opensaml.xmlsec.encryption.Specified;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings({"javadoc", "null"})
public class KeyAgreementKeyInfoGeneratorTest extends XMLObjectBaseTestCase {
    
    private KeyPair keyPairOriginatorECDH, keyPairRecipientECDH;
    private KeyPair keyPairOriginatorDiffieHellman, keyPairRecipientDiffieHellman;
    
    private Credential credOriginatorECDH, credRecipientECDH;
    private Credential credOriginatorDiffieHellman, credRecipientDiffieHellman;
    
    private SecretKey derivedKey;
    
    private KeyAgreementCredential credECDH;
    private KeyAgreementCredential credDiffieHellmanExplicitKDF;
    private KeyAgreementCredential credDiffieHellmanLegacyKDF;
    
    private KeyAgreementKeyInfoGeneratorFactory factory;
    
    @BeforeClass
    public void beforeClass() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        keyPairOriginatorECDH = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, new ECGenParameterSpec("secp256r1"), null);
        credOriginatorECDH = new BasicCredential(keyPairOriginatorECDH.getPublic(), keyPairOriginatorECDH.getPrivate());
        keyPairRecipientECDH = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, new ECGenParameterSpec("secp256r1"), null);
        credRecipientECDH = new BasicCredential(keyPairRecipientECDH.getPublic());
        
        keyPairOriginatorDiffieHellman = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_DIFFIE_HELLMAN, 1024, null);
        credOriginatorDiffieHellman = new BasicCredential(keyPairOriginatorDiffieHellman.getPublic(), keyPairOriginatorDiffieHellman.getPrivate());
        keyPairRecipientDiffieHellman = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_DIFFIE_HELLMAN, 1024, null);
        credRecipientDiffieHellman = new BasicCredential(keyPairRecipientDiffieHellman.getPublic());
        
        derivedKey = KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 256, null);
    }
    
    @BeforeMethod
    public void beforeMethod() {
        factory = new KeyAgreementKeyInfoGeneratorFactory(); 
        
        credECDH = new BasicKeyAgreementCredential(derivedKey, EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES, credOriginatorECDH, credRecipientECDH);
        credDiffieHellmanExplicitKDF = new BasicKeyAgreementCredential(derivedKey, EncryptionConstants.ALGO_ID_KEYAGREEMENT_DH_EXPLICIT_KDF, credOriginatorDiffieHellman, credRecipientDiffieHellman);
        credDiffieHellmanLegacyKDF = new BasicKeyAgreementCredential(derivedKey, EncryptionConstants.ALGO_ID_KEYAGREEMENT_DH, credOriginatorDiffieHellman, credRecipientDiffieHellman);
    }
    
    @Test
    public void ECDHWithConcatKDFWithDefaults() throws Exception {
        final ConcatKDF kdf = new ConcatKDF();
        kdf.setDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        kdf.setAlgorithmID("AA");
        kdf.setPartyUInfo("BB");
        kdf.setPartyVInfo("CC");
        kdf.setSuppPubInfo("DD");
        kdf.setSuppPrivInfo("EE");
        kdf.initialize();
        
        credECDH.getParameters().add(kdf);
        
        final KeyInfoGenerator generator = factory.newInstance();
        final KeyInfo keyInfo = generator.generate(credECDH);
        assert keyInfo != null;
        List<XMLObject> children = keyInfo.getOrderedChildren();
        
        assert children != null;
        Assert.assertEquals(children.size(), 1);
        Assert.assertEquals(keyInfo.getAgreementMethods().size(), 1);
        
        final AgreementMethod agreementMethod = keyInfo.getAgreementMethods().get(0);
        Assert.assertEquals(agreementMethod.getAlgorithm(), credECDH.getAlgorithm());
        children = agreementMethod.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 3);
        
        //Originator
        final OriginatorKeyInfo originatorKeyInfo = agreementMethod.getOriginatorKeyInfo();
        assert originatorKeyInfo != null;
        children = originatorKeyInfo.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 2);
        Assert.assertEquals(originatorKeyInfo.getDEREncodedKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(originatorKeyInfo.getDEREncodedKeyValues().get(0)), keyPairOriginatorECDH.getPublic());
        Assert.assertEquals(originatorKeyInfo.getKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(originatorKeyInfo.getKeyValues().get(0)), keyPairOriginatorECDH.getPublic());
        
        //Recipient
        final RecipientKeyInfo recipientKeyInfo = agreementMethod.getRecipientKeyInfo();
        assert recipientKeyInfo != null;
        children = recipientKeyInfo.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 2);
        Assert.assertEquals(recipientKeyInfo.getDEREncodedKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(recipientKeyInfo.getDEREncodedKeyValues().get(0)), keyPairRecipientECDH.getPublic());
        Assert.assertEquals(recipientKeyInfo.getKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(recipientKeyInfo.getKeyValues().get(0)), keyPairRecipientECDH.getPublic());
        
        //Params
        Assert.assertEquals(agreementMethod.getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).size(), 1);
        final KeyDerivationMethod kdm = (KeyDerivationMethod) agreementMethod.getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).get(0);
        Assert.assertEquals(kdm.getAlgorithm(), EncryptionConstants.ALGO_ID_KEYDERIVATION_CONCATKDF);
        Assert.assertEquals(kdm.getUnknownXMLObjects().size(), 1);
        Assert.assertEquals(kdm.getUnknownXMLObjects(ConcatKDFParams.DEFAULT_ELEMENT_NAME).size(), 1);
        final ConcatKDFParams kdfParams = (ConcatKDFParams) kdm.getUnknownXMLObjects(ConcatKDFParams.DEFAULT_ELEMENT_NAME).get(0);
        final org.opensaml.xmlsec.signature.DigestMethod dm = kdfParams.getDigestMethod();
        assert dm != null;
        Assert.assertEquals(dm.getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA512);
        Assert.assertEquals(kdfParams.getAlgorithmID(), "00AA");
        Assert.assertEquals(kdfParams.getPartyUInfo(), "00BB");
        Assert.assertEquals(kdfParams.getPartyVInfo(), "00CC");
        Assert.assertEquals(kdfParams.getSuppPubInfo(), "00DD");
        Assert.assertEquals(kdfParams.getSuppPrivInfo(), "00EE");
    }

    @Test
    public void ECDHWithPBKDF2WithDefaults() throws Exception {
        final PBKDF2 kdf = new PBKDF2();
        kdf.setIterationCount(1500);
        kdf.setKeyLength(256);
        kdf.setPRF(SignatureConstants.ALGO_ID_MAC_HMAC_SHA512);
        kdf.setSalt("ABCD");
        kdf.initialize();
        
        credECDH.getParameters().add(kdf);
        
        final KeyInfoGenerator generator = factory.newInstance();
        final KeyInfo keyInfo = generator.generate(credECDH);
        
        assert keyInfo != null;
        List<XMLObject> children = keyInfo.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 1);
        Assert.assertEquals(keyInfo.getAgreementMethods().size(), 1);
        
        final AgreementMethod agreementMethod = keyInfo.getAgreementMethods().get(0);
        Assert.assertEquals(agreementMethod.getAlgorithm(), credECDH.getAlgorithm());
        
        children = agreementMethod.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 3);
        
        //Originator
        final OriginatorKeyInfo originatorKeyInfo = agreementMethod.getOriginatorKeyInfo();
        assert originatorKeyInfo != null;
        children = originatorKeyInfo.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 2);
        Assert.assertEquals(originatorKeyInfo.getDEREncodedKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(originatorKeyInfo.getDEREncodedKeyValues().get(0)), keyPairOriginatorECDH.getPublic());
        Assert.assertEquals(originatorKeyInfo.getKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(originatorKeyInfo.getKeyValues().get(0)), keyPairOriginatorECDH.getPublic());
        
        //Recipient
        final RecipientKeyInfo recipientKeyInfo = agreementMethod.getRecipientKeyInfo();
        assert recipientKeyInfo != null;
        children = recipientKeyInfo.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 2);
        Assert.assertEquals(recipientKeyInfo.getDEREncodedKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(recipientKeyInfo.getDEREncodedKeyValues().get(0)), keyPairRecipientECDH.getPublic());
        Assert.assertEquals(recipientKeyInfo.getKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(recipientKeyInfo.getKeyValues().get(0)), keyPairRecipientECDH.getPublic());
        
        //Params
        Assert.assertEquals(agreementMethod.getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).size(), 1);
        KeyDerivationMethod kdm = (KeyDerivationMethod) agreementMethod.getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).get(0);
        Assert.assertEquals(kdm.getAlgorithm(), EncryptionConstants.ALGO_ID_KEYDERIVATION_PBKDF2);
        Assert.assertEquals(kdm.getUnknownXMLObjects().size(), 1);
        Assert.assertEquals(kdm.getUnknownXMLObjects(PBKDF2Params.DEFAULT_ELEMENT_NAME).size(), 1);
        PBKDF2Params kdfParams = (PBKDF2Params) kdm.getUnknownXMLObjects(PBKDF2Params.DEFAULT_ELEMENT_NAME).get(0);
        
        final IterationCount icount = kdfParams.getIterationCount();
        assert icount != null;
        Assert.assertEquals(icount.getValue(), 1500);
        
        final KeyLength keyLength = kdfParams.getKeyLength();
        assert keyLength != null;
        Assert.assertEquals(keyLength.getValue(), 256/8); // bytes
        
        final PRF prf = kdfParams.getPRF();
        assert prf != null;
        Assert.assertEquals(prf.getAlgorithm(), SignatureConstants.ALGO_ID_MAC_HMAC_SHA512);
        
        final Salt salt = kdfParams.getSalt();
        assert salt != null;
        
        final Specified spec = salt.getSpecified();
        assert spec != null;
        Assert.assertEquals(spec.getValue(), "ABCD");
    }
    
    @Test
    public void DiffieHellmanWithConcatKDFWithDefaults() throws Exception {
        final ConcatKDF kdf = new ConcatKDF();
        kdf.setDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        kdf.setAlgorithmID("AA");
        kdf.setPartyUInfo("BB");
        kdf.setPartyVInfo("CC");
        kdf.setSuppPubInfo("DD");
        kdf.setSuppPrivInfo("EE");
        kdf.initialize();
        
        credDiffieHellmanExplicitKDF.getParameters().add(kdf);
        
        final KeyInfoGenerator generator = factory.newInstance();
        final KeyInfo keyInfo = generator.generate(credDiffieHellmanExplicitKDF);
        assert keyInfo != null;
        
        List<XMLObject> children = keyInfo.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 1);
        Assert.assertEquals(keyInfo.getAgreementMethods().size(), 1);
        
        final AgreementMethod agreementMethod = keyInfo.getAgreementMethods().get(0);
        Assert.assertEquals(agreementMethod.getAlgorithm(), credDiffieHellmanExplicitKDF.getAlgorithm());
        
        children = agreementMethod.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 3);
        
        //Originator
        final OriginatorKeyInfo originatorKeyInfo = agreementMethod.getOriginatorKeyInfo();
        assert originatorKeyInfo != null;
        children = originatorKeyInfo.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 2);
        Assert.assertEquals(originatorKeyInfo.getDEREncodedKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(originatorKeyInfo.getDEREncodedKeyValues().get(0)), keyPairOriginatorDiffieHellman.getPublic());
        Assert.assertEquals(originatorKeyInfo.getKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(originatorKeyInfo.getKeyValues().get(0)), keyPairOriginatorDiffieHellman.getPublic());
        
        //Recipient
        final RecipientKeyInfo recipientKeyInfo = agreementMethod.getRecipientKeyInfo();
        assert recipientKeyInfo != null;
        children = recipientKeyInfo.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 2);
        Assert.assertEquals(recipientKeyInfo.getDEREncodedKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(recipientKeyInfo.getDEREncodedKeyValues().get(0)), keyPairRecipientDiffieHellman.getPublic());
        Assert.assertEquals(recipientKeyInfo.getKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(recipientKeyInfo.getKeyValues().get(0)), keyPairRecipientDiffieHellman.getPublic());
        
        //Params
        Assert.assertEquals(agreementMethod.getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).size(), 1);
        final KeyDerivationMethod kdm = (KeyDerivationMethod) agreementMethod.getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).get(0);
        Assert.assertEquals(kdm.getAlgorithm(), EncryptionConstants.ALGO_ID_KEYDERIVATION_CONCATKDF);
        Assert.assertEquals(kdm.getUnknownXMLObjects().size(), 1);
        Assert.assertEquals(kdm.getUnknownXMLObjects(ConcatKDFParams.DEFAULT_ELEMENT_NAME).size(), 1);
        final ConcatKDFParams kdfParams = (ConcatKDFParams) kdm.getUnknownXMLObjects(ConcatKDFParams.DEFAULT_ELEMENT_NAME).get(0);
        
        final org.opensaml.xmlsec.signature.DigestMethod dm = kdfParams.getDigestMethod();
        assert dm != null;
        Assert.assertEquals(dm.getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA512);
        Assert.assertEquals(kdfParams.getAlgorithmID(), "00AA");
        Assert.assertEquals(kdfParams.getPartyUInfo(), "00BB");
        Assert.assertEquals(kdfParams.getPartyVInfo(), "00CC");
        Assert.assertEquals(kdfParams.getSuppPubInfo(), "00DD");
        Assert.assertEquals(kdfParams.getSuppPrivInfo(), "00EE");
    }

    @Test
    public void DiffieHellmanWithLegacyKDFWithDefaults() throws Exception {
        DigestMethod dm = new DigestMethod();
        dm.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        dm.initialize();
        
        KANonce nonce = new KANonce();
        nonce.setValue("ABCD");
        nonce.initialize();
        
        credDiffieHellmanLegacyKDF.getParameters().add(dm);
        credDiffieHellmanLegacyKDF.getParameters().add(nonce);
        
        final KeyInfoGenerator generator = factory.newInstance();
        final KeyInfo keyInfo = generator.generate(credDiffieHellmanLegacyKDF);
        
        assert keyInfo != null;
        
        List<XMLObject> children = keyInfo.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 1);
        Assert.assertEquals(keyInfo.getAgreementMethods().size(), 1);
        
        final AgreementMethod agreementMethod = keyInfo.getAgreementMethods().get(0);
        Assert.assertEquals(agreementMethod.getAlgorithm(), credDiffieHellmanLegacyKDF.getAlgorithm());
        
        children = agreementMethod.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 4);
        
        //Originator
        final OriginatorKeyInfo originatorKeyInfo = agreementMethod.getOriginatorKeyInfo();
        assert originatorKeyInfo != null;
        children = originatorKeyInfo.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 2);
        Assert.assertEquals(originatorKeyInfo.getDEREncodedKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(originatorKeyInfo.getDEREncodedKeyValues().get(0)), keyPairOriginatorDiffieHellman.getPublic());
        Assert.assertEquals(originatorKeyInfo.getKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(originatorKeyInfo.getKeyValues().get(0)), keyPairOriginatorDiffieHellman.getPublic());
        
        //Recipient
        final RecipientKeyInfo recipientKeyInfo = agreementMethod.getRecipientKeyInfo();
        assert recipientKeyInfo != null;
        children = recipientKeyInfo.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 2);
        Assert.assertEquals(recipientKeyInfo.getDEREncodedKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(recipientKeyInfo.getDEREncodedKeyValues().get(0)), keyPairRecipientDiffieHellman.getPublic());
        Assert.assertEquals(recipientKeyInfo.getKeyValues().size(), 1);
        Assert.assertEquals(KeyInfoSupport.getKey(recipientKeyInfo.getKeyValues().get(0)), keyPairRecipientDiffieHellman.getPublic());
        
        //Params
        final org.opensaml.xmlsec.encryption.KANonce kanonce = agreementMethod.getKANonce();
        assert kanonce != null;
        Assert.assertEquals(kanonce.getValue(), "ABCD");
        Assert.assertEquals(agreementMethod.getUnknownXMLObjects(org.opensaml.xmlsec.signature.DigestMethod.DEFAULT_ELEMENT_NAME).size(), 1);
        org.opensaml.xmlsec.signature.DigestMethod xmlDigest =
                (org.opensaml.xmlsec.signature.DigestMethod) agreementMethod.getUnknownXMLObjects(
                        org.opensaml.xmlsec.signature.DigestMethod.DEFAULT_ELEMENT_NAME).get(0);
        Assert.assertEquals(xmlDigest.getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA512);
    }

    
    @Test
    public void noEmitKeyinfos() throws Exception {
        factory.setEmitOriginatorKeyInfo(false);
        factory.setEmitRecipientKeyInfo(false);
        
        final ConcatKDF kdf = new ConcatKDF();
        kdf.setDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        kdf.setAlgorithmID("AA");
        kdf.setPartyUInfo("BB");
        kdf.setPartyVInfo("CC");
        kdf.setSuppPubInfo("DD");
        kdf.setSuppPrivInfo("EE");
        kdf.initialize();
        
        credECDH.getParameters().add(kdf);
        
        final KeyInfoGenerator generator = factory.newInstance();
        final KeyInfo keyInfo = generator.generate(credECDH);
        
        assert keyInfo != null;
        
        List<XMLObject> children = keyInfo.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 1);
        Assert.assertEquals(keyInfo.getAgreementMethods().size(), 1);
        
        final AgreementMethod agreementMethod = keyInfo.getAgreementMethods().get(0);
        children = agreementMethod.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 1);
        
        Assert.assertEquals(agreementMethod.getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).size(), 1);
        Assert.assertNull(agreementMethod.getOriginatorKeyInfo());
        Assert.assertNull(agreementMethod.getRecipientKeyInfo());
    }
     
    @Test
    public void noKeyInfoManagers() throws Exception {
        factory.setOriginatorKeyInfoGeneratorManager(null);
        factory.setRecipientKeyInfoGeneratorManager(null);
        
        final ConcatKDF kdf = new ConcatKDF();
        kdf.setDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        kdf.setAlgorithmID("AA");
        kdf.setPartyUInfo("BB");
        kdf.setPartyVInfo("CC");
        kdf.setSuppPubInfo("DD");
        kdf.setSuppPrivInfo("EE");
        kdf.initialize();
        
        credECDH.getParameters().add(kdf);
        
        final KeyInfoGenerator generator = factory.newInstance();
        final KeyInfo keyInfo = generator.generate(credECDH);
        
        assert keyInfo != null;
        
        List<XMLObject> children = keyInfo.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 1);
        Assert.assertEquals(keyInfo.getAgreementMethods().size(), 1);
        
        final AgreementMethod agreementMethod = keyInfo.getAgreementMethods().get(0);
        children = agreementMethod.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 1);
        
        Assert.assertEquals(agreementMethod.getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).size(), 1);
        Assert.assertNull(agreementMethod.getOriginatorKeyInfo());
        Assert.assertNull(agreementMethod.getRecipientKeyInfo());
    }
     

}
