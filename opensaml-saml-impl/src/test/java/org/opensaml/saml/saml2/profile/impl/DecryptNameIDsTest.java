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

import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

import org.testng.annotations.BeforeMethod;
import org.testng.Assert;

import java.security.KeyException;
import java.security.NoSuchAlgorithmException;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;

/** Unit test for {@link DecryptNameIDs}. */
@SuppressWarnings({"null", "javadoc"})
public class DecryptNameIDsTest extends OpenSAMLInitBaseTestCase {
    
    private KeyInfoCredentialResolver keyResolver;
    
    private String encURI;
    
    private DataEncryptionParameters encParams;
    
    private Encrypter encrypter;
    
    private ProfileRequestContext prc;
    
    private DecryptNameIDs action;
    
    private SAMLObjectBuilder<NameID> nameIdBuilder;

    private SAMLObjectBuilder<Subject> subjectBuilder;
    
    /**
     * Test set up.
     * 
     * @throws NoSuchAlgorithmException
     * @throws KeyException
     */
    @BeforeMethod
    public void setUp() throws NoSuchAlgorithmException, KeyException {
        encURI = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
        nameIdBuilder = (SAMLObjectBuilder<NameID>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<NameID>ensureBuilder(
                        NameID.DEFAULT_ELEMENT_NAME);
        subjectBuilder = (SAMLObjectBuilder<Subject>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Subject>ensureBuilder(
                        Subject.DEFAULT_ELEMENT_NAME);

        final Credential encCred = AlgorithmSupport.generateSymmetricKeyAndCredential(encURI);
        keyResolver = new StaticKeyInfoCredentialResolver(encCred);
        encParams = new DataEncryptionParameters();
        encParams.setAlgorithm(encURI);
        encParams.setEncryptionCredential(encCred);
        
        encrypter = new Encrypter(encParams);
        
        final DecryptionParameters decParams = new DecryptionParameters();
        decParams.setDataKeyInfoCredentialResolver(keyResolver);
        
        prc = new RequestContextBuilder().buildProfileRequestContext();
        prc.ensureInboundMessageContext().ensureSubcontext(
                SecurityParametersContext.class).setDecryptionParameters(decParams);
        
        action = new DecryptNameIDs();
    }
    
    /**
     * Test with no message.
     * 
     * @throws ComponentInitializationException
     */
    @Test
    public void testNoMessage() throws ComponentInitializationException {
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }
    
    /**
     * Test decryption of an NameID as an EncryptedID.
     *  
     * @throws EncryptionException ...
     * @throws ComponentInitializationException ...
     */
    @Test
    public void testEncryptedNameIDNoParams() throws EncryptionException, ComponentInitializationException {
        final AuthnRequest authnRequest = SAML2ActionTestingSupport.buildAuthnRequest();
        prc.ensureInboundMessageContext().setMessage(authnRequest);
        Subject subject = subjectBuilder.buildObject();
        authnRequest.setSubject(subject);
        
        final NameID nameId = nameIdBuilder.buildObject();
        nameId.setFormat(NameID.TRANSIENT);
        nameId.setValue("foo");
        
        final EncryptedID encryptedTarget = encrypter.encrypt(nameId);
        subject.setEncryptedID(encryptedTarget);

        action.initialize();
        
        prc.ensureInboundMessageContext().removeSubcontext(SecurityParametersContext.class);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.DECRYPT_NAMEID_FAILED);
        
        subject = ((AuthnRequest) prc.ensureInboundMessageContext().ensureMessage()).getSubject();
        assert subject != null;
        Assert.assertNull(subject.getNameID());
        
        action = new DecryptNameIDs();
        action.setErrorFatal(false);
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        subject = ((AuthnRequest) prc.ensureInboundMessageContext().ensureMessage()).getSubject();
        assert subject != null;
        Assert.assertNull(subject.getNameID());
    }

    
    /**
     * Test decryption of an NameID as an EncryptedID.
     *  
     * @throws EncryptionException ...
     * @throws ComponentInitializationException ...
     */
    @Test
    public void testEncryptedNameID() throws EncryptionException, ComponentInitializationException {
        final AuthnRequest authnRequest = SAML2ActionTestingSupport.buildAuthnRequest();
        prc.ensureInboundMessageContext().setMessage(authnRequest);
        Subject subject = subjectBuilder.buildObject();
        authnRequest.setSubject(subject);
        
        NameID nameId = nameIdBuilder.buildObject();
        nameId.setFormat(NameID.TRANSIENT);
        nameId.setValue("foo");
        
        final EncryptedID encryptedTarget = encrypter.encrypt(nameId);
        subject.setEncryptedID(encryptedTarget);

        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        subject = ((AuthnRequest) prc.ensureInboundMessageContext().ensureMessage()).getSubject();
        assert subject != null;
        nameId = subject.getNameID();
        assert nameId != null;
        Assert.assertEquals(nameId.getValue(), "foo");
        Assert.assertEquals(nameId.getFormat(), NameID.TRANSIENT);
    }

    /**
     * Test failed decryption of an NameID as an EncryptedID.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testWrongKeyFatal() throws Exception {
        final AuthnRequest authnRequest = SAML2ActionTestingSupport.buildAuthnRequest();
        prc.ensureInboundMessageContext().setMessage(authnRequest);
        Subject subject = subjectBuilder.buildObject();
        authnRequest.setSubject(subject);
        
        final NameID nameId = nameIdBuilder.buildObject();
        nameId.setFormat(NameID.TRANSIENT);
        nameId.setValue("foo");
        
        final EncryptedID encryptedTarget = encrypter.encrypt(nameId);
        subject.setEncryptedID(encryptedTarget);

        Credential encCred = AlgorithmSupport.generateSymmetricKeyAndCredential(encURI);
        KeyInfoCredentialResolver badKeyResolver = new StaticKeyInfoCredentialResolver(encCred);
        final DecryptionParameters params = prc.ensureInboundMessageContext().ensureSubcontext(
                SecurityParametersContext.class).getDecryptionParameters();
        assert params != null;
        params.setDataKeyInfoCredentialResolver(badKeyResolver);
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.DECRYPT_NAMEID_FAILED);
        
        subject = ((AuthnRequest) prc.ensureInboundMessageContext().ensureMessage()).getSubject();
        assert subject != null;
        Assert.assertNull(subject.getNameID());
    }

    /**
     * Test failed decryption of an NameID as an EncryptedID.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testWrongKeyNonFatal() throws Exception {
        final AuthnRequest authnRequest = SAML2ActionTestingSupport.buildAuthnRequest();
        prc.ensureInboundMessageContext().setMessage(authnRequest);
        Subject subject = subjectBuilder.buildObject();
        authnRequest.setSubject(subject);
        
        final NameID nameId = nameIdBuilder.buildObject();
        nameId.setFormat(NameID.TRANSIENT);
        nameId.setValue("foo");
        
        final EncryptedID encryptedTarget = encrypter.encrypt(nameId);
        subject.setEncryptedID(encryptedTarget);

        Credential encCred = AlgorithmSupport.generateSymmetricKeyAndCredential(encURI);
        KeyInfoCredentialResolver badKeyResolver = new StaticKeyInfoCredentialResolver(encCred);
        final DecryptionParameters params = prc.ensureInboundMessageContext().ensureSubcontext(
                SecurityParametersContext.class).getDecryptionParameters();
        assert params != null;
        params.setDataKeyInfoCredentialResolver(badKeyResolver);
        
        action.setErrorFatal(false);
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        subject = ((AuthnRequest) prc.ensureInboundMessageContext().ensureMessage()).getSubject();
        assert subject != null;
        Assert.assertNull(subject.getNameID());
    }
    
}
