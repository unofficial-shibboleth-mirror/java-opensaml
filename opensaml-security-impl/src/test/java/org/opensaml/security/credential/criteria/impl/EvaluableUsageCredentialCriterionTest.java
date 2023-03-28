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

package org.opensaml.security.credential.criteria.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.crypto.KeySupport;

@SuppressWarnings("javadoc")
public class EvaluableUsageCredentialCriterionTest {
    
    private BasicCredential credential;
    private UsageType usage;
    private UsageCriterion criteria;
    
    public EvaluableUsageCredentialCriterionTest() {
        usage = UsageType.SIGNING;
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        credential = new BasicCredential(KeySupport.generateKey("AES", 128, null));
        credential.setUsageType(usage);
        
        criteria = new UsageCriterion(usage);
    }
    
    @Test
    public void testSatisfyExactMatch() {
        final EvaluableUsageCredentialCriterion evalCrit = new EvaluableUsageCredentialCriterion(criteria);
        Assert.assertTrue(evalCrit.test(credential), "Credential should have matched the evaluable criteria");
    }
    
    @Test
    public void testSatisfyWithUnspecifiedCriteria() {
        criteria.setUsage(UsageType.UNSPECIFIED);
        final EvaluableUsageCredentialCriterion evalCrit = new EvaluableUsageCredentialCriterion(criteria);
        Assert.assertTrue(evalCrit.test(credential), "Credential should have matched the evaluable criteria");
    }
    
    @Test
    public void testSatisfyWithUnspecifiedCredential() {
        credential.setUsageType(UsageType.UNSPECIFIED);
        final EvaluableUsageCredentialCriterion evalCrit = new EvaluableUsageCredentialCriterion(criteria);
        Assert.assertTrue(evalCrit.test(credential), "Credential should have matched the evaluable criteria");
    }

    @Test
    public void testNotSatisfy() {
        criteria.setUsage(UsageType.ENCRYPTION);
        final EvaluableUsageCredentialCriterion evalCrit = new EvaluableUsageCredentialCriterion(criteria);
        Assert.assertFalse(evalCrit.test(credential), "Credential should NOT have matched the evaluable criteria");
    }
    
    @Test
    public void testRegistry() throws Exception {
        final EvaluableCredentialCriterion evalCrit = EvaluableCredentialCriteriaRegistry.getEvaluator(criteria);
        assert evalCrit != null;
        Assert.assertTrue(evalCrit.test(credential), "Credential should have matched the evaluable criteria");
    }
}
