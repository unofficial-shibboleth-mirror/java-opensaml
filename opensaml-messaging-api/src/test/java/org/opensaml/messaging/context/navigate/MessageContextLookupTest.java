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

package org.opensaml.messaging.context.navigate;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.MessageContextLookup.Direction;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.logic.ConstraintViolationException;

public class MessageContextLookupTest {
    
    private InOutOperationContext opContext;
    
    @BeforeMethod
    public void setUp() {
        opContext = new InOutOperationContext(new MessageContext(), new MessageContext());
    }
    
    @Test
    public void testInboundFromSame() {
        MockContext input = new MockContext();
        opContext.getInboundMessageContext().addSubcontext(input);
        MessageContextLookup<BaseContext> lookup  = new MessageContextLookup<>(Direction.INBOUND);
        Assert.assertSame(lookup.apply(input), opContext.getInboundMessageContext());
    }

    @Test
    public void testOutboundFromSame() {
        MockContext input = new MockContext();
        opContext.getOutboundMessageContext().addSubcontext(input);
        MessageContextLookup<BaseContext> lookup  = new MessageContextLookup<>(Direction.OUTBOUND);
        Assert.assertSame(lookup.apply(input), opContext.getOutboundMessageContext());
    }
    
    @Test
    public void testInboundFromCrosswalk() {
        MockContext input = new MockContext();
        opContext.getOutboundMessageContext().addSubcontext(input);
        MessageContextLookup<BaseContext> lookup  = new MessageContextLookup<>(Direction.INBOUND);
        Assert.assertSame(lookup.apply(input), opContext.getInboundMessageContext());
    }

    @Test
    public void testOutboundFromCrosswalk() {
        MockContext input = new MockContext();
        opContext.getInboundMessageContext().addSubcontext(input);
        MessageContextLookup<BaseContext> lookup  = new MessageContextLookup<>(Direction.OUTBOUND);
        Assert.assertSame(lookup.apply(input), opContext.getOutboundMessageContext());
    }
    
    @Test
    public void testNoParentOpContext() {
        MessageContextLookup<BaseContext> lookup  = new MessageContextLookup<>(Direction.INBOUND);
        Assert.assertNull(lookup.apply(new MessageContext()));
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testCtorNoDirection() {
        new MessageContextLookup<>(null);
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testCtorNoLookup() {
        new MessageContextLookup<>(Direction.INBOUND, null);
    }
    
    
    // Helpers
    
    private static class MockContext extends BaseContext {
        
    }

}
