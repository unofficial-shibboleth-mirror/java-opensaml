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

package org.opensaml.saml.common.binding.security.impl;

import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.binding.security.impl.MessageContextEntityIDLookup.Direction;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import junit.framework.Assert;

/**
 *
 */
public class MessageContextEntityIDLookupTest {
    
    private InOutOperationContext opContext;
    
    @BeforeMethod
    public void setUp() {
        opContext = new InOutOperationContext(new MessageContext(), new MessageContext());
        opContext.getInboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).setEntityId("inbound-peer");
        opContext.getInboundMessageContext().getSubcontext(SAMLSelfEntityContext.class, true).setEntityId("inbound-self");
        opContext.getOutboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).setEntityId("outbound-peer");
        opContext.getOutboundMessageContext().getSubcontext(SAMLSelfEntityContext.class, true).setEntityId("outbound-self");
    }
    
    @Test
    public void testInboundPeer() {
        MessageContextEntityIDLookup lookup  = new MessageContextEntityIDLookup(SAMLPeerEntityContext.class);
        lookup.setDirection(Direction.INBOUND);
        Assert.assertEquals("inbound-peer", lookup.apply(opContext.getInboundMessageContext()));
        Assert.assertEquals("inbound-peer", lookup.apply(opContext.getOutboundMessageContext()));
    }
    
    @Test
    public void testInboundSelf() {
        MessageContextEntityIDLookup lookup  = new MessageContextEntityIDLookup(SAMLSelfEntityContext.class);
        lookup.setDirection(Direction.INBOUND);
        Assert.assertEquals("inbound-self", lookup.apply(opContext.getInboundMessageContext()));
        Assert.assertEquals("inbound-self", lookup.apply(opContext.getOutboundMessageContext()));
    }
    
    @Test
    public void testOutboundPeer() {
        MessageContextEntityIDLookup lookup  = new MessageContextEntityIDLookup(SAMLPeerEntityContext.class);
        lookup.setDirection(Direction.OUTBOUND);
        Assert.assertEquals("outbound-peer", lookup.apply(opContext.getInboundMessageContext()));
        Assert.assertEquals("outbound-peer", lookup.apply(opContext.getOutboundMessageContext()));
    }
    
    @Test
    public void testOutboundSelf() {
        MessageContextEntityIDLookup lookup  = new MessageContextEntityIDLookup(SAMLSelfEntityContext.class);
        lookup.setDirection(Direction.OUTBOUND);
        Assert.assertEquals("outbound-self", lookup.apply(opContext.getInboundMessageContext()));
        Assert.assertEquals("outbound-self", lookup.apply(opContext.getOutboundMessageContext()));
    }
    
    @Test
    public void testNoParentOpContext() {
        MessageContextEntityIDLookup lookup  = new MessageContextEntityIDLookup(SAMLPeerEntityContext.class);
        Assert.assertNull(lookup.apply(new MessageContext()));
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void testNoDirection() {
        MessageContextEntityIDLookup lookup  = new MessageContextEntityIDLookup(SAMLPeerEntityContext.class);
        Assert.assertEquals("outbound-peer", lookup.apply(opContext.getInboundMessageContext()));
    }
    

}
