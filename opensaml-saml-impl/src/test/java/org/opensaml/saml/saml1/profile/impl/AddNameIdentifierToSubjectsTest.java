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

package org.opensaml.saml.saml1.profile.impl;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.common.SAMLException;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Subject;
import org.opensaml.saml.saml1.profile.AbstractSAML1NameIdentifierGenerator;
import org.opensaml.saml.saml1.testing.SAML1ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.PredicateSupport;

/** Test for {@link AddNameIdentifierToSubjects}. */
@SuppressWarnings("javadoc")
public class AddNameIdentifierToSubjectsTest extends OpenSAMLInitBaseTestCase {

    private static final String NAME_QUALIFIER = "https://idp.example.org";
    
    private ChainingSAML1NameIdentifierGenerator generator;

    private ProfileRequestContext prc;
    
    private AddNameIdentifierToSubjects action;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        
        action = new AddNameIdentifierToSubjects();
        
        final MockSAML1NameIdentifierGenerator mock = new MockSAML1NameIdentifierGenerator("foo");
        mock.setFormat(NameIdentifier.X509_SUBJECT);
        mock.initialize();

        final MockSAML1NameIdentifierGenerator mock2 = new MockSAML1NameIdentifierGenerator("bar");
        mock2.setFormat(NameIdentifier.EMAIL);
        mock2.setActivationCondition(PredicateSupport.alwaysFalse());
        mock2.initialize();

        final MockSAML1NameIdentifierGenerator mock3 = new MockSAML1NameIdentifierGenerator("baz");
        mock3.setFormat(NameIdentifier.EMAIL);
        mock3.initialize();
        
        generator = new ChainingSAML1NameIdentifierGenerator();
        generator.setGenerators(CollectionSupport.listOf(mock, mock2, mock3));
        
        action.setNameIdentifierGenerator(generator);
    }
    
    @Test
    public void testNoMessage() throws ComponentInitializationException {
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertNull(prc.ensureOutboundMessageContext().getMessage());
    }

    @Test
    public void testNoAssertions() throws ComponentInitializationException {
        prc.ensureOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildResponse());
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertTrue(prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().isEmpty());
    }

    @Test
    public void testNoStatements() throws ComponentInitializationException {
        prc.ensureOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildResponse());
        prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        
        action.setFormatLookupStrategy(new X509FormatLookupStrategy());
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertTrue(prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().get(0).getStatements().isEmpty());
    }

    @Test void testArbitraryFormat() throws ComponentInitializationException {
        addStatements();
        
        action.setNameIdentifierGenerator(generator);
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().get(0);
        Subject subject = assertion.getAuthenticationStatements().get(0).getSubject();
        Assert.assertNull(subject);

        assertion = prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().get(1);
        subject = assertion.getAttributeStatements().get(0).getSubject();
        Assert.assertNull(subject);
    }

    @Test void testSingleGenerator() throws ComponentInitializationException {
        addStatements();
        
        action.setFormatLookupStrategy(new X509FormatLookupStrategy());
        action.setNameIdentifierGenerator(generator);
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().get(0);
        Subject subject = assertion.getAuthenticationStatements().get(0).getSubject();
        assert subject != null;
        NameIdentifier nameID = subject.getNameIdentifier();
        assert nameID != null;
        Assert.assertEquals(nameID.getValue(), "foo");
        Assert.assertEquals(nameID.getFormat(), NameIdentifier.X509_SUBJECT);

        assertion = prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().get(1);
        subject = assertion.getAttributeStatements().get(0).getSubject();
        assert subject != null;
        nameID = subject.getNameIdentifier();
        assert nameID != null;
        Assert.assertEquals(nameID.getValue(), "foo");
        Assert.assertEquals(nameID.getFormat(), NameIdentifier.X509_SUBJECT);
    }

    @Test void testMultipleGenerators() throws ComponentInitializationException {
        addStatements();
        
        action.setFormatLookupStrategy(new EmailFormatLookupStrategy());
        action.setNameIdentifierGenerator(generator);
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().get(0);
        Subject subject = assertion.getAuthenticationStatements().get(0).getSubject();
        assert subject != null;
        NameIdentifier nameID = subject.getNameIdentifier();
        assert nameID != null;
        Assert.assertEquals(nameID.getValue(), "baz");
        Assert.assertEquals(nameID.getFormat(), NameIdentifier.EMAIL);

        assertion = prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().get(1);
        subject = assertion.getAttributeStatements().get(0).getSubject();
        assert subject != null;
        nameID = subject.getNameIdentifier();
        assert nameID != null;
        Assert.assertEquals(nameID.getValue(), "baz");
        Assert.assertEquals(nameID.getFormat(), NameIdentifier.EMAIL);
    }
    
    /** Set up the test message with some statements. */
    private void addStatements() {
        final Response response = SAML1ActionTestingSupport.buildResponse();
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        response.getAssertions().get(0).getAuthenticationStatements().add(SAML1ActionTestingSupport.buildAuthenticationStatement());
        response.getAssertions().get(1).getAttributeStatements().add(SAML1ActionTestingSupport.buildAttributeStatement());
        prc.ensureOutboundMessageContext().setMessage(response);
    }
    
    private class MockSAML1NameIdentifierGenerator extends AbstractSAML1NameIdentifierGenerator {

        private final String identifier;
        
        public MockSAML1NameIdentifierGenerator(@Nonnull final String id) {
            setId("test");
            setDefaultIdPNameQualifierLookupStrategy(new Function<ProfileRequestContext,String>() {
                public String apply(ProfileRequestContext input) {
                    return NAME_QUALIFIER;
                }
            });
            identifier = id;
        }
        
        /** {@inheritDoc} */
        @Override
        protected String getIdentifier(ProfileRequestContext profileRequestContext) throws SAMLException {
            return identifier;
        }
    }
    
    private class X509FormatLookupStrategy implements Function<ProfileRequestContext, List<String>> {

        /** {@inheritDoc} */
        @Override
        public List<String> apply(ProfileRequestContext input) {
            return CollectionSupport.listOf(NameIdentifier.WIN_DOMAIN_QUALIFIED, NameIdentifier.X509_SUBJECT);
        }
    }

    private class EmailFormatLookupStrategy implements Function<ProfileRequestContext, List<String>> {

        /** {@inheritDoc} */
        @Override
        public List<String> apply(ProfileRequestContext input) {
            return CollectionSupport.listOf(NameIdentifier.WIN_DOMAIN_QUALIFIED, NameIdentifier.EMAIL);
        }
    }
    
}