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

package org.opensaml.saml.security.impl;

import org.testng.annotations.Test;

import net.shibboleth.shared.logic.Constraint;

import org.testng.annotations.BeforeMethod;
import org.testng.Assert;

import javax.annotation.Nonnull;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureImpl;
import org.opensaml.xmlsec.signature.support.SignatureException;

/**
 * Test the SAML XML Signature profile validator.
 */
@SuppressWarnings("javadoc")
public class SAMLSignatureProfileValidatorTest extends XMLObjectBaseTestCase {
    
    private SAMLSignatureProfileValidator validator;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        validator = new SAMLSignatureProfileValidator();
    }


    @Test
    public void testValid() {
        Signature sig = getSignature("/org/opensaml/saml/security/Signed-AuthnRequest-Valid.xml");
        assertValidationPass("Valid signature", sig);
    }
    
    @Test
    public void testInvalidNoXMLSignature() {
        Signature sig = getSignature("/org/opensaml/saml/security/Signed-AuthnRequest-Valid.xml");
        ((SignatureImpl)sig).setXMLSignature(null);
        assertValidationFail("Invalid signature - no XMLSignature", sig);
    }
    
    @Test
    public void testInvalidTooManyReferences() {
        Signature sig = getSignature("/org/opensaml/saml/security/Signed-AuthnRequest-TooManyReferences.xml");
        assertValidationFail("Invalid signature - too many References", sig);
    }
    
    @Test
    public void testInvalidNonLocalURI() {
        Signature sig = getSignature("/org/opensaml/saml/security/Signed-AuthnRequest-NonLocalURI.xml");
        assertValidationFail("Invalid signature - non-local Reference URI", sig);
    }
    
    @Test
    public void testInvalidMissingID() {
        Signature sig = getSignature("/org/opensaml/saml/security/Signed-AuthnRequest-MissingID.xml");
        assertValidationFail("Invalid signature - missing ID on parent object", sig);
    }
    
    @Test
    public void testInvalidBadURIValue() {
        Signature sig = getSignature("/org/opensaml/saml/security/Signed-AuthnRequest-BadURIValue.xml");
        assertValidationFail("Invalid signature - bad URI value", sig);
    }
    
    @Test
    public void testInvalidTooManyTransforms() {
        Signature sig = getSignature("/org/opensaml/saml/security/Signed-AuthnRequest-TooManyTransforms.xml");
        assertValidationFail("Invalid signature - too many Transforms", sig);
    }
    
    @Test
    public void testInvalidBadTransform() {
        Signature sig = getSignature("/org/opensaml/saml/security/Signed-AuthnRequest-BadTransform.xml");
        assertValidationFail("Invalid signature - bad Transform", sig);
    }
    
    @Test
    public void testInvalidMissingEnvelopedTransform() {
        Signature sig = getSignature("/org/opensaml/saml/security/Signed-AuthnRequest-MissingEnvelopedTransform.xml");
        assertValidationFail("Invalid signature - missing Enveloped Transform", sig);
    }

    @Test
    public void testInvalidDuplicateIDs() {
        Signature sig = getSignature("/org/opensaml/saml/security/Signed-AuthnRequest-DuplicateIDs.xml");
        assertValidationFail("Invalid signature - duplicate IDs", sig);
    }    
    
    /**
     * Get the signature to validated.  Assume the document element of the file is 
     * a SignableSAMLObject.
     * 
     * @param filename file containing a signed SignableSAMLObject as its document element.
     * @return the signature from the indicated element
     */
    @Nonnull protected Signature getSignature(@Nonnull String filename) {
        SignableSAMLObject signableObj = (SignableSAMLObject) unmarshallElement(filename);
        assert signableObj != null;
        return Constraint.isNotNull(signableObj.getSignature(), "Signature was null");
    }
    
    /**
     * Asserts that the validation of the specified Signature target 
     * was successful, as expected.
     * 
     * @param message failure message if the validation does not pass
     * @param validateTarget the XMLObject to validate
     */
    protected void assertValidationPass(@Nonnull String message, @Nonnull Signature validateTarget) {
       try {
           validator.validate(validateTarget);
       } catch (final SignatureException e) {
           Assert.fail(message + " : Expected success, but validation failure raised ValidationException: " + e.getMessage());
       }
    }
    
    /**
     * Asserts that the validation of the specified Signature target 
     * failed, as expected.
     * 
     * @param message failure message if the validation does not fail
     * @param validateTarget XMLObject to validate
     */
    protected void assertValidationFail(@Nonnull String message, @Nonnull Signature validateTarget) {
       try {
           validator.validate(validateTarget);
           Assert.fail(message + " : Validation success, expected failure to raise ValidationException");
       } catch (final SignatureException e) {
       }
    }
    
}