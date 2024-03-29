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

package org.opensaml.soap.wsaddressing.messaging.impl;

import javax.annotation.Nonnull;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.security.IdentifierGenerationStrategy;

import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.testing.SOAPMessagingBaseTestCase;
import org.opensaml.soap.wsaddressing.MessageID;
import org.opensaml.soap.wsaddressing.messaging.WSAddressingContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class AddMessageIDHandlerTest extends SOAPMessagingBaseTestCase {
    
    private AddMessageIDHandler handler;
    
    @BeforeMethod
    protected void setUp() throws ComponentInitializationException {
        handler = new AddMessageIDHandler();
    }
    
    @Test
    public void testDefaultInternalUUID() throws ComponentInitializationException, MessageHandlerException {
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).isEmpty());
        MessageID messageID = (MessageID) SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).get(0);
        final String uri = messageID.getURI();
        Assert.assertTrue(uri != null && uri.startsWith("urn:uuid:"));
    }

    @Test
    public void testStrategy() throws ComponentInitializationException, MessageHandlerException {
        handler.setIdentifierGenerationStrategy(new IdentifierGenerationStrategy() {
            @Nonnull public String generateIdentifier(boolean xmlSafe) {
                if (xmlSafe) {
                    return "urn:test:abc123:xmlsafe";
                } else {
                    return "urn:test:abc123";
                }
            }
            @Nonnull public String generateIdentifier() {
                return generateIdentifier(true);
            }
        });
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).isEmpty());
        MessageID messageID = (MessageID) SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).get(0);
        Assert.assertEquals(messageID.getURI(), "urn:test:abc123");
    }
    
    @Test
    public void testContext() throws ComponentInitializationException, MessageHandlerException {
        getMessageContext().ensureSubcontext(WSAddressingContext.class).setMessageIDURI("urn:test:abc123");
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).isEmpty());
        MessageID messageID = (MessageID) SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).get(0);
        Assert.assertEquals(messageID.getURI(), "urn:test:abc123");
    }

    @Test
    public void testContextOverride() throws ComponentInitializationException, MessageHandlerException {
        handler.setIdentifierGenerationStrategy(new IdentifierGenerationStrategy() {
            @Nonnull public String generateIdentifier(boolean xmlSafe) {
                if (xmlSafe) {
                    return "urn:test:abc123:xmlsafe";
                }
                return "urn:test:abc123";
            }
            @Nonnull public String generateIdentifier() {
                return generateIdentifier(true);
            }
        });
        
        getMessageContext().ensureSubcontext(WSAddressingContext.class).setMessageIDURI("urn:test:def456");
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).isEmpty());
        MessageID messageID = (MessageID) SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).get(0);
        Assert.assertEquals(messageID.getURI(), "urn:test:def456");
    }

}
