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

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.security.testing.SecurityProviderTestSupport;
import org.opensaml.xmlsec.algorithm.AlgorithmRegistry;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.config.GlobalAlgorithmRegistryInitializer;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@SuppressWarnings({"javadoc", "null"})
public class AlgorithmRuntimeSupportedPredicateTest extends OpenSAMLInitBaseTestCase {
    
    private AlgorithmRuntimeSupportedPredicate predicate;
    
    private SecurityProviderTestSupport providerSupport;
    
    public AlgorithmRuntimeSupportedPredicateTest() {
        providerSupport = new SecurityProviderTestSupport();
    }
    
    @DataProvider
    public Object[][] loadBCTestData() {
        return new Object[][] {
                new Object[] {Boolean.FALSE},
                new Object[] {Boolean.TRUE},
        };
    }
    
    @BeforeMethod
    public void setUp() {
        final AlgorithmRegistry registry = AlgorithmSupport.ensureGlobalAlgorithmRegistry();
        predicate = new AlgorithmRuntimeSupportedPredicate(registry);
    }
    
    @Test(dataProvider = "loadBCTestData")
    public void testCommon(boolean loadBC) throws InitializationException {
        final AlgorithmRegistry originalRegistry = AlgorithmSupport.ensureGlobalAlgorithmRegistry();
        
        if (loadBC) {
            providerSupport.loadBC();
            new GlobalAlgorithmRegistryInitializer().init();
            predicate = new AlgorithmRuntimeSupportedPredicate(AlgorithmSupport.ensureGlobalAlgorithmRegistry());
        }
        
        try {
            // These should be unconditionally supported on Java 7+ with no additional security providers
            Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_DIGEST_SHA1));
            Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_DIGEST_SHA256));
            Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_DIGEST_SHA384));
            Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_DIGEST_SHA512));
            
            Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1));
            Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
            Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384));
            Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512));
            
            Assert.assertTrue(predicate.test(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
            Assert.assertTrue(predicate.test(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP));
            
            Assert.assertTrue(predicate.test(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128));
            Assert.assertTrue(predicate.test(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192));
            Assert.assertTrue(predicate.test(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
            Assert.assertTrue(predicate.test(EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES));
        } finally {
            providerSupport.unloadBC();
            ConfigurationService.register(AlgorithmRegistry.class, originalRegistry);
        }
    }
    
    @Test(dataProvider = "loadBCTestData")
    public void testConditional(boolean loadBC) throws InitializationException {
        final AlgorithmRegistry originalRegistry = AlgorithmSupport.ensureGlobalAlgorithmRegistry();
        
        if (loadBC) {
            providerSupport.loadBC();
            new GlobalAlgorithmRegistryInitializer().init();
            predicate = new AlgorithmRuntimeSupportedPredicate(AlgorithmSupport.ensureGlobalAlgorithmRegistry());
        }
        
        try {
            
            if (providerSupport.haveSunEC() || providerSupport.haveBC()) {
                Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1));
                Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256));
                Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA384));
                Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512));
                
                Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA224));
            } else {
                Assert.assertFalse(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1));
                Assert.assertFalse(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA224));
                Assert.assertFalse(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256));
                Assert.assertFalse(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA384));
                Assert.assertFalse(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512));
            }
            
            Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_DIGEST_SHA224));
            Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA224));
            Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_MAC_HMAC_SHA224));
            
            Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA256));
            
            Assert.assertTrue(predicate.test(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM));
            Assert.assertTrue(predicate.test(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM));
            Assert.assertTrue(predicate.test(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM));
            Assert.assertTrue(predicate.test(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11));
            
            if (providerSupport.haveBC()) {
                Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_DIGEST_RIPEMD160));
                Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_MAC_HMAC_RIPEMD160));
                Assert.assertTrue(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_RSA_RIPEMD160));
            } else {
                Assert.assertFalse(predicate.test(SignatureConstants.ALGO_ID_DIGEST_RIPEMD160));
                Assert.assertFalse(predicate.test(SignatureConstants.ALGO_ID_MAC_HMAC_RIPEMD160));
                Assert.assertFalse(predicate.test(SignatureConstants.ALGO_ID_SIGNATURE_RSA_RIPEMD160));
            }
        
        } finally {
            providerSupport.unloadBC();
            ConfigurationService.register(AlgorithmRegistry.class, originalRegistry);
        }
    }


}
