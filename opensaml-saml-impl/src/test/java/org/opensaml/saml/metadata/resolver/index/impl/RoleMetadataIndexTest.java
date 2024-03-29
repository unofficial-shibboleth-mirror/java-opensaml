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

package org.opensaml.saml.metadata.resolver.index.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.impl.RoleDescriptorImpl;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.resolver.CriteriaSet;

@SuppressWarnings("javadoc")
public class RoleMetadataIndexTest extends XMLObjectBaseTestCase {
    
    private RoleMetadataIndex metadataIndex;
    
    private EntityDescriptor idp, sp, idpAndSp, custom;
    private MetadataIndexKey keyIDP, keySP, keyCustom;
    
    @BeforeClass
    protected void setUp() {
        metadataIndex = new RoleMetadataIndex();
        
        keyIDP = new RoleMetadataIndex.RoleMetadataIndexKey(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        keySP = new RoleMetadataIndex.RoleMetadataIndexKey(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        keyCustom = new RoleMetadataIndex.RoleMetadataIndexKey(MyCustomRoleType.TYPE_NAME);
        
        
        idp = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        idp.setEntityID("urn:test:idp");
        idp.getRoleDescriptors().add((RoleDescriptor) buildXMLObject(IDPSSODescriptor.DEFAULT_ELEMENT_NAME));
        
        sp = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        sp.setEntityID("urn:test:sp");
        sp.getRoleDescriptors().add((RoleDescriptor) buildXMLObject(SPSSODescriptor.DEFAULT_ELEMENT_NAME));
        
        idpAndSp = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        idpAndSp.setEntityID("urn:test:idpAndSp");
        idpAndSp.getRoleDescriptors().add((RoleDescriptor) buildXMLObject(IDPSSODescriptor.DEFAULT_ELEMENT_NAME));
        idpAndSp.getRoleDescriptors().add((RoleDescriptor) buildXMLObject(SPSSODescriptor.DEFAULT_ELEMENT_NAME));
        
        custom = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        custom.setEntityID("urn:test:custom");
        custom.getRoleDescriptors().add(new MyCustomRoleType(
                MyCustomRoleType.TYPE_NAME.getNamespaceURI(), 
                MyCustomRoleType.TYPE_NAME.getLocalPart(), 
                MyCustomRoleType.TYPE_NAME.getPrefix()));
    }
    
    @Test
    public void testGenerateKeysFromDescriptor() {
        Set<MetadataIndexKey> keys = null;
        
        keys = metadataIndex.generateKeys(idp);
        assert keys != null;
        Assert.assertEquals(keys.size(), 1);
        Assert.assertTrue(keys.contains(keyIDP));
        
        keys = metadataIndex.generateKeys(sp);
        assert keys != null;
        Assert.assertEquals(keys.size(), 1);
        Assert.assertTrue(keys.contains(keySP));
        
        keys = metadataIndex.generateKeys(idpAndSp);
        assert keys != null;
        Assert.assertEquals(keys.size(), 2);
        Assert.assertTrue(keys.contains(keyIDP));
        Assert.assertTrue(keys.contains(keySP));
        
        keys = metadataIndex.generateKeys(custom);
        assert keys != null;
        Assert.assertEquals(keys.size(), 1);
        Assert.assertTrue(keys.contains(keyCustom));
    }
    
    @Test
    public void testGenerateKeysFromCriteria() {
        CriteriaSet criteriaSet = new CriteriaSet();
        Set<MetadataIndexKey> keys = null;
        
        criteriaSet.clear();
        criteriaSet.add(new EntityRoleCriterion(IDPSSODescriptor.DEFAULT_ELEMENT_NAME));
        keys = metadataIndex.generateKeys(criteriaSet);
        assert keys != null;
        Assert.assertEquals(keys.size(), 1);
        Assert.assertTrue(keys.contains(keyIDP));
        
        criteriaSet.clear();
        criteriaSet.add(new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME));
        keys = metadataIndex.generateKeys(criteriaSet);
        assert keys != null;
        Assert.assertEquals(keys.size(), 1);
        Assert.assertTrue(keys.contains(keySP));
        
        criteriaSet.clear();
        criteriaSet.add(new EntityRoleCriterion(MyCustomRoleType.TYPE_NAME));
        keys = metadataIndex.generateKeys(criteriaSet);
        assert keys != null;
        Assert.assertEquals(keys.size(), 1);
        Assert.assertTrue(keys.contains(keyCustom));
    }
    
    public static class MyCustomRoleType extends RoleDescriptorImpl {
        
        @Nonnull public static final QName TYPE_NAME = new QName("urn:test:metadata", "MyCustomRoleType", "custom");

        protected MyCustomRoleType(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
                @Nullable final String namespacePrefix) {
            super(namespaceURI, elementLocalName, namespacePrefix);
        }

        /** {@inheritDoc} */
        @Nonnull public List<Endpoint> getEndpoints() {
            return CollectionSupport.emptyList();
        }

        /** {@inheritDoc} */
        @Nonnull public List<Endpoint> getEndpoints(@Nonnull final QName type) {
            return CollectionSupport.emptyList();
        }
        
    }

}