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

import java.util.Set;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.metadata.resolver.index.SimpleStringMetadataIndexKey;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import net.shibboleth.shared.resolver.CriteriaSet;

@SuppressWarnings("javadoc")
public class FunctionDrivenMetadataIndexTest extends OpenSAMLInitBaseTestCase {
    
    private FunctionDrivenMetadataIndex metadataIndex;
    
    private EntityDescriptor a, b, c;
    private MetadataIndexKey keyA;
    
    @BeforeClass
    protected void setUp() {
        metadataIndex = new FunctionDrivenMetadataIndex(new UppercaseEntityIdDescriptorFunction(), new SimpleStringCriteriaFunction());
        
        a = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        a.setEntityID("urn:test:a");
        keyA = new SimpleStringMetadataIndexKey("urn:test:a".toUpperCase());
        
        b = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        b.setEntityID("urn:test:b");
        new SimpleStringMetadataIndexKey("urn:test:b".toUpperCase());
        
        c = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        c.setEntityID("urn:test:c");
        new SimpleStringMetadataIndexKey("urn:test:c".toUpperCase());
    }
    
    @Test
    public void testGenerateKeysFromDescriptor() {
        final Set<MetadataIndexKey> keys = metadataIndex.generateKeys(a);
        assert keys != null;
        Assert.assertEquals(keys.size(), 1);
        Assert.assertTrue(keys.contains(keyA));
    }
    
    @Test
    public void testGenerateKeysFromCriteria() {
        CriteriaSet criteriaSet = new CriteriaSet();
        
        criteriaSet.add(new SimpleStringCriterion("URN:TEST:A"));
        
        final Set<MetadataIndexKey> keys = metadataIndex.generateKeys(criteriaSet);
        assert keys != null;
        Assert.assertEquals(keys.size(), 1);
        Assert.assertTrue(keys.contains(keyA));
    }

}
