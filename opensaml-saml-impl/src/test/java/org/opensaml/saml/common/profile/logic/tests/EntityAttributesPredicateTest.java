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

package org.opensaml.saml.common.profile.logic.tests;

import java.util.regex.Pattern;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.spring.resource.ResourceHelper;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.saml.common.profile.logic.EntityAttributesPredicate;
import org.opensaml.saml.common.profile.logic.EntityAttributesPredicate.Candidate;
import org.opensaml.saml.metadata.resolver.impl.ResourceBackedMetadataResolver;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link EntityAttributesPredicate}.
 */
@SuppressWarnings("javadoc")
public class EntityAttributesPredicateTest extends XMLObjectBaseTestCase {

    private ResourceBackedMetadataResolver metadataProvider;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        
        final Resource resource =
                new ClassPathResource("/org/opensaml/saml/metadata/resolver/filter/impl/EntitiesDescriptor-Name-metadata.xml");
        metadataProvider = new ResourceBackedMetadataResolver(null, ResourceHelper.of(resource));
        metadataProvider.setId("test");
        metadataProvider.setParserPool(parserPool);
        metadataProvider.initialize();
    }

    @Test
    public void testWrongName() throws Exception {

        final Candidate candidate = new Candidate("urn:foo:bar", Attribute.URI_REFERENCE);
        candidate.setValues(CollectionSupport.singletonList("bar"));
        final EntityAttributesPredicate condition =
                new EntityAttributesPredicate(CollectionSupport.singletonList(candidate), false);
                
        final EntityDescriptor entity =
                metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-top.example.org")));
        Assert.assertNotNull(entity);
        
        Assert.assertFalse(condition.test(entity));
    }

    @Test
    public void testWrongNameFormat() throws Exception {

        final Candidate candidate = new Candidate("urn:foo", Attribute.BASIC);
        candidate.setValues(CollectionSupport.singletonList("bar"));
        final EntityAttributesPredicate condition =
                new EntityAttributesPredicate(CollectionSupport.singletonList(candidate), false);
                
        final EntityDescriptor entity =
                metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-top.example.org")));
        Assert.assertNotNull(entity);
        
        Assert.assertFalse(condition.test(entity));
    }
    
    @Test
    public void testGroupUnspecified() throws Exception {

        final Candidate candidate = new Candidate("urn:foo", null);
        candidate.setValues(CollectionSupport.singletonList("bar"));
        final EntityAttributesPredicate condition =
                new EntityAttributesPredicate(CollectionSupport.singletonList(candidate), false);
                
        final EntityDescriptor entity =
                metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-top.example.org")));
        Assert.assertNotNull(entity);
        
        Assert.assertTrue(condition.test(entity));
    }
    
    @Test
    public void testGroupExact() throws Exception {

        final Candidate candidate = new Candidate("urn:foo", Attribute.URI_REFERENCE);
        candidate.setValues(CollectionSupport.singletonList("bar"));
        final EntityAttributesPredicate condition =
                new EntityAttributesPredicate(CollectionSupport.singletonList(candidate), false);
                
        final EntityDescriptor entity =
                metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-top.example.org")));
        Assert.assertNotNull(entity);
        
        Assert.assertTrue(condition.test(entity));
    }
    
    @Test
    public void testIdP1475()  throws Exception {
        final Candidate candidate = new Candidate("https://its.umich.edu/identity/activationCondition/isMemberOf");
        candidate.setValues(CollectionSupport.singletonList("true"));
        final EntityAttributesPredicate condition =
                new EntityAttributesPredicate(CollectionSupport.singletonList(candidate));

        final EntityDescriptor entity =
                metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-1475.example.org")));
        Assert.assertNotNull(entity);
        Assert.assertTrue(condition.test(entity));
    }

    @Test
    public void testGroupAdditional() throws Exception {

        final Candidate candidate = new Candidate("urn:foo", Attribute.URI_REFERENCE);
        candidate.setValues(CollectionSupport.singletonList("bar"));
        candidate.setRegexps(CollectionSupport.singletonList(Pattern.compile("baz")));
        final EntityAttributesPredicate condition =
                new EntityAttributesPredicate(CollectionSupport.singletonList(candidate), false);
                
        final EntityDescriptor entity =
                metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-top.example.org")));
        Assert.assertNotNull(entity);
        Assert.assertFalse(condition.test(entity));

        final EntityDescriptor entity2 =
                metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-sub1.example.org")));
        Assert.assertNotNull(entity2);
        Assert.assertTrue(condition.test(entity2));
    }
}