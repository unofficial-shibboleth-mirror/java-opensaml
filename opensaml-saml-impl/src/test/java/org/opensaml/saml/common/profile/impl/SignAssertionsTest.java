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

package org.opensaml.saml.common.profile.impl;

import java.security.KeyPair;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.saml1.testing.SAML1ActionTestingSupport;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/** {@link SignAssertions} unit test. */
@SuppressWarnings({"null", "javadoc"})
public class SignAssertionsTest extends OpenSAMLInitBaseTestCase {

    private SignAssertions action;

    private ProfileRequestContext prc;

    @BeforeMethod public void setUp() throws ComponentInitializationException {

        action = new SignAssertions();
        action.initialize();

        prc = new RequestContextBuilder().setOutboundMessage(
                SAML2ActionTestingSupport.buildResponse()).buildProfileRequestContext();
    }

    @Test public void testNoOutboundMessageContext() throws Exception {
        prc.setOutboundMessageContext(null);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test public void testNoMessage() throws Exception {
        prc.ensureOutboundMessageContext().setMessage(null);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test public void testBadMessage() throws Exception {
        prc.ensureOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildAttributeQueryRequest(null));
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }
    
    @Test public void testNoSecurityParametersContext() throws Exception {
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    @Test public void testNoSignatureSigningParameters() throws Exception {
        prc.addSubcontext(new SecurityParametersContext());
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    @Test public void testNoAssertions() throws Exception {
        final SecurityParametersContext secParamCtx = new SecurityParametersContext();
        secParamCtx.setSignatureSigningParameters(new SignatureSigningParameters());
        prc.addSubcontext(secParamCtx);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    @Test public void testSignSAML1Assertions() throws Exception {
        prc.ensureOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildResponse());
        final var assertion = SAML1ActionTestingSupport.buildAssertion();
        ((org.opensaml.saml.saml1.core.Response) prc.ensureOutboundMessageContext().ensureMessage()).getAssertions().add(assertion);

        final SignatureSigningParameters signingParameters = new SignatureSigningParameters();
        final KeyPair kp = KeySupport.generateKeyPair("RSA", 1024, null);
        signingParameters.setSigningCredential(CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate()));
        signingParameters.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signingParameters.setSignatureReferenceDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        signingParameters.setSignatureCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

        final SecurityParametersContext secParamCtx = new SecurityParametersContext();
        secParamCtx.setSignatureSigningParameters(signingParameters);
        prc.addSubcontext(secParamCtx);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }
    
    @Test public void testSignSAML2Assertions() throws Exception {
        final var assertion = SAML2ActionTestingSupport.buildAssertion();
        ((org.opensaml.saml.saml2.core.Response) prc.ensureOutboundMessageContext().ensureMessage()).getAssertions().add(assertion);

        final SignatureSigningParameters signingParameters = new SignatureSigningParameters();
        final KeyPair kp = KeySupport.generateKeyPair("RSA", 1024, null);
        signingParameters.setSigningCredential(CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate()));
        signingParameters.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signingParameters.setSignatureReferenceDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        signingParameters.setSignatureCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

        final SecurityParametersContext secParamCtx = new SecurityParametersContext();
        secParamCtx.setSignatureSigningParameters(signingParameters);
        prc.addSubcontext(secParamCtx);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    // TODO Test that assertion was signed correctly ?

    // TODO Test event id when signing throws an exception.
}