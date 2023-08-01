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
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;
import net.shibboleth.shared.xml.XMLParserException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

@SuppressWarnings("javadoc")
public class DOMMetadataResolverTest extends XMLObjectBaseTestCase {

    private DOMMetadataResolver metadataProvider;
    
    private File mdFile;

    private String entityID;

    private CriteriaSet criteriaSet;
    
    private boolean allowActivation;

    @BeforeMethod
    protected void setUp() throws Exception {
        entityID = "urn:mace:incommon:washington.edu";

        URL mdURL = DOMMetadataResolverTest.class
                .getResource("/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        mdFile = new File(mdURL.toURI());
        
        try (final FileInputStream fis = new FileInputStream(mdFile)) {
            Document document = parserPool.parse(fis);
            metadataProvider = new DOMMetadataResolver(document.getDocumentElement());
        }
        
        metadataProvider.setId("test");
        metadataProvider.setActivationCondition(prc -> {return allowActivation;});
        metadataProvider.initialize();
        
        criteriaSet = new CriteriaSet(new EntityIdCriterion(entityID));
    }

    @Test
    public void testGetEntityDescriptor() throws ResolverException {
        allowActivation = true;
        
        EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        assert descriptor != null;
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");

        allowActivation = false;
        
        descriptor = metadataProvider.resolveSingle(criteriaSet);
        Assert.assertNull(descriptor, "Retrieved entity descriptor was not null");
    }
    
    @Test
    public void testFilterFailureAndNoFailFast() throws URISyntaxException, XMLParserException, IOException, ResolverException {

        try (final FileInputStream fis = new FileInputStream(mdFile)) {
            Document document = parserPool.parse(fis);
            metadataProvider = new DOMMetadataResolver(document.getDocumentElement());
            metadataProvider.setMetadataFilter(new MockFailureFilter());
            metadataProvider.setFailFastInitialization(false);
            metadataProvider.setRequireValidMetadata(true);
            metadataProvider.setParserPool(parserPool);
            metadataProvider.setId("test");
            metadataProvider.initialize();
        } catch (final ComponentInitializationException e) {
            Assert.fail("DOM metadata provider init failed due to filter exception and fail fast = false");
        }
        
        EntityDescriptor entity = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp.example.org")));
        Assert.assertNull(entity);
    }
    
    
}