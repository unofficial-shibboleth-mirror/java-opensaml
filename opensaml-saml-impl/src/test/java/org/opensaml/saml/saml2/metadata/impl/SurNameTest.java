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

/**
 * 
 */
package org.opensaml.saml.saml2.metadata.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.metadata.SurName;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.SurName}.
 */
@SuppressWarnings({"null", "javadoc"})
public class SurNameTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected description */
    protected String expectedName;
    
    /**
     * Constructor
     */
    public SurNameTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/SurName.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedName = "Smith";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final SurName name = (SurName) unmarshallElement(singleElementFile);
        assert name!=null;
        Assert.assertEquals(name.getValue(), expectedName, "Name was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final SurName name = (new SurNameBuilder()).buildObject();
        
        name.setValue(expectedName);

        assertXMLEquals(expectedDOM, name);
    }
}