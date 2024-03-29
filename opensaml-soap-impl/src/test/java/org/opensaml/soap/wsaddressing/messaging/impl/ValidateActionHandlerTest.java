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
import org.opensaml.soap.wsaddressing.Action;
import org.opensaml.soap.wsaddressing.messaging.WSAddressingContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

@SuppressWarnings("javadoc")
public class ValidateActionHandlerTest extends SOAPMessagingBaseTestCase {
    
    private ValidateActionHandler handler;
    
    @BeforeMethod
    protected void setUp() throws ComponentInitializationException {
        handler = new ValidateActionHandler();
    }
    
    @Test
    public void testNoExpected() throws ComponentInitializationException, MessageHandlerException {
        Action action = buildXMLObject(Action.ELEMENT_NAME);
        action.setURI("urn:test:action1");
        SOAPMessagingSupport.addHeaderBlock(getMessageContext(), action);
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.checkUnderstoodHeader(getMessageContext(), action));
    }
    
    @Test
    public void testLocalExpected() throws ComponentInitializationException, MessageHandlerException {
        Action action = buildXMLObject(Action.ELEMENT_NAME);
        action.setURI("urn:test:action1");
        SOAPMessagingSupport.addHeaderBlock(getMessageContext(), action);
        
        handler.setExpectedActionURI("urn:test:action1");
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertTrue(SOAPMessagingSupport.checkUnderstoodHeader(getMessageContext(), action));
    }
    
    @Test
    public void testContextExpected() throws ComponentInitializationException, MessageHandlerException {
        Action action = buildXMLObject(Action.ELEMENT_NAME);
        action.setURI("urn:test:action1");
        SOAPMessagingSupport.addHeaderBlock(getMessageContext(), action);
        
        getMessageContext().ensureSubcontext(WSAddressingContext.class).setActionURI("urn:test:action1");
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertTrue(SOAPMessagingSupport.checkUnderstoodHeader(getMessageContext(), action));
    }
    
    @Test
    public void testContextOverride() throws ComponentInitializationException, MessageHandlerException {
        Action action = buildXMLObject(Action.ELEMENT_NAME);
        action.setURI("urn:test:action1");
        SOAPMessagingSupport.addHeaderBlock(getMessageContext(), action);
        
        handler.setExpectedActionURI("urn:test:action2");
        getMessageContext().ensureSubcontext(WSAddressingContext.class).setActionURI("urn:test:action1");
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertTrue(SOAPMessagingSupport.checkUnderstoodHeader(getMessageContext(), action));
    }
    
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testNoMatch() throws ComponentInitializationException, MessageHandlerException {
        Action action = buildXMLObject(Action.ELEMENT_NAME);
        action.setURI("urn:test:action1");
        SOAPMessagingSupport.addHeaderBlock(getMessageContext(), action);
        
        handler.setExpectedActionURI("urn:test:action2");
        
        handler.initialize();
        handler.invoke(getMessageContext());
    }
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testExpectedButNoHeader() throws ComponentInitializationException, MessageHandlerException {
        handler.setExpectedActionURI("urn:test:action1");
        
        handler.initialize();
        handler.invoke(getMessageContext());
    }

}
