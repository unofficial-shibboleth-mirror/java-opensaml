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

package org.opensaml.saml.saml2.assertion.tests;

import java.net.URISyntaxException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.ConditionValidator;
import org.opensaml.saml.saml2.assertion.SAML20AssertionValidator;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.assertion.StatementValidator;
import org.opensaml.saml.saml2.assertion.SubjectConfirmationValidator;
import org.opensaml.saml.saml2.assertion.impl.BearerSubjectConfirmationValidator;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.OneTimeUse;
import org.opensaml.saml.saml2.core.Statement;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.impl.CollectionCredentialResolver;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignaturePrevalidator;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.resolver.CriteriaSet;


/** Unit test for {@link SAML20AssertionValidator}. */
@SuppressWarnings("javadoc")
public class SAML20AssertionValidatorTest extends BaseAssertionValidationTest {
    
    private SAML20AssertionValidator validator;
    
    private List<ConditionValidator> conditionValidators;
    private List<SubjectConfirmationValidator> subjectConfirmationValidators;
    private List<StatementValidator> statementValidators;
    
    private Set<Credential> trustedCredentials;
    private CollectionCredentialResolver credentialResolver;
    private SignatureTrustEngine signatureTrustEngine;
    private SignaturePrevalidator signaturePrevalidator;
    
    private X509Certificate cert1, cert2;
    
    private PublicKey publicKey1, publicKey2;
    private PrivateKey privateKey1, privateKey2;
    private Credential cred1, cred2;
    
    @BeforeClass
    protected void readCertsAndKeys() throws CertificateException, URISyntaxException, KeyException {
        cert1 = getCertificate("subject1.crt");
        publicKey1 = cert1.getPublicKey();
        privateKey1 = getPrivateKey("subject1.key");
        cred1 = getSigningCredential(publicKey1, privateKey1);
        
        cert2 = getCertificate("subject2.crt");
        publicKey2 = cert2.getPublicKey();
        privateKey2 = getPrivateKey("subject2.key");
        cred2 = getSigningCredential(publicKey2, privateKey2);
    }
    
    @BeforeMethod(dependsOnMethods="setUpBasicAssertion")
    protected void setUp() throws NoSuchAlgorithmException, NoSuchProviderException {
        conditionValidators = new ArrayList<>();
        subjectConfirmationValidators = new ArrayList<>();
        statementValidators = new ArrayList<>();
        
        // In general must always satisfy at least 1 SubjectConfirmation, so default in a bearer confirmation validator.
        subjectConfirmationValidators.add(new BearerSubjectConfirmationValidator());
        
        trustedCredentials = new HashSet<>();
        credentialResolver = new CollectionCredentialResolver(trustedCredentials);
        signatureTrustEngine = new ExplicitKeySignatureTrustEngine(credentialResolver, DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver());
        signaturePrevalidator = new SAMLSignatureProfileValidator();
    }
    
    @Test
    public void testNoSubjectConfirmationValidators() throws AssertionValidationException {
        subjectConfirmationValidators.clear();
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        // This will fail b/c an assertion token with a Subject and SubjectConfirmation must satisfy at least one SubjectConfirmation.
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION));
    }
    
    @Test
    public void testNoSubject() throws AssertionValidationException {
        getAssertion().setSubject(null);
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        // No Subject, so is valid.
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.VALID);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION));
    }
    
    @Test
    public void testNoSubjectConfirmations() throws AssertionValidationException {
        getAssertion().getSubject().getSubjectConfirmations().clear();
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        // No SubjectConfirmations, so is valid.
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.VALID);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION));
    }
    
    @Test
    public void testNoSignatureAndNotRequired() throws AssertionValidationException {
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.VALID);
        
        Assert.assertSame(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION),
                assertion.getSubject().getSubjectConfirmations().get(0));
    }
    
    @Test
    public void testNoSignatureAndRequired() throws AssertionValidationException {
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, true);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
    }
    
    @Test
    public void testWithTrustedSignature() throws AssertionValidationException, SecurityException, MarshallingException, SignatureException {
        trustedCredentials.add(cred1);
        signAssertion(getAssertion(), cred1);
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, true);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.VALID);
        
        Assert.assertSame(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION),
                assertion.getSubject().getSubjectConfirmations().get(0));
    }
    
    @Test
    public void testWithTrustedSignatureAndContextTrustEngine() throws AssertionValidationException, SecurityException, MarshallingException, SignatureException {
        trustedCredentials.add(cred1);
        signAssertion(getAssertion(), cred1);
        
        validator = new SAML20AssertionValidator(conditionValidators, subjectConfirmationValidators, statementValidators, null, null, signaturePrevalidator);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, true);
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_TRUST_ENGINE, signatureTrustEngine);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.VALID);
        
        Assert.assertSame(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION),
                assertion.getSubject().getSubjectConfirmations().get(0));
    }
    
    @Test
    public void testWithTrustedSignatureAndContextPrevalidator() throws AssertionValidationException, SecurityException, MarshallingException, SignatureException {
        trustedCredentials.add(cred1);
        signAssertion(getAssertion(), cred1);
        
        validator = new SAML20AssertionValidator(conditionValidators, subjectConfirmationValidators, statementValidators, null, signatureTrustEngine, null);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, true);
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_PREVALIDATOR, signaturePrevalidator);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.VALID);
        
        Assert.assertSame(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION),
                assertion.getSubject().getSubjectConfirmations().get(0));
    }
    
    @Test
    public void testWithSignatureAndUntrustedCredential() throws AssertionValidationException, SecurityException, MarshallingException, SignatureException {
        trustedCredentials.add(cred2);
        signAssertion(getAssertion(), cred1);
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, true);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
    }
    
    @Test
    public void testWithSignatureNoSignatureTrustEngine() throws AssertionValidationException, SecurityException, MarshallingException, SignatureException {
        signAssertion(getAssertion(), cred1);
        
        validator = new SAML20AssertionValidator(conditionValidators, subjectConfirmationValidators, statementValidators, null, null, signaturePrevalidator);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, true);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INDETERMINATE);
    }
    
    @Test
    public void testWithSignatureFailsSignaturePrevalidation() throws AssertionValidationException, SecurityException, MarshallingException, SignatureException {
        trustedCredentials.add(cred1);
        signAssertion(getAssertion(), cred1);
        
        SignaturePrevalidator failingValidator = new SignaturePrevalidator() {
            public void validate(@Nonnull Signature signature) throws SignatureException {
                throw new SignatureException();
            }
        };
        
        validator = new SAML20AssertionValidator(conditionValidators, subjectConfirmationValidators, statementValidators, null, signatureTrustEngine, failingValidator);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, true);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
    }
    
    @Test
    public void testWithSignatureTrustEngineFailure() throws AssertionValidationException, SecurityException, MarshallingException, SignatureException {
        trustedCredentials.add(cred1);
        signAssertion(getAssertion(), cred1);
        
        SignatureTrustEngine failingEngine = new SignatureTrustEngine() {
            public boolean validate(Signature token, CriteriaSet trustBasisCriteria) throws SecurityException {
                throw new SecurityException();
            }
            @Nullable public KeyInfoCredentialResolver getKeyInfoResolver() {
                return null;
            }
            public boolean validate(@Nonnull byte[] signature, @Nonnull byte[] content, @Nonnull String algorithmURI,
                    @Nullable CriteriaSet trustBasisCriteria, @Nullable Credential candidateCredential)
                    throws SecurityException {
                throw new SecurityException();
            }
        };
        
        validator = new SAML20AssertionValidator(conditionValidators, subjectConfirmationValidators, statementValidators, null, failingEngine, signaturePrevalidator);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, true);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INDETERMINATE);
    }
    
    @Test
    public void testNoConditions() throws AssertionValidationException {
        getAssertion().setConditions(null);
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.VALID);
        
        Assert.assertSame(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION), 
                assertion.getSubject().getSubjectConfirmations().get(0));
    }
    
    @Test
    public void testNoConditionsWithRequired() throws AssertionValidationException {
        getAssertion().setConditions(null);
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        staticParams.put(SAML2AssertionValidationParameters.COND_REQUIRED_CONDITIONS,
                Collections.singleton(MockCondition.ELEMENT_NAME));
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
    }
    
    @Test
    public void testConditionsWithRequiredPresent() throws AssertionValidationException {
        getAssertion().getConditions().getConditions().add(new MockCondition());
        getAssertion().getConditions().getConditions().add(new MockCondition2());
        
        conditionValidators.add(new MockConditionValidator());
        conditionValidators.add(new MockCondition2Validator());
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        staticParams.put(SAML2AssertionValidationParameters.COND_REQUIRED_CONDITIONS,
                Collections.singleton(MockCondition.ELEMENT_NAME));
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.VALID);
    }
    
    @Test
    public void testConditionsWithRequiredMissing() throws AssertionValidationException {
        getAssertion().getConditions().getConditions().add(new MockCondition2());
        
        conditionValidators.add(new MockConditionValidator());
        conditionValidators.add(new MockCondition2Validator());
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        staticParams.put(SAML2AssertionValidationParameters.COND_REQUIRED_CONDITIONS,
                Collections.singleton(MockCondition.ELEMENT_NAME));
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
    }
    
    @Test
    public void testInvalidConditionsNotBefore() throws AssertionValidationException {
        getAssertion().getConditions().setNotBefore(Instant.now().plus(30, ChronoUnit.MINUTES));
        getAssertion().getConditions().setNotOnOrAfter(Instant.now().plus(60, ChronoUnit.MINUTES));
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
    }
    
    @Test
    public void testInvalidConditionsNotOnOrAfter() throws AssertionValidationException {
        getAssertion().getConditions().setNotBefore(Instant.now().minus(60, ChronoUnit.MINUTES));
        getAssertion().getConditions().setNotOnOrAfter(Instant.now().minus(30, ChronoUnit.MINUTES));
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
    }
    
    @Test
    public void testFailConditionValidator() throws AssertionValidationException {
        ConditionValidator failingValidator = new ConditionValidator() {
            @Nonnull public ValidationResult validate(@Nonnull Condition condition, @Nonnull Assertion assertion,
                    @Nonnull ValidationContext context) throws AssertionValidationException {
                return ValidationResult.INVALID;
            }
            @Nonnull public QName getServicedCondition() {
                return OneTimeUse.DEFAULT_ELEMENT_NAME;
            }
        };
        conditionValidators.add(failingValidator);
        getAssertion().getConditions().getConditions().add((Condition) buildXMLObject(OneTimeUse.DEFAULT_ELEMENT_NAME));
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
    }
    
    @Test
    public void testUnknownCondition() throws AssertionValidationException {
        getAssertion().getConditions().getConditions().add((Condition) buildXMLObject(OneTimeUse.DEFAULT_ELEMENT_NAME));
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INDETERMINATE);
    }
    
    @Test
    public void testFailStatementValidator() throws AssertionValidationException {
        StatementValidator failingValidator = new StatementValidator() {
            @Nonnull public QName getServicedStatement() {
                return AuthnStatement.DEFAULT_ELEMENT_NAME;
            }
            @Nonnull public ValidationResult validate(@Nonnull Statement statement, @Nonnull Assertion assertion,
                    @Nonnull ValidationContext context) throws AssertionValidationException {
                return ValidationResult.INVALID;
            }
        };
        statementValidators.add(failingValidator);
        getAssertion().getStatements().add((Statement) buildXMLObject(AuthnStatement.DEFAULT_ELEMENT_NAME));
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
    }
    
    @Test
    public void testInvalidSAMLVersion() throws AssertionValidationException {
        getAssertion().setVersion(SAMLVersion.VERSION_11);
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
    }
    
    @Test
    public void testInvalidIssuer() throws AssertionValidationException {
        getAssertion().getIssuer().setValue("invalid");
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
    }
    
    @Test
    public void testNoIssuer() throws AssertionValidationException {
        getAssertion().setIssuer(null);
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
    }
    

    @Test
    public void testNoIssueInstant() throws AssertionValidationException {
        getAssertion().setIssueInstant(null);
        
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
    }
    
    @Test
    public void testGetLifetime() {
        ValidationContext validationContext;
        Map<String,Object> staticParams = new HashMap<>();
       
        // Default
        validationContext = new ValidationContext(staticParams);
        Assert.assertEquals(SAML20AssertionValidator.getLifetime(validationContext), Duration.ofMinutes(5));
        
        staticParams.put(SAML2AssertionValidationParameters.LIFETIME, Duration.ofMinutes(10));
        validationContext = new ValidationContext(staticParams);
        Assert.assertEquals(SAML20AssertionValidator.getLifetime(validationContext), Duration.ofMinutes(10));
        
        staticParams.put(SAML2AssertionValidationParameters.LIFETIME, Duration.ofMinutes(8).negated());
        validationContext = new ValidationContext(staticParams);
        Assert.assertEquals(SAML20AssertionValidator.getLifetime(validationContext), Duration.ofMinutes(8));
        
        // Duration==0 means use default
        staticParams.put(SAML2AssertionValidationParameters.LIFETIME, Duration.ofSeconds(0));
        validationContext = new ValidationContext(staticParams);
        Assert.assertEquals(SAML20AssertionValidator.getLifetime(validationContext), Duration.ofMinutes(5));
    }
    
    @Test
    public void testIssueInstantInFuture() throws AssertionValidationException {
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Instant now = Instant.now();
        Duration clockSkew = SAML20AssertionValidator.getClockSkew(validationContext);
        getAssertion().setIssueInstant(now.plus(clockSkew).plusSeconds(5));
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
    }
    
    @Test
    public void testIssueInstantInFutureWithinClockSkew() throws AssertionValidationException {
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Instant now = Instant.now();
        Duration clockSkew = SAML20AssertionValidator.getClockSkew(validationContext);
        getAssertion().setIssueInstant(now.plus(clockSkew).minusSeconds(5));
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.VALID);
    }
    
    @Test
    public void testIssueInstantExpired() throws AssertionValidationException {
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Instant now = Instant.now();
        Duration clockSkew = SAML20AssertionValidator.getClockSkew(validationContext);
        Duration lifetime = SAML20AssertionValidator.getLifetime(validationContext);
        getAssertion().setIssueInstant(now.minus(lifetime.plus(clockSkew).plusSeconds(5)));
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.INVALID);
    }
    
    @Test
    public void testIssueInstantExpiredWithinClockSkew() throws AssertionValidationException {
        validator = getCurrentValidator();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, false);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Instant now = Instant.now();
        Duration clockSkew = SAML20AssertionValidator.getClockSkew(validationContext);
        Duration lifetime = SAML20AssertionValidator.getLifetime(validationContext);
        getAssertion().setIssueInstant(now.minus(lifetime.plus(clockSkew).minusSeconds(5)));
        
        Assertion assertion = getAssertion();
        
        Assert.assertEquals(validator.validate(assertion, validationContext), ValidationResult.VALID);
    }
    
    
    
    // Helper methods
    
    private SAML20AssertionValidator getCurrentValidator() {
        return new SAML20AssertionValidator(conditionValidators, subjectConfirmationValidators, statementValidators, null, signatureTrustEngine, signaturePrevalidator);
    }
    
    public static class MockCondition extends AbstractXMLObject implements Condition {
        
        public static final QName ELEMENT_NAME = new QName("urn:test:conditions", "MockCondition", "mock");
        
        public MockCondition() {
            this(ELEMENT_NAME.getNamespaceURI(), ELEMENT_NAME.getLocalPart(), ELEMENT_NAME.getPrefix());
        }

        protected MockCondition(String namespaceURI, String elementLocalName, String namespacePrefix) {
            super(namespaceURI, elementLocalName, namespacePrefix);
        }

        /** {@inheritDoc} */
        public List<XMLObject> getOrderedChildren() {
            return null;
        }
        
    }
    
    public static class MockCondition2 extends AbstractXMLObject implements Condition {
        
        public static final QName ELEMENT_NAME = new QName("urn:test:conditions", "MockCondition2", "mock");
        
        public MockCondition2() {
            this(ELEMENT_NAME.getNamespaceURI(), ELEMENT_NAME.getLocalPart(), ELEMENT_NAME.getPrefix());
        }

        protected MockCondition2(String namespaceURI, String elementLocalName, String namespacePrefix) {
            super(namespaceURI, elementLocalName, namespacePrefix);
        }

        /** {@inheritDoc} */
        public List<XMLObject> getOrderedChildren() {
            return null;
        }
        
    }
    
    public static class MockConditionValidator implements ConditionValidator {
        
        /** {@inheritDoc} */
        public QName getServicedCondition() {
            return MockCondition.ELEMENT_NAME;
        }

        /** {@inheritDoc} */
        public ValidationResult validate(Condition condition, Assertion assertion, ValidationContext context)
                throws AssertionValidationException {
            return ValidationResult.VALID;
        }
        
    }

    public static class MockCondition2Validator implements ConditionValidator {
        
        /** {@inheritDoc} */
        public QName getServicedCondition() {
            return MockCondition2.ELEMENT_NAME;
        }

        /** {@inheritDoc} */
        public ValidationResult validate(Condition condition, Assertion assertion, ValidationContext context)
                throws AssertionValidationException {
            return ValidationResult.VALID;
        }
        
    }

}
