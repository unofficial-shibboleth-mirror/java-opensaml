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

package org.opensaml.saml.common.binding.impl;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Function;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataLookupParametersContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLPresenterEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.metadata.criteria.entity.DetectDuplicateEntityIDsCriterion;
import org.opensaml.saml.metadata.resolver.DetectDuplicateEntityIDs;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.PredicateRoleDescriptorResolver;
import org.opensaml.saml.saml1.core.AttributeQuery;
import org.opensaml.saml.saml1.core.Request;
import org.opensaml.saml.saml1.testing.SAML1ActionTestingSupport;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/**
 * Test for {@link SAMLMetadataLookupHandler}.
 */
@SuppressWarnings({"null", "javadoc"})
public class SAMLMetadataLookupHandlerTest extends XMLObjectBaseTestCase {

    private PredicateRoleDescriptorResolver roleResolver;
    private SAMLMetadataLookupHandler handler;
    private MessageContext messageContext;
    
    private SAMLMetadataContext existingMetadataContext;
    private EntityDescriptor existingEntityDescriptor;
    private RoleDescriptor existingRoleDescriptor;
    private Function<MessageContext, SAMLMetadataContext> copyContextStrategy;

    @BeforeClass
    public void classSetUp() throws ResolverException, URISyntaxException, ComponentInitializationException {
        final URL mdURL = SAMLMetadataLookupHandlerTest.class
                .getResource("/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        final File mdFile = new File(mdURL.toURI());

        final FilesystemMetadataResolver metadataProvider = new FilesystemMetadataResolver(mdFile);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");
        metadataProvider.initialize();
        
        roleResolver = new PredicateRoleDescriptorResolver(metadataProvider);
        roleResolver.initialize();
    }
    
    @BeforeMethod
    public void setUp() {
        handler = new SAMLMetadataLookupHandler();
        messageContext = new MessageContext();

        existingEntityDescriptor = buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        existingEntityDescriptor.setEntityID("urn:mace:incommon:osu.edu");
        existingRoleDescriptor = buildXMLObject(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);

        existingMetadataContext = new SAMLMetadataContext();
        existingMetadataContext.setEntityDescriptor(existingEntityDescriptor);
        existingMetadataContext.setRoleDescriptor(existingRoleDescriptor);

        copyContextStrategy = mc -> { return existingMetadataContext; };
    }
    
    @Test
    public void testConfigFailure() {
        try {
            handler.initialize();
            Assert.fail();
        } catch (ComponentInitializationException e) {
            
        }
    }
    
    @Test
    public void testMissingContexts() throws ComponentInitializationException, MessageHandlerException {
        handler.setRoleDescriptorResolver(roleResolver);
        handler.initialize();
        
        handler.invoke(messageContext);
        Assert.assertNull(messageContext.getSubcontext(SAMLPeerEntityContext.class));
        
        final SAMLPeerEntityContext peerContext = messageContext.ensureSubcontext(SAMLPeerEntityContext.class);
        peerContext.setRole(org.opensaml.saml.saml2.metadata.SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        
        handler.invoke(messageContext);
        Assert.assertNull(messageContext.getSubcontext(SAMLMetadataContext.class));
    }
    
    @Test
    public void testNotFound() throws ComponentInitializationException, MessageHandlerException {
        handler.setRoleDescriptorResolver(roleResolver);
        handler.initialize();
        
        final SAMLPeerEntityContext peerContext = messageContext.ensureSubcontext(SAMLPeerEntityContext.class);
        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        
        final Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        final AttributeQuery query = request.getAttributeQuery();
        assert query != null;
        query.setResource("urn:notfound");
        messageContext.setMessage(request);

        handler.invoke(messageContext);
        Assert.assertNull(peerContext.getSubcontext(SAMLMetadataContext.class));
    }
 
    @Test
    public void testBadRole() throws ComponentInitializationException, MessageHandlerException {
        handler.setRoleDescriptorResolver(roleResolver);
        handler.initialize();
        
        final SAMLPeerEntityContext peerContext = messageContext.ensureSubcontext(SAMLPeerEntityContext.class);
        peerContext.setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        
        final Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        final AttributeQuery query = request.getAttributeQuery();
        assert query != null;
        query.setResource("urn:mace:incommon:osu.edu");
        messageContext.setMessage(request);

        handler.invoke(messageContext);
        Assert.assertNull(peerContext.getSubcontext(SAMLMetadataContext.class));
    }

    @Test
    public void testBadProtocol() throws ComponentInitializationException, MessageHandlerException {
        handler.setRoleDescriptorResolver(roleResolver);
        handler.initialize();
        
        final SAMLPeerEntityContext peerContext = messageContext.ensureSubcontext(SAMLPeerEntityContext.class);
        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        messageContext.ensureSubcontext(SAMLProtocolContext.class).setProtocol("urn:foo");
        
        final Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        final AttributeQuery query = request.getAttributeQuery();
        assert query != null;
        query.setResource("urn:mace:incommon:osu.edu");
        messageContext.setMessage(request);

        handler.invoke(messageContext);
        Assert.assertNull(peerContext.getSubcontext(SAMLMetadataContext.class));
    }
    
    @Test
    public void testSuccess() throws ComponentInitializationException, MessageHandlerException {
        handler.setRoleDescriptorResolver(roleResolver);
        handler.initialize();
        
        final SAMLPeerEntityContext peerContext = messageContext.ensureSubcontext(SAMLPeerEntityContext.class);
        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        messageContext.ensureSubcontext(SAMLProtocolContext.class).setProtocol(SAMLConstants.SAML11P_NS);
        
        final Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        final AttributeQuery query = request.getAttributeQuery();
        assert query != null;
        query.setResource("urn:mace:incommon:osu.edu");
        messageContext.setMessage(request);

        handler.invoke(messageContext);
        
        final SAMLMetadataContext mdCtx = peerContext.getSubcontext(SAMLMetadataContext.class);
        assert mdCtx != null;
        Assert.assertNotNull(mdCtx.getRoleDescriptor());
        Assert.assertNotNull(mdCtx.getEntityDescriptor());
    }
    
    @Test
    public void testSuccessWithContextClass() throws ComponentInitializationException, MessageHandlerException {
        handler.setEntityContextClass(SAMLPresenterEntityContext.class);
        handler.setRoleDescriptorResolver(roleResolver);
        handler.initialize();
        
        final SAMLPresenterEntityContext presenterContext = messageContext.ensureSubcontext(SAMLPresenterEntityContext.class);
        presenterContext.setEntityId("https://carmenwiki.osu.edu/shibboleth");
        presenterContext.setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        messageContext.ensureSubcontext(SAMLProtocolContext.class).setProtocol(SAMLConstants.SAML20P_NS);
        
        final AuthnRequest authnRequest = SAML2ActionTestingSupport.buildAuthnRequest();
        final Issuer issuer = authnRequest.getIssuer();
        assert issuer != null;
        issuer.setValue("https://carmenwiki.osu.edu/shibboleth");
        messageContext.setMessage(authnRequest);

        handler.invoke(messageContext);
        
        final SAMLMetadataContext mdCtx = presenterContext.getSubcontext(SAMLMetadataContext.class);
        assert mdCtx != null;
        Assert.assertNotNull(mdCtx.getRoleDescriptor());
        Assert.assertNotNull(mdCtx.getEntityDescriptor());
    }
    
    @Test
    public void testCopySuccess() throws ComponentInitializationException, MessageHandlerException {
        handler.setRoleDescriptorResolver(roleResolver);
        handler.setCopyContextStrategy(copyContextStrategy);
        handler.initialize();

        final SAMLPeerEntityContext peerContext = messageContext.ensureSubcontext(SAMLPeerEntityContext.class);
        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        messageContext.ensureSubcontext(SAMLProtocolContext.class).setProtocol(SAMLConstants.SAML11P_NS);

        final Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        final AttributeQuery query = request.getAttributeQuery();
        assert query != null;
        query.setResource("urn:mace:incommon:osu.edu");
        messageContext.setMessage(request);

        handler.invoke(messageContext);

        final SAMLMetadataContext mdCtx = peerContext.getSubcontext(SAMLMetadataContext.class);
        assert mdCtx != null;
        Assert.assertNotSame(mdCtx, existingMetadataContext);
        Assert.assertNotNull(mdCtx.getRoleDescriptor());
        Assert.assertSame(mdCtx.getRoleDescriptor(), existingMetadataContext.getRoleDescriptor());
        Assert.assertNotNull(mdCtx.getEntityDescriptor());
        Assert.assertSame(mdCtx.getEntityDescriptor(), existingMetadataContext.getEntityDescriptor());
    }

    @Test
    public void testCopyFailMissingExistingEntityDescriptor() throws ComponentInitializationException, MessageHandlerException {
        existingMetadataContext.setEntityDescriptor(null);

        handler.setRoleDescriptorResolver(roleResolver);
        handler.setCopyContextStrategy(copyContextStrategy);
        handler.initialize();

        final SAMLPeerEntityContext peerContext = messageContext.ensureSubcontext(SAMLPeerEntityContext.class);
        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        messageContext.ensureSubcontext(SAMLProtocolContext.class).setProtocol(SAMLConstants.SAML11P_NS);

        final Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        final AttributeQuery query = request.getAttributeQuery();
        assert query != null;
        query.setResource("urn:mace:incommon:osu.edu");
        messageContext.setMessage(request);

        handler.invoke(messageContext);

        final SAMLMetadataContext mdCtx = peerContext.getSubcontext(SAMLMetadataContext.class);
        assert mdCtx != null;
        Assert.assertNotSame(mdCtx, existingMetadataContext);
        Assert.assertNotNull(mdCtx.getRoleDescriptor());
        Assert.assertNotSame(mdCtx.getRoleDescriptor(), existingMetadataContext.getRoleDescriptor());
        Assert.assertNotNull(mdCtx.getEntityDescriptor());
        Assert.assertNotSame(mdCtx.getEntityDescriptor(), existingMetadataContext.getEntityDescriptor());
    }

    @Test
    public void testCopyFailMissingExistingRoleDescriptor() throws ComponentInitializationException, MessageHandlerException {
        existingMetadataContext.setRoleDescriptor(null);

        handler.setRoleDescriptorResolver(roleResolver);
        handler.setCopyContextStrategy(copyContextStrategy);
        handler.initialize();

        final SAMLPeerEntityContext peerContext = messageContext.ensureSubcontext(SAMLPeerEntityContext.class);
        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        messageContext.ensureSubcontext(SAMLProtocolContext.class).setProtocol(SAMLConstants.SAML11P_NS);

        final Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        final AttributeQuery query = request.getAttributeQuery();
        assert query != null;
        query.setResource("urn:mace:incommon:osu.edu");
        messageContext.setMessage(request);

        handler.invoke(messageContext);

        final SAMLMetadataContext mdCtx = peerContext.getSubcontext(SAMLMetadataContext.class);
        assert mdCtx != null;
        Assert.assertNotSame(mdCtx, existingMetadataContext);
        Assert.assertNotNull(mdCtx.getRoleDescriptor());
        Assert.assertNotSame(mdCtx.getRoleDescriptor(), existingMetadataContext.getRoleDescriptor());
        Assert.assertNotNull(mdCtx.getEntityDescriptor());
        Assert.assertNotSame(mdCtx.getEntityDescriptor(), existingMetadataContext.getEntityDescriptor());
    }

    @Test
    public void testCopyFailEntityIDMismatch() throws ComponentInitializationException, MessageHandlerException {
        existingEntityDescriptor.setEntityID("https://bogus.example.org");

        handler.setRoleDescriptorResolver(roleResolver);
        handler.setCopyContextStrategy(copyContextStrategy);
        handler.initialize();

        final SAMLPeerEntityContext peerContext = messageContext.ensureSubcontext(SAMLPeerEntityContext.class);
        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        messageContext.ensureSubcontext(SAMLProtocolContext.class).setProtocol(SAMLConstants.SAML11P_NS);

        final Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        final AttributeQuery query = request.getAttributeQuery();
        assert query != null;
        query.setResource("urn:mace:incommon:osu.edu");
        messageContext.setMessage(request);

        handler.invoke(messageContext);

        final SAMLMetadataContext mdCtx = peerContext.getSubcontext(SAMLMetadataContext.class);
        assert mdCtx != null;
        Assert.assertNotSame(mdCtx, existingMetadataContext);
        Assert.assertNotNull(mdCtx.getRoleDescriptor());
        Assert.assertNotSame(mdCtx.getRoleDescriptor(), existingMetadataContext.getRoleDescriptor());
        Assert.assertNotNull(mdCtx.getEntityDescriptor());
        Assert.assertNotSame(mdCtx.getEntityDescriptor(), existingMetadataContext.getEntityDescriptor());
    }

    @Test
    public void testCopyFailRoleMismatch() throws ComponentInitializationException, MessageHandlerException {
        existingMetadataContext.setRoleDescriptor(buildXMLObject(SPSSODescriptor.DEFAULT_ELEMENT_NAME));

        handler.setRoleDescriptorResolver(roleResolver);
        handler.setCopyContextStrategy(copyContextStrategy);
        handler.initialize();

        final SAMLPeerEntityContext peerContext = messageContext.ensureSubcontext(SAMLPeerEntityContext.class);
        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        messageContext.ensureSubcontext(SAMLProtocolContext.class).setProtocol(SAMLConstants.SAML11P_NS);

        final Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        final AttributeQuery query = request.getAttributeQuery();
        assert query != null;
        query.setResource("urn:mace:incommon:osu.edu");
        messageContext.setMessage(request);

        handler.invoke(messageContext);

        final SAMLMetadataContext mdCtx = peerContext.getSubcontext(SAMLMetadataContext.class);
        assert mdCtx != null;
        Assert.assertNotSame(mdCtx, existingMetadataContext);
        Assert.assertNotNull(mdCtx.getRoleDescriptor());
        Assert.assertNotSame(mdCtx.getRoleDescriptor(), existingMetadataContext.getRoleDescriptor());
        Assert.assertNotNull(mdCtx.getEntityDescriptor());
        Assert.assertNotSame(mdCtx.getEntityDescriptor(), existingMetadataContext.getEntityDescriptor());
    }
    
    @Test
    public void testDetectDuplicateEntityIDs() throws ComponentInitializationException, MessageHandlerException {
        handler.setRoleDescriptorResolver(roleResolver);
        handler.initialize();
        
        final SAMLPeerEntityContext peerContext = messageContext.ensureSubcontext(SAMLPeerEntityContext.class);
        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        messageContext.ensureSubcontext(SAMLProtocolContext.class).setProtocol(SAMLConstants.SAML11P_NS);
        
        messageContext.ensureSubcontext(SAMLMetadataLookupParametersContext.class).setDetectDuplicateEntityIDs(DetectDuplicateEntityIDs.Batch);
        
        final Request request = SAML1ActionTestingSupport.buildAttributeQueryRequest(null);
        final AttributeQuery query = request.getAttributeQuery();
        assert query != null;
        query.setResource("urn:mace:incommon:osu.edu");
        messageContext.setMessage(request);

        // The context data/criterion won't influence the actual results, so just test that criterion has been added as expected.
        final CriteriaSet criteria = handler.buildLookupCriteria(messageContext, "urn:mace:incommon:osu.edu", IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        assert criteria != null;
        final DetectDuplicateEntityIDsCriterion criterion = criteria.get(DetectDuplicateEntityIDsCriterion.class);
        assert criterion != null;
        Assert.assertEquals(criterion.getValue(), DetectDuplicateEntityIDs.Batch);
        
        // For good measure actually test resolution and that hasn't caused any failures due to side effects, etc.
        handler.invoke(messageContext);
        
        final SAMLMetadataContext mdCtx = peerContext.getSubcontext(SAMLMetadataContext.class);
        assert mdCtx != null;
        Assert.assertNotNull(mdCtx.getRoleDescriptor());
        Assert.assertNotNull(mdCtx.getEntityDescriptor());
    }

}