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

package org.opensaml.saml.saml1.profile.impl;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Status;
import org.opensaml.saml.saml1.core.StatusCode;
import org.opensaml.saml.saml1.testing.SAML1ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/** {@link AddResponseShell} unit test. */
@SuppressWarnings("javadoc")
public class AddResponseShellTest extends OpenSAMLInitBaseTestCase {

    private AddResponseShell action;

    @BeforeMethod public void setUp() throws ComponentInitializationException {
        action = new AddResponseShell();
        action.initialize();
    }

    @Test public void testAddResponse() {
        final ProfileRequestContext prc = new RequestContextBuilder().buildProfileRequestContext();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        final Response response = prc.ensureOutboundMessageContext().ensureMessage(Response.class);

        Assert.assertNotNull(response.getID());
        Assert.assertNotNull(response.getIssueInstant());
        Assert.assertEquals(response.getVersion(), SAMLVersion.VERSION_11);

        final Status status = response.getStatus();
        assert status != null;
        final StatusCode code = status.getStatusCode();
        assert code != null;
        Assert.assertEquals(code.getValue(), StatusCode.SUCCESS);
    }

    @Test public void testAddResponseWhenResponseAlreadyExist() {
        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(
                SAML1ActionTestingSupport.buildResponse()).buildProfileRequestContext();

        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

}