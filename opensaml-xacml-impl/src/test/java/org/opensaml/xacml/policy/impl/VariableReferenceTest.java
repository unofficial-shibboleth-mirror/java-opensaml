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
import org.opensaml.xacml.policy.VariableReferenceType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.VariableReferenceType}.
 */
public class VariableReferenceTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedVariableId;
    /**
     * Constructor
     */
    public VariableReferenceTest(){
        singleElementFile = "/org/opensaml/xacml/policy/impl/VariableReference.xml";

        expectedVariableId = "VariableReferenceId";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        VariableReferenceType variableReference = (VariableReferenceType) unmarshallElement(singleElementFile);

        Assert.assertEquals(variableReference.getVariableId(), expectedVariableId);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        VariableReferenceType variableReference   = (new VariableReferenceTypeImplBuilder()).buildObject();
        
        variableReference.setVariableId(expectedVariableId);
        assertXMLEquals(expectedDOM, variableReference );
    }
    
}