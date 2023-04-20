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

package org.opensaml.saml.saml2.wssecurity.messaging.impl;

import java.io.File;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.x509.X509Support;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.resolver.CriteriaSet;

@SuppressWarnings({"null", "javadoc"})
public class DefaultSAML20AssertionValidationContextBuilderTest extends XMLObjectBaseTestCase {
    
    private DefaultSAML20AssertionValidationContextBuilder builder;
    
    private SAML20AssertionTokenValidationInput input;
    
    private String issuerEntityID = "https://idp.example.org";
    private String rpEntityID = "https://rp.example.com";
    private String requestURL = "https://rp.example.com/wss/saml";
    private String remoteAddr = "10.1.2.3";
    private X509Certificate clientTLSCert;
    

    @BeforeMethod
    protected void setUp() throws URISyntaxException, CertificateException {
        builder = new DefaultSAML20AssertionValidationContextBuilder();
        input = new SAML20AssertionTokenValidationInput(buildMessageContext(), buildHttpServletRequest(), buildAssertion());
    }
    
    @Test
    public void testDefaults() {
        final ValidationContext context = builder.apply(input);
        assert context != null;
        
        // Signature
        Assert.assertEquals(context.getStaticParameters().get(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED), Boolean.TRUE);
        
        CriteriaSet signatureCriteriaSet = (CriteriaSet) context.getStaticParameters().get(SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_CRITERIA_SET);
        Assert.assertEquals(signatureCriteriaSet.size(), 2);
        final EntityIdCriterion entityCrit = signatureCriteriaSet.get(EntityIdCriterion.class); 
        assert entityCrit != null;
        Assert.assertEquals(entityCrit.getEntityId(), issuerEntityID);
        final UsageCriterion usageCrit = signatureCriteriaSet.get(UsageCriterion.class);
        assert usageCrit != null;
        Assert.assertEquals(usageCrit.getUsage(), UsageType.SIGNING);
        
        // Subject confirmation
        Assert.assertEquals(context.getStaticParameters().get(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_CERT), clientTLSCert);
        Assert.assertEquals(context.getStaticParameters().get(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_KEY), null);
        
        Set<String> validRecipients = (Set<String>) context.getStaticParameters().get(SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS);
        Assert.assertEquals(validRecipients.size(), 2);
        Assert.assertTrue(validRecipients.contains(requestURL));
        Assert.assertTrue(validRecipients.contains(rpEntityID));
        
        // Conditions
        Set<String> validAudiences = (Set<String>) context.getStaticParameters().get(SAML2AssertionValidationParameters.COND_VALID_AUDIENCES);
        Assert.assertEquals(validAudiences.size(), 1);
        Assert.assertTrue(validAudiences.contains(rpEntityID));
        
    }
    
    @Test
    public void testCustom() {
        builder.setSignatureRequired(false);
        builder.setSignatureCriteriaSetFunction(
                new Function<Pair<MessageContext,Assertion>, CriteriaSet>() {
                    @Nullable public CriteriaSet apply(@Nullable Pair<MessageContext, Assertion> input) {
                        CriteriaSet criteria = new CriteriaSet();
                        criteria.add(new EntityRoleCriterion(IDPSSODescriptor.DEFAULT_ELEMENT_NAME));
                        criteria.add(new ProtocolCriterion(SAMLConstants.SAML20P_NS));
                        return criteria;
                    }
                });
        
        
        final ValidationContext context = builder.apply(input);
        assert context != null;
        
        // Signature
        Assert.assertEquals(context.getStaticParameters().get(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED), Boolean.FALSE);
        
        CriteriaSet signatureCriteriaSet = (CriteriaSet) context.getStaticParameters().get(SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_CRITERIA_SET);
        Assert.assertEquals(signatureCriteriaSet.size(), 4);
        final EntityIdCriterion entityCrit = signatureCriteriaSet.get(EntityIdCriterion.class); 
        assert entityCrit != null;
        Assert.assertEquals(entityCrit.getEntityId(), issuerEntityID);
        final UsageCriterion usageCrit = signatureCriteriaSet.get(UsageCriterion.class);
        assert usageCrit != null;
        Assert.assertEquals(usageCrit.getUsage(), UsageType.SIGNING);
        final EntityRoleCriterion roleCrit = signatureCriteriaSet.get(EntityRoleCriterion.class); 
        assert roleCrit != null;
        Assert.assertEquals(roleCrit.getRole(), IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        final ProtocolCriterion protocolCrit = signatureCriteriaSet.get(ProtocolCriterion.class); 
        assert protocolCrit != null;
        Assert.assertEquals(protocolCrit.getProtocol(), SAMLConstants.SAML20P_NS);
        
        // Subject confirmation
        Assert.assertEquals(context.getStaticParameters().get(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_CERT), clientTLSCert);
        Assert.assertEquals(context.getStaticParameters().get(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_KEY), null);
        
        Set<String> validRecipients = (Set<String>) context.getStaticParameters().get(SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS);
        Assert.assertEquals(validRecipients.size(), 2);
        Assert.assertTrue(validRecipients.contains(requestURL));
        Assert.assertTrue(validRecipients.contains(rpEntityID));
        
        // Conditions
        Set<String> validAudiences = (Set<String>) context.getStaticParameters().get(SAML2AssertionValidationParameters.COND_VALID_AUDIENCES);
        Assert.assertEquals(validAudiences.size(), 1);
        Assert.assertTrue(validAudiences.contains(rpEntityID));
        
    }

    @Nonnull private MessageContext buildMessageContext() {
        final MessageContext messageContext = new MessageContext();
        messageContext.ensureSubcontext(SAMLSelfEntityContext.class).setEntityId(rpEntityID);
        return messageContext;
    }

    @Nonnull private HttpServletRequest buildHttpServletRequest() throws URISyntaxException, CertificateException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("https");
        request.setServerName("rp.example.com");
        request.setServerPort(443);
        request.setRequestURI("/wss/saml");
        request.setRemoteAddr(remoteAddr);
        
        File presenterCertFile = new File(this.getClass().getResource("/org/opensaml/saml/saml2/wssecurity/messaging/impl/presenter.crt").toURI());
        clientTLSCert = X509Support.decodeCertificate(presenterCertFile);
        request.setAttribute("jakarta.servlet.request.X509Certificate", new X509Certificate[] {clientTLSCert});
        
        return request;
    }

    @Nonnull private Assertion buildAssertion() {
        Assertion assertion = SAML2ActionTestingSupport.buildAssertion();
        assertion.setSubject(SAML2ActionTestingSupport.buildSubject("barney"));
        assertion.setIssuer(SAML2ActionTestingSupport.buildIssuer(issuerEntityID));
        return assertion;
    }

}