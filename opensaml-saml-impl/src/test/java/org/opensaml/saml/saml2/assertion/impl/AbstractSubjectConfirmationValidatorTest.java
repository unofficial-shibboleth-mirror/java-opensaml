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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import javax.annotation.Nonnull;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.assertion.tests.BaseAssertionValidationTest;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.logic.Constraint;

@SuppressWarnings({"null", "javadoc"})
public class AbstractSubjectConfirmationValidatorTest extends BaseAssertionValidationTest {
    
    private MockSubjectConfirmationValidator validator;
    
    private SubjectConfirmation subjectConfirmation;
    
    @BeforeMethod(dependsOnMethods="setUpBasicAssertion")
    public void setUp() {
        validator = new MockSubjectConfirmationValidator();
        subjectConfirmation = getSubject().getSubjectConfirmations().get(0);
        subjectConfirmation.setMethod(validator.getServicedMethod());
    }
    
    @Test
    public void testValidConfirmationData() throws AssertionValidationException {
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);
    }
    
    @Test
    public void testNoConfirmationData() throws AssertionValidationException {
        subjectConfirmation.setSubjectConfirmationData(null);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);
    }
    
    @Test
    public void testInvalidAddress() throws AssertionValidationException {
        getSubjectConfirmationData().setAddress("1.2.3.4");
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
    }
    
    @Test
    public void testInvalidAddressWithAddressCheckDisabled() throws AssertionValidationException {
        getSubjectConfirmationData().setAddress("1.2.3.4");

        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_CHECK_ADDRESS, Boolean.FALSE);
        ValidationContext validationContext = new ValidationContext(staticParams);

        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext),
                ValidationResult.VALID);
    }

    @Test
    public void testInvalidAddressParamType() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        // It should be a Set<String>, not a String
        staticParams.put(SAML2AssertionValidationParameters.SC_VALID_ADDRESSES, SUBJECT_CONFIRMATION_ADDRESS);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
    }
    
    @Test
    public void testMissingAddressParam() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.remove(SAML2AssertionValidationParameters.SC_VALID_ADDRESSES);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
    }
    
    @Test
    public void testNoAddress() throws AssertionValidationException {
        getSubjectConfirmationData().setAddress(null);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);
    }
    
    @Test
    public void testNoAddressAndRequired() throws AssertionValidationException {
        getSubjectConfirmationData().setAddress(null);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_ADDRESS_REQUIRED, Boolean.TRUE);
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
    }
    
    @Test
    public void testNoConfirmationDataAndAddressRequired() throws AssertionValidationException {
        subjectConfirmation.setSubjectConfirmationData(null);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_ADDRESS_REQUIRED, Boolean.TRUE);
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
    }
    
    @Test
    public void testInvalidRecipient() throws AssertionValidationException {
        getSubjectConfirmationData().setRecipient("https://bogussp.example.com");
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
    }
    
    @Test
    public void testInvalidRecipientParamType() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        // It should be a Set<String>, not a String
        staticParams.put(SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS, SUBJECT_CONFIRMATION_RECIPIENT);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
    }
    
    @Test
    public void testMissingRecipientParam() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.remove(SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
    }
    
    @Test
    public void testNoRecipient() throws AssertionValidationException {
        getSubjectConfirmationData().setRecipient(null);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);
    }
    
    @Test
    public void testNoRecipientAndRequired() throws AssertionValidationException {
        getSubjectConfirmationData().setRecipient(null);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_RECIPIENT_REQUIRED, Boolean.TRUE);
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
    }
    
    @Test
    public void testNoConfirmationDataAndRecipientRequired() throws AssertionValidationException {
        subjectConfirmation.setSubjectConfirmationData(null);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_RECIPIENT_REQUIRED, Boolean.TRUE);
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
    }
    
    @Test
    public void testInvalidNotBefore() throws AssertionValidationException {
        // Adjust them both just so they make sense
        getSubjectConfirmationData().setNotBefore(Instant.now().plus(30, ChronoUnit.MINUTES));
        getSubjectConfirmationData().setNotOnOrAfter(Instant.now().plus(60, ChronoUnit.MINUTES));
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
    }
    
    @Test
    public void testNoNotBefore() throws AssertionValidationException {
        getSubjectConfirmationData().setNotBefore(null);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);
    }
    
    @Test
    public void testNoNotBeforeAndRequired() throws AssertionValidationException {
        getSubjectConfirmationData().setNotBefore(null);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_NOT_BEFORE_REQUIRED, Boolean.TRUE);
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
    }
    
    @Test
    public void testNoConfirmationDataAndNotBeforeRequired() throws AssertionValidationException {
        subjectConfirmation.setSubjectConfirmationData(null);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_NOT_BEFORE_REQUIRED, Boolean.TRUE);
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
    }
    
    @Test
    public void testInvalidNotOnOrAfter() throws AssertionValidationException {
        // Adjust them both just so they make sense
        getSubjectConfirmationData().setNotBefore(Instant.now().minus(60, ChronoUnit.MINUTES));
        getSubjectConfirmationData().setNotOnOrAfter(Instant.now().minus(30, ChronoUnit.MINUTES));
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
    }
    
    @Test
    public void testNoNotOnOrAfter() throws AssertionValidationException {
        getSubjectConfirmationData().setNotOnOrAfter(null);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);
    }
    
    @Test
    public void testNoNotOnOrAfterAndRequired() throws AssertionValidationException {
        getSubjectConfirmationData().setNotOnOrAfter(null);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_NOT_ON_OR_AFTER_REQUIRED, Boolean.TRUE);
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
    }
    
    @Test
    public void testNoConfirmationDataAndNotOnOrAfterRequired() throws AssertionValidationException {
        subjectConfirmation.setSubjectConfirmationData(null);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_NOT_ON_OR_AFTER_REQUIRED, Boolean.TRUE);
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
    }
    
    @Test
    public void testInvalidInResponseTo() throws AssertionValidationException {
        getSubjectConfirmationData().setInResponseTo("invalid");
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
    }

    @Test
    public void testInvalidInResponseToButIgnored() throws AssertionValidationException {
        getSubjectConfirmationData().setInResponseTo("invalid");
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_IN_RESPONSE_TO_IGNORED, Boolean.valueOf(true));
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);
    }

    @Test
    public void testInvalidInResponseToParamType() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        // It should be a Set<String>, not a String
        staticParams.put(SAML2AssertionValidationParameters.SC_VALID_IN_RESPONSE_TO, Integer.valueOf(42));
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
    }
    
    @Test
    public void testMissingInResponseToParam() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.remove(SAML2AssertionValidationParameters.SC_VALID_IN_RESPONSE_TO);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
    }
    
    @Test
    public void testNoInResponseTo() throws AssertionValidationException {
        getSubjectConfirmationData().setInResponseTo(null);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);
    }
    
    @Test
    public void testNoInResponseToAndRequired() throws AssertionValidationException {
        getSubjectConfirmationData().setInResponseTo(null);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_IN_RESPONSE_TO_REQUIRED, Boolean.TRUE);
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
    }
    
    @Test
    public void testNoConfirmationDataAndInResponseToRequired() throws AssertionValidationException {
        subjectConfirmation.setSubjectConfirmationData(null);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_IN_RESPONSE_TO_REQUIRED, Boolean.TRUE);
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
    }
    
    @Nonnull private SubjectConfirmationData getSubjectConfirmationData() {
        return Constraint.isNotNull(subjectConfirmation.getSubjectConfirmationData(), "Conf data was null");
    }

    // Mock concrete class for testing
    
    public static class MockSubjectConfirmationValidator extends AbstractSubjectConfirmationValidator {

        /** {@inheritDoc} */
        public String getServicedMethod() {
            return "urn:test:foo";
        }

        /** {@inheritDoc} */
        @Nonnull protected ValidationResult doValidate(@Nonnull SubjectConfirmation confirmation,
                @Nonnull Assertion assertion, @Nonnull ValidationContext context) throws AssertionValidationException {
            return ValidationResult.VALID;
        }
        
    }

}