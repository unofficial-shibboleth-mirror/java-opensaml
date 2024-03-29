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

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.crypto.KeySupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class EvaluableEntityIDCredentialCriterionTest {
    
    private BasicCredential credential;
    private String entityID;
    private EntityIdCriterion criteria;
    
    public EvaluableEntityIDCredentialCriterionTest() {
        entityID = "someEntityID";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        credential = new BasicCredential(KeySupport.generateKey("AES", 128, null));
        credential.setEntityId(entityID);
        
        criteria = new EntityIdCriterion(entityID);
    }
    
    @Test
    public void testSatisfy() {
        final EvaluableEntityIDCredentialCriterion evalCrit = new EvaluableEntityIDCredentialCriterion(criteria);
        Assert.assertTrue(evalCrit.test(credential), "Credential should have matched the evaluable criteria");
    }

    @Test
    public void testNotSatisfy() {
        criteria = new EntityIdCriterion("OTHER");
        final EvaluableEntityIDCredentialCriterion evalCrit = new EvaluableEntityIDCredentialCriterion(criteria);
        Assert.assertFalse(evalCrit.test(credential), "Credential should NOT have matched the evaluable criteria");
    }
    
    @Test
    public void testCanNotEvaluate() {
        credential.setEntityId(null);
        final EvaluableEntityIDCredentialCriterion evalCrit = new EvaluableEntityIDCredentialCriterion(criteria);
        Assert.assertEquals(evalCrit.test(credential), evalCrit.isUnevaluableSatisfies(), "Credential should have been unevaluable against the criteria");
    }
    
    @Test
    public void testRegistry() throws Exception {
        final EvaluableCredentialCriterion evalCrit = EvaluableCredentialCriteriaRegistry.getEvaluator(criteria);
        assert evalCrit != null;
        Assert.assertTrue(evalCrit.test(credential), "Credential should have matched the evaluable criteria");
    }
}
