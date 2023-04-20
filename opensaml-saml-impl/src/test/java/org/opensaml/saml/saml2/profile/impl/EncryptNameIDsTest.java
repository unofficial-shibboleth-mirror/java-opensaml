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

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.profile.context.EncryptionContext;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Strings;

import net.shibboleth.shared.component.ComponentInitializationException;

/** Unit test for {@link EncryptNameIDs}. */
@SuppressWarnings({"null", "javadoc"})
public class EncryptNameIDsTest extends OpenSAMLInitBaseTestCase {
    
    private EncryptionParameters encParams;
    
    private ProfileRequestContext prc;
    
    private EncryptNameIDs action;
    
    @BeforeMethod
    public void setUp() throws NoSuchAlgorithmException, NoSuchProviderException {
        
        final BasicKeyInfoGeneratorFactory generator = new BasicKeyInfoGeneratorFactory();
        generator.setEmitPublicKeyValue(true);
        
        encParams = new EncryptionParameters();
        encParams.setDataEncryptionAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        encParams.setDataKeyInfoGenerator(generator.newInstance());
        encParams.setKeyTransportEncryptionAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        encParams.setKeyTransportEncryptionCredential(
                AlgorithmSupport.generateKeyPairAndCredential(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, 1024, false));
        encParams.setKeyTransportKeyInfoGenerator(generator.newInstance());
        
        prc = new RequestContextBuilder().buildProfileRequestContext();
        prc.ensureOutboundMessageContext().ensureSubcontext(EncryptionContext.class).setIdentifierEncryptionParameters(encParams);
        
        action = new EncryptNameIDs();
    }
    
    @Test
    public void testEmptyMessage() throws ComponentInitializationException {
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        prc.ensureOutboundMessageContext().setMessage(SAML2ActionTestingSupport.buildResponse());
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }
        
    @Test
    public void testEncryptedNameID() throws EncryptionException, ComponentInitializationException, MarshallingException {
        final Response response = SAML2ActionTestingSupport.buildResponse();
        prc.ensureOutboundMessageContext().setMessage(response);
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        response.getAssertions().get(0).setSubject(SAML2ActionTestingSupport.buildSubject("morpheus"));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assert.assertEquals(response.getAssertions().size(), 1);
        final Assertion assertion = response.getAssertions().get(0);
        assert assertion != null;
        final Subject subject = assertion.getSubject();
        assert subject != null;
        Assert.assertNull(subject.getNameID());
        Assert.assertNotNull(subject.getEncryptedID());
        
        final EncryptedID encTarget = subject.getEncryptedID();
        assert encTarget != null;

        final EncryptedData encData = encTarget.getEncryptedData();
        assert encData != null;
        Assert.assertEquals(encData.getType(), EncryptionConstants.TYPE_ELEMENT, "Type attribute");
        final EncryptionMethod method = encData.getEncryptionMethod();
        assert method != null;
        Assert.assertEquals(method.getAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, "Algorithm attribute");
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertEquals(keyInfo.getEncryptedKeys().size(), 1, "Number of EncryptedKeys");
        Assert.assertFalse(Strings.isNullOrEmpty(encData.getID()), "EncryptedData ID attribute was empty");
    }
    
    @Test
    public void testFailure() throws EncryptionException, ComponentInitializationException, MarshallingException {
        final Response response = SAML2ActionTestingSupport.buildResponse();
        prc.ensureOutboundMessageContext().setMessage(response);
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        response.getAssertions().get(0).setSubject(SAML2ActionTestingSupport.buildSubject("morpheus"));
        
        action.initialize();
        
        encParams.setKeyTransportEncryptionCredential(null);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.UNABLE_TO_ENCRYPT);
        
        Assert.assertEquals(response.getAssertions().size(), 1);
        final Assertion assertion = response.getAssertions().get(0);
        assert assertion != null;
        final Subject subject = assertion.getSubject();
        assert subject != null;
        Assert.assertNotNull(subject.getNameID());
        Assert.assertNull(subject.getEncryptedID());
    }
    
}