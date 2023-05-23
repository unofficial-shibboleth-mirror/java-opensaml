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

package org.opensaml.saml.metadata.resolver.filter.impl;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolverTest;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class NameIDFormatFilterTest extends XMLObjectBaseTestCase implements Predicate<EntityDescriptor> {
    
    private FilesystemMetadataResolver metadataProvider;
    
    private File mdFile;
    
    private NameIDFormatFilter metadataFilter;
    
    private Collection<String> formats;
    
    @BeforeMethod
    protected void setUp() throws Exception {

        URL mdURL = FilesystemMetadataResolverTest.class
                .getResource("/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        mdFile = new File(mdURL.toURI());

        metadataProvider = new FilesystemMetadataResolver(mdFile);
        metadataProvider.setParserPool(parserPool);
        
        metadataFilter = new NameIDFormatFilter();
        
        formats = CollectionSupport.listOf(NameIDType.EMAIL, NameIDType.PERSISTENT);
    }
    
    @Test
    public void test() throws ComponentInitializationException, ResolverException {
        
        metadataFilter.setRules(CollectionSupport.singletonMap(this, formats));
        metadataFilter.initialize();
        
        metadataProvider.setMetadataFilter(metadataFilter);
        metadataProvider.setId("test");
        metadataProvider.initialize();

        EntityIdCriterion key = new EntityIdCriterion("https://carmenwiki.osu.edu/shibboleth");
        EntityDescriptor entity = metadataProvider.resolveSingle(new CriteriaSet(key));
        assert entity != null;
        
        SPSSODescriptor role = entity.getSPSSODescriptor(SAMLConstants.SAML20P_NS);
        assert role != null;
        final List<NameIDFormat> attachedFormats = role.getNameIDFormats();
        Assert.assertEquals(attachedFormats.stream().map(NameIDFormat::getURI).collect(Collectors.toUnmodifiableList()),
                CollectionSupport.listOf(NameIDType.PERSISTENT, NameIDType.EMAIL));
                
        key = new EntityIdCriterion("https://cms.psu.edu/Shibboleth");
        entity = metadataProvider.resolveSingle(new CriteriaSet(key));
        assert entity != null;
        role = entity.getSPSSODescriptor(SAMLConstants.SAML11P_NS);
        assert role != null;
        Assert.assertEquals(role.getNameIDFormats().size(), 1);
    }

    @Test
    public void testWithRemoval() throws ComponentInitializationException, ResolverException {
        
        metadataFilter.setRules(CollectionSupport.singletonMap(this, formats));
        metadataFilter.setRemoveExistingFormats(true);
        metadataFilter.initialize();
        
        metadataProvider.setMetadataFilter(metadataFilter);
        metadataProvider.setId("test");
        metadataProvider.initialize();

        final EntityIdCriterion key = new EntityIdCriterion("https://carmenwiki.osu.edu/shibboleth");
        final EntityDescriptor entity = metadataProvider.resolveSingle(new CriteriaSet(key));
        assert entity != null;
        final SPSSODescriptor role = entity.getSPSSODescriptor(SAMLConstants.SAML20P_NS);
        assert role != null;
        Assert.assertEquals(role.getNameIDFormats().size(), 2);
    }

    /** {@inheritDoc} */
    public boolean test(EntityDescriptor input) {
        return "https://carmenwiki.osu.edu/shibboleth".equals(input.getEntityID());
    }

}