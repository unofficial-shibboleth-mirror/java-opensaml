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

package org.opensaml.saml.metadata.support.tests;

import java.io.File;
import java.net.URL;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.ext.saml2mdquery.AttributeQueryDescriptorType;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.PredicateRoleDescriptorResolver;
import org.opensaml.saml.metadata.support.AttributeConsumingServiceSelector;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/**
 * Tests of AttributeConsumingServiceSelector.
 */
public class AttributeConsumingServiceSelectorTest extends XMLObjectBaseTestCase {
    
    private String mdFileName;
    
    private FilesystemMetadataResolver mdProvider;
    
    private PredicateRoleDescriptorResolver roleResolver;
    
    private AttributeConsumingServiceSelector acsSelector;

    @BeforeMethod
    protected void setUp() throws Exception {
        mdFileName = "/org/opensaml/saml/saml2/metadata/support/metadata-AttributeConsumingService.xml";
        
        URL mdURL = AttributeConsumingServiceSelectorTest.class.getResource(mdFileName);
        File mdFile = new File(mdURL.toURI());
        
        mdProvider = new FilesystemMetadataResolver(mdFile);
        mdProvider.setParserPool(parserPool);
        mdProvider.setId("test");
        mdProvider.initialize();
        
        roleResolver = new PredicateRoleDescriptorResolver(mdProvider);
        roleResolver.initialize();
        
        acsSelector = new AttributeConsumingServiceSelector();
    }
    
    // Success cases
    
    /**
     * Test valid index.
     * 
     * @throws ResolverException ...
     */
    @Test
    public void testWithValidIndex() throws ResolverException {
        RoleDescriptor role =  roleResolver.resolveSingle(new CriteriaSet(
                new EntityIdCriterion("urn:test:entity:A"),
                new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME),
                new ProtocolCriterion(SAMLConstants.SAML20P_NS)));
        Assert.assertNotNull(role);
        acsSelector.setRoleDescriptor(role);
        
        acsSelector.setIndex(1);
        
        AttributeConsumingService acs = acsSelector.selectService();
        Assert.assertNotNull(acs);
        
        Assert.assertEquals(getName(acs), "A-SP-1", "Wrong service selected");
    }
    
    /**
     * Test explicit isDefault="true".
     * 
     * @throws ResolverException ...
     */
    @Test
    public void testExplicitDefault() throws ResolverException {
        RoleDescriptor role =  roleResolver.resolveSingle(new CriteriaSet(
                new EntityIdCriterion("urn:test:entity:A"),
                new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME),
                new ProtocolCriterion(SAMLConstants.SAML20P_NS)));
        Assert.assertNotNull(role);
        acsSelector.setRoleDescriptor(role);
        
        AttributeConsumingService acs = acsSelector.selectService();
        Assert.assertNotNull(acs);
        
        Assert.assertEquals(getName(acs), "A-SP-0", "Wrong service selected");
    }
    
    /**
     * Test default as first missing default.
     * 
     * @throws ResolverException ...
     */
    @Test
    public void testFirstMissingDefault() throws ResolverException {
        RoleDescriptor role =  roleResolver.resolveSingle(new CriteriaSet(
                new EntityIdCriterion("urn:test:entity:B"),
                new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME),
                new ProtocolCriterion(SAMLConstants.SAML20P_NS)));
        Assert.assertNotNull(role);
        acsSelector.setRoleDescriptor(role);
        
        AttributeConsumingService acs = acsSelector.selectService();
        Assert.assertNotNull(acs);
        
        Assert.assertEquals(getName(acs), "B-SP-2", "Wrong service selected");
    }
    
    /**
     * Test default as first isDefault="false".
     * 
     * @throws ResolverException ...
     */
    @Test
    public void testFirstFalseDefault() throws ResolverException {
        RoleDescriptor role =  roleResolver.resolveSingle(new CriteriaSet(
                new EntityIdCriterion("urn:test:entity:C"),
                new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME),
                new ProtocolCriterion(SAMLConstants.SAML20P_NS)));
        Assert.assertNotNull(role);
        acsSelector.setRoleDescriptor(role);
        
        AttributeConsumingService acs = acsSelector.selectService();
        Assert.assertNotNull(acs);
        
        Assert.assertEquals(getName(acs), "C-SP-0", "Wrong service selected");
    }
    
    /**
     * Test AttributeQueryDescriptorType.
     * 
     * @throws ResolverException ...
     */
    @Test
    public void testAttributeQueryType() throws ResolverException {
        RoleDescriptor role =  roleResolver.resolveSingle(new CriteriaSet(
                new EntityIdCriterion("urn:test:entity:A"),
                new EntityRoleCriterion(AttributeQueryDescriptorType.DEFAULT_ELEMENT_NAME),
                new ProtocolCriterion(SAMLConstants.SAML20P_NS)));
        Assert.assertNotNull(role);
        acsSelector.setRoleDescriptor(role);
        
        acsSelector.setIndex(0);
        
        AttributeConsumingService acs = acsSelector.selectService();
        Assert.assertNotNull(acs);
        
        Assert.assertEquals(getName(acs), "A-AQ-0", "Wrong service selected");
    }
    
    /**
     * Test invalid index.
     * 
     * @throws ResolverException ...
     */
    @Test
    public void testInvalidIndex() throws ResolverException {
        RoleDescriptor role =  roleResolver.resolveSingle(new CriteriaSet(
                new EntityIdCriterion("urn:test:entity:A"),
                new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME),
                new ProtocolCriterion(SAMLConstants.SAML20P_NS)));
        Assert.assertNotNull(role);
        acsSelector.setRoleDescriptor(role);
        
        acsSelector.setIndex(3);
        
        AttributeConsumingService acs = acsSelector.selectService();
        Assert.assertNull(acs, "Service should have been null due to invalid index");
    }
    
    /**
     * Test invalid index with onBadIndexUseDefault of true.
     * 
     * @throws ResolverException ...
     */
    @Test
    public void testInvalidIndexWithUseDefault() throws ResolverException {
        RoleDescriptor role =  roleResolver.resolveSingle(new CriteriaSet(
                new EntityIdCriterion("urn:test:entity:A"),
                new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME),
                new ProtocolCriterion(SAMLConstants.SAML20P_NS)));
        Assert.assertNotNull(role);
        acsSelector.setRoleDescriptor(role);
        
        acsSelector.setIndex(3);
        acsSelector.setOnBadIndexUseDefault(true);
        
        AttributeConsumingService acs = acsSelector.selectService();
        Assert.assertNotNull(acs);
        
        Assert.assertEquals(getName(acs), "A-SP-0", "Wrong service selected");
    }
    
    /**
     * Test missing RoleDescriptor input.
     */
    @Test
    public void testNoRoleDescriptor() {
        AttributeConsumingService acs = acsSelector.selectService();
        Assert.assertNull(acs, "Service should have been null due to lack of role descriptor");
    }
    
    
    /////////////////////////////////
    
    /**
     * Get  the first service name of an AttributeConsumingService.
     * 
     * @param acs the attribute consuming service
     * @return the first name of the service
     */
    private String getName(AttributeConsumingService acs) {
        return acs.getNames().get(0).getValue();
    }

}
