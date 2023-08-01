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

package org.opensaml.soap.soap11.profile.impl;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.FunctionSupport;
import net.shibboleth.shared.logic.PredicateSupport;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Detail;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultActor;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.soap11.FaultString;
import org.opensaml.soap.util.SOAPSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** {@link AddSOAPFault} unit test. */
@SuppressWarnings("javadoc")
public class AddSOAPFaultTest extends OpenSAMLInitBaseTestCase {
    
    private ProfileRequestContext prc;
    
    private AddSOAPFault action;
    
    @BeforeMethod public void setUp() throws ComponentInitializationException {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        action = new AddSOAPFault();
    }

    @Test public void testMinimal() throws ComponentInitializationException {
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final MessageContext mc = prc.getOutboundMessageContext();
        assert mc != null;
        
        Assert.assertNull(mc.getMessage());
        
        final Fault fault = mc.ensureSubcontext(SOAP11Context.class).getFault();
        assert fault != null;
        
        final FaultCode fcode = fault.getCode();
        assert fcode != null;
        Assert.assertEquals(fcode.getValue(), FaultCode.SERVER);
        
        Assert.assertNull(fault.getMessage());
    }

    @Test public void testFixedMessage() throws ComponentInitializationException {
        action.setFaultString("Foo");
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        final MessageContext mc = prc.getOutboundMessageContext();
        assert mc != null;

        Assert.assertNull(mc.getMessage());
        
        final Fault fault = mc.ensureSubcontext(SOAP11Context.class).getFault();
        assert fault != null;
        
        final FaultCode fcode = fault.getCode();
        assert fcode != null;
        Assert.assertEquals(fcode.getValue(), FaultCode.SERVER);
        
        final FaultString fstring = fault.getMessage();
        assert fstring != null;
        Assert.assertEquals(fstring.getValue(), "Foo");
    }
    
    @Test public void testCodeAndStringViaLookupWithDetailedErrors() throws ComponentInitializationException {
        action.setFaultCodeLookupStrategy(FunctionSupport.constant(FaultCode.CLIENT));
        action.setFaultStringLookupStrategy(FunctionSupport.constant("TheClientError"));
        
        action.setDetailedErrorsCondition(PredicateSupport.alwaysTrue());
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        final MessageContext mc = prc.getOutboundMessageContext();
        assert mc != null;

        Assert.assertNull(mc.getMessage());
        
        final Fault fault = mc.ensureSubcontext(SOAP11Context.class).getFault();
        assert fault != null;
        
        final FaultCode fcode = fault.getCode();
        assert fcode != null;
        Assert.assertEquals(fcode.getValue(), FaultCode.CLIENT);
        
        final FaultString fstring = fault.getMessage();
        assert fstring != null;
        Assert.assertEquals(fstring.getValue(), "TheClientError");
        
        Assert.assertNull(fault.getActor());
        
        Assert.assertNull(fault.getDetail());
    }
    
    @Test public void testCodeAndStringViaLookupWithoutDetailedErrors() throws ComponentInitializationException {
        action.setFaultCodeLookupStrategy(FunctionSupport.constant(FaultCode.CLIENT));
        action.setFaultStringLookupStrategy(FunctionSupport.constant("TheClientError"));
        
        action.setDetailedErrorsCondition(PredicateSupport.alwaysFalse());
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final MessageContext mc = prc.getOutboundMessageContext();
        assert mc != null;
        
        Assert.assertNull(mc.getMessage());
        
        final Fault fault = mc.ensureSubcontext(SOAP11Context.class).getFault();
        assert fault != null;
        
        final FaultCode fcode = fault.getCode();
        assert fcode != null;
        Assert.assertEquals(fcode.getValue(), FaultCode.CLIENT);
        
        Assert.assertNull(fault.getMessage());
        
        Assert.assertNull(fault.getActor());
        
        Assert.assertNull(fault.getDetail());
    }
    
