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

package org.opensaml.saml.saml2.assertion.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import javax.annotation.Nonnull;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.assertion.tests.BaseAssertionValidationTest;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.SubjectLocality;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.logic.Constraint;

@SuppressWarnings("javadoc")
public class AuthnStatementValidatorTest extends BaseAssertionValidationTest {
    
    private AuthnStatementValidator validator;
    
    private AuthnStatement authnStatement;
    
    @BeforeMethod(dependsOnMethods="setUpBasicAssertion")
    public void setUp() {
        validator = new AuthnStatementValidator();
        authnStatement = buildBasicAuthnStatement();
        getAssertion().getAuthnStatements().add(authnStatement);
    }
    
    @Test
    public void testValid() throws AssertionValidationException {
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(authnStatement, getAssertion(), validationContext), 
                ValidationResult.VALID);  
    }

    @Test
    void testMaxTimeSinceAuthnExceeded() throws AssertionValidationException {
        authnStatement.setAuthnInstant(Instant.now().minus(Duration.ofHours(1)));
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(authnStatement, getAssertion(), validationContext), 
                ValidationResult.INVALID);  
    }

    @Test
    void testNoAuthnInstant() throws AssertionValidationException {
        authnStatement.setAuthnInstant(null);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(authnStatement, getAssertion(), validationContext), 
                ValidationResult.INVALID);  
    }

    @Test
    void testNoMaxTimeSinceAuthnParam() throws AssertionValidationException {
        authnStatement.setAuthnInstant(Instant.now().minus(Duration.ofHours(1)));
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.remove(SAML2AssertionValidationParameters.STMT_AUTHN_MAX_TIME);
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(authnStatement, getAssertion(), validationContext), 
                ValidationResult.VALID);  
    }

    @Test
    public void testInvalidAddress() throws AssertionValidationException {
        getSubjectLocality().setAddress("1.2.3.4");
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(authnStatement, getAssertion(), validationContext), 
                ValidationResult.INVALID);  
    }

    @Test
    public void testInvalidAddressWithAddressCheckDisabled() throws AssertionValidationException {
        getSubjectLocality().setAddress("1.2.3.4");
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.STMT_AUTHN_CHECK_ADDRESS, Boolean.FALSE);
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(authnStatement, getAssertion(), validationContext), 
                ValidationResult.VALID);  
    }
    
    @Test
    public void testInvalidAddressParamType() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        // It should be a Set<String>, not a String
        staticParams.put(SAML2AssertionValidationParameters.STMT_AUTHN_VALID_ADDRESSES, AUTHN_STATEMENT_ADDRESS);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(authnStatement, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);  
    }
    
    @Test
    public void testMissingAddressParam() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.remove(SAML2AssertionValidationParameters.STMT_AUTHN_VALID_ADDRESSES);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(authnStatement, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);  
    }
    
    @Test
    public void testNoAddress() throws AssertionValidationException {
        getSubjectLocality().setAddress(null);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(authnStatement, getAssertion(), validationContext), 
                ValidationResult.VALID);  
    }

    @Test
    public void testNoSubjectLocality() throws AssertionValidationException {
        authnStatement.setSubjectLocality(null);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(authnStatement, getAssertion(), validationContext), 
                ValidationResult.VALID);  
    }
    
    @Test
    public void testAuthnContextEval() throws AssertionValidationException {
        // Just testing that if a subclass overrides this method, it gets processed.
        validator = new AuthnStatementValidator() {
            /** {@inheritDoc} */
            @Nonnull protected ValidationResult validateAuthnContext(@Nonnull final AuthnStatement statement,
                    @Nonnull final Assertion assertion, @Nonnull final ValidationContext context) throws AssertionValidationException {
                final AuthnContext ac = statement.getAuthnContext();
                if (ac != null) {
                    final AuthnContextClassRef acRef = ac.getAuthnContextClassRef();
                    if (acRef != null) {
                        return AuthnContext.SMARTCARD_AUTHN_CTX.equals(acRef.getURI())
                                ? ValidationResult.VALID : ValidationResult.INVALID;
                    }
                }
                return ValidationResult.INVALID;
            }
        };
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(authnStatement, getAssertion(), validationContext), 
                ValidationResult.INVALID);  
    }
    
    @Test
    public void testValidationThrows() throws AssertionValidationException {
        validator = new AuthnStatementValidator() {
            /** {@inheritDoc} */
            @Nonnull protected ValidationResult validateAuthnInstant(@Nonnull final AuthnStatement statement,
                    @Nonnull final Assertion assertion, @Nonnull final ValidationContext context)
                            throws AssertionValidationException {
                throw new RuntimeException();
            }
        };
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(authnStatement, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);  
    }
    
    @Test
    public void testWrongStatementType() throws AssertionValidationException {
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(buildXMLObject(AttributeStatement.DEFAULT_ELEMENT_NAME), getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);  
    }
 
    @Nonnull private SubjectLocality getSubjectLocality() {
        return Constraint.isNotNull(authnStatement.getSubjectLocality(), "SubjectLocality was null");
    }

}