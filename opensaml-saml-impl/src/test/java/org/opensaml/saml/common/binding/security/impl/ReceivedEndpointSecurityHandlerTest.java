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

package org.opensaml.saml.common.binding.security.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.messaging.MessageException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.testing.ConstantSupplier;

/**
 * Test the received endpoint check message handler.
 */
@SuppressWarnings({"null", "javadoc"})
public class ReceivedEndpointSecurityHandlerTest extends XMLObjectBaseTestCase {
    
    private MessageContext messageContext;
    
    private SAMLBindingContext samlBindingContext;
    
    private MockHttpServletRequest httpRequest;
    
    private String intendedDestinationUri;
    
    private ReceivedEndpointSecurityHandler handler;
    
    @BeforeMethod
    public void setUp() throws MessageException, ComponentInitializationException {
        messageContext = new MessageContext();
        messageContext.setMessage(unmarshallElement("/org/opensaml/saml/saml2/binding/AuthnRequest.xml"));
        
        httpRequest = new MockHttpServletRequest();
        
        samlBindingContext = messageContext.ensureSubcontext(SAMLBindingContext.class);
        samlBindingContext.setBindingUri(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
        samlBindingContext.setHasBindingSignature(false);
        samlBindingContext.setIntendedDestinationEndpointURIRequired(false);
        
        intendedDestinationUri = SAMLBindingSupport.getIntendedDestinationEndpointURI(messageContext);
        
        handler = new ReceivedEndpointSecurityHandler();
        handler.setHttpServletRequestSupplier(new ConstantSupplier<>(httpRequest));
        handler.initialize();
    }
    
    @Test
    public void testEndpointGood() throws MessageHandlerException {
        String deliveredEndpointURL = intendedDestinationUri;
        
        populateRequestURL(httpRequest, deliveredEndpointURL);
        
        handler.invoke(messageContext);
    }
    
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testEndpointInvalidPath() throws MessageHandlerException {
        String deliveredEndpointURL = intendedDestinationUri + "/some/other/endpointURI";
        
        
        populateRequestURL(httpRequest, deliveredEndpointURL);
        
        handler.invoke(messageContext);
    }
    
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testEndpointInvalidHost() throws MessageHandlerException {
        String deliveredEndpointURL = "https://bogusidp.example.com/idp/sso";
        
        populateRequestURL(httpRequest, deliveredEndpointURL);
        
        handler.invoke(messageContext);
    }
    
    @Test
    public void testEndpointMissingDestinationNotRequired() throws MessageHandlerException {
        final AuthnRequest authnRequest = (AuthnRequest) messageContext.getMessage();
        assert authnRequest != null;
        authnRequest.setDestination(null);
        
        samlBindingContext.setIntendedDestinationEndpointURIRequired(false);
        
        String deliveredEndpointURL = intendedDestinationUri;
        
        populateRequestURL(httpRequest, deliveredEndpointURL);
        
        handler.invoke(messageContext);
    }
    
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testEndpointMissingDestinationRequired() throws MessageHandlerException {
        final AuthnRequest authnRequest = (AuthnRequest) messageContext.getMessage();
        assert authnRequest != null;
        authnRequest.setDestination(null);
        
        samlBindingContext.setIntendedDestinationEndpointURIRequired(true);
        
        String deliveredEndpointURL = intendedDestinationUri;
        
        populateRequestURL(httpRequest, deliveredEndpointURL);
        
        handler.invoke(messageContext);
    }
    
    
    // Helpers
    
    private void populateRequestURL(MockHttpServletRequest request, String requestURL) {
        URL url = null;
        try {
            url = new URL(requestURL);
        } catch (MalformedURLException e) {
            Assert.fail("Malformed URL: " + e.getMessage());
        }
        assert url != null;
        request.setScheme(url.getProtocol());
        request.setServerName(url.getHost());
        if (url.getPort() != -1) {
            request.setServerPort(url.getPort());
        } else {
            if ("https".equalsIgnoreCase(url.getProtocol())) {
                request.setServerPort(443);
            } else if ("http".equalsIgnoreCase(url.getProtocol())) {
                request.setServerPort(80);
            }
        }
        request.setRequestURI(url.getPath());
        request.setQueryString(url.getQuery());
    }

}