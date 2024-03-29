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
import java.util.ArrayList;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.saml.common.xml.SAMLSchemaBuilder;
import org.opensaml.saml.common.xml.SAMLSchemaBuilder.SAML1Version;
import org.opensaml.saml.metadata.resolver.ChainingMetadataResolver;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.impl.SchemaValidationFilter;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.resolver.CriteriaSet;

@SuppressWarnings("javadoc")
public class ChainingMetadataResolverTest extends XMLObjectBaseTestCase {

    private ChainingMetadataResolver metadataProvider;

    private String entityID;

    private String entityID2;

    @BeforeMethod
    protected void setUp() throws Exception {
        entityID = "urn:mace:incommon:washington.edu";
        entityID2 = "urn:mace:switch.ch:SWITCHaai:ethz.ch";

        metadataProvider = new ChainingMetadataResolver();
        metadataProvider.setId("test MP provider");
        ArrayList<MetadataResolver> resolvers = new ArrayList<>();

        URL mdURL = ChainingMetadataResolverTest.class
                .getResource("/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        File mdFile = new File(mdURL.toURI());
        FilesystemMetadataResolver fileProvider = new FilesystemMetadataResolver(mdFile);
        fileProvider.setParserPool(parserPool);
        fileProvider.setId("test");

        fileProvider.initialize();
        resolvers.add(fileProvider);

        URL mdURL2 = ChainingMetadataResolverTest.class
                .getResource("/org/opensaml/saml/saml2/metadata/metadata.switchaai_signed.xml");
        File mdFile2 = new File(mdURL2.toURI());
        FilesystemMetadataResolver fileProvider2 = new FilesystemMetadataResolver(mdFile2);
        fileProvider2.setParserPool(parserPool);
        // For this test, need to set this because metadata.switchaai_signed.xml has an expired validUntil
        fileProvider2.setRequireValidMetadata(false);
        fileProvider2.setId("fp2");
        fileProvider2.initialize();
        resolvers.add(fileProvider2);
        
        metadataProvider.setResolvers(resolvers);
        metadataProvider.setId("test");
    }
    
    @Test()
    public void testInactive() throws Exception {
        metadataProvider.setActivationCondition(PredicateSupport.alwaysFalse());
        metadataProvider.initialize();
        
        EntityDescriptor descriptor = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(entityID)));
        Assert.assertNull(descriptor, "Retrieved entity descriptor was not null");
    }

    @Test()
    public void testGetEntityDescriptor() throws Exception {
        metadataProvider.initialize();
        
        EntityDescriptor descriptor = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(entityID)));
        assert descriptor != null;
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");

        EntityDescriptor descriptor2 = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(entityID2)));
        assert descriptor2 != null;
        Assert.assertEquals(descriptor2.getEntityID(), entityID2, "Entity's ID does not match requested ID");
    }

    @Test()
    public void testFilterDisallowed() throws ComponentInitializationException {
        metadataProvider.initialize();

        try {
            metadataProvider.setMetadataFilter(new SchemaValidationFilter(new SAMLSchemaBuilder(SAML1Version.SAML_11)));
            Assert.fail("Should fail with an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected, do nothing
        }
    }

}