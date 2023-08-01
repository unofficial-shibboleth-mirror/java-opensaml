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

package org.opensaml.saml.saml2.profile.impl;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.ArtifactResponse;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/** {@link AddStatusResponseShell} unit test. */
@SuppressWarnings("javadoc")
public class AddStatusResponseShellTest extends OpenSAMLInitBaseTestCase {

    private String issuer;
    
    private AddStatusResponseShell action;

    @BeforeMethod public void setUp() {

        issuer = null;

        action = new AddStatusResponseShell();
        action.setIssuerLookupStrategy(prc -> issuer);
    }

    @Test public void testAddResponse() throws ComponentInitializationException {
        final ProfileRequestContext prc = new RequestContextBuilder().buildProfileRequestContext();
        
        action.setMessageType(Response.DEFAULT_ELEMENT_NAME);
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        final Response response = (Response) prc.ensureOutboundMessageContext().ensureMessage();

        Assert.assertNotNull(response.getID());
        Assert.assertNotNull(response.getIssueInstant());
        Assert.assertEquals(response.getVersion(), SAMLVersion.VERSION_20);
        
        Assert.assertNull(response.getIssuer());

        final Status status = response.getStatus();
        assert status != null;
        final StatusCode code = status.getStatusCode();
        assert code != null;
        Assert.assertEquals(code.getValue(), StatusCode.SUCCESS);
    }

    @Test public void testAddResponseWithIssuer() throws ComponentInitializationException {
        final ProfileRequestContext prc = new RequestContextBuilder().buildProfileRequestContext();

        issuer = "foo";

        action.setMessageType(Response.DEFAULT_ELEMENT_NAME);
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        final Response response = (Response) prc.ensureOutboundMessageContext().ensureMessage();
        
        final Issuer issuer = response.getIssuer();
        assert issuer != null;
        Assert.assertEquals(issuer.getValue(), "foo");
    }
    
    @Test public void testAddResponseWhenResponseAlreadyExist() throws ComponentInitializationException {
        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(
                SAML2ActionTestingSupport.buildResponse()).buildProfileRequestContext();

        action.setMessageType(Response.DEFAULT_ELEMENT_NAME);
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test public void testAddArtifactResponse() throws ComponentInitializationException {
        final ProfileRequestContext prc = new RequestContextBuilder().buildProfileRequestContext();
        
        action.setMessageType(ArtifactResponse.DEFAULT_ELEMENT_NAME);
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        final ArtifactResponse response = (ArtifactResponse) prc.ensureOutboundMessageContext().ensureMessage();

        Assert.assertNotNull(response.getID());
        Assert.assertNotNull(response.getIssueInstant());
        Assert.assertEquals(response.getVersion(), SAMLVersion.VERSION_20);
        
        Assert.assertNull(response.getIssuer());

        final Status status = response.getStatus();
        assert status != null;
        final StatusCode code = status.getStatusCode();
        assert code != null;
        Assert.assertEquals(code.getValue(), StatusCode.SUCCESS);
    }
    
    @Test public void testAddLogoutResponse() throws ComponentInitializationException {
        final ProfileRequestContext prc = new RequestContextBuilder().buildProfileRequestContext();
        
        action.setMessageType(LogoutResponse.DEFAULT_ELEMENT_NAME);
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        final LogoutResponse response = (LogoutResponse) prc.ensureOutboundMessageContext().ensureMessage();

        Assert.assertNotNull(response.getID());
        Assert.assertNotNull(response.getIssueInstant());
        Assert.assertEquals(response.getVersion(), SAMLVersion.VERSION_20);
        
        Assert.assertNull(response.getIssuer());

        final Status status = response.getStatus();
        assert status != null;
        final StatusCode code = status.getStatusCode();
        assert code != null;
        Assert.assertEquals(code.getValue(), StatusCode.SUCCESS);
    }

}