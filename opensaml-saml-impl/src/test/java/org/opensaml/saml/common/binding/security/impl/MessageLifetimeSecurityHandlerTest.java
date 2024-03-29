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

package org.opensaml.saml.common.binding.security.impl;

import java.time.Duration;
import java.time.Instant;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.SAMLMessageInfoContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Testing SAML issue instant security policy rule.
 */
@SuppressWarnings({"null", "javadoc"})
public class MessageLifetimeSecurityHandlerTest extends XMLObjectBaseTestCase {
    
    private MessageContext messageContext;
    
    private MessageLifetimeSecurityHandler handler;
    
    private Duration clockSkew;
    private Duration messageLifetime;
    
    private Instant now;

    @BeforeMethod
    protected void setUp() throws Exception {
        now = Instant.now();
        clockSkew = Duration.ofMinutes(5);
        messageLifetime = Duration.ofMinutes(10);
        
        messageContext = new MessageContext();
        
        messageContext.ensureSubcontext(SAMLMessageInfoContext.class).setMessageIssueInstant(now);
        
        handler = new MessageLifetimeSecurityHandler();
        handler.setClockSkew(clockSkew);
        handler.setMessageLifetime(messageLifetime);
        handler.initialize();
    }
    
    /**
     * Test valid issue instant.
     *  
     * @throws MessageHandlerException ...
     */
    @Test
    public void testValid() throws MessageHandlerException {
        handler.invoke(messageContext);
    }
    
    /**
     * Test invalid when issued in future, beyond allowed clock skew.
     * 
     * @throws MessageHandlerException ...
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testInvalidIssuedInFuture() throws MessageHandlerException {
        messageContext.ensureSubcontext(SAMLMessageInfoContext.class).setMessageIssueInstant(now.plus(clockSkew).plusSeconds(5));
        handler.invoke(messageContext);
    }
    
    /**
     * Test valid when issued in future, but within allowed clock skew.
     * 
     * @throws MessageHandlerException ...
     */
    @Test
    public void testValidIssuedInFutureWithinClockSkew() throws MessageHandlerException {
        messageContext.ensureSubcontext(SAMLMessageInfoContext.class).setMessageIssueInstant(now.plus(clockSkew).minusSeconds(5));
        handler.invoke(messageContext);
    }
    
    /**
     * Test invalid when expired, beyond allowed clock skew.
     * 
     * @throws MessageHandlerException ...
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testInvalidExpired() throws MessageHandlerException {
        messageContext.ensureSubcontext(SAMLMessageInfoContext.class).setMessageIssueInstant(now.minus(messageLifetime.plus(clockSkew).plusSeconds(5)));
        handler.invoke(messageContext);
    }
    
    /**
     * Test valid when expired, but within allowed clock skew.
     * 
     * @throws MessageHandlerException ...
     */
    @Test
    public void testValidExpiredWithinClockSkew() throws MessageHandlerException {
        messageContext.ensureSubcontext(SAMLMessageInfoContext.class).setMessageIssueInstant(now.minus(messageLifetime.plus(clockSkew).minusSeconds(5)));
        handler.invoke(messageContext);
    }
 
}