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

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.assertion.tests.BaseAssertionValidationTest;
import org.opensaml.saml.saml2.core.KeyInfoConfirmationDataType;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.codec.EncodingException;

@SuppressWarnings({"null", "javadoc"})
public class HolderOfKeySubjectConfirmationValidatorTest extends BaseAssertionValidationTest {
    
    private HolderOfKeySubjectConfirmationValidator validator;
    
    private SubjectConfirmation subjectConfirmation;
    
    private SubjectConfirmationData subjectConfirmationData;
    
    private KeyInfo keyInfo;
    
    private X509Certificate cert1, cert2;
    
    private PublicKey publicKey1, publicKey2;
    
    @BeforeClass
    protected void readCertsAndKeys() throws CertificateException, URISyntaxException {
        cert1 = getCertificate("subject1.crt");
        publicKey1 = cert1.getPublicKey();
        cert2 = getCertificate("subject2.crt");
        publicKey2 = cert2.getPublicKey();
    }
    
    @BeforeMethod(dependsOnMethods="setUpBasicAssertion")
    protected void setUp() throws NoSuchAlgorithmException, NoSuchProviderException {
        validator = new HolderOfKeySubjectConfirmationValidator();
        
        subjectConfirmation = getSubject().getSubjectConfirmations().get(0);
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_HOLDER_OF_KEY);
        subjectConfirmationData = buildBasicSubjectConfirmationData(KeyInfoConfirmationDataType.TYPE_NAME);
        keyInfo = buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        ((KeyInfoConfirmationDataType)subjectConfirmationData).getKeyInfos().add(keyInfo);
        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);
    }

    @Test
    public void testValidPublicKeyViaKeyValue() throws AssertionValidationException, EncodingException {
        KeyInfoSupport.addPublicKey(keyInfo, publicKey1);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_KEY, publicKey1);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);
        
        Assert.assertSame(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.SC_HOK_CONFIRMED_KEYINFO), keyInfo);
    }
    
    @Test
    public void testValidPublicKeyViaDEREncodedKeyValue() throws AssertionValidationException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeyInfoSupport.addDEREncodedPublicKey(keyInfo, publicKey1);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_KEY, publicKey1);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);
        
        Assert.assertSame(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.SC_HOK_CONFIRMED_KEYINFO), keyInfo);
    }
    
    @Test
    public void testInvalidPublicKey() throws AssertionValidationException, EncodingException {
        KeyInfoSupport.addPublicKey(keyInfo, publicKey1);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_KEY, publicKey2);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.SC_HOK_CONFIRMED_KEYINFO));
    }
    
    @Test
    public void testValidCert() throws AssertionValidationException, CertificateEncodingException {
        KeyInfoSupport.addCertificate(keyInfo, cert1);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_CERT, cert1);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);
        
        Assert.assertSame(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.SC_HOK_CONFIRMED_KEYINFO), keyInfo);
    }
    
    @Test
    public void testInvalidCert() throws AssertionValidationException, CertificateEncodingException {
        KeyInfoSupport.addCertificate(keyInfo, cert1);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_CERT, cert2);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.SC_HOK_CONFIRMED_KEYINFO));
    }
    
    @Test
    public void testMissingKeyInfos() throws AssertionValidationException {
        subjectConfirmationData.getUnknownXMLObjects().clear();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_KEY, publicKey1);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.SC_HOK_CONFIRMED_KEYINFO));
    }
    
    @Test
    public void testMissingPresenterParams() throws AssertionValidationException, EncodingException {
        KeyInfoSupport.addPublicKey(keyInfo, publicKey1);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.SC_HOK_CONFIRMED_KEYINFO));
    }
    
    @Test
    public void testInvalidPublicKeyParam() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_KEY, "foobar");
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.SC_HOK_CONFIRMED_KEYINFO));
    }
    
    @Test
    public void testInvalidCertParam() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_CERT, "foobar");
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.SC_HOK_CONFIRMED_KEYINFO));
    }
    
    @Test
    public void testCertAndKeyParamMismatch() throws AssertionValidationException, EncodingException {
        KeyInfoSupport.addPublicKey(keyInfo, publicKey1);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_KEY, publicKey1);
        staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_CERT, cert2);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.SC_HOK_CONFIRMED_KEYINFO));
    }
    
    @Test
    public void testNonHOKMethod() throws AssertionValidationException {
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
        subjectConfirmation.setSubjectConfirmationData(buildBasicSubjectConfirmationData());
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_KEY, publicKey1);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.SC_HOK_CONFIRMED_KEYINFO));
    }
    
    @Test
    public void testNonKeyInfoConfirmationData() throws AssertionValidationException {
        subjectConfirmation.setSubjectConfirmationData(buildBasicSubjectConfirmationData());
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_KEY, publicKey1);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(SAML2AssertionValidationParameters.SC_HOK_CONFIRMED_KEYINFO));
    }
    

}
