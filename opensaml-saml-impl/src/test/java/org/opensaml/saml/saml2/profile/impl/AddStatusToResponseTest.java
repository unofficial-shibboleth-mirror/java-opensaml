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

package org.opensaml.saml.saml2.profile.impl;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusMessage;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;

/** {@link AddStatusToResponse} unit test. */
@SuppressWarnings("javadoc")
public class AddStatusToResponseTest extends OpenSAMLInitBaseTestCase {
    
    private ProfileRequestContext prc;
    
    private AddStatusToResponse action;
    
    @BeforeMethod public void setUp() {
        prc = new RequestContextBuilder().setOutboundMessage(
                SAML2ActionTestingSupport.buildResponse()).buildProfileRequestContext();
        action = new AddStatusToResponse();
    }

    @Test public void testMinimal() throws ComponentInitializationException {
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final Response response = (Response) prc.ensureOutboundMessageContext().ensureMessage();

        final Status status = response.getStatus();
        assert status != null;
        final StatusCode code = status.getStatusCode();
        assert code != null;
        Assert.assertEquals(code.getValue(), StatusCode.RESPONDER);
        Assert.assertNull(code.getStatusCode());
        
        Assert.assertNull(status.getStatusMessage());
    }

    @Test public void testMultiStatus() throws ComponentInitializationException {
        action.setStatusCodes(CollectionSupport.listOf(StatusCode.REQUESTER, StatusCode.REQUEST_VERSION_DEPRECATED));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final Response response = (Response) prc.ensureOutboundMessageContext().ensureMessage();

        final Status status = response.getStatus();
        assert status != null;
        StatusCode code = status.getStatusCode();
        assert code != null;
        
        Assert.assertEquals(code.getValue(), StatusCode.REQUESTER);
        
        code = code.getStatusCode();
        assert code != null;
        Assert.assertEquals(code.getValue(), StatusCode.REQUEST_VERSION_DEPRECATED);
        Assert.assertNull(code.getStatusCode());
        
        Assert.assertNull(status.getStatusMessage());
    }

    @Test public void testFixedMessage() throws ComponentInitializationException {
        action.setStatusMessage("Foo");
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final Response response = (Response) prc.ensureOutboundMessageContext().ensureMessage();

        final Status status = response.getStatus();
        assert status != null;
        final StatusMessage msg = status.getStatusMessage();
        assert msg != null;
        Assert.assertEquals(msg.getValue(), "Foo");
    }
    
 }