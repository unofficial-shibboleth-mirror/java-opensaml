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

package org.opensaml.saml.saml2.assertion.impl;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.ext.saml2delrestrict.DelegationRestrictionType;
import org.opensaml.saml.saml2.assertion.tests.BaseAssertionValidationTest;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.OneTimeUse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings({"null", "javadoc"})
public class DelegationRestrictionConditionValidatorTest extends BaseAssertionValidationTest {
    
    private DelegationRestrictionConditionValidator validator;
    
    private Condition condition;
    
    @BeforeMethod(dependsOnMethods="setUpBasicAssertion")
    public void setUp() {
        validator = new DelegationRestrictionConditionValidator();
        condition = (Condition) getBuilder(DelegationRestrictionType.TYPE_NAME).buildObject(Condition.DEFAULT_ELEMENT_NAME, DelegationRestrictionType.TYPE_NAME);
        getConditions().getConditions().add(condition);
    }
    
    @Test
    public void testExpected() throws AssertionValidationException {
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(condition, getAssertion(), validationContext), 
                ValidationResult.VALID);
    }

    @Test
    public void testUnexpected() throws AssertionValidationException {
        condition = buildXMLObject(OneTimeUse.DEFAULT_ELEMENT_NAME);
        getConditions().getConditions().add(condition);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(condition, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
    }

}