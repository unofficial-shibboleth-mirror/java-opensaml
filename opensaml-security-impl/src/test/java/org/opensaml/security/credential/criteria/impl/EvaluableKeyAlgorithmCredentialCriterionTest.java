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
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.criteria.KeyAlgorithmCriterion;
import org.opensaml.security.crypto.KeySupport;


@SuppressWarnings("javadoc")
public class EvaluableKeyAlgorithmCredentialCriterionTest {
    
    private BasicCredential credential;
    private String keyAlgo;
    private KeyAlgorithmCriterion criteria;
    
    public EvaluableKeyAlgorithmCredentialCriterionTest() {
        keyAlgo = "RSA";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        credential = new BasicCredential(KeySupport.generateKeyPair(keyAlgo, 1024, null).getPublic());
        criteria = new KeyAlgorithmCriterion(keyAlgo);
    }
    
    @Test
    public void testSatisfy() {
        final EvaluableKeyAlgorithmCredentialCriterion evalCrit = new EvaluableKeyAlgorithmCredentialCriterion(criteria);
        Assert.assertTrue(evalCrit.test(credential), "Credential should have matched the evaluable criteria");
    }

    @Test
    public void testNotSatisfy() {
        criteria.setKeyAlgorithm("SomeOtherKeyAlgo");
        final EvaluableKeyAlgorithmCredentialCriterion evalCrit = new EvaluableKeyAlgorithmCredentialCriterion(criteria);
        Assert.assertFalse(evalCrit.test(credential), "Credential should NOT have matched the evaluable criteria");
    }
    
    @Test
    public void testRegistry() throws Exception {
        final EvaluableCredentialCriterion evalCrit = EvaluableCredentialCriteriaRegistry.getEvaluator(criteria);
        assert evalCrit != null;
        Assert.assertTrue(evalCrit.test(credential), "Credential should have matched the evaluable criteria");
    }
}
