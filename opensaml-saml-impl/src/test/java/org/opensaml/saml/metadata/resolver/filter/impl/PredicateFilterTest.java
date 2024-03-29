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

package org.opensaml.saml.metadata.resolver.filter.impl;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.spring.resource.ResourceHelper;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.saml.common.profile.logic.EntityIdPredicate;
import org.opensaml.saml.metadata.resolver.filter.impl.PredicateFilter.Direction;
import org.opensaml.saml.metadata.resolver.impl.ResourceBackedMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link PredicateFilter}.
 */
@SuppressWarnings("javadoc")
public class PredicateFilterTest extends XMLObjectBaseTestCase {

    private ResourceBackedMetadataResolver metadataProvider;
    
    private ResourceBackedMetadataResolver singleEntityProvider;

    @BeforeMethod
    protected void setUp() throws Exception {
        
        final Resource resource = new ClassPathResource("/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        metadataProvider = new ResourceBackedMetadataResolver(null, ResourceHelper.of(resource));
        metadataProvider.setId("multi");
        metadataProvider.setParserPool(parserPool);
        
        final Resource singleResource = new ClassPathResource("/org/opensaml/saml/saml2/metadata/entitydescriptor-metadata.xml");
        singleEntityProvider = new ResourceBackedMetadataResolver(null, ResourceHelper.of(singleResource));
        singleEntityProvider.setId("single");
        singleEntityProvider.setParserPool(parserPool);
    }
    
    @Test
    public void testDenyList() throws Exception {
        
        final String allowed = "urn:mace:incommon:dartmouth.edu";
        final String denied = "urn:mace:incommon:osu.edu";
        final String osu = "urn:mace:incommon:osu.edu";

        final EntityIdPredicate condition = new EntityIdPredicate(CollectionSupport.singletonList(denied));
        final PredicateFilter filter = new PredicateFilter(Direction.EXCLUDE, condition);
        filter.initialize();
        
        metadataProvider.setMetadataFilter(filter);
        metadataProvider.initialize();
        
        EntityDescriptor entity = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(denied)));
        Assert.assertNull(entity);
        
        entity = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(allowed)));
        Assert.assertNotNull(entity);
        
        singleEntityProvider.setMetadataFilter(filter);
        singleEntityProvider.initialize();
        
        entity = singleEntityProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(osu)));
        Assert.assertNull(entity);
    }
    
    @Test
    public void testAllowList() throws Exception {
        
        final String allowed = "urn:mace:incommon:dartmouth.edu";
        final String denied = "urn:mace:incommon:osu.edu";
        final String osu = "urn:mace:incommon:osu.edu";

        final EntityIdPredicate condition = new EntityIdPredicate(CollectionSupport.singletonList(allowed));
        
        PredicateFilter filter = new PredicateFilter(Direction.INCLUDE, condition);
        filter.initialize();
        
        metadataProvider.setMetadataFilter(filter);
        metadataProvider.initialize();
        
        EntityDescriptor entity = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(denied)));
        Assert.assertNull(entity);
        
        entity = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(allowed)));
        Assert.assertNotNull(entity);
        
        filter = new PredicateFilter(Direction.INCLUDE, new EntityIdPredicate(CollectionSupport.singletonList(osu)));
        filter.initialize();
        
        singleEntityProvider.setMetadataFilter(filter);
        singleEntityProvider.initialize();
        
        entity = singleEntityProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(osu)));
        Assert.assertNotNull(entity);
    }
    
}