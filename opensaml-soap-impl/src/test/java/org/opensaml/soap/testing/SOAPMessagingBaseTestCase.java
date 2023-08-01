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

package org.opensaml.soap.testing;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.testng.annotations.BeforeMethod;

/**
 *
 */
public class SOAPMessagingBaseTestCase extends XMLObjectBaseTestCase {
    
    private MessageContext messageContext;
    
    private Envelope envelope;
    
    protected MessageContext getMessageContext() {
        return messageContext;
    }
    
    protected Envelope getEnvelope() {
        return envelope;
    }
    
    @BeforeMethod
    protected void setUpMessageContextAndEnvelope() {
        messageContext = new MessageContext();
        messageContext.setMessage(buildXMLObject(simpleXMLObjectQName));
        
        envelope = buildXMLObject(Envelope.DEFAULT_ELEMENT_NAME);
        final Body body = buildXMLObject(Body.DEFAULT_ELEMENT_NAME);
        envelope.setBody(body);
        body.getUnknownXMLObjects().add((XMLObject) messageContext.getMessage());
        messageContext.ensureSubcontext(SOAP11Context.class).setEnvelope(envelope);
    }

}