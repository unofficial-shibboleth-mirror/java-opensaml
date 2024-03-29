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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;

import net.shibboleth.shared.codec.EncodingException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.impl.CollectionCredentialResolver;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolutionMode;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolutionMode.Mode;
import org.opensaml.xmlsec.keyinfo.KeyInfoCriterion;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.keyinfo.impl.provider.RSAKeyValueProvider;
import org.opensaml.xmlsec.signature.KeyInfo;

/**
 * Test the local credential resolver specialization of the KeyInfo credential resolver.
 */
@SuppressWarnings({"javadoc", "null"})
public class LocalKeyInfoCredentialResolverTest extends XMLObjectBaseTestCase {
    
    private String keyName;
    private KeyPair keyPair;
    private BasicCredential localCred;
    
    private CollectionCredentialResolver localCredResolver;
    private LocalKeyInfoCredentialResolver keyInfoResolver;
    
    private KeyInfo keyInfo;

    @BeforeMethod
    protected void setUp() throws Exception {
        keyName = "MyKey";
        keyPair = KeySupport.generateKeyPair("RSA", 1024, null);
        
        localCred = new BasicCredential(keyPair.getPublic(), keyPair.getPrivate());
        localCred.getKeyNames().add(keyName);
        
        localCredResolver = new CollectionCredentialResolver();
        localCredResolver.getCollection().add(localCred);
        
        final ArrayList<KeyInfoProvider> providers = new ArrayList<>();
        providers.add( new RSAKeyValueProvider() );
        keyInfoResolver = new LocalKeyInfoCredentialResolver(providers, localCredResolver);
        
        keyInfo = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
    }
    
    @Test
    public void testKeyInfoWithKeyName() throws ResolverException {
        KeyInfoSupport.addKeyName(keyInfo, keyName);
        
        final CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo) );
        final Credential resolvedCred = keyInfoResolver.resolveSingle(criteriaSet);
        
        Assert.assertEquals(resolvedCred, localCred, "Unexpected local credential resolved");
    }

    @Test
    public void testKeyInfoWithKnownPublicKey() throws ResolverException, EncodingException {
        KeyInfoSupport.addPublicKey(keyInfo, keyPair.getPublic());
        
        final CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo) );
        final Credential resolvedCred = keyInfoResolver.resolveSingle(criteriaSet);
        
        Assert.assertEquals(resolvedCred, localCred, "Unexpected local credential resolved");
    }
    
    @Test
    public void testKeyInfoWithUnknownPublicKey() throws IllegalArgumentException,
        NoSuchAlgorithmException, NoSuchProviderException, ResolverException, EncodingException {
        
        KeyInfoSupport.addPublicKey(keyInfo, 
                KeySupport.generateKeyPair("RSA", 1024, null).getPublic());
        
        final CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo) );
        final Credential resolvedCred = keyInfoResolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(resolvedCred, "Expected no credential to be resolved");
    }
    
    @Test
    public void testLocalMode() throws ResolverException, EncodingException {
        // Nominally the same as "testKeyInfoWithKnownPublicKey", just testing an explicit mode which is the default
        KeyInfoSupport.addPublicKey(keyInfo, keyPair.getPublic());
        
        final CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo), new KeyInfoCredentialResolutionMode(Mode.LOCAL) );
        final Credential resolvedCred = keyInfoResolver.resolveSingle(criteriaSet);
        
        Assert.assertEquals(resolvedCred, localCred, "Unexpected local credential resolved");
    }
    
    @Test
    public void testPublicMode() throws ResolverException, EncodingException {
        localCredResolver.getCollection().clear();
        
        KeyInfoSupport.addPublicKey(keyInfo, keyPair.getPublic());
        
        final CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo), new KeyInfoCredentialResolutionMode(Mode.PUBLIC) );
        final Credential resolvedCred = keyInfoResolver.resolveSingle(criteriaSet);
        
        assert resolvedCred != null;
        Assert.assertNotSame(resolvedCred, localCred);
        Assert.assertNull(resolvedCred.getPrivateKey());
        Assert.assertNotNull(resolvedCred.getPublicKey());
        Assert.assertEquals(resolvedCred.getPublicKey(), keyPair.getPublic());
    }
    
    @Test
    public void testBothModeWithLocalNotPresent() throws ResolverException, EncodingException {
        localCredResolver.getCollection().clear();
        
        KeyInfoSupport.addPublicKey(keyInfo, keyPair.getPublic());
        
        final CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo), new KeyInfoCredentialResolutionMode(Mode.BOTH) );
        final Credential resolvedCred = keyInfoResolver.resolveSingle(criteriaSet);
        
        assert resolvedCred != null;
        Assert.assertNotSame(resolvedCred, localCred);
        Assert.assertNull(resolvedCred.getPrivateKey());
        Assert.assertNotNull(resolvedCred.getPublicKey());
        Assert.assertEquals(resolvedCred.getPublicKey(), keyPair.getPublic());
    }
    
    @Test
    public void testBothModeWithLocalPresent() throws ResolverException, EncodingException {
        KeyInfoSupport.addPublicKey(keyInfo, keyPair.getPublic());
        
        final CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo), new KeyInfoCredentialResolutionMode(Mode.BOTH) );
        final Credential resolvedCred = keyInfoResolver.resolveSingle(criteriaSet);
        
        Assert.assertEquals(resolvedCred, localCred, "Unexpected local credential resolved");
    }
    
}
