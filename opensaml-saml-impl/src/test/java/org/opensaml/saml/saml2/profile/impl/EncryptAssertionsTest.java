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

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.profile.context.EncryptionContext;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Strings;

/** Unit test for {@link EncryptAssertions}. */
public class EncryptAssertionsTest extends OpenSAMLInitBaseTestCase {
    
    private EncryptionParameters encParams;
    
    private ProfileRequestContext prc;
    
    private EncryptAssertions action;
    
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
        prc.getOutboundMessageContext().getSubcontext(EncryptionContext.class, true).setAssertionEncryptionParameters(encParams);
        
        action = new EncryptAssertions();
    }
    
    @Test
    public void testEmptyMessage() throws ComponentInitializationException {
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        prc.getOutboundMessageContext().setMessage(SAML2ActionTestingSupport.buildResponse());
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }
        
    @Test
    public void testEncryptedAssertion() throws EncryptionException, ComponentInitializationException, MarshallingException {
        final Response response = SAML2ActionTestingSupport.buildResponse();
        prc.getOutboundMessageContext().setMessage(response);
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assert.assertEquals(response.getAssertions().size(), 0);
        Assert.assertEquals(response.getEncryptedAssertions().size(), 2);
        
        final EncryptedAssertion encTarget = response.getEncryptedAssertions().get(0);

        Assert.assertEquals(encTarget.getEncryptedData().getType(), EncryptionConstants.TYPE_ELEMENT, "Type attribute");
        Assert.assertEquals(encTarget.getEncryptedData().getEncryptionMethod().getAlgorithm(),
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, "Algorithm attribute");
        Assert.assertNotNull(encTarget.getEncryptedData().getKeyInfo(), "KeyInfo");
        Assert.assertEquals(encTarget.getEncryptedData().getKeyInfo().getEncryptedKeys().size(), 1, 
                "Number of EncryptedKeys");
        Assert.assertFalse(Strings.isNullOrEmpty(encTarget.getEncryptedData().getID()),
                "EncryptedData ID attribute was empty");
    }
    
    @Test
    public void testFailure() throws EncryptionException, ComponentInitializationException, MarshallingException {
        final Response response = SAML2ActionTestingSupport.buildResponse();
        prc.getOutboundMessageContext().setMessage(response);
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        action.initialize();
        
        encParams.setKeyTransportEncryptionCredential(null);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.UNABLE_TO_ENCRYPT);
        
        Assert.assertEquals(response.getAssertions().size(), 2);
        Assert.assertEquals(response.getEncryptedAssertions().size(), 0);
    }
    
}