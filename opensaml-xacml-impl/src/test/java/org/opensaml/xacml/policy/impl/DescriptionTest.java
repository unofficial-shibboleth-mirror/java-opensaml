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

package org.opensaml.xacml.policy.impl;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.xacml.policy.DescriptionType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.DescriptionType}.
 */
public class DescriptionTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedDescription;
    /**
     * Constructor
     */
    public DescriptionTest(){
        singleElementFile = "/org/opensaml/xacml/policy/impl/Description.xml";

        expectedDescription = "This is a Description";    
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        DescriptionType description = (DescriptionType) unmarshallElement(singleElementFile);

        Assert.assertEquals(description.getValue(), expectedDescription);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        DescriptionType description  = (new DescriptionTypeImplBuilder()).buildObject();
        
        description.setValue(expectedDescription);
        assertXMLEquals(expectedDOM, description);
    }
}