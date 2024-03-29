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

import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.testing.SOAPMessagingBaseTestCase;
import org.opensaml.soap.wsaddressing.MessageID;
import org.opensaml.soap.wsaddressing.messaging.WSAddressingContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

@SuppressWarnings("javadoc")
public class ExtractMessageIDHandlerTest extends SOAPMessagingBaseTestCase {
    
    private ExtractMessageIDHandler handler;
    
    @BeforeMethod
    protected void setUp() throws ComponentInitializationException {
        handler = new ExtractMessageIDHandler();
    }
    
    @Test
    public void testHeaderPresent() throws ComponentInitializationException, MessageHandlerException {
        MessageID messageID = buildXMLObject(MessageID.ELEMENT_NAME);
        messageID.setURI("abc123");
        SOAPMessagingSupport.addHeaderBlock(getMessageContext(), messageID);
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertEquals(getMessageContext().ensureSubcontext(WSAddressingContext.class).getMessageIDURI(), "abc123");
        
        Assert.assertTrue(SOAPMessagingSupport.checkUnderstoodHeader(getMessageContext(), messageID));
    }

    @Test
    public void testHeaderAbsent() throws ComponentInitializationException, MessageHandlerException {
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertNull(getMessageContext().getSubcontext(WSAddressingContext.class));
        
        Assert.assertTrue(SOAPMessagingSupport.getInboundSOAPContext(getMessageContext()).getUnderstoodHeaders().isEmpty());
    }

}
