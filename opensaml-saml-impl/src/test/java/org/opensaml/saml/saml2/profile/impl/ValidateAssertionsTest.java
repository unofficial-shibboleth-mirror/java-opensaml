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

package org.opensaml.saml.saml2.profile.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationProcessingData;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.saml2.assertion.tests.MockAssertionValidator;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.servlet.http.HttpServletResponse;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.NonNullSupplier;

public class ValidateAssertionsTest extends OpenSAMLInitBaseTestCase {
    
    private ValidateAssertions action;
    
    private ProfileRequestContext prc;
    
    private Response samlResponse;
    
    private Map<Assertion,Object> resultsMap;
    
    private MockHttpServletRequest httpRequest;
    private MockHttpServletResponse httpResponse;
    
    @BeforeMethod
    public void beforeMethod() {
        httpRequest = new MockHttpServletRequest();
        httpResponse = new MockHttpServletResponse();
        
        resultsMap = new HashMap<>();
        
        action = new ValidateAssertions();
        action.setHttpServletRequestSupplier(new NonNullSupplier<>() { public MockHttpServletRequest get() {return httpRequest;}}); 
        action.setHttpServletResponseSupplier(new NonNullSupplier<> () {public HttpServletResponse get() { return httpResponse;}});
        action.setValidationContextBuilder(new MockAssertionValidationContextBuilder());
        action.setAssertionValidator(new MockAssertionValidator(resultsMap));
        
        samlResponse = SAML2ActionTestingSupport.buildResponse();
        samlResponse.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        prc = new RequestContextBuilder().setInboundMessage(samlResponse).buildProfileRequestContext();
        
    }
    
    @Test
    public void testValid() throws ComponentInitializationException {
        resultsMap.put(samlResponse.getAssertions().get(0), ValidationResult.VALID);
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        checkObjectMetadata(samlResponse.getAssertions().get(0), ValidationResult.VALID);
    }

    @Test
    public void testInvalid() throws ComponentInitializationException {
        resultsMap.put(samlResponse.getAssertions().get(0), ValidationResult.INVALID);
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.ASSERTION_INVALID);
        checkObjectMetadata(samlResponse.getAssertions().get(0), ValidationResult.INVALID);
    }

    @Test
    public void testIndeterminate() throws ComponentInitializationException {
        resultsMap.put(samlResponse.getAssertions().get(0), ValidationResult.INDETERMINATE);
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.ASSERTION_INVALID);
        checkObjectMetadata(samlResponse.getAssertions().get(0), ValidationResult.INDETERMINATE);
    }
    
    @Test
    public void testInvalidNonFatal() throws ComponentInitializationException {
        resultsMap.put(samlResponse.getAssertions().get(0), ValidationResult.INVALID);
        
        action.setInvalidFatal(false);
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        checkObjectMetadata(samlResponse.getAssertions().get(0), ValidationResult.INVALID);
    }

    @Test
    public void testIndeterminateNonFatal() throws ComponentInitializationException {
        resultsMap.put(samlResponse.getAssertions().get(0), ValidationResult.INDETERMINATE);
        
        action.setInvalidFatal(false);
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        checkObjectMetadata(samlResponse.getAssertions().get(0), ValidationResult.INDETERMINATE);
    }
    
    @Test
    public void testMultipleValid() throws ComponentInitializationException {
        samlResponse.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        
        resultsMap.put(samlResponse.getAssertions().get(0), ValidationResult.VALID);
        resultsMap.put(samlResponse.getAssertions().get(1), ValidationResult.VALID);
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        checkObjectMetadata(samlResponse.getAssertions().get(0), ValidationResult.VALID);
        checkObjectMetadata(samlResponse.getAssertions().get(1), ValidationResult.VALID);
    }

    @Test
    public void testMultipleMixed() throws ComponentInitializationException {
        samlResponse.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        
        resultsMap.put(samlResponse.getAssertions().get(0), ValidationResult.INVALID);
        resultsMap.put(samlResponse.getAssertions().get(1), ValidationResult.VALID);
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.ASSERTION_INVALID);
        checkObjectMetadata(samlResponse.getAssertions().get(0), ValidationResult.INVALID);
        checkObjectMetadata(samlResponse.getAssertions().get(1), ValidationResult.VALID);
    }
    
    @Test
    public void testMultipleMixedNonFatal() throws ComponentInitializationException {
        samlResponse.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        
        resultsMap.put(samlResponse.getAssertions().get(0), ValidationResult.INVALID);
        resultsMap.put(samlResponse.getAssertions().get(1), ValidationResult.VALID);
        
        action.setInvalidFatal(false);
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        checkObjectMetadata(samlResponse.getAssertions().get(0), ValidationResult.INVALID);
        checkObjectMetadata(samlResponse.getAssertions().get(1), ValidationResult.VALID);
    }
    
    @Test
    public void testValidatorLookup() throws ComponentInitializationException {
        resultsMap.put(samlResponse.getAssertions().get(0), ValidationResult.VALID);
        
        action.setAssertionValidator(null);
        action.setAssertionValidatorLookup(input -> {return new MockAssertionValidator(resultsMap);});
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        checkObjectMetadata(samlResponse.getAssertions().get(0), ValidationResult.VALID);
    }
    
    @Test
    public void testValidatorLookupFails() throws ComponentInitializationException {
        action.setAssertionValidator(null);
        action.setAssertionValidatorLookup(input -> {return null;});
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.UNABLE_VALIDATE_ASSERTION);
    }
    
    @Test
    public void testCustomAssertionResolution() throws ComponentInitializationException {
        Assertion assertion1 = SAML2ActionTestingSupport.buildAssertion();
        resultsMap.put(assertion1, ValidationResult.VALID);
        
        action.setAssertionResolver(input -> {return Collections.singletonList(assertion1);});
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        checkObjectMetadata(assertion1, ValidationResult.VALID);
        checkObjectMetadataEmpty(samlResponse.getAssertions().get(0));
    }
    
    @Test
    public void testNoAssertions() throws ComponentInitializationException {
        action.setAssertionResolver(input -> {return Collections.emptyList();});
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }
    
    @Test
    public void testValidationThrows() throws ComponentInitializationException {
        resultsMap.put(samlResponse.getAssertions().get(0), new AssertionValidationException());
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.UNABLE_VALIDATE_ASSERTION);
        checkObjectMetadataEmpty(samlResponse.getAssertions().get(0));
    }
    
    @Test
    public void testValidationThrowsUnchecked() throws ComponentInitializationException {
        resultsMap.put(samlResponse.getAssertions().get(0), new RuntimeException());
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.UNABLE_VALIDATE_ASSERTION);
        checkObjectMetadataEmpty(samlResponse.getAssertions().get(0));
    }
    
    @Test
    public void testValidationThrowsMultiple() throws ComponentInitializationException {
        samlResponse.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        samlResponse.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        
        resultsMap.put(samlResponse.getAssertions().get(0), ValidationResult.VALID);
        resultsMap.put(samlResponse.getAssertions().get(1), new AssertionValidationException());
        resultsMap.put(samlResponse.getAssertions().get(2), ValidationResult.VALID);
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.UNABLE_VALIDATE_ASSERTION);
        checkObjectMetadata(samlResponse.getAssertions().get(0), ValidationResult.VALID);
        checkObjectMetadataEmpty(samlResponse.getAssertions().get(1));
        checkObjectMetadataEmpty(samlResponse.getAssertions().get(2));
    }
    
    @Test
    public void testUnableToBuildValidationContext() throws ComponentInitializationException {
        resultsMap.put(samlResponse.getAssertions().get(0), ValidationResult.VALID);
        
        action.setValidationContextBuilder(input -> {return null;});
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.UNABLE_VALIDATE_ASSERTION);
        checkObjectMetadataEmpty(samlResponse.getAssertions().get(0));
    }

    @Test(expectedExceptions = ComponentInitializationException.class)
    public void testNoAssertionResolver() throws ComponentInitializationException {
        action.setAssertionResolver(null);
        
        action.initialize();
    }

    @Test(expectedExceptions = ComponentInitializationException.class)
    public void testNoValidators() throws ComponentInitializationException {
        action.setAssertionValidator(null);
        action.setAssertionValidatorLookup(null);
        
        action.initialize();
    }

    @Test(expectedExceptions = ComponentInitializationException.class)
    public void testNoHttpRequest() throws ComponentInitializationException {
        action.setHttpServletRequestSupplier(null);
        
        action.initialize();
    }

    @Test(expectedExceptions = ComponentInitializationException.class)
    public void testNoContextBuilder() throws ComponentInitializationException {
        action.setValidationContextBuilder(null);
        
        action.initialize();
    }

    
    
    // Helpers
    
    public void checkObjectMetadata(Assertion assertion, ValidationResult result) {
        List<ValidationProcessingData> dataItems = assertion.getObjectMetadata().get(ValidationProcessingData.class);
        Assert.assertNotNull(dataItems);
        Assert.assertEquals(dataItems.size(), 1);
        ValidationProcessingData data = dataItems.get(0);
        Assert.assertEquals(data.getResult(), result);
        Assert.assertNotNull(data.getContext());
    }

    public void checkObjectMetadataEmpty(Assertion assertion) {
        Assert.assertTrue(assertion.getObjectMetadata().get(ValidationProcessingData.class).isEmpty());
    }
    
}
