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

package org.opensaml.saml.saml2.metadata.impl;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.metadata.AttributeProfile;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.AttributeProfileImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class AttributeProfileTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected ProfileURI */
    private String expectedProfileURI;
    
    /**
     * Constructor
     */
    public AttributeProfileTest(){
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/AttributeProfile.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedProfileURI = "http://example.org";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        AttributeProfile profile = (AttributeProfile) unmarshallElement(singleElementFile);
        assert profile!=null;        
        Assert.assertEquals(profile.getURI(), expectedProfileURI);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        AttributeProfile profile = (new AttributeProfileBuilder()).buildObject();
        
        profile.setURI(expectedProfileURI);
        
        assertXMLEquals(expectedDOM, profile);
    }
}