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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;
import net.shibboleth.shared.xml.XMLParserException;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.BestMatchLocationCriterion;
import org.opensaml.saml.criterion.BindingCriterion;
import org.opensaml.saml.criterion.EndpointCriterion;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/** Test for {@link DefaultEndpointResolver}. */
@SuppressWarnings("javadoc")
public class DefaultEndpointResolverTest extends XMLObjectBaseTestCase {

    private static final String LOCATION = "https://sp.example.org/ACS";
    private static final String LOCATION_POST = "https://sp.example.org/POST2";
    private static final String LOCATION_ART = "https://sp.example.org/Art2";
    
    private DefaultEndpointResolver<AssertionConsumerService> resolver;
    
    private EndpointCriterion<AssertionConsumerService> endpointCrit;
    
    @BeforeClass
    public void classSetUp() throws ComponentInitializationException {
        resolver = new DefaultEndpointResolver<>();
        resolver.initialize();
    }
    
    @BeforeMethod
    public void setUp() {
        final AssertionConsumerService ep = (AssertionConsumerService) builderFactory.ensureBuilder(
                AssertionConsumerService.DEFAULT_ELEMENT_NAME).buildObject(
                        AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        ep.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
        ep.setLocation(LOCATION);
        endpointCrit = new EndpointCriterion<>(ep, false);
    }

    @Test(expectedExceptions = ResolverException.class)
    public void testNoCriteria() throws ResolverException {
        resolver.resolveSingle(new CriteriaSet());
    }
    
    @Test
    public void testNoMetadata() throws ResolverException {
        final AssertionConsumerService ep = resolver.resolveSingle(new CriteriaSet(endpointCrit));
        Assert.assertNull(ep);
    }
    
    @Test
    public void testSignedRequest() throws ResolverException {
        final CriteriaSet crits = new CriteriaSet(new EndpointCriterion<>(endpointCrit.getEndpoint(), true));
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        Assert.assertNotNull(ep);
        Assert.assertSame(ep, endpointCrit.getEndpoint());
    }

    /**
     * SP requests an endpoint but we don't support the binding.
     * 
     * @throws ResolverException ...
     */
    @Test
    public void testSignedRequestBadBinding() throws ResolverException {
        final CriteriaSet crits = new CriteriaSet(new EndpointCriterion<>(endpointCrit.getEndpoint(), true),
                new BindingCriterion(CollectionSupport.emptyList()));
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        Assert.assertNull(ep);
    }
    
    /**
     * An SP with no endpoints in metadata.
     * 
     * @throws UnmarshallingException ...
     * @throws ResolverException ...
     */
    @Test
    public void testNoEndpoints() throws UnmarshallingException, ResolverException {
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/org/opensaml/saml/common/binding/SPNoEndpoints.xml"));
        final AssertionConsumerService ep = resolver.resolveSingle(new CriteriaSet(endpointCrit, roleCrit));
        Assert.assertNull(ep);
    }

    /**
     * No endpoint with the location requested.
     * 
     * @throws UnmarshallingException ...
     * @throws ResolverException ...
     */
    @Test
    public void testBadLocation() throws UnmarshallingException, ResolverException {
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit);
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        Assert.assertNull(ep);
    }

