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

package org.opensaml.xmlsec.impl;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

import javax.annotation.Nullable;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.KeyTransportAlgorithmPredicate;
import org.opensaml.xmlsec.encryption.support.RSAOAEPParameters;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;

@SuppressWarnings({"javadoc", "null"})
public class BasicEncryptionConfigurationTest {
    
    private BasicEncryptionConfiguration config;
    
    private Credential cred1, cred2;
    
    @BeforeClass
    public void generateCredentials() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair kp1 = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null);
        cred1 = CredentialSupport.getSimpleCredential(kp1.getPublic(), kp1.getPrivate());
        
        KeyPair kp2 = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null);
        cred2 = CredentialSupport.getSimpleCredential(kp2.getPublic(), kp2.getPrivate());
    }
    
    @BeforeMethod
    public void setUp() {
        config = new BasicEncryptionConfiguration();
    }
    
    @Test
    public void testDefaults() {
        Assert.assertNotNull(config.getDataEncryptionCredentials());
        Assert.assertTrue(config.getDataEncryptionCredentials().isEmpty());
        
        Assert.assertNotNull(config.getDataEncryptionAlgorithms());
        Assert.assertTrue(config.getDataEncryptionAlgorithms().isEmpty());
        
        Assert.assertNotNull(config.getKeyTransportEncryptionCredentials());
        Assert.assertTrue(config.getKeyTransportEncryptionCredentials().isEmpty());
        
        Assert.assertNotNull(config.getKeyTransportEncryptionAlgorithms());
        Assert.assertTrue(config.getKeyTransportEncryptionAlgorithms().isEmpty());
        
        Assert.assertNull(config.getDataKeyInfoGeneratorManager());
        Assert.assertNull(config.getKeyTransportKeyInfoGeneratorManager());
        
        Assert.assertNull(config.getRSAOAEPParameters());
        
        Assert.assertNull(config.getKeyTransportAlgorithmPredicate());
    }

    @Test
    public void testDataEncryptionCredentials() {
        Assert.assertNotNull(config.getDataEncryptionCredentials());
        Assert.assertEquals(config.getDataEncryptionCredentials().size(), 0);
        
        config.setDataEncryptionCredentials(Arrays.asList(cred1, cred2));
        
        Assert.assertNotNull(config.getDataEncryptionCredentials());
        Assert.assertEquals(config.getDataEncryptionCredentials().size(), 2);
        
        config.setDataEncryptionCredentials(null);
        
        Assert.assertNotNull(config.getDataEncryptionCredentials());
        Assert.assertEquals(config.getDataEncryptionCredentials().size(), 0);
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testDataEncryptionCredentialsImmutable() {
        config.setDataEncryptionCredentials(CollectionSupport.singletonList(cred1));
        config.getDataEncryptionCredentials().add(cred2);
    }
    
    @Test
    public void testDataEncryptionAlgorithmURIs() {
        Assert.assertNotNull(config.getDataEncryptionAlgorithms());
        Assert.assertEquals(config.getDataEncryptionAlgorithms().size(), 0);
        
        config.setDataEncryptionAlgorithms(CollectionSupport.listOf("   A   ", "   B    ", "   C    "));
        
        Assert.assertNotNull(config.getDataEncryptionAlgorithms());
        Assert.assertEquals(config.getDataEncryptionAlgorithms().size(), 3);
        Assert.assertEquals(config.getDataEncryptionAlgorithms().get(0), "A");
        Assert.assertEquals(config.getDataEncryptionAlgorithms().get(1), "B");
        Assert.assertEquals(config.getDataEncryptionAlgorithms().get(2), "C");
        
        config.setDataEncryptionAlgorithms(null);
        
        Assert.assertNotNull(config.getDataEncryptionAlgorithms());
        Assert.assertEquals(config.getDataEncryptionAlgorithms().size(), 0);
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testDataEncryptionAlgorithmURIsImmutable() {
        config.setDataEncryptionAlgorithms(CollectionSupport.listOf("A", "B", "C"));
        config.getDataEncryptionAlgorithms().add("D");
    }
    
    @Test
    public void testKeyTransportEncryptionCredentials() {
        Assert.assertNotNull(config.getKeyTransportEncryptionCredentials());
        Assert.assertEquals(config.getKeyTransportEncryptionCredentials().size(), 0);
        
        config.setKeyTransportEncryptionCredentials(CollectionSupport.listOf(cred1, cred2));
        
        Assert.assertNotNull(config.getKeyTransportEncryptionCredentials());
        Assert.assertEquals(config.getKeyTransportEncryptionCredentials().size(), 2);
        
        config.setKeyTransportEncryptionCredentials(null);
        
        Assert.assertNotNull(config.getKeyTransportEncryptionCredentials());
        Assert.assertEquals(config.getKeyTransportEncryptionCredentials().size(), 0);
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testKeyTransportEncryptionCredentialsImmutable() {
        config.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(cred1));
        config.getKeyTransportEncryptionCredentials().add(cred2);
    }
    
    @Test
    public void testKeyTransportEncryptionAlgorithmURIs() {
        Assert.assertNotNull(config.getKeyTransportEncryptionAlgorithms());
        Assert.assertEquals(config.getKeyTransportEncryptionAlgorithms().size(), 0);
        
        config.setKeyTransportEncryptionAlgorithms(Arrays.asList("   A    ", null, null, "   B   ", null, "   C   "));
        
        Assert.assertNotNull(config.getKeyTransportEncryptionAlgorithms());
        Assert.assertEquals(config.getKeyTransportEncryptionAlgorithms().size(), 3);
        Assert.assertEquals(config.getKeyTransportEncryptionAlgorithms().get(0), "A");
        Assert.assertEquals(config.getKeyTransportEncryptionAlgorithms().get(1), "B");
        Assert.assertEquals(config.getKeyTransportEncryptionAlgorithms().get(2), "C");
        
        config.setKeyTransportEncryptionAlgorithms(null);
        
        Assert.assertNotNull(config.getKeyTransportEncryptionAlgorithms());
        Assert.assertEquals(config.getKeyTransportEncryptionAlgorithms().size(), 0);
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testKeyTransportEncryptionAlgorithmURIsImmutable() {
        config.setKeyTransportEncryptionAlgorithms(Arrays.asList("A", "B", "C"));
        config.getKeyTransportEncryptionAlgorithms().add("D");
    }
    
    @Test
    public void testDataKeyInfoGeneratorManager() {
        Assert.assertNull(config.getDataKeyInfoGeneratorManager());
        
        config.setDataKeyInfoGeneratorManager(new NamedKeyInfoGeneratorManager());
        
        Assert.assertNotNull(config.getDataKeyInfoGeneratorManager());
        
        config.setDataKeyInfoGeneratorManager(null);
        
        Assert.assertNull(config.getDataKeyInfoGeneratorManager());
    }
    
    @Test
    public void testKeyTransportKeyInfoGeneratorManager() {
        Assert.assertNull(config.getKeyTransportKeyInfoGeneratorManager());
        
        config.setKeyTransportKeyInfoGeneratorManager(new NamedKeyInfoGeneratorManager());
        
        Assert.assertNotNull(config.getKeyTransportKeyInfoGeneratorManager());
        
        config.setKeyTransportKeyInfoGeneratorManager(null);
        
        Assert.assertNull(config.getKeyTransportKeyInfoGeneratorManager());
    }
    
    @Test
    public void testRSAOAEPParameters() {
        Assert.assertNull(config.getRSAOAEPParameters());
        
        config.setRSAOAEPParameters(new RSAOAEPParameters());
        
        Assert.assertNotNull(config.getRSAOAEPParameters());
        
        config.setRSAOAEPParameters(null);
        
        Assert.assertNull(config.getRSAOAEPParameters());
    }
    
    @Test
    public void testRSAOAEPParametersMerge() {
        Assert.assertTrue(config.isRSAOAEPParametersMerge());
        
        config.setRSAOAEPParametersMerge(false);
        
        Assert.assertFalse(config.isRSAOAEPParametersMerge());
        
        config.setRSAOAEPParametersMerge(true);
        
        Assert.assertTrue(config.isRSAOAEPParametersMerge());
    }
    
    @Test
    public void testKeyTransportAlgorithmPredicate() {
        Assert.assertNull(config.getKeyTransportAlgorithmPredicate());
        
        KeyTransportAlgorithmPredicate predicate = new KeyTransportAlgorithmPredicate() {
            public boolean test(@Nullable SelectionInput input) {
                return true;
            }
        };
        
        config.setKeyTransportAlgorithmPredicate(predicate);
        
        Assert.assertNotNull(config.getKeyTransportAlgorithmPredicate());
        
        config.setKeyTransportAlgorithmPredicate(null);
        
        Assert.assertNull(config.getKeyTransportAlgorithmPredicate());
    }
}
