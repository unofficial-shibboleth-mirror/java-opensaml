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

import java.security.KeyPair;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialContextSet;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.credential.impl.CollectionCredentialResolver;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.agreement.KeyAgreementCredential;
import org.opensaml.xmlsec.agreement.KeyAgreementParameters;
import org.opensaml.xmlsec.agreement.impl.ECDHKeyAgreementProcessor;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.derivation.impl.ConcatKDF;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedType;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.KeySize;
import org.opensaml.xmlsec.encryption.OriginatorKeyInfo;
import org.opensaml.xmlsec.encryption.RecipientKeyInfo;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.keyinfo.KeyInfoCriterion;
import org.opensaml.xmlsec.keyinfo.impl.provider.AgreementMethodKeyInfoProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.DEREncodedKeyValueProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.DSAKeyValueProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.InlineX509DataProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.RSAKeyValueProvider;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

@SuppressWarnings({"javadoc", "null"})
public class AgreementMethodTest extends XMLObjectBaseTestCase {
    
    private LocalKeyInfoCredentialResolver resolver;
    
    private Credential credRecipientPrivateEC, credRecipientPublicEC;
    private KeyAgreementCredential credKeyAgreementOriginatorEC;
    
    private CollectionCredentialResolver recipientLocalCredResolver;
    
    private KeyAgreementKeyInfoGeneratorFactory keyInfoFactory;
    
    private String expectedEncryptionAlgorithm = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM;
    
    @BeforeClass
    public void beforeClass() throws Exception {
        KeyPair kpRecipientEC = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, new ECGenParameterSpec("secp256r1"), null);
        credRecipientPrivateEC = CredentialSupport.getSimpleCredential(kpRecipientEC.getPublic(), kpRecipientEC.getPrivate());
        credRecipientPublicEC = CredentialSupport.getSimpleCredential(kpRecipientEC.getPublic(), null);
        
        recipientLocalCredResolver = new CollectionCredentialResolver();
        
        List<KeyInfoProvider> providers = new ArrayList<>();
        providers.add( new RSAKeyValueProvider() );
        providers.add( new DSAKeyValueProvider() );
        providers.add( new DEREncodedKeyValueProvider());
        providers.add( new InlineX509DataProvider() );
        providers.add( new AgreementMethodKeyInfoProvider() );
        resolver = new LocalKeyInfoCredentialResolver(providers, recipientLocalCredResolver);
        
        keyInfoFactory = new KeyAgreementKeyInfoGeneratorFactory();
    }
    
    @BeforeMethod
    public void beforeMethod() throws Exception {
        recipientLocalCredResolver.getCollection().clear();
        recipientLocalCredResolver.getCollection().add(credRecipientPrivateEC);
        
        ConcatKDF kdf = new ConcatKDF();
        kdf.setDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        kdf.setAlgorithmID("AA");
        kdf.setPartyUInfo("BB");
        kdf.setPartyVInfo("CC");
        kdf.setSuppPubInfo("DD");
        kdf.setSuppPrivInfo("EE");
        kdf.initialize();
        
        KeyAgreementParameters params = new KeyAgreementParameters();
        params.add(kdf);
        
        ECDHKeyAgreementProcessor processor = new ECDHKeyAgreementProcessor();
        
        credKeyAgreementOriginatorEC = processor.execute(credRecipientPublicEC, expectedEncryptionAlgorithm, params);
    }
    
    @Test
    public void ECDHWithConcatKDF_Success() throws Exception {
        KeyInfo keyInfo = prepareAndValidateKeyInfo(credKeyAgreementOriginatorEC);
        
        Iterable<Credential> creds = resolver.resolve(new CriteriaSet(new KeyInfoCriterion(keyInfo)));
        
        Assert.assertNotNull(creds);
        Assert.assertEquals(Iterables.size(creds), 1);
        
        Credential cred = creds.iterator().next();
        
        Assert.assertTrue(KeyAgreementCredential.class.isInstance(cred));
        
        
        KeyAgreementCredential keyAgreementCred = KeyAgreementCredential.class.cast(cred);
        Assert.assertEquals(keyAgreementCred.getAlgorithm(), EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        validateDerivedKey(keyAgreementCred, expectedEncryptionAlgorithm);
        
        // Originator credential
        Assert.assertNotNull(keyAgreementCred.getOriginatorCredential());
        Assert.assertNotNull(keyAgreementCred.getOriginatorCredential().getPublicKey());
        Assert.assertNull(keyAgreementCred.getOriginatorCredential().getPrivateKey());
        Assert.assertEquals(keyAgreementCred.getOriginatorCredential().getPublicKey(), credKeyAgreementOriginatorEC.getOriginatorCredential().getPublicKey());
        
        // Recipient credential
        Assert.assertNotNull(keyAgreementCred.getRecipientCredential());
        Assert.assertNotNull(keyAgreementCred.getRecipientCredential().getPublicKey());
        Assert.assertNotNull(keyAgreementCred.getRecipientCredential().getPrivateKey());
        Assert.assertEquals(keyAgreementCred.getRecipientCredential().getPublicKey(), credRecipientPrivateEC.getPublicKey());
        Assert.assertEquals(keyAgreementCred.getRecipientCredential().getPrivateKey(), credRecipientPrivateEC.getPrivateKey());
        
        // Parameters
        Assert.assertTrue(keyAgreementCred.getParameters().contains(ConcatKDF.class));
        final ConcatKDF kdf = keyAgreementCred.getParameters().get(ConcatKDF.class);
        assert kdf != null;
        Assert.assertEquals(kdf.getDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA512);
        Assert.assertEquals(kdf.getAlgorithmID(), "AA");
        Assert.assertEquals(kdf.getPartyUInfo(), "BB");
        Assert.assertEquals(kdf.getPartyVInfo(), "CC");
        Assert.assertEquals(kdf.getSuppPubInfo(), "DD");
        Assert.assertEquals(kdf.getSuppPrivInfo(), "EE");
        
        final CredentialContextSet ctx = keyAgreementCred.getCredentialContextSet();
        assert ctx != null;
        final KeyInfoCredentialContext keyInfoCtx = ctx.get(KeyInfoCredentialContext.class);
        assert keyInfoCtx != null;
        Assert.assertSame(keyInfoCtx.getKeyInfo(), keyInfo);
    }
    
    @Test
    public void agreementAlgorithmNotRegistered() throws Exception {
        final KeyInfo keyInfo = prepareAndValidateKeyInfo(credKeyAgreementOriginatorEC);
        
        keyInfo.getAgreementMethods().get(0).setAlgorithm("INVALID");
        
        final Iterable<Credential> creds = resolver.resolve(new CriteriaSet(new KeyInfoCriterion(keyInfo)));
        
        Assert.assertNotNull(creds);
        Assert.assertEquals(Iterables.size(creds), 0);
    }
    
    @Test
    public void agreementMethodNotGranndchildOfEncryptedType() throws Exception {
        final KeyInfo keyInfo = prepareAndValidateKeyInfo(credKeyAgreementOriginatorEC);
        
        keyInfo.setParent(null);
        
        final Iterable<Credential> creds = resolver.resolve(new CriteriaSet(new KeyInfoCriterion(keyInfo)));
        
        Assert.assertNotNull(creds);
        Assert.assertEquals(Iterables.size(creds), 0);
    }
    
    @Test(expectedExceptions = ResolverException.class)
    public void missingEncryptionAlgorithm() throws Exception {
        final KeyInfo keyInfo = prepareAndValidateKeyInfo(credKeyAgreementOriginatorEC);
        final EncryptionMethod method = EncryptedType.class.cast(keyInfo.getParent()).getEncryptionMethod(); 
        assert method != null;
        method.setAlgorithm(null);
        
        resolver.resolve(new CriteriaSet(new KeyInfoCriterion(keyInfo)));
    }
    
    @Test(expectedExceptions = ResolverException.class)
    public void unknownEncryptionAlgorithm() throws Exception {
        final KeyInfo keyInfo = prepareAndValidateKeyInfo(credKeyAgreementOriginatorEC);
        
        final EncryptionMethod method = EncryptedType.class.cast(keyInfo.getParent()).getEncryptionMethod(); 
        assert method != null;
        method.setAlgorithm("INVALID");
        
        resolver.resolve(new CriteriaSet(new KeyInfoCriterion(keyInfo)));
    }
    
    @Test(expectedExceptions = ResolverException.class)
    public void missingOriginatorKeyInfo() throws Exception {
        final KeyInfo keyInfo = prepareAndValidateKeyInfo(credKeyAgreementOriginatorEC);
        
        keyInfo.getAgreementMethods().get(0).setOriginatorKeyInfo(null);
        
        resolver.resolve(new CriteriaSet(new KeyInfoCriterion(keyInfo)));
    }
    
    @Test(expectedExceptions = ResolverException.class)
    public void originatorCredResolutionFailedMissingKeyInfoData() throws Exception {
        final KeyInfo keyInfo = prepareAndValidateKeyInfo(credKeyAgreementOriginatorEC);
        final OriginatorKeyInfo oki = keyInfo.getAgreementMethods().get(0).getOriginatorKeyInfo();
        assert oki != null;
        
        oki.getDEREncodedKeyValues().clear();
        oki.getKeyValues().clear();
        
        resolver.resolve(new CriteriaSet(new KeyInfoCriterion(keyInfo)));
    }
    
    @Test(expectedExceptions = ResolverException.class)
    public void missingRecipientKeyInfo() throws Exception {
        final KeyInfo keyInfo = prepareAndValidateKeyInfo(credKeyAgreementOriginatorEC);
        
        keyInfo.getAgreementMethods().get(0).setRecipientKeyInfo(null);
        
        resolver.resolve(new CriteriaSet(new KeyInfoCriterion(keyInfo)));
    }
    
    @Test(expectedExceptions = ResolverException.class)
    public void recipientCredResolutionFailedMissingKeyInfoData() throws Exception {
        final KeyInfo keyInfo = prepareAndValidateKeyInfo(credKeyAgreementOriginatorEC);
        final RecipientKeyInfo rki = keyInfo.getAgreementMethods().get(0).getRecipientKeyInfo(); 
        assert rki != null;
        
        rki.getDEREncodedKeyValues().clear();
        rki.getKeyValues().clear();
        
        resolver.resolve(new CriteriaSet(new KeyInfoCriterion(keyInfo)));
    }
    
    
    @Test(expectedExceptions = ResolverException.class)
    public void recipientCredResolutionFailedAtCredentialResolver() throws Exception {
        final KeyInfo keyInfo = prepareAndValidateKeyInfo(credKeyAgreementOriginatorEC);
        
        recipientLocalCredResolver.getCollection().clear();
        
        resolver.resolve(new CriteriaSet(new KeyInfoCriterion(keyInfo)));
    }
    
    @Test(expectedExceptions = ResolverException.class)
    public void recipientCredMissingPrivateKey() throws Exception {
        final KeyInfo keyInfo = prepareAndValidateKeyInfo(credKeyAgreementOriginatorEC);
        
        recipientLocalCredResolver.getCollection().clear();
        recipientLocalCredResolver.getCollection().add(credRecipientPublicEC);
        
        resolver.resolve(new CriteriaSet(new KeyInfoCriterion(keyInfo)));
    }
    
    //
    // Helpers
    //
    
    @Nonnull private KeyInfo prepareAndValidateKeyInfo(KeyAgreementCredential cred) throws SecurityException {
        KeyInfo keyInfo = keyInfoFactory.newInstance().generate(credKeyAgreementOriginatorEC);
        assert keyInfo != null;
        final List<XMLObject> children = keyInfo.getOrderedChildren();
        assert children != null;
        Assert.assertEquals(children.size(), 1);
        Assert.assertEquals(keyInfo.getAgreementMethods().size(), 1);
        makeEncryptionMethodChild(keyInfo, expectedEncryptionAlgorithm, null, EncryptedData.DEFAULT_ELEMENT_NAME); 
        return keyInfo;
    }
    
    @Nonnull private EncryptedType makeEncryptionMethodChild(KeyInfo keyinfo, String algorithm, Integer keySize, QName elementType) {
        EncryptedType encryptedType = buildXMLObject(elementType);
        encryptedType.setKeyInfo(keyinfo);
        
        EncryptionMethod encryptionMethod = buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME);
        encryptionMethod.setAlgorithm(algorithm);
        if (keySize != null) {
            KeySize keySizeElement = buildXMLObject(KeySize.DEFAULT_ELEMENT_NAME);
            keySizeElement.setValue(keySize);
            encryptionMethod.setKeySize(keySizeElement);
        }
        encryptedType.setEncryptionMethod(encryptionMethod);;
        return encryptedType;
    }
    
    private void validateDerivedKey(@Nonnull final Credential credential, @Nonnull final String algorithmURI) {
        Assert.assertNotNull(credential.getSecretKey());
        final SecretKey skey = credential.getSecretKey();
        assert skey != null;
        Assert.assertEquals(skey.getAlgorithm(), AlgorithmSupport.getKeyAlgorithm(algorithmURI));
        Assert.assertEquals(KeySupport.getKeyLength(skey), AlgorithmSupport.getKeyLength(algorithmURI));
    }

}