    /**
     * No endpoint at a location with the right binding requested.
     * 
     * @throws UnmarshallingException ...
     * @throws ResolverException ...
     */
    @Test
    public void testBadBinding() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(LOCATION_POST);
        endpointCrit.getEndpoint().setBinding(SAMLConstants.SAML2_SOAP11_BINDING_URI);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit);
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        Assert.assertNull(ep);
    }

    /**
     * Endpoint matches but we don't support the binding.
     * 
     * @throws UnmarshallingException ...
     * @throws ResolverException ...
     */
    @Test
    public void testUnsupportedBinding() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(LOCATION_POST);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit,
                new BindingCriterion(CollectionSupport.singletonList(SAMLConstants.SAML2_ARTIFACT_BINDING_URI)));
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        Assert.assertNull(ep);
    }
    
    /**
     * No endpoint with a requested index.
     * 
     * @throws UnmarshallingException ...
     * @throws ResolverException ...
     */
    @Test
    public void testBadIndex() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(null);
        endpointCrit.getEndpoint().setBinding(null);
        endpointCrit.getEndpoint().setIndex(5);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit);
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        Assert.assertNull(ep);
    }
    
    /**
     * Requested location/binding are in metadata.
     * 
     * @throws UnmarshallingException ...
     * @throws ResolverException ...
     */
    @Test
    public void testInMetadata() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(LOCATION_POST);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit);
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        assert ep != null;
        Assert.assertEquals(ep.getBinding(), SAMLConstants.SAML2_POST_BINDING_URI);
        Assert.assertEquals(ep.getLocation(), LOCATION_POST);
        Assert.assertEquals(ep.getIndex(), Integer.valueOf(2));
    }

    /**
     * Get the default endpoint.
     * 
     * @throws UnmarshallingException ...
     * @throws ResolverException ...
     */
    @Test
    public void testDefault() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(null);
        endpointCrit.getEndpoint().setBinding(null);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit);
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        assert ep != null;
        Assert.assertEquals(ep.getBinding(), SAMLConstants.SAML2_ARTIFACT_BINDING_URI);
        Assert.assertEquals(ep.getLocation(), LOCATION_ART);
        Assert.assertEquals(ep.getIndex(), Integer.valueOf(4));
    }

    /**
     * Get the default endpoint with a binding.
     * 
     * @throws UnmarshallingException ...
     * @throws ResolverException ...
     */
    @Test
    public void testDefaultForBinding() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(null);
        endpointCrit.getEndpoint().setBinding(null);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit,
                new BindingCriterion(CollectionSupport.singletonList(SAMLConstants.SAML2_POST_BINDING_URI)));
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        assert ep != null;
        Assert.assertEquals(ep.getBinding(), SAMLConstants.SAML2_POST_BINDING_URI);
        Assert.assertEquals(ep.getLocation(), LOCATION_POST.replace("POST2", "POST"));
        Assert.assertEquals(ep.getIndex(), Integer.valueOf(1));
    }
    
    /**
     * All endpoints of the right type.
     * 
     * @throws UnmarshallingException ...
     * @throws ResolverException ...
     */
    @Test
    public void testMultiple() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(null);
        endpointCrit.getEndpoint().setBinding(null);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit);
        final List<AssertionConsumerService> eps = new ArrayList<>();
        for (final AssertionConsumerService ep : resolver.resolve(crits)) {
            eps.add(ep);
        }
        Assert.assertEquals(eps.size(), 4);
        Assert.assertEquals(
                eps.stream().map(Endpoint::getBinding).collect(Collectors.toUnmodifiableList()),
                CollectionSupport.listOf(SAMLConstants.SAML2_ARTIFACT_BINDING_URI,
                        SAMLConstants.SAML2_POST_BINDING_URI,
                        SAMLConstants.SAML2_POST_BINDING_URI,
                        SAMLConstants.SAML2_ARTIFACT_BINDING_URI));
    }

    /**
     * All endpoints of the right type in a supplied binding order.
     * 
     * @throws UnmarshallingException ...
     * @throws ResolverException ...
     */
    @Test
    public void testMultipleBindingMetadataOrdered() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(null);
        endpointCrit.getEndpoint().setBinding(null);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit,
                new BindingCriterion(CollectionSupport.listOf(SAMLConstants.SAML2_POST_BINDING_URI, SAMLConstants.SAML2_ARTIFACT_BINDING_URI)));
        final List<AssertionConsumerService> eps = new ArrayList<>();
        for (final AssertionConsumerService ep : resolver.resolve(crits)) {
            eps.add(ep);
        }
        Assert.assertEquals(eps.size(), 4);
        Assert.assertEquals(
                eps.stream().map(Endpoint::getBinding).collect(Collectors.toUnmodifiableList()),
                CollectionSupport.listOf(SAMLConstants.SAML2_ARTIFACT_BINDING_URI,
                        SAMLConstants.SAML2_POST_BINDING_URI,
                        SAMLConstants.SAML2_POST_BINDING_URI,
                        SAMLConstants.SAML2_ARTIFACT_BINDING_URI));
    }

    /**
     * All endpoints of the right type in a supplied binding order.
     * 
     * @throws UnmarshallingException ...
     * @throws ResolverException ...
     * @throws ComponentInitializationException 
     */
    @Test
    public void testMultipleBindingImposeOrdered() throws UnmarshallingException, ResolverException, ComponentInitializationException {
        final DefaultEndpointResolver<AssertionConsumerService> overridden = new DefaultEndpointResolver<>();
        overridden.setInMetadataOrder(false);
        overridden.initialize();
        
        endpointCrit.getEndpoint().setLocation(null);
        endpointCrit.getEndpoint().setBinding(null);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit,
                new BindingCriterion(CollectionSupport.listOf(SAMLConstants.SAML2_POST_BINDING_URI, SAMLConstants.SAML2_ARTIFACT_BINDING_URI)));
        final List<AssertionConsumerService> eps = new ArrayList<>();
        for (final AssertionConsumerService ep : overridden.resolve(crits)) {
            eps.add(ep);
        }
        Assert.assertEquals(eps.size(), 4);
        Assert.assertEquals(
                eps.stream().map(Endpoint::getBinding).collect(Collectors.toUnmodifiableList()),
                CollectionSupport.listOf(SAMLConstants.SAML2_POST_BINDING_URI,
                        SAMLConstants.SAML2_POST_BINDING_URI,
                        SAMLConstants.SAML2_ARTIFACT_BINDING_URI,
                        SAMLConstants.SAML2_ARTIFACT_BINDING_URI));
    }    
    
    /**
     * All endpoints of the right type and binding.
     * @throws UnmarshallingException ...
     * @throws ResolverException ...
     */
    @Test
    public void testMultipleWithBinding() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(null);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit);
        final List<AssertionConsumerService> eps = new ArrayList<>();
        for (final AssertionConsumerService ep : resolver.resolve(crits)) {
            eps.add(ep);
        }
        Assert.assertEquals(eps.size(), 2);
    }
    
    /**
     * Get the "best" endpoint.
     * 
     * @throws UnmarshallingException ...
     * @throws ResolverException ...
     */
    @Test
    public void testBestMatch() throws UnmarshallingException, ResolverException {
        
        endpointCrit.getEndpoint().setLocation(null);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/org/opensaml/saml/common/binding/SPWithVhosts.xml"));
        
        CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit, new BestMatchLocationCriterion("https://sp.example.org/Foo"));
        AssertionConsumerService ep = resolver.resolveSingle(crits);
        assert ep != null;
        Assert.assertEquals(ep.getLocation(), "https://sp.example.org/POST");
        
        crits = new CriteriaSet(endpointCrit, roleCrit, new BestMatchLocationCriterion("https://sp2.example.org/Foo"));
        ep = resolver.resolveSingle(crits);
        assert ep != null;
        Assert.assertEquals(ep.getLocation(), "https://sp2.example.org/POST");
        
        crits = new CriteriaSet(endpointCrit, roleCrit, new BestMatchLocationCriterion("https://sp2.example.org/bar/Foo"));
        ep = resolver.resolveSingle(crits);
        assert ep != null;
        Assert.assertEquals(ep.getLocation(), "https://sp2.example.org/POST");
        
        crits = new CriteriaSet(endpointCrit, roleCrit, new BestMatchLocationCriterion("https://sp2.example.org/sub/Foo"));
        ep = resolver.resolveSingle(crits);
        assert ep != null;
        Assert.assertEquals(ep.getLocation(), "https://sp2.example.org/sub/POST");
    }
    
    @Nonnull private SPSSODescriptor loadMetadata(@Nonnull @NotEmpty final String path) throws UnmarshallingException {
        
        try {
            final URL url = getClass().getResource(path);
            Document doc = parserPool.parse(new FileInputStream(new File(url.toURI())));
            final Unmarshaller unmarshaller = unmarshallerFactory.ensureUnmarshaller(doc.getDocumentElement());
            return (SPSSODescriptor) unmarshaller.unmarshall(doc.getDocumentElement());
        } catch (final FileNotFoundException | XMLParserException | URISyntaxException e) {
            throw new UnmarshallingException(e);
        }
    }
    
}