    @Test public void testContextFaultWithDetailedErrors() throws ComponentInitializationException {
        final Fault contextFault = SOAPSupport.buildSOAP11Fault(FaultCode.CLIENT, "TheClientError", "TheFaultActor", 
                CollectionSupport.singletonList(XMLObjectSupport.buildXMLObject(SimpleXMLObject.ELEMENT_NAME)), null);
        
        action.setContextFaultStrategy(FunctionSupport.constant(contextFault));
        
        action.setDetailedErrorsCondition(PredicateSupport.alwaysTrue());
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final MessageContext mc = prc.getOutboundMessageContext();
        assert mc != null;
        Assert.assertNull(mc.getMessage());
        
        final Fault fault = mc.ensureSubcontext(SOAP11Context.class).getFault();
        assert fault != null;
        
        final FaultCode fcode = fault.getCode();
        assert fcode != null;
        Assert.assertEquals(fcode.getValue(), FaultCode.CLIENT);
        
        final FaultString fstring = fault.getMessage();
        assert fstring != null;
        Assert.assertEquals(fstring.getValue(), "TheClientError");
        
        final FaultActor actor = fault.getActor();
        assert actor != null;
        Assert.assertEquals(actor.getURI(), "TheFaultActor");
        
        final Detail detail = fault.getDetail();
        assert detail != null;
        Assert.assertEquals(detail.getUnknownXMLObjects().size(), 1);
    }
    
    @Test public void testContextFaultWithoutDetailedErrors() throws ComponentInitializationException {
        final Fault contextFault = SOAPSupport.buildSOAP11Fault(FaultCode.CLIENT, "TheClientError", "TheFaultActor", 
                CollectionSupport.singletonList(XMLObjectSupport.buildXMLObject(SimpleXMLObject.ELEMENT_NAME)), null);
        
        action.setContextFaultStrategy(FunctionSupport.constant(contextFault));
        action.setDetailedErrorsCondition(PredicateSupport.alwaysFalse());
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final MessageContext mc = prc.getOutboundMessageContext();
        assert mc != null;
        Assert.assertNull(mc.getMessage());
        
        final Fault fault = mc.ensureSubcontext(SOAP11Context.class).getFault();
        assert fault != null;
        
        final FaultCode fcode = fault.getCode();
        assert fcode != null;
        Assert.assertEquals(fcode.getValue(), FaultCode.CLIENT);
        
        Assert.assertNull(fault.getMessage());
        
        Assert.assertNull(fault.getActor());
        
        Assert.assertNull(fault.getDetail());
    }
    
    @Test public void testDefaultContextFaultStrategyFromOutbound() throws ComponentInitializationException {
        final Fault contextFault = SOAPSupport.buildSOAP11Fault(FaultCode.CLIENT, "TheClientError", "TheFaultActor", 
                CollectionSupport.singletonList(XMLObjectSupport.buildXMLObject(SimpleXMLObject.ELEMENT_NAME)), null);
        
        final MessageContext inbound = prc.getInboundMessageContext();
        assert inbound != null;
        inbound.ensureSubcontext(SOAP11Context.class).setFault(contextFault);
        
        action.setDetailedErrorsCondition(PredicateSupport.alwaysFalse());
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final MessageContext mc = prc.getOutboundMessageContext();
        assert mc != null;
        Assert.assertNull(mc.getMessage());
        
        final Fault fault = mc.ensureSubcontext(SOAP11Context.class).getFault();
        assert fault != null;
        
        final FaultCode fcode = fault.getCode();
        assert fcode != null;
        Assert.assertEquals(fcode.getValue(), FaultCode.CLIENT);
        
        Assert.assertNull(fault.getMessage());
        
        Assert.assertNull(fault.getActor());
        
        Assert.assertNull(fault.getDetail());
    }
    
    @Test public void testDefaultContextFaultStrategyFromInbound() throws ComponentInitializationException {
        final Fault contextFault = SOAPSupport.buildSOAP11Fault(FaultCode.CLIENT, "TheClientError", "TheFaultActor", 
                CollectionSupport.singletonList(XMLObjectSupport.buildXMLObject(SimpleXMLObject.ELEMENT_NAME)), null);
        
        final MessageContext inbound = prc.getInboundMessageContext();
        assert inbound != null;
        inbound.ensureSubcontext(SOAP11Context.class).setFault(contextFault);
        
        action.setDetailedErrorsCondition(PredicateSupport.alwaysFalse());
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final MessageContext mc = prc.getOutboundMessageContext();
        assert mc != null;
        Assert.assertNull(mc.getMessage());
        
        final Fault fault = mc.ensureSubcontext(SOAP11Context.class).getFault();
        assert fault != null;
        
        final FaultCode fcode = fault.getCode();
        assert fcode != null;
        Assert.assertEquals(fcode.getValue(), FaultCode.CLIENT);
        
        Assert.assertNull(fault.getMessage());
        
        Assert.assertNull(fault.getActor());
        
        Assert.assertNull(fault.getDetail());
    }
    
 }