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

package org.opensaml.xmlsec.encryption.support.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.impl.CollectionCredentialResolver;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.DecryptionConfiguration;
import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.DecryptionParametersResolver;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.EncryptionParametersResolver;
import org.opensaml.xmlsec.agreement.impl.DigestMethod;
import org.opensaml.xmlsec.agreement.impl.KANonce;
import org.opensaml.xmlsec.criterion.DecryptionConfigurationCriterion;
import org.opensaml.xmlsec.criterion.EncryptionConfigurationCriterion;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.encryption.support.Decrypter;
import org.opensaml.xmlsec.encryption.support.Encrypter;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.KeyAgreementEncryptionConfiguration;
import org.opensaml.xmlsec.encryption.support.KeyEncryptionParameters;
import org.opensaml.xmlsec.impl.BasicDecryptionConfiguration;
import org.opensaml.xmlsec.impl.BasicDecryptionParametersResolver;
import org.opensaml.xmlsec.impl.BasicEncryptionConfiguration;
import org.opensaml.xmlsec.impl.BasicEncryptionParametersResolver;
import org.opensaml.xmlsec.keyinfo.impl.KeyInfoProvider;
import org.opensaml.xmlsec.keyinfo.impl.LocalKeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.provider.AgreementMethodKeyInfoProvider;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.testing.XMLSecurityTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.xml.ParserPool;
import net.shibboleth.shared.xml.SerializeSupport;

@SuppressWarnings({"javadoc", "null"})
public class DHWithLegacyKDFTest extends XMLObjectBaseTestCase {
    
    private String targetFile;
    
    private Credential recipientCredPrivate;
    private Credential recipientCredPublic;
    private CollectionCredentialResolver localCredentialResolver;
    private LocalKeyInfoCredentialResolver localKeyInfoResolver;
    
    private Encrypter encrypter;
    private EncryptionParametersResolver encParamsResolver;
    private CriteriaSet encCriteria;
    private BasicEncryptionConfiguration encConfig, encConfig2;
    
    private DecryptionParametersResolver decryptParamsResolver;
    private CriteriaSet decryptCriteria;
    private BasicDecryptionConfiguration decryptConfig;
    
    @BeforeClass
    public void beforeClass() throws Exception {
        targetFile = "/org/opensaml/xmlsec/encryption/support/SimpleEncryptionTest.xml";
        
        KeyPair kp = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_DIFFIE_HELLMAN, 2048, null);
        recipientCredPrivate = new BasicCredential(kp.getPublic(), kp.getPrivate());
        recipientCredPublic = new BasicCredential(kp.getPublic());
        
        encrypter = new Encrypter();
        encParamsResolver = new BasicEncryptionParametersResolver();
        
        decryptParamsResolver = new BasicDecryptionParametersResolver();
        
        localCredentialResolver = new CollectionCredentialResolver(Set.of(recipientCredPrivate));
        
        List<KeyInfoProvider> keyInfoProviders = new ArrayList<>(XMLSecurityTestingSupport.getBasicInlineKeyInfoProviders());
        keyInfoProviders.add(new AgreementMethodKeyInfoProvider());
        localKeyInfoResolver = new LocalKeyInfoCredentialResolver(keyInfoProviders, localCredentialResolver);
    }
    
    @BeforeMethod
    public void beforeMethod() throws Exception {
        encConfig = new BasicEncryptionConfiguration();
        encConfig2 = new BasicEncryptionConfiguration();
        encCriteria = new CriteriaSet(new EncryptionConfigurationCriterion(encConfig, encConfig2,
                ConfigurationService.get(EncryptionConfiguration.class)));
        
        // Configure the middle slot explicitly so that we aren't relying on whichever DH variant the library wide config has.
        KeyAgreementEncryptionConfiguration kaConfig = new KeyAgreementEncryptionConfiguration();
        kaConfig.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_DH);
        DigestMethod dm = new DigestMethod();
        dm.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        dm.initialize();
        KANonce nonce = new KANonce();
        nonce.initialize();
        kaConfig.setParameters(CollectionSupport.setOf(dm, nonce));
        encConfig2.setKeyAgreementConfigurations(Map.of("DH", kaConfig));
        
        decryptConfig = new BasicDecryptionConfiguration();
        decryptConfig.setDataKeyInfoCredentialResolver(localKeyInfoResolver);
        decryptConfig.setKEKKeyInfoCredentialResolver(localKeyInfoResolver);
        
        decryptCriteria = new CriteriaSet(new DecryptionConfigurationCriterion(decryptConfig,
                ConfigurationService.get(DecryptionConfiguration.class)));
    }
    
    @Test
    public void roundtripDirectDataEncryption() throws Exception {
        encConfig.setDataEncryptionCredentials(List.of(recipientCredPublic));
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, null, SignatureConstants.ALGO_ID_DIGEST_SHA256, true);
    }

    @Test
    public void roundtripDirectDataEncryptionWithAlgorithmOverrides() throws Exception {
        encConfig.setDataEncryptionCredentials(List.of(recipientCredPublic));
        encConfig.setDataEncryptionAlgorithms(List.of(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM));
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM, null, SignatureConstants.ALGO_ID_DIGEST_SHA256, true);
    }

    @Test
    public void roundtripWithKeyWrap() throws Exception {
        encConfig.setKeyTransportEncryptionCredentials(List.of(recipientCredPublic));
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, EncryptionConstants.ALGO_ID_KEYWRAP_AES128, SignatureConstants.ALGO_ID_DIGEST_SHA256, true);
    }

    @Test
    public void roundtripWithKeyWrapAndAlgorithmOverrides() throws Exception {
        encConfig.setKeyTransportEncryptionCredentials(List.of(recipientCredPublic));
        encConfig.setKeyTransportEncryptionAlgorithms(List.of(EncryptionConstants.ALGO_ID_KEYWRAP_AES256));
        encConfig.setDataEncryptionAlgorithms(List.of(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM));
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM, EncryptionConstants.ALGO_ID_KEYWRAP_AES256, SignatureConstants.ALGO_ID_DIGEST_SHA256, true);
    }

    @Test
    public void roundtripWithSHA512AndNoNonce() throws Exception {
        encConfig.setDataEncryptionCredentials(List.of(recipientCredPublic));
        
        KeyAgreementEncryptionConfiguration kaConfig = new KeyAgreementEncryptionConfiguration();
        DigestMethod dm = new DigestMethod();
        dm.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        kaConfig.setParameters(Set.of(dm));
        encConfig.setKeyAgreementConfigurations(Map.of("DH", kaConfig));
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, null, SignatureConstants.ALGO_ID_DIGEST_SHA512, false);
    }
    
    private void testRoundtrip(String expectedDataAlgo, String expectedKEKAlgo, String expectedDigestMethod, boolean nonceExpected) throws Exception {
        // Encrypt
        final SignableSimpleXMLObject sxoOrig = (SignableSimpleXMLObject) unmarshallElement(targetFile);
        assert sxoOrig != null;
        
        final EncryptionParameters encParams = encParamsResolver.resolveSingle(encCriteria);
        assert encParams != null;
        
        final DataEncryptionParameters dataEncParams = new DataEncryptionParameters(encParams);
        final List<KeyEncryptionParameters> kekParams = encParams.getKeyTransportEncryptionCredential() != null ?
                List.of(new KeyEncryptionParameters(encParams, null)) : CollectionSupport.emptyList();
        
        final EncryptedData encryptedDataOrig = encrypter.encryptElement(sxoOrig, dataEncParams, kekParams);
        Assert.assertNotNull(encryptedDataOrig);

        final KeyInfo encKeyInfo = encryptedDataOrig.getKeyInfo();
        assert encKeyInfo != null;
        
        if (expectedDataAlgo != null) {
            final EncryptionMethod method = encryptedDataOrig.getEncryptionMethod(); 
            assert method != null;
            Assert.assertEquals(method.getAlgorithm(), expectedDataAlgo);
        }
        
        if (expectedKEKAlgo != null) {
            final EncryptedKey ekey = encKeyInfo.getEncryptedKeys().get(0);
            assert ekey != null;
            final EncryptionMethod nestedMethod = ekey.getEncryptionMethod();
            assert nestedMethod != null;
            Assert.assertEquals(nestedMethod.getAlgorithm(), expectedKEKAlgo);
        }
        
        final AgreementMethod agreementMethod;
        if (!encKeyInfo.getEncryptedKeys().isEmpty())  {
            final EncryptedKey ekey = encKeyInfo.getEncryptedKeys().get(0);
            assert ekey != null;
            final KeyInfo nestedKeyInfo = ekey.getKeyInfo();
            assert nestedKeyInfo != null;
            agreementMethod = nestedKeyInfo.getAgreementMethods().get(0);
        } else {
            agreementMethod = encKeyInfo.getAgreementMethods().get(0);
        }
        Assert.assertNotNull(agreementMethod);
        Assert.assertEquals(agreementMethod.getAlgorithm(), EncryptionConstants.ALGO_ID_KEYAGREEMENT_DH);
        
        if (expectedDigestMethod != null) {
            org.opensaml.xmlsec.signature.DigestMethod digestMethod =
                    (org.opensaml.xmlsec.signature.DigestMethod) agreementMethod
                    .getUnknownXMLObjects(org.opensaml.xmlsec.signature.DigestMethod.DEFAULT_ELEMENT_NAME).get(0);
            Assert.assertNotNull(digestMethod);
            Assert.assertEquals(digestMethod.getAlgorithm(), expectedDigestMethod);
        }
        
        org.opensaml.xmlsec.encryption.KANonce nonce = agreementMethod.getKANonce();
        if (nonceExpected) {
            assert nonce != null;
            Assert.assertNotNull(nonce.getValue());
        } else {
            Assert.assertNull(nonce);
        }
        
        // Serialize out and back in
        Element domEncrypted = XMLObjectSupport.marshall(encryptedDataOrig);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializeSupport.writeNode(domEncrypted, baos);
        baos.flush();
        byte[] bytesEncrypted = baos.toByteArray();
        
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytesEncrypted);
        final ParserPool parser = XMLObjectProviderRegistrySupport.getParserPool();
        assert parser != null;
        final EncryptedData encryptedData = (EncryptedData) XMLObjectSupport.unmarshallFromInputStream(parser, bais);
        Assert.assertNotNull(encryptedData);
        
        // Decrypt
        DecryptionParameters decryptParams = decryptParamsResolver.resolveSingle(decryptCriteria);
        
        Decrypter decrypter = new Decrypter(decryptParams);
        
        XMLObject decryptedXMLObject = decrypter.decryptData(encryptedData);
        Assert.assertNotNull(decryptedXMLObject);
        Assert.assertTrue(decryptedXMLObject instanceof SignableSimpleXMLObject);
        
        final Element origDOM = sxoOrig.getDOM();
        assert origDOM != null;
        assertXMLEquals(origDOM.getOwnerDocument(), decryptedXMLObject);
    }

}
