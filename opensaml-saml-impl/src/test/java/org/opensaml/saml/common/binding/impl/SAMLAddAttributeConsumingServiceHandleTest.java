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

package org.opensaml.saml.common.binding.impl;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ParentContextLookup;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.AttributeConsumingServiceContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml1.core.AuthenticationStatement;
import org.opensaml.saml.saml1.testing.SAML1ActionTestingSupport;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/**
 * test for {@link SAMLAddAttributeConsumingServiceHandler}.
 */
@SuppressWarnings("javadoc")
public class SAMLAddAttributeConsumingServiceHandleTest extends XMLObjectBaseTestCase {
    
    private SPSSODescriptor withACS;
    private SPSSODescriptor noACS;
    
    private SAMLMetadataContext getMetadataContext(final MessageContext message) {
        return message.ensureSubcontext(SAMLPeerEntityContext.class).ensureSubcontext(SAMLMetadataContext.class);
    }
    
    private SAMLAddAttributeConsumingServiceHandler handler() throws ComponentInitializationException {
        SAMLAddAttributeConsumingServiceHandler h = new SAMLAddAttributeConsumingServiceHandler();
        h.initialize();
        return h;
    }

    
    @BeforeClass
    public void classSetUp() throws ComponentInitializationException {
        withACS = unmarshallElement("/org/opensaml/saml/common/binding/SPSSOwithACS.xml");
        noACS = unmarshallElement("/org/opensaml/saml/common/binding/SPSSOnoACS.xml");
    }

    @Test public void noMetadataDataContext() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext messageContext = new MessageContext();
        
        handler().invoke(messageContext);
    }
    
    @Test public void noSPSSODesc() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext messageContext = new MessageContext();
        final SAMLMetadataContext metadataContext = getMetadataContext(messageContext);
        
        handler().invoke(messageContext);
        Assert.assertNull(metadataContext.getSubcontext(AttributeConsumingServiceContext.class));
    }
    
    @Test public void authnNoIndex() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext messageContext = new MessageContext();
        final SAMLMetadataContext metadataContext = getMetadataContext(messageContext);
        metadataContext.setRoleDescriptor(withACS);
        
        final AuthnRequest request = SAML2ActionTestingSupport.buildAuthnRequest();
        messageContext.setMessage(request);
        
        handler().invoke(messageContext);
        final AttributeConsumingService acs = metadataContext.ensureSubcontext(AttributeConsumingServiceContext.class).getAttributeConsumingService();
        assert acs != null;
        
        Assert.assertEquals(acs.isDefault(), true);
        Assert.assertEquals(acs.getIndex(), 3);
    }

    @Test public void authnWithIndex() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext messageContext = new MessageContext();
        final SAMLMetadataContext metadataContext = getMetadataContext(messageContext);
        metadataContext.setRoleDescriptor(withACS);
        
        final AuthnRequest request = SAML2ActionTestingSupport.buildAuthnRequest();
        request.setAttributeConsumingServiceIndex(Integer.valueOf(1));
        
        messageContext.setMessage(request);
        
        handler().invoke(messageContext);
        final AttributeConsumingService acs = metadataContext.ensureSubcontext(AttributeConsumingServiceContext.class).getAttributeConsumingService();
        assert acs != null;
        
        Assert.assertEquals(acs.isDefault(), false);
        Assert.assertEquals(acs.getIndex(), 1);
    }

    @Test public void authnBadIndex() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext messageContext = new MessageContext();
        final SAMLMetadataContext metadataContext = getMetadataContext(messageContext);
        metadataContext.setRoleDescriptor(withACS);
        
        final AuthnRequest request = SAML2ActionTestingSupport.buildAuthnRequest();
        request.setAttributeConsumingServiceIndex(Integer.valueOf(9));
        
        messageContext.setMessage(request);
        
        handler().invoke(messageContext);
        final AttributeConsumingService acs = metadataContext.ensureSubcontext(AttributeConsumingServiceContext.class).getAttributeConsumingService();
        assert acs != null;
        
        Assert.assertEquals(acs.isDefault(), true);
        Assert.assertEquals(acs.getIndex(), 3);
    }

    @Test public void authnNoACS() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext messageContext = new MessageContext();
        final SAMLMetadataContext metadataContext = getMetadataContext(messageContext);
        metadataContext.setRoleDescriptor(noACS);
        
        final AuthnRequest request = SAML2ActionTestingSupport.buildAuthnRequest();
        messageContext.setMessage(request);
        
        handler().invoke(messageContext);
        Assert.assertNull(metadataContext.getSubcontext(AttributeConsumingServiceContext.class));
    }
    
    @Test public void noAuthn() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext messageContext = new MessageContext();
        final SAMLMetadataContext metadataContext = getMetadataContext(messageContext);
        metadataContext.setRoleDescriptor(withACS);
        
        final AuthenticationStatement request = SAML1ActionTestingSupport.buildAuthenticationStatement();
        
        messageContext.setMessage(request);
        
        handler().invoke(messageContext);
        final AttributeConsumingService acs = metadataContext.ensureSubcontext(AttributeConsumingServiceContext.class).getAttributeConsumingService();
        assert acs != null;
        
        Assert.assertEquals(acs.isDefault(), true);
        Assert.assertEquals(acs.getIndex(), 3);
    } 

    @Test public void navigate() throws MessageHandlerException, ComponentInitializationException {
        final SAMLAddAttributeConsumingServiceHandler navigatedHandler = new SAMLAddAttributeConsumingServiceHandler();
        
        navigatedHandler.setMetadataContextLookupStrategy(new ParentContextLookup<>(SAMLMetadataContext.class));
        final SAMLMetadataContext metadataContext = new SAMLMetadataContext();
        final MessageContext messageContext = metadataContext.ensureSubcontext(MessageContext.class);
        metadataContext.setRoleDescriptor(withACS);
        
        final AuthnRequest request = SAML2ActionTestingSupport.buildAuthnRequest();
        request.setAttributeConsumingServiceIndex(Integer.valueOf(1));
        navigatedHandler.initialize();
        
        messageContext.setMessage(request);
        
        navigatedHandler.invoke(messageContext);
        final AttributeConsumingService acs = metadataContext.ensureSubcontext(AttributeConsumingServiceContext.class).getAttributeConsumingService();
        assert acs != null;
        
        Assert.assertEquals(acs.isDefault(), false);
        Assert.assertEquals(acs.getIndex(), 1);
    }
    
}