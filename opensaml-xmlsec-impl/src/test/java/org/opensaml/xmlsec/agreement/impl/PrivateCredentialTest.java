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

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.ECGenParameterSpec;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.logic.ConstraintViolationException;

/**
 *
 */
public class PrivateCredentialTest extends OpenSAMLInitBaseTestCase  {
    
    @Test
    public void basic() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPair kp = KeySupport.generateKeyPair("EC", new ECGenParameterSpec("secp256r1"), null);
        
        PrivateCredential privateCredential = new PrivateCredential(CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate()));
        Assert.assertNotNull(privateCredential.getCredential());
        
        
        try {
            privateCredential = new PrivateCredential(CredentialSupport.getSimpleCredential(kp.getPublic(), null));
            Assert.fail("PrivateCredential accepted Credential without PrivateKey");
        } catch (ConstraintViolationException e) {
            // expected, do nothing
        }
    }
    
}
