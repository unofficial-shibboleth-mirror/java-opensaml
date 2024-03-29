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

import java.time.Duration;
import java.util.Map;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.StringSupport;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.ext.saml2delrestrict.DelegationRestrictionType;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.assertion.tests.BaseAssertionValidationTest;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.OneTimeUse;
import org.opensaml.storage.impl.MemoryStorageService;
import org.opensaml.storage.impl.StorageServiceReplayCache;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit test for {@link OneTimeUseConditionValidator}.
 */
@SuppressWarnings({"null", "javadoc"})
public class OneTimeUseConditionValidatorTest extends BaseAssertionValidationTest {
    
    private MemoryStorageService storageService;
    private StorageServiceReplayCache replayCache;
    
    private OneTimeUseConditionValidator validator;
    
    private Condition condition;
    
    @BeforeMethod(dependsOnMethods="setUpBasicAssertion")
    public void setUp() throws ComponentInitializationException {
        storageService = new MemoryStorageService();
        storageService.setId("mySS");
        storageService.initialize();
        
        replayCache = new StorageServiceReplayCache();
        replayCache.setId("myRC");
        replayCache.setStorage(storageService);
        replayCache.initialize();
        
        validator = new OneTimeUseConditionValidator(replayCache, null);
        condition = buildXMLObject(OneTimeUse.DEFAULT_ELEMENT_NAME);
        getConditions().getConditions().add(condition);
    }
    
    @AfterMethod
    public void tearDown() {
        replayCache.destroy();
        storageService.destroy();
    }
    
    @Test
    public void testNoReplay() throws AssertionValidationException, InterruptedException {
        Assertion assertion = getAssertion();
        Assert.assertNotNull(StringSupport.trimOrNull(assertion.getID()));
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(condition, assertion, validationContext), 
                ValidationResult.VALID);
        
        Thread.sleep(1000);
        
        assertion.setID(assertion.getID() + "moreID");
        
        validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(condition, assertion, validationContext), 
                ValidationResult.VALID);
    }
    
    @Test
    public void testReplay() throws AssertionValidationException, InterruptedException {
        Assertion assertion = getAssertion();
        Assert.assertNotNull(StringSupport.trimOrNull(assertion.getID()));
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(condition, assertion, validationContext), 
                ValidationResult.VALID);
        
        Thread.sleep(1000);
        
        validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(condition, assertion, validationContext), 
                ValidationResult.INVALID);
    }

    @Test
    public void testReplayWithGlobalExpiration() throws AssertionValidationException, InterruptedException {
        // Set validator expiration to 500ms.
        validator = new OneTimeUseConditionValidator(replayCache, Duration.ofMillis(500));
        
        Assertion assertion = getAssertion();
        Assert.assertNotNull(StringSupport.trimOrNull(assertion.getID()));
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(condition, assertion, validationContext), 
                ValidationResult.VALID);
        
        // Sleep past the expiration
        Thread.sleep(1000);
        
        validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(condition, assertion, validationContext), 
                ValidationResult.VALID);
    }
    
    @Test
    public void testReplayWithExpirationParam() throws AssertionValidationException, InterruptedException {
        Assertion assertion = getAssertion();
        Assert.assertNotNull(StringSupport.trimOrNull(assertion.getID()));
        
        Map<String, Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.COND_ONE_TIME_USE_EXPIRES, Duration.ofMillis(500));
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(condition, assertion, validationContext), 
                ValidationResult.VALID);
        
        // Sleep past the expiration
        Thread.sleep(1000);
        
        validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(condition, assertion, validationContext), 
                ValidationResult.VALID);
    }
    
    @Test
    public void testInvalidExpirationParam() throws AssertionValidationException, InterruptedException {
        Assertion assertion = getAssertion();
        Assert.assertNotNull(StringSupport.trimOrNull(assertion.getID()));
        
        Map<String, Object> staticParams = buildBasicStaticParameters();
        // This value is not a Duration or Long and so will be effectively ignored
        staticParams.put(SAML2AssertionValidationParameters.COND_ONE_TIME_USE_EXPIRES, "500");
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(condition, assertion, validationContext), 
                ValidationResult.VALID);
        
        Thread.sleep(1000);
        
        validationContext = new ValidationContext(buildBasicStaticParameters());
        
        // Invalid b/c the param was ignored and so global expiration in effect
        Assert.assertEquals(validator.validate(condition, assertion, validationContext), 
                ValidationResult.INVALID);
    }
    
    @Test 
    public void testUnexpectedCondition() throws AssertionValidationException {
        condition = (Condition) getBuilder(DelegationRestrictionType.TYPE_NAME).buildObject(Condition.DEFAULT_ELEMENT_NAME, DelegationRestrictionType.TYPE_NAME);
        
        Assertion assertion = getAssertion();
        Assert.assertNotNull(StringSupport.trimOrNull(assertion.getID()));
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(condition, assertion, validationContext), 
                ValidationResult.INDETERMINATE);
    }

}
