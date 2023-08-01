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

import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.RecursiveTypedParentContextLookup;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLPresenterEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

@SuppressWarnings({"null", "javadoc"})
public class SAMLProtocolAndRoleHandlerTest {
    
    private SAMLProtocolAndRoleHandler handler;
    private MessageContext messageContext;
    
    @BeforeMethod
    public void setup() {
        handler = new SAMLProtocolAndRoleHandler();
        messageContext = new MessageContext();
    }
    
    @Test
    public void testSetters() throws ComponentInitializationException, MessageHandlerException {
        handler.setProtocol(SAMLConstants.SAML20P_NS); 
        handler.setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        handler.initialize();

        handler.invoke(messageContext);

        final SAMLProtocolContext protocolCtx = messageContext.getSubcontext(SAMLProtocolContext.class); 
        assert protocolCtx != null;
        Assert.assertEquals(protocolCtx.getProtocol(), SAMLConstants.SAML20P_NS);

        final SAMLPeerEntityContext presenterCtx = messageContext.getSubcontext(SAMLPeerEntityContext.class);
        assert presenterCtx != null;
        Assert.assertEquals(presenterCtx.getRole(), SPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }

    @Test
    public void testSettersWithEntityClass() throws ComponentInitializationException, MessageHandlerException {
        handler.setProtocol(SAMLConstants.SAML20P_NS); 
        handler.setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        handler.setEntityContextClass(SAMLPresenterEntityContext.class);
        handler.initialize();

        handler.invoke(messageContext);

        final SAMLProtocolContext protocolCtx = messageContext.getSubcontext(SAMLProtocolContext.class); 
        assert protocolCtx != null;
        Assert.assertEquals(protocolCtx.getProtocol(), SAMLConstants.SAML20P_NS);

        final SAMLPresenterEntityContext presenterCtx = messageContext.getSubcontext(SAMLPresenterEntityContext.class);
        assert presenterCtx != null;
        Assert.assertEquals(presenterCtx.getRole(), SPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }
    
    @Test
    public void testCopySource() throws ComponentInitializationException, MessageHandlerException {
        handler.setCopyContextLookup(new RecursiveTypedParentContextLookup<>(InOutOperationContext.class));
        handler.initialize();
        
        final InOutOperationContext opContext = new InOutOperationContext(messageContext, new MessageContext());
        opContext.ensureSubcontext(SAMLProtocolContext.class).setProtocol(SAMLConstants.SAML20P_NS); 
        opContext.ensureSubcontext(SAMLPeerEntityContext.class).setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        handler.invoke(messageContext);

        final SAMLProtocolContext protocolCtx = messageContext.getSubcontext(SAMLProtocolContext.class); 
        assert protocolCtx != null;
        Assert.assertEquals(protocolCtx.getProtocol(), SAMLConstants.SAML20P_NS);

        final SAMLPeerEntityContext presenterCtx = messageContext.getSubcontext(SAMLPeerEntityContext.class);
        assert presenterCtx != null;
        Assert.assertEquals(presenterCtx.getRole(), SPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }
    
    @Test
    public void testCopySourceWithEntityClass() throws ComponentInitializationException, MessageHandlerException {
        handler.setCopyContextLookup(new RecursiveTypedParentContextLookup<>(InOutOperationContext.class));
        handler.setEntityContextClass(SAMLPresenterEntityContext.class);
        handler.initialize();
        
        final InOutOperationContext opContext = new InOutOperationContext(messageContext, new MessageContext());
        opContext.ensureSubcontext(SAMLProtocolContext.class).setProtocol(SAMLConstants.SAML20P_NS); 
        opContext.ensureSubcontext(SAMLPresenterEntityContext.class).setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        handler.invoke(messageContext);

        final SAMLProtocolContext protocolCtx = messageContext.getSubcontext(SAMLProtocolContext.class); 
        assert protocolCtx != null;
        Assert.assertEquals(protocolCtx.getProtocol(), SAMLConstants.SAML20P_NS);

        final SAMLPresenterEntityContext presenterCtx = messageContext.getSubcontext(SAMLPresenterEntityContext.class);
        assert presenterCtx != null;
        Assert.assertEquals(presenterCtx.getRole(), SPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }

    @Test(expectedExceptions=ComponentInitializationException.class)
    public void testMissingConfiguredProtocol() throws ComponentInitializationException, MessageHandlerException {
        handler.setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        handler.setEntityContextClass(SAMLPresenterEntityContext.class);
        handler.initialize();
    }
    
    @Test(expectedExceptions=ComponentInitializationException.class)
    public void testMissingConfiguredRole() throws ComponentInitializationException, MessageHandlerException {
        handler.setProtocol(SAMLConstants.SAML20P_NS);
        handler.setEntityContextClass(SAMLPresenterEntityContext.class);
        handler.initialize();
    }
    
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testResolverWithNoCopySource() throws ComponentInitializationException, MessageHandlerException {
        handler.setCopyContextLookup(new RecursiveTypedParentContextLookup<>(InOutOperationContext.class));
        handler.initialize();
        
        handler.invoke(messageContext);
    }

}