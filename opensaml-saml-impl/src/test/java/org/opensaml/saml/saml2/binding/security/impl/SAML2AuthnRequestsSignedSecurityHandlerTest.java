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

package org.opensaml.saml.saml2.binding.security.impl;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * Test SAML 2 AuthnRequetsSigned rule.
 */
public class SAML2AuthnRequestsSignedSecurityHandlerTest extends XMLObjectBaseTestCase {
    
    private SAML2AuthnRequestsSignedSecurityHandler handler;
    
    private MessageContext messageContext;
 
    private final String issuer = "urn:test:issuer";
    
    private SPSSODescriptor spssoDescriptor;

    /**
     * Test set up.
     * 
     * @throws Exception
     */
    @BeforeMethod
    protected void setUp() throws Exception {
        handler = new SAML2AuthnRequestsSignedSecurityHandler();
        handler.initialize();
        
        spssoDescriptor = buildXMLObject(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        spssoDescriptor.setAuthnRequestsSigned(false);
        
        messageContext = new MessageContext();
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class).setEntityId(issuer);
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class)
            .ensureSubcontext(SAMLMetadataContext.class).setRoleDescriptor(spssoDescriptor);
    }
    
    /**
     * Test message not signed, signing not required.
     * 
     * @throws MessageHandlerException ...
     */
    @Test
    public void testNotSignedAndNotRequired() throws MessageHandlerException {
        AuthnRequest authnRequest = 
            (AuthnRequest) unmarshallElement("/org/opensaml/saml/saml2/binding/AuthnRequest.xml");
        messageContext.setMessage(authnRequest);
        
        handler.invoke(messageContext);
    }
    
    
    /**
     * Test message not signed, signing required.
     * 
     * @throws MessageHandlerException ...
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testNotSignedAndRequired() throws MessageHandlerException {
        AuthnRequest authnRequest = 
            (AuthnRequest) unmarshallElement("/org/opensaml/saml/saml2/binding/AuthnRequest.xml");
        messageContext.setMessage(authnRequest);
        
        spssoDescriptor.setAuthnRequestsSigned(true);
        
        handler.invoke(messageContext);
    }
    
    /**
     * Test message XML signed, signing not required.
     * 
     * @throws MessageHandlerException ...
     */
    @Test
    public void testSignedAndNotRequired() throws MessageHandlerException {
        AuthnRequest authnRequest = 
            (AuthnRequest) unmarshallElement("/org/opensaml/saml/saml2/binding/AuthnRequest-Signed.xml");
        messageContext.setMessage(authnRequest);
        
        handler.invoke(messageContext);
    }
 
    /**
     * Test message XML signed, signing required.
     * 
     * @throws MessageHandlerException ...
     */
    @Test
    public void testSignedAndRequired() throws MessageHandlerException {
        AuthnRequest authnRequest = 
            (AuthnRequest) unmarshallElement("/org/opensaml/saml/saml2/binding/AuthnRequest-Signed.xml");
        messageContext.setMessage(authnRequest);
        
        spssoDescriptor.setAuthnRequestsSigned(true);
        
        handler.invoke(messageContext);
    }
    
    /**
     * Test message simple signed, signing not required.
     * 
     * @throws MessageHandlerException ...
     */
    @Test
    public void testSimpleSignedAndRequired() throws MessageHandlerException {
        AuthnRequest authnRequest = 
            (AuthnRequest) unmarshallElement("/org/opensaml/saml/saml2/binding/AuthnRequest.xml");
        messageContext.setMessage(authnRequest);
        
        spssoDescriptor.setAuthnRequestsSigned(true);
        
        messageContext.ensureSubcontext(SAMLBindingContext.class).setHasBindingSignature(true);
        
        handler.invoke(messageContext);
    }
    
}
