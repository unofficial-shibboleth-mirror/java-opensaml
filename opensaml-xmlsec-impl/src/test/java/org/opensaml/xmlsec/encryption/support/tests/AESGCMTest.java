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

import javax.crypto.Cipher;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.testing.SecurityProviderTestSupport;
import org.opensaml.xmlsec.algorithm.AlgorithmDescriptor;
import org.opensaml.xmlsec.algorithm.AlgorithmRegistry;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.algorithm.KeyLengthSpecifiedAlgorithm;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.encryption.support.Decrypter;
import org.opensaml.xmlsec.encryption.support.Encrypter;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

@SuppressWarnings({"javadoc", "null"})
public class AESGCMTest extends XMLObjectBaseTestCase {
    
    private Logger log = LoggerFactory.getLogger(AESGCMTest.class);
    
    private String targetFile;
    
    private SecurityProviderTestSupport providerSupport;
    
    public AESGCMTest() {
        super();
        
        providerSupport = new SecurityProviderTestSupport();
        
        targetFile = "/org/opensaml/xmlsec/encryption/support/SimpleEncryptionTest.xml";
    }
    
    @DataProvider
    public Object[][] testDataAESGCM() {
        final AlgorithmRegistry registry = AlgorithmSupport.ensureGlobalAlgorithmRegistry();
        AlgorithmDescriptor aesGCM128 = registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM);
        AlgorithmDescriptor aesGCM192 = registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM);
        AlgorithmDescriptor aesGCM256 = registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM);
        
        return new Object[][] {
                new Object[] {aesGCM128, true},
                new Object[] {aesGCM192, true},
                new Object[] {aesGCM256, true},
                
                new Object[] {aesGCM128, false},
                new Object[] {aesGCM192, false},
                new Object[] {aesGCM256, false},
                
                new Object[] {aesGCM128, true},
                new Object[] {aesGCM192, true},
                new Object[] {aesGCM256, true},
        };
    }

    @Test(dataProvider="testDataAESGCM")
    public void testEncryptDecrypt(AlgorithmDescriptor descriptor, boolean loadBC) throws Exception {
        
        int maxKeyLength = Cipher.getMaxAllowedKeyLength(descriptor.getJCAAlgorithmID());
        log.debug("Installed policy indicates max allowed key length for '{}' is: {}", descriptor.getJCAAlgorithmID(), maxKeyLength);
        if (descriptor instanceof KeyLengthSpecifiedAlgorithm 
                && ((KeyLengthSpecifiedAlgorithm)descriptor).getKeyLength() > maxKeyLength) {
            log.debug("Key length {} will exceed max key length {}", 
                    ((KeyLengthSpecifiedAlgorithm)descriptor).getKeyLength(), maxKeyLength);
        } else {
            log.debug("Key length {} is ok for max key length {}", 
                    ((KeyLengthSpecifiedAlgorithm)descriptor).getKeyLength(), maxKeyLength);
        }
        
        try {
            if (loadBC) {
                providerSupport.loadBC();
            }
        
            final SignableSimpleXMLObject sxo = (SignableSimpleXMLObject) unmarshallElement(targetFile);
            assert sxo != null;
            
            final Credential encCred = AlgorithmSupport.generateSymmetricKeyAndCredential(descriptor.getURI());
            
            final DataEncryptionParameters encParams = new DataEncryptionParameters();
            encParams.setAlgorithm(descriptor.getURI());
            encParams.setEncryptionCredential(encCred);
            
            final Encrypter encrypter = new Encrypter();
            
            final EncryptedData encryptedData = encrypter.encryptElement(sxo, encParams);
            assert encryptedData != null;
            
            final EncryptionMethod method = encryptedData.getEncryptionMethod();
            assert method != null;
            Assert.assertEquals(method.getAlgorithm(), descriptor.getURI());
            
            final StaticKeyInfoCredentialResolver dataKeyInfoResolver = new StaticKeyInfoCredentialResolver(encCred);
            
            final Decrypter decrypter = new Decrypter(dataKeyInfoResolver, null, null);
            
            final XMLObject decryptedXMLObject = decrypter.decryptData(encryptedData);
            
            Assert.assertNotNull(decryptedXMLObject);
            Assert.assertTrue(decryptedXMLObject instanceof SignableSimpleXMLObject);
            final Element dom = sxo.getDOM();
            assert dom != null;
            assertXMLEquals(dom.getOwnerDocument(), decryptedXMLObject);
            
        } finally {
            providerSupport.unloadBC();
        }
        
        
    }
}
