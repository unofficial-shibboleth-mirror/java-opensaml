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

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.xmlsec.agreement.KeyAgreementException;
import org.opensaml.xmlsec.agreement.KeyAgreementParameters;
import org.opensaml.xmlsec.derivation.impl.ConcatKDF;
import org.opensaml.xmlsec.derivation.impl.PBKDF2;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.ConcatKDFParams;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.IterationCount;
import org.opensaml.xmlsec.encryption.KeyDerivationMethod;
import org.opensaml.xmlsec.encryption.KeyLength;
import org.opensaml.xmlsec.encryption.PBKDF2Params;
import org.opensaml.xmlsec.encryption.PRF;
import org.opensaml.xmlsec.encryption.Salt;
import org.opensaml.xmlsec.encryption.Specified;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class KeyAgreementParametersParserTest extends XMLObjectBaseTestCase {
    
    @Test
    public void ECDHWithConcatKDF() throws KeyAgreementException {
        AgreementMethod agreementMethod = buildXMLObject(AgreementMethod.DEFAULT_ELEMENT_NAME);
        agreementMethod.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        
        KeyDerivationMethod kdm = buildXMLObject(KeyDerivationMethod.DEFAULT_ELEMENT_NAME);
        kdm.setAlgorithm(EncryptionConstants.ALGO_ID_KEYDERIVATION_CONCATKDF);
        
        ConcatKDFParams xmlParams = buildXMLObject(ConcatKDFParams.DEFAULT_ELEMENT_NAME);
        xmlParams.setAlgorithmID("00AA");
        xmlParams.setPartyUInfo("00BB");
        xmlParams.setPartyVInfo("00CC");
        xmlParams.setSuppPubInfo("00DD");
        xmlParams.setSuppPrivInfo("00EE");
        
        org.opensaml.xmlsec.signature.DigestMethod digestMethod = buildXMLObject(org.opensaml.xmlsec.signature.DigestMethod.DEFAULT_ELEMENT_NAME);
        digestMethod.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        xmlParams.setDigestMethod(digestMethod);
        
        kdm.getUnknownXMLObjects().add(xmlParams);
        
        agreementMethod.getUnknownXMLObjects().add(kdm);
        
        KeyAgreementParametersParser parser = new KeyAgreementParametersParser();
        
        KeyAgreementParameters parameters = parser.parse(agreementMethod);
        Assert.assertNotNull(parameters);
        Assert.assertEquals(parameters.size(), 1);
        
        Assert.assertTrue(parameters.contains(ConcatKDF.class));
        
        ConcatKDF kdf = parameters.get(ConcatKDF.class);
        Assert.assertTrue(kdf.isInitialized());
        Assert.assertEquals(kdf.getDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA512);
        Assert.assertEquals(kdf.getAlgorithmID(), "AA");
        Assert.assertEquals(kdf.getPartyUInfo(), "BB");
        Assert.assertEquals(kdf.getPartyVInfo(), "CC");
        Assert.assertEquals(kdf.getSuppPubInfo(), "DD");
        Assert.assertEquals(kdf.getSuppPrivInfo(), "EE");
    }
    
    @Test
    public void ECDHWithConcatKDFWithKeySize() throws KeyAgreementException {
        org.opensaml.xmlsec.encryption.KeySize xmlKeySize = buildXMLObject(org.opensaml.xmlsec.encryption.KeySize.DEFAULT_ELEMENT_NAME);
        xmlKeySize.setValue(80);
        
        EncryptionMethod em = buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME);
        em.setKeySize(xmlKeySize);
        
        EncryptedData ed = buildXMLObject(EncryptedData.DEFAULT_ELEMENT_NAME);
        ed.setEncryptionMethod(em);
        
        KeyInfo keyInfo = buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        ed.setKeyInfo(keyInfo);
        
        AgreementMethod agreementMethod = buildXMLObject(AgreementMethod.DEFAULT_ELEMENT_NAME);
        agreementMethod.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        keyInfo.getAgreementMethods().add(agreementMethod);
        
        KeyDerivationMethod kdm = buildXMLObject(KeyDerivationMethod.DEFAULT_ELEMENT_NAME);
        kdm.setAlgorithm(EncryptionConstants.ALGO_ID_KEYDERIVATION_CONCATKDF);
        
        ConcatKDFParams xmlParams = buildXMLObject(ConcatKDFParams.DEFAULT_ELEMENT_NAME);
        xmlParams.setAlgorithmID("00AA");
        xmlParams.setPartyUInfo("00BB");
        xmlParams.setPartyVInfo("00CC");
        xmlParams.setSuppPubInfo("00DD");
        xmlParams.setSuppPrivInfo("00EE");
        
        org.opensaml.xmlsec.signature.DigestMethod digestMethod = buildXMLObject(org.opensaml.xmlsec.signature.DigestMethod.DEFAULT_ELEMENT_NAME);
        digestMethod.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        xmlParams.setDigestMethod(digestMethod);
        
        kdm.getUnknownXMLObjects().add(xmlParams);
        
        agreementMethod.getUnknownXMLObjects().add(kdm);
        
        KeyAgreementParametersParser parser = new KeyAgreementParametersParser();
        
        KeyAgreementParameters parameters = parser.parse(agreementMethod);
        Assert.assertNotNull(parameters);
        Assert.assertEquals(parameters.size(), 2);
        
        Assert.assertTrue(parameters.contains(ConcatKDF.class));
        
        ConcatKDF kdf = parameters.get(ConcatKDF.class);
        Assert.assertTrue(kdf.isInitialized());
        Assert.assertEquals(kdf.getDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA512);
        Assert.assertEquals(kdf.getAlgorithmID(), "AA");
        Assert.assertEquals(kdf.getPartyUInfo(), "BB");
        Assert.assertEquals(kdf.getPartyVInfo(), "CC");
        Assert.assertEquals(kdf.getSuppPubInfo(), "DD");
        Assert.assertEquals(kdf.getSuppPrivInfo(), "EE");
        
        Assert.assertTrue(parameters.contains(KeySize.class));
        Assert.assertEquals(parameters.get(KeySize.class).getSize().intValue(), 80);
    }
    
    @Test
    public void ECDHWithPBKDF2() throws KeyAgreementException {
        AgreementMethod agreementMethod = buildXMLObject(AgreementMethod.DEFAULT_ELEMENT_NAME);
        agreementMethod.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        
        KeyDerivationMethod kdm = buildXMLObject(KeyDerivationMethod.DEFAULT_ELEMENT_NAME);
        kdm.setAlgorithm(EncryptionConstants.ALGO_ID_KEYDERIVATION_PBKDF2);
        
        PBKDF2Params xmlParams = buildXMLObject(PBKDF2Params.DEFAULT_ELEMENT_NAME);
        
        IterationCount iterationCount = buildXMLObject(IterationCount.DEFAULT_ELEMENT_NAME);
        iterationCount.setValue(1500);
        xmlParams.setIterationCount(iterationCount);
        
        KeyLength keyLength = buildXMLObject(KeyLength.DEFAULT_ELEMENT_NAME);
        keyLength.setValue(32);
        xmlParams.setKeyLength(keyLength);
        
        PRF prf = buildXMLObject(PRF.DEFAULT_ELEMENT_NAME);
        prf.setAlgorithm(SignatureConstants.ALGO_ID_MAC_HMAC_SHA512);
        xmlParams.setPRF(prf);
        
        Salt salt = buildXMLObject(Salt.DEFAULT_ELEMENT_NAME);
        Specified specified = buildXMLObject(Specified.DEFAULT_ELEMENT_NAME);
        specified.setValue("ABCD");
        salt.setSpecified(specified);
        xmlParams.setSalt(salt);
        
        kdm.getUnknownXMLObjects().add(xmlParams);
        
        agreementMethod.getUnknownXMLObjects().add(kdm);
        
        KeyAgreementParametersParser parser = new KeyAgreementParametersParser();
        
        KeyAgreementParameters parameters = parser.parse(agreementMethod);
        Assert.assertNotNull(parameters);
        Assert.assertEquals(parameters.size(), 1);
        
        Assert.assertTrue(parameters.contains(PBKDF2.class));
        
        PBKDF2 kdf = parameters.get(PBKDF2.class);
        Assert.assertTrue(kdf.isInitialized());
        Assert.assertEquals(kdf.getIterationCount().intValue(), 1500);
        Assert.assertEquals(kdf.getKeyLength().intValue(), 256);
        Assert.assertEquals(kdf.getPRF(), SignatureConstants.ALGO_ID_MAC_HMAC_SHA512);
        Assert.assertEquals(kdf.getSalt(), "ABCD");
    }

    @Test
    public void DHWithConcatKDF() throws KeyAgreementException {
        AgreementMethod agreementMethod = buildXMLObject(AgreementMethod.DEFAULT_ELEMENT_NAME);
        agreementMethod.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_DH_EXPLICIT_KDF);
        
        KeyDerivationMethod kdm = buildXMLObject(KeyDerivationMethod.DEFAULT_ELEMENT_NAME);
        kdm.setAlgorithm(EncryptionConstants.ALGO_ID_KEYDERIVATION_CONCATKDF);
        
        ConcatKDFParams xmlParams = buildXMLObject(ConcatKDFParams.DEFAULT_ELEMENT_NAME);
        xmlParams.setAlgorithmID("00AA");
        xmlParams.setPartyUInfo("00BB");
        xmlParams.setPartyVInfo("00CC");
        xmlParams.setSuppPubInfo("00DD");
        xmlParams.setSuppPrivInfo("00EE");
        
        org.opensaml.xmlsec.signature.DigestMethod digestMethod = buildXMLObject(org.opensaml.xmlsec.signature.DigestMethod.DEFAULT_ELEMENT_NAME);
        digestMethod.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        xmlParams.setDigestMethod(digestMethod);
        
        kdm.getUnknownXMLObjects().add(xmlParams);
        
        agreementMethod.getUnknownXMLObjects().add(kdm);
        
        KeyAgreementParametersParser parser = new KeyAgreementParametersParser();
        
        KeyAgreementParameters parameters = parser.parse(agreementMethod);
        Assert.assertNotNull(parameters);
        Assert.assertEquals(parameters.size(), 1);
        
        Assert.assertTrue(parameters.contains(ConcatKDF.class));
        
        ConcatKDF kdf = parameters.get(ConcatKDF.class);
        Assert.assertTrue(kdf.isInitialized());
        Assert.assertEquals(kdf.getDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA512);
        Assert.assertEquals(kdf.getAlgorithmID(), "AA");
        Assert.assertEquals(kdf.getPartyUInfo(), "BB");
        Assert.assertEquals(kdf.getPartyVInfo(), "CC");
        Assert.assertEquals(kdf.getSuppPubInfo(), "DD");
        Assert.assertEquals(kdf.getSuppPrivInfo(), "EE");
    }
    
    @Test
    public void DHWithLegacyKDF() throws KeyAgreementException {
        AgreementMethod agreementMethod = buildXMLObject(AgreementMethod.DEFAULT_ELEMENT_NAME);
        agreementMethod.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_DH);
        
        org.opensaml.xmlsec.encryption.KANonce xmlNonce = buildXMLObject(org.opensaml.xmlsec.encryption.KANonce.DEFAULT_ELEMENT_NAME);
        xmlNonce.setValue("ABCD");
        agreementMethod.getUnknownXMLObjects().add(xmlNonce);
        
        org.opensaml.xmlsec.signature.DigestMethod xmlDigest = buildXMLObject(org.opensaml.xmlsec.signature.DigestMethod.DEFAULT_ELEMENT_NAME);
        xmlDigest.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        agreementMethod.getUnknownXMLObjects().add(xmlDigest);
        
        KeyAgreementParametersParser parser = new KeyAgreementParametersParser();
        
        KeyAgreementParameters parameters = parser.parse(agreementMethod);
        Assert.assertNotNull(parameters);
        Assert.assertEquals(parameters.size(), 2);
        
        Assert.assertTrue(parameters.contains(KANonce.class));
        KANonce nonce = parameters.get(KANonce.class);
        Assert.assertTrue(nonce.isInitialized());
        Assert.assertEquals(nonce.getValue(), "ABCD");
        Assert.assertTrue(parameters.contains(KANonce.class));
        
        Assert.assertTrue(parameters.contains(DigestMethod.class));
        DigestMethod digestMethod = parameters.get(DigestMethod.class);
        Assert.assertTrue(digestMethod.isInitialized());
        Assert.assertEquals(digestMethod.getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA512);
    }
    
    @Test(expectedExceptions = KeyAgreementException.class)
    public void unknownChildType() throws KeyAgreementException {
        AgreementMethod agreementMethod = buildXMLObject(AgreementMethod.DEFAULT_ELEMENT_NAME);
        agreementMethod.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        
        agreementMethod.getUnknownXMLObjects().add(buildXMLObject(simpleXMLObjectQName));
        
        KeyAgreementParametersParser parser = new KeyAgreementParametersParser();
        
        parser.parse(agreementMethod);
    }
    
    @Test(expectedExceptions = KeyAgreementException.class)
    public void unknownKeyDerivationAlgorithm() throws KeyAgreementException {
        AgreementMethod agreementMethod = buildXMLObject(AgreementMethod.DEFAULT_ELEMENT_NAME);
        agreementMethod.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        
        KeyDerivationMethod kdm = buildXMLObject(KeyDerivationMethod.DEFAULT_ELEMENT_NAME);
        kdm.setAlgorithm("UNKNOWN");
        
        agreementMethod.getUnknownXMLObjects().add(kdm);
        
        KeyAgreementParametersParser parser = new KeyAgreementParametersParser();
        
        parser.parse(agreementMethod);
    }
    
    @Test(expectedExceptions = KeyAgreementException.class)
    public void invalidParamData() throws KeyAgreementException {
        AgreementMethod agreementMethod = buildXMLObject(AgreementMethod.DEFAULT_ELEMENT_NAME);
        agreementMethod.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        
        KeyDerivationMethod kdm = buildXMLObject(KeyDerivationMethod.DEFAULT_ELEMENT_NAME);
        kdm.setAlgorithm(EncryptionConstants.ALGO_ID_KEYDERIVATION_CONCATKDF);
        
        ConcatKDFParams xmlParams = buildXMLObject(ConcatKDFParams.DEFAULT_ELEMENT_NAME);
        xmlParams.setAlgorithmID("01AA");
        xmlParams.setPartyUInfo("02BB");
        xmlParams.setPartyVInfo("03CC");
        xmlParams.setSuppPubInfo("04DD");
        xmlParams.setSuppPrivInfo("05EE");
        
        org.opensaml.xmlsec.signature.DigestMethod digestMethod = buildXMLObject(org.opensaml.xmlsec.signature.DigestMethod.DEFAULT_ELEMENT_NAME);
        digestMethod.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        xmlParams.setDigestMethod(digestMethod);
        
        kdm.getUnknownXMLObjects().add(xmlParams);
        
        agreementMethod.getUnknownXMLObjects().add(kdm);
        
        KeyAgreementParametersParser parser = new KeyAgreementParametersParser();
        
        parser.parse(agreementMethod);
    }
}
