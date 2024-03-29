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

package org.opensaml.saml.metadata.resolver.impl;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.Timer;

import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;
import net.shibboleth.shared.resource.Resource;
import net.shibboleth.shared.spring.resource.ResourceHelper;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.core.io.FileSystemResource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** Unit test for {@link ResourceBackedMetadataResolver}. */
public class ResourceBackedMetadataResolverTest extends XMLObjectBaseTestCase {

    private ResourceBackedMetadataResolver metadataProvider;

    private String entityID;

    private CriteriaSet criteriaSet;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        entityID = "urn:mace:incommon:washington.edu";

        final URL mdURL = ResourceBackedMetadataResolverTest.class
                .getResource("/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        final Resource mdResource = ResourceHelper.of(new FileSystemResource(new File(mdURL.toURI()).getAbsolutePath()));

        metadataProvider = new ResourceBackedMetadataResolver(new Timer(true), mdResource);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setMaxRefreshDelay(Duration.ofSeconds(500));
        metadataProvider.setId("test");
        metadataProvider.initialize();
        
        criteriaSet = new CriteriaSet(new EntityIdCriterion(entityID));
    }

    @Test
    protected void testInactive() throws Exception {
        entityID = "urn:mace:incommon:washington.edu";

        final URL mdURL = ResourceBackedMetadataResolverTest.class
                .getResource("/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        final Resource mdResource = ResourceHelper.of(new FileSystemResource(new File(mdURL.toURI()).getAbsolutePath()));

        metadataProvider = new ResourceBackedMetadataResolver(new Timer(true), mdResource);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setMaxRefreshDelay(Duration.ofSeconds(500));
        metadataProvider.setId("test");
        metadataProvider.setActivationCondition(PredicateSupport.alwaysFalse());
        metadataProvider.initialize();
        
        criteriaSet = new CriteriaSet(new EntityIdCriterion(entityID));
        Assert.assertNull(metadataProvider.resolveSingle(criteriaSet));
    }

    /**
     * Tests the {@link ResourceBackedMetadataResolver#lookupEntityID(String)} method.
     * 
     * @throws ResolverException ...
     */
    @Test
    public void testGetEntityDescriptor() throws ResolverException {
        final EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        assert descriptor != null;
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");
    }
    
}