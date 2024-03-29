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

import java.util.Map;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.assertion.tests.BaseAssertionValidationTest;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.OneTimeUse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
@SuppressWarnings({"null", "javadoc"})
public class AudienceRestrictionConditionValidatorTest extends BaseAssertionValidationTest {
    
    private String expectedAudienceURI = "https://sp.example.com";
    
    private AudienceRestrictionConditionValidator validator;
    
    private Condition condition;
    
    private Audience audience;
    
    @BeforeMethod(dependsOnMethods="setUpBasicAssertion")
    public void setUp() {
        validator = new AudienceRestrictionConditionValidator();
        audience = buildXMLObject(Audience.DEFAULT_ELEMENT_NAME);
        audience.setURI(expectedAudienceURI);
        condition = buildXMLObject(AudienceRestriction.DEFAULT_ELEMENT_NAME);
        ((AudienceRestriction)condition).getAudiences().add(audience);
        getConditions().getConditions().add(condition);
    }
    
    @Test
    public void testValid() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.COND_VALID_AUDIENCES, CollectionSupport.singleton(expectedAudienceURI));
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(condition, getAssertion(), validationContext), 
                ValidationResult.VALID);        
    }

    @Test
    public void testInvalidParamType() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        // Param should bet a Set<String>, not a String
        staticParams.put(SAML2AssertionValidationParameters.COND_VALID_AUDIENCES, expectedAudienceURI);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(condition, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);        
    }

    @Test
    public void testMissingParam() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(condition, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);        
    }

    @Test
    public void testConditionWithNoAudiences() throws AssertionValidationException {
        // This is syntactically invalid per the schema, and should validate as invalid.
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.COND_VALID_AUDIENCES, CollectionSupport.singleton(expectedAudienceURI));
        
        ((AudienceRestriction)condition).getAudiences().clear();
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(condition, getAssertion(), validationContext), 
                ValidationResult.INVALID);        
    }

    @Test
    public void testUnexpectedCondition() throws AssertionValidationException {
        condition = buildXMLObject(OneTimeUse.DEFAULT_ELEMENT_NAME);
        getConditions().getConditions().add(condition);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.COND_VALID_AUDIENCES, CollectionSupport.singleton(expectedAudienceURI));
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(condition, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);        
    }


}