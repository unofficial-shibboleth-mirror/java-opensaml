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

package org.opensaml.xmlsec.derivation.tests;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.xmlsec.derivation.KeyDerivationException;
import org.opensaml.xmlsec.derivation.KeyDerivationSupport;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class KeyDerivationSupportTest extends OpenSAMLInitBaseTestCase {
    
    @Test
    public void getJCAKeyAlgorithm() throws Exception {
        Assert.assertEquals(KeyDerivationSupport.getJCAKeyAlgorithm(
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM), JCAConstants.KEY_ALGO_AES);
        
        Assert.assertEquals(KeyDerivationSupport.getJCAKeyAlgorithm(
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES), JCAConstants.KEY_ALGO_DESEDE);
        
        Assert.assertEquals(KeyDerivationSupport.getJCAKeyAlgorithm(
                EncryptionConstants.ALGO_ID_KEYWRAP_AES128), JCAConstants.KEY_ALGO_AES);
        
        try {
            KeyDerivationSupport.getJCAKeyAlgorithm("INVALID");
            Assert.fail("Should have failed invalid URI");
        } catch (KeyDerivationException e) {
            // expected 
        }
    }

    @Test
    public void getEffectiveKeyLength() throws Exception {
        Assert.assertEquals(KeyDerivationSupport.getEffectiveKeyLength(
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM, null).intValue(), 128);
        Assert.assertEquals(KeyDerivationSupport.getEffectiveKeyLength(
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM, 128).intValue(), 128);
        
        Assert.assertEquals(KeyDerivationSupport.getEffectiveKeyLength(
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES, null).intValue(), 192);
        Assert.assertEquals(KeyDerivationSupport.getEffectiveKeyLength(
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES, 192).intValue(), 192);
        
        Assert.assertEquals(KeyDerivationSupport.getEffectiveKeyLength(
                EncryptionConstants.ALGO_ID_KEYWRAP_AES128, null).intValue(), 128);
        Assert.assertEquals(KeyDerivationSupport.getEffectiveKeyLength(
                EncryptionConstants.ALGO_ID_KEYWRAP_AES128, 128).intValue(), 128);
        
        // Non-length algorithm with non-null specified length should succeed as specified length
        Assert.assertEquals(KeyDerivationSupport.getEffectiveKeyLength("SomeAlgo", 128).intValue(), 128);
        
        try {
            KeyDerivationSupport.getEffectiveKeyLength(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM, 256);
            Assert.fail("Should have failed mismatched specified length");
        } catch (KeyDerivationException e) {
           // expected 
        }
        
        try {
            KeyDerivationSupport.getEffectiveKeyLength( "SomeAlgo", null);
            Assert.fail("Should have failed non-length URI and null specified length");
        } catch (KeyDerivationException e) {
           //expected 
        }
    }

}
