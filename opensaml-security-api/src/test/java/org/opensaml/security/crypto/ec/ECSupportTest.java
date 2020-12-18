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

package org.opensaml.security.crypto.ec;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;

import org.opensaml.security.crypto.JCAConstants;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 */
public class ECSupportTest {
    
    @DataProvider
    public Object[][] namedCurves() {
        return new Object[][] {
            new Object[] {"secp256r1"},
            new Object[] {"secp384r1"},
            new Object[] {"secp521r1"},
        };
    }
    
    @Test(dataProvider="namedCurves")
    public void generateCompatibleKeyPair(String namedCurve) throws Exception {
        final KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(JCAConstants.KEY_ALGO_EC);
        kpGenerator.initialize(new ECGenParameterSpec(namedCurve));
        final KeyPair origKeyPair = kpGenerator.generateKeyPair();
        Assert.assertNotNull(origKeyPair);
        Assert.assertTrue(ECPublicKey.class.isInstance(origKeyPair.getPublic()));
        ECPublicKey origPublicKey = ECPublicKey.class.cast(origKeyPair.getPublic());
        
        final KeyPair generatedKeyPair = ECSupport.generateCompatibleKeyPair(origPublicKey, null);
        
        Assert.assertNotNull(generatedKeyPair);
        Assert.assertTrue(ECPublicKey.class.isInstance(generatedKeyPair.getPublic()));
        Assert.assertTrue(ECPrivateKey.class.isInstance(generatedKeyPair.getPrivate()));
    }
    
    @Test(dataProvider="namedCurves")
    public void performKeyAgreement(String namedCurve) throws Exception {
        final KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(JCAConstants.KEY_ALGO_EC);
        kpGenerator.initialize(new ECGenParameterSpec(namedCurve));
        final KeyPair recipientKeyPair = kpGenerator.generateKeyPair();
        ECPublicKey recipientPublicKey = ECPublicKey.class.cast(recipientKeyPair.getPublic());
        
        final KeyPair originatorKeyPair = ECSupport.generateCompatibleKeyPair(recipientPublicKey, null);
        final ECPrivateKey originatorPrivateKey = ECPrivateKey.class.cast(originatorKeyPair.getPrivate());
        
        byte[] secret = ECSupport.performKeyAgreement(recipientPublicKey, originatorPrivateKey, null);
        Assert.assertNotNull(secret);
    }

}
