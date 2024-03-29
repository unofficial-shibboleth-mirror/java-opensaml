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

package org.opensaml.security.credential.criteria.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;

import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.criteria.PublicKeyCriterion;
import org.opensaml.security.crypto.KeySupport;

@SuppressWarnings("javadoc")
public class EvaluablePublicKeyCredentialCriterionTest {
    
    private BasicCredential credential;
    private String keyAlgo;
    PublicKey pubKey;
    private PublicKeyCriterion criteria;
    
    public EvaluablePublicKeyCredentialCriterionTest() {
        keyAlgo = "RSA";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        pubKey = KeySupport.generateKeyPair(keyAlgo, 1024, null).getPublic();
        credential = new BasicCredential(pubKey);
        
        criteria = new PublicKeyCriterion(pubKey);
    }
    
    @Test
    public void testSatisfy() {
        final EvaluablePublicKeyCredentialCriterion evalCrit = new EvaluablePublicKeyCredentialCriterion(criteria);
        Assert.assertTrue(evalCrit.test(credential), "Credential should have matched the evaluable criteria");
    }

    @Test
    public void testNotSatisfyDifferentKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        criteria.setPublicKey(KeySupport.generateKeyPair(keyAlgo, 1024, null).getPublic());
        final EvaluablePublicKeyCredentialCriterion evalCrit = new EvaluablePublicKeyCredentialCriterion(criteria);
        Assert.assertFalse(evalCrit.test(credential), "Credential should NOT have matched the evaluable criteria");
    }
    
    @Test
    public void testCanNotEvaluate() {
        //Only unevaluable case is null credential
        final EvaluablePublicKeyCredentialCriterion evalCrit = new EvaluablePublicKeyCredentialCriterion(criteria);
        Assert.assertEquals(evalCrit.test(null), evalCrit.isUnevaluableSatisfies(), "Credential should have been unevaluable against the criteria");
    }
    
    @Test
    public void testRegistry() throws Exception {
        final EvaluableCredentialCriterion evalCrit = EvaluableCredentialCriteriaRegistry.getEvaluator(criteria);
        assert evalCrit != null;
        Assert.assertTrue(evalCrit.test(credential), "Credential should have matched the evaluable criteria");
    }
}
