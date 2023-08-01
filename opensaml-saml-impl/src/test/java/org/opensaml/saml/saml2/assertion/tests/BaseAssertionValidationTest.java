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

package org.opensaml.saml.saml2.assertion.tests;

import java.io.File;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.core.SubjectLocality;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.x509.X509Support;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;

@SuppressWarnings("javadoc")
public class BaseAssertionValidationTest extends XMLObjectBaseTestCase {
    
    @Nonnull public static final Duration CLOCK_SKEW = Duration.ofMinutes(5);
    
    @Nonnull @NotEmpty public static final String PRINCIPAL_NAME = "gollum";
    
    @Nonnull @NotEmpty public static final String ISSUER = "https://idp.example.org";
    
    @Nonnull @NotEmpty public static final String SUBJECT_CONFIRMATION_RECIPIENT = "https://sp.example.com";
    
    @Nonnull @NotEmpty public static final String SUBJECT_CONFIRMATION_ADDRESS = "10.1.2.3";
    
    @Nonnull @NotEmpty public static final String SUBJECT_CONFIRMATION_IN_RESPONSE_TO = "id-123";
    
    @Nonnull @NotEmpty public static final String AUTHN_STATEMENT_ADDRESS = "10.1.2.3";
    
    
    private Assertion assertion;
    
    @Nonnull protected Assertion getAssertion() {
        return Constraint.isNotNull(assertion, "Assertion was null");
    }
    
    @Nonnull protected Conditions getConditions() {
        return Constraint.isNotNull(assertion.getConditions(), "Conditions was null");
    }
    
    @Nonnull protected Subject getSubject() {
        return Constraint.isNotNull(assertion.getSubject(), "Subject was null");
    }

    @Nonnull protected Issuer getIssuer() {
        return Constraint.isNotNull(assertion.getIssuer(), "Issuer was null");
    }

    @BeforeMethod
    protected void setUpBasicAssertion() {
        assertion = SAML2ActionTestingSupport.buildAssertion();
        assertion.setIssueInstant(Instant.now());
        assertion.setIssuer(SAML2ActionTestingSupport.buildIssuer(ISSUER));
        final Subject subject = SAML2ActionTestingSupport.buildSubject(PRINCIPAL_NAME);
        assertion.setSubject(subject);
        assertion.setConditions(buildBasicConditions());
        
        final SubjectConfirmation subjectConfirmation = buildXMLObject(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        // Default to bearer with basic valid confirmation data, but the test can change as appropriate
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
        subjectConfirmation.setSubjectConfirmationData(buildBasicSubjectConfirmationData());
        subject.getSubjectConfirmations().add(subjectConfirmation);
    }
    
    protected Conditions buildBasicConditions() {
        Conditions conditions = buildXMLObject(Conditions.DEFAULT_ELEMENT_NAME);
        Instant now = Instant.now();
        conditions.setNotBefore(now.minus(5, ChronoUnit.MINUTES));
        conditions.setNotOnOrAfter(now.plus(5, ChronoUnit.MINUTES));
        return conditions;
    }
    
    protected SubjectConfirmationData buildBasicSubjectConfirmationData() {
        return buildBasicSubjectConfirmationData(null);
    }
    
    protected SubjectConfirmationData buildBasicSubjectConfirmationData(QName type) {
       SubjectConfirmationData scd = null;
       if (type != null) {
           XMLObjectBuilder<SubjectConfirmationData> builder = getBuilder(type);
           scd = builder.buildObject(SubjectConfirmationData.DEFAULT_ELEMENT_NAME, type);
       }
       else {
           scd = buildXMLObject(SubjectConfirmationData.DEFAULT_ELEMENT_NAME); 
       }
       scd.setInResponseTo(SUBJECT_CONFIRMATION_IN_RESPONSE_TO);
       scd.setRecipient(SUBJECT_CONFIRMATION_RECIPIENT);
       scd.setAddress(SUBJECT_CONFIRMATION_ADDRESS);
       Instant now = Instant.now();
       scd.setNotBefore(now.minus(5, ChronoUnit.MINUTES));
       scd.setNotOnOrAfter(now.plus(5, ChronoUnit.MINUTES));
       return scd;
    }
    
    protected AuthnStatement buildBasicAuthnStatement() {
        AuthnStatement authnStatement = buildXMLObject(AuthnStatement.DEFAULT_ELEMENT_NAME);
        
        Instant now = Instant.now();
        authnStatement.setAuthnInstant(now.minusSeconds(5));
        
        SubjectLocality sl = buildXMLObject(SubjectLocality.DEFAULT_ELEMENT_NAME);
        sl.setAddress(AUTHN_STATEMENT_ADDRESS);
        authnStatement.setSubjectLocality(sl);
        
        AuthnContextClassRef accr = buildXMLObject(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        accr.setURI(AuthnContext.PASSWORD_AUTHN_CTX);
        AuthnContext ac = buildXMLObject(AuthnContext.DEFAULT_ELEMENT_NAME);
        ac.setAuthnContextClassRef(accr);
        authnStatement.setAuthnContext(ac);
        
        return authnStatement;
    }
    
    protected Map<String,Object> buildBasicStaticParameters() {
        HashMap<String,Object> params = new HashMap<>();
        
        params.put(SAML2AssertionValidationParameters.CLOCK_SKEW, CLOCK_SKEW);
        
        params.put(SAML2AssertionValidationParameters.VALID_ISSUERS, CollectionSupport.singleton(ISSUER));
        
        params.put(SAML2AssertionValidationParameters.SC_VALID_IN_RESPONSE_TO, SUBJECT_CONFIRMATION_IN_RESPONSE_TO);
        
        params.put(SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS, 
                CollectionSupport.singleton(SUBJECT_CONFIRMATION_RECIPIENT));
        try {
            params.put(SAML2AssertionValidationParameters.SC_VALID_ADDRESSES, 
                    CollectionSupport.singleton(InetAddress.getByName(SUBJECT_CONFIRMATION_ADDRESS)));
        } catch(UnknownHostException e) {
            Assert.fail("Invalid address: " + SUBJECT_CONFIRMATION_ADDRESS);
        }
        
        params.put(SAML2AssertionValidationParameters.STMT_AUTHN_MAX_TIME, Duration.ofMinutes(5));
        
        try {
            params.put(SAML2AssertionValidationParameters.STMT_AUTHN_VALID_ADDRESSES, 
                    CollectionSupport.singleton(InetAddress.getByName(AUTHN_STATEMENT_ADDRESS)));
        } catch(UnknownHostException e) {
            Assert.fail("Invalid address: " + AUTHN_STATEMENT_ADDRESS);
        }
        
        return params;
    }
    
    protected X509Certificate getCertificate(String name) throws CertificateException, URISyntaxException {
        File certFile = new File(this.getClass().getResource("/org/opensaml/saml/saml2/assertion/" + name).toURI());
        return X509Support.decodeCertificate(certFile);
    }
    
    protected PrivateKey getPrivateKey(String name) throws KeyException, URISyntaxException {
        File keyFile = new File(this.getClass().getResource("/org/opensaml/saml/saml2/assertion/" + name).toURI());
        return KeySupport.decodePrivateKey(keyFile, null);
    }
    
    protected Credential getSigningCredential(PublicKey publicKey, PrivateKey privateKey) {
        BasicCredential cred = CredentialSupport.getSimpleCredential(publicKey, privateKey);
        cred.setUsageType(UsageType.SIGNING);
        cred.setEntityId(ISSUER);
        return cred;
    }
    
    protected void signAssertion(Assertion a, Credential credential) throws SecurityException, MarshallingException, SignatureException {
        SignatureSigningParameters parameters = new SignatureSigningParameters();
        parameters.setSigningCredential(credential);
        parameters.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        parameters.setSignatureReferenceDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        parameters.setSignatureCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        SignatureSupport.signObject(a, parameters);
    }
    
}
