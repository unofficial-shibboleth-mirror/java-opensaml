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

package org.opensaml.messaging.handler;

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.MessageContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.logic.PredicateSupport;

/** Unit test for {@link AbstractMessageHandler}. */
public class AbstractMessageHandlerTest {


    /**
     * Test a successful action run.
     * 
     * @throws Exception if something bad happens
     */
    @Test
    public void testSuccess() throws Exception {

        final BaseMessageHandler handler = new BaseMessageHandler();
        handler.initialize();
        handler.invoke(new MessageContext());

        Assert.assertTrue(handler.didPre);
        Assert.assertTrue(handler.didExec);
        Assert.assertTrue(handler.didPost);
    }

    /**
     * Test a failure in the preexec step.
     * 
     * @throws Exception if something bad happens
     */
    @Test
    public void testPreFailure() throws Exception {

        final BaseMessageHandler handler = new PreFailMessageHandler();
        handler.initialize();

        try {
            handler.invoke(new MessageContext());
        } catch (MessageHandlerException e) {
            Assert.assertFalse(handler.didPre);
            Assert.assertFalse(handler.didExec);
            Assert.assertFalse(handler.didPost);
        }
    }

    /**
     * Test a failure in the exec step.
     * 
     * @throws Exception if something bad happens
     */
    @Test
    public void testExecFailure() throws Exception {

        final BaseMessageHandler handler = new ExecFailMessageHandler();
        handler.initialize();

        try {
            handler.invoke(new MessageContext());
        } catch (NullPointerException e) {
            Assert.assertTrue(e.getSuppressed()[0] instanceof MessageHandlerException);
            Assert.assertTrue(handler.didPre);
            Assert.assertFalse(handler.didExec);
            Assert.assertFalse(handler.didPost);
        }
    }

    /**
     * Test an unchecked error in the exec step.
     * 
     * @throws Exception if something bad happens
     */
    @Test
    public void testExecUnchecked() throws Exception {

        final BaseMessageHandler handler = new ExecUncheckedMessageHandler();
        handler.initialize();

        try {
            handler.invoke(new MessageContext());
        } catch (NullPointerException e) {
            Assert.assertTrue(e.getSuppressed()[0] instanceof IllegalArgumentException);
            Assert.assertTrue(handler.didPre);
            Assert.assertFalse(handler.didExec);
            Assert.assertTrue(handler.didPost);
        }
    }

    /**
     * Test a failure in the post step.
     * 
     * @throws Exception if something bad happens
     */
    @Test
    public void testPostFailure() throws Exception {

        final BaseMessageHandler handler = new PostFailMessageHandler();
        handler.initialize();

        try {
            handler.invoke(new MessageContext());
        } catch (NullPointerException e) {
            Assert.assertTrue(handler.didPre);
            Assert.assertTrue(handler.didExec);
            Assert.assertFalse(handler.didPost);
        }
    }
    
    /**
     * Test handler with an always true activation condition.
     * 
     * @throws Exception
     */
    @Test
    public void testTrueActivationCondition() throws Exception {
        final MockMutatingHandler handler = new MockMutatingHandler();
        handler.setActivationCondition(PredicateSupport.alwaysTrue());
        handler.initialize();
        
        MessageContext messageContext = new MessageContext();
        handler.invoke(messageContext);
        
        Assert.assertTrue(messageContext.containsSubcontext(MockContext.class));
        Assert.assertEquals(messageContext.ensureSubcontext(MockContext.class).value, "hello");
    }

    /**
     * Test handler with an always false activation condition.
     * 
     * @throws Exception
     */
    @Test
    public void testFalseActivationCondition() throws Exception {
        final MockMutatingHandler handler = new MockMutatingHandler();
        handler.setActivationCondition(PredicateSupport.alwaysFalse());
        handler.initialize();
        
        MessageContext messageContext = new MessageContext();
        handler.invoke(messageContext);
        
        Assert.assertFalse(messageContext.containsSubcontext(MockContext.class));
    }

    private class BaseMessageHandler extends AbstractMessageHandler {
        private boolean didPre = false;
        private boolean didExec = false;
        private boolean didPost = false;
        
        protected boolean doPreInvoke(@Nonnull final MessageContext mc) throws MessageHandlerException {
            return didPre = true;
        }
        
        protected void doInvoke(@Nonnull final MessageContext mc) throws MessageHandlerException {
            didExec = true;
        }

        protected void doPostInvoke(@Nonnull final MessageContext mc) {
            didPost = true; 
        }
    }

    private class PreFailMessageHandler extends BaseMessageHandler {
        
        protected boolean doPreInvoke(@Nonnull final MessageContext mc) throws MessageHandlerException {
            throw new MessageHandlerException();
        }
    }
    
    private class ExecFailMessageHandler extends BaseMessageHandler {
        
        protected void doInvoke(@Nonnull final MessageContext mc) throws MessageHandlerException {
            throw new MessageHandlerException();
        }

        protected void doPostInvoke(@Nonnull final MessageContext mc) {
            throw new NullPointerException();
        }
    }

    private class ExecUncheckedMessageHandler extends BaseMessageHandler {
        
        protected void doInvoke(@Nonnull final MessageContext mc) throws MessageHandlerException {
            throw new IllegalArgumentException();
        }

        protected void doPostInvoke(@Nonnull final MessageContext mc) {
            super.doPostInvoke(mc);
            throw new NullPointerException();
        }
    }

    private class PostFailMessageHandler extends BaseMessageHandler {
        
        protected void doPostInvoke(@Nonnull final MessageContext mc) {
            throw new NullPointerException();
        }
    }
    
    private class MockMutatingHandler extends AbstractMessageHandler {

        protected void doInvoke(@Nonnull MessageContext messageContext) throws MessageHandlerException {
            messageContext.ensureSubcontext(MockContext.class).value = "hello";
        }
    }
    
    /**
     * Mock context
     */
    public static class MockContext extends BaseContext {
        
        /** Mock value. */
        public String value;
    }

}