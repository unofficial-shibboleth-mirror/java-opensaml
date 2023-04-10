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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/** Unit test for {@link ByReferenceMetadataFilter}. */
@SuppressWarnings("javadoc")
public class ByReferenceMetadataFilterTest extends XMLObjectBaseTestCase implements Predicate<EntityDescriptor> {
    
    protected MetadataResolver resolver;
    
    private FilesystemMetadataResolver metadataProvider;
    
    private ByReferenceMetadataFilter refFilter;
    
    private NameIDFormatFilter nameIDFilter;
    
    private Collection<String> formats;
    
    @BeforeMethod
    protected void setUp() throws URISyntaxException, ResolverException {

        final URL mdURL = ByReferenceMetadataFilterTest.class
                .getResource("/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        final File mdFile = new File(mdURL.toURI());

        metadataProvider = new FilesystemMetadataResolver(mdFile);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("ICMD");

        refFilter = new ByReferenceMetadataFilter();
        metadataProvider.setMetadataFilter(refFilter);
        
        nameIDFilter = new NameIDFormatFilter();
        formats = Arrays.asList(NameIDType.EMAIL, NameIDType.KERBEROS);
    }
    
    @Test
    public void notApplicable() throws ComponentInitializationException, ResolverException {
        
        nameIDFilter.setRules(CollectionSupport.singletonMap(this, formats));
        nameIDFilter.initialize();
        
        refFilter.setFilterMappings(CollectionSupport.singletonMap("Foo", nameIDFilter));
        
        metadataProvider.initialize();
        
        validate(false);
    }

    @Test
    public void applicable() throws ComponentInitializationException, ResolverException {
        
        nameIDFilter.setRules(CollectionSupport.singletonMap(this, formats));
        nameIDFilter.initialize();
        
        refFilter.setFilterMappings(CollectionSupport.singletonMap(CollectionSupport.listOf("ICMD", "Foo"), nameIDFilter));
        
        metadataProvider.initialize();
        
        validate(true);
    }

    /**
     * Validate whether the filter was or wasn't applied.
     * 
     * @param applied <code>true</code> if validating that the filter was applied, <code>false</code> otherwise
     * 
     * @throws ResolverException if something goes wrong
     */
    private void validate(final boolean applied) throws ResolverException {
        EntityIdCriterion key = new EntityIdCriterion("https://carmenwiki.osu.edu/shibboleth");
        EntityDescriptor entity = metadataProvider.resolveSingle(new CriteriaSet(key));
        assert entity != null;
        SPSSODescriptor role = entity.getSPSSODescriptor(SAMLConstants.SAML20P_NS);
        assert role != null;
        Assert.assertEquals(role.getNameIDFormats().size(), applied ? 3 : 1);
        
        key = new EntityIdCriterion("https://cms.psu.edu/Shibboleth");
        entity = metadataProvider.resolveSingle(new CriteriaSet(key));
        assert entity != null;
        role = entity.getSPSSODescriptor(SAMLConstants.SAML11P_NS);
        assert role != null;
        Assert.assertEquals(role.getNameIDFormats().size(), 1);
    }
    
    /** {@inheritDoc} */
    public boolean test(EntityDescriptor input) {
        return "https://carmenwiki.osu.edu/shibboleth".equals(input.getEntityID());
    }
}
