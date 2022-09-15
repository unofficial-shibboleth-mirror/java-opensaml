/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the * NOTICE file distributed with this work for additional information regarding
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

package org.opensaml.security.crypto.dh.tests;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHPublicKeySpec;

import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.dh.DHSupport;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class DHSupportTest extends BaseDHTest {
    
    @Test(dataProvider="dhKeySizes")
    public void generateCompatibleKeyPair(int keySize) throws Exception {
        final KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(JCAConstants.KEY_ALGO_DIFFIE_HELLMAN);
        kpGenerator.initialize(keySize);
        final KeyPair origKeyPair = kpGenerator.generateKeyPair();
        Assert.assertNotNull(origKeyPair);
        Assert.assertTrue(DHPublicKey.class.isInstance(origKeyPair.getPublic()));
        DHPublicKey origPublicKey = DHPublicKey.class.cast(origKeyPair.getPublic());
        
        final KeyPair generatedKeyPair = DHSupport.generateCompatibleKeyPair(origPublicKey, null);
        
        Assert.assertNotNull(generatedKeyPair);
        Assert.assertTrue(DHPublicKey.class.isInstance(generatedKeyPair.getPublic()));
        Assert.assertTrue(DHPrivateKey.class.isInstance(generatedKeyPair.getPrivate()));
    }
    
    @Test(dataProvider="dhKeySizes")
    public void performKeyAgreement(int keySize) throws Exception {
        final KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(JCAConstants.KEY_ALGO_DIFFIE_HELLMAN);
        kpGenerator.initialize(keySize);
        final KeyPair publicKeyPair = kpGenerator.generateKeyPair();
        DHPublicKey publicKey = DHPublicKey.class.cast(publicKeyPair.getPublic());
        
        final KeyPair privateKeyPair = DHSupport.generateCompatibleKeyPair(publicKey, null);
        
        final DHPrivateKey privateKey = DHPrivateKey.class.cast(privateKeyPair.getPrivate());
        
        byte[] secret = DHSupport.performKeyAgreement(publicKey, privateKey, null);
        Assert.assertNotNull(secret);
    }
    
    @Test(dataProvider="dhKeySizes")
    public void getPrimeQDomainParameter(int keySize) throws Exception {
        final KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(JCAConstants.KEY_ALGO_DIFFIE_HELLMAN);
        kpGenerator.initialize(keySize);
        final KeyPair publicKeyPair = kpGenerator.generateKeyPair();
        DHPublicKey publicKey1 = DHPublicKey.class.cast(publicKeyPair.getPublic());
        
        BigInteger q1 = DHSupport.getPrimeQDomainParameter(publicKey1);
        Assert.assertNotNull(q1);
        
        // Check that a DHPublicKey constructed via a KeyFactory from G and P alone still has Q component
        
        final DHPublicKeySpec dhPubSpec = new DHPublicKeySpec(publicKey1.getY(), publicKey1.getParams().getP(), publicKey1.getParams().getG());
        final KeyFactory keyFactory = KeyFactory.getInstance(JCAConstants.KEY_ALGO_DIFFIE_HELLMAN);
        final DHPublicKey publicKey2 = DHPublicKey.class.cast(keyFactory.generatePublic(dhPubSpec));
        Assert.assertEquals(publicKey2, publicKey2);
        
        /* Turns out this doesn't actually hold. BC throws IllegalArgumentException on new instance of DomainParameters
         * b/c ASN1Sequence has only 2 elements (P and G), not 3 as it should.
         * Seems like a bug in Java, b/c Q is required in the ASN.1 encoding.
         * See: https://tools.ietf.org/html/rfc3279, section 2.3.3.
        BigInteger q2 = DHSupport.getPrimeQDomainParameter(publicKey2);
        Assert.assertNotNull(q2);
        Assert.assertEquals(q2, q1);
        */
        
        // Check that a new DHPublicKey generated from the params spec of public key created with P and G alone,
        // still has a Q component.  This does work, despite the issue with the input key itself.
        // More reason to think the above is a bug, e.g. in KeyFactory.
        
        final KeyPairGenerator kpGenerator2 = KeyPairGenerator.getInstance(JCAConstants.KEY_ALGO_DIFFIE_HELLMAN);
        kpGenerator2.initialize(publicKey2.getParams());
        final KeyPair compatKeyPair = kpGenerator.generateKeyPair();
        final DHPublicKey publicKey3 = DHPublicKey.class.cast(compatKeyPair.getPublic());
        
        BigInteger q3 = DHSupport.getPrimeQDomainParameter(publicKey3);
        Assert.assertNotNull(q3);
        Assert.assertEquals(q3, q1);
    }
    
}
