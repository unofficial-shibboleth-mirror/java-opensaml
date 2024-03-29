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
import org.opensaml.xacml.policy.VariableDefinitionType;
import org.opensaml.xacml.policy.VariableReferenceType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.VariableDefinitionType}.
 */
public class VariableDefinitionTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected ProfileURI */
    private String expectedVariableDefinitionId;
    private String expectedVariableReferenceId;
    
    /**
     * Constructor
     */
    public VariableDefinitionTest(){
        singleElementFile = "/org/opensaml/xacml/policy/impl/VariableDefinition.xml";
        childElementsFile  = "/org/opensaml/xacml/policy/impl/VariableDefinitionChildElements.xml";

        expectedVariableDefinitionId = "VariableDefinitionId";
        expectedVariableReferenceId = "VariableReferenceId";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        VariableDefinitionType definition = (VariableDefinitionType) unmarshallElement(singleElementFile);
        
        Assert.assertEquals(definition.getVariableId(), expectedVariableDefinitionId);
        Assert.assertNull(definition.getExpression());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        VariableDefinitionType definition = (new VariableDefinitionTypeImplBuilder()).buildObject();
        definition.setVariableId(expectedVariableDefinitionId);
        
        assertXMLEquals(expectedDOM, definition);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        VariableDefinitionType definition = (VariableDefinitionType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(definition.getVariableId(), expectedVariableDefinitionId);
        VariableReferenceType variableReference = (VariableReferenceType) definition.getExpression();
        Assert.assertEquals(variableReference.getVariableId(), expectedVariableReferenceId);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        VariableDefinitionType definition = (new VariableDefinitionTypeImplBuilder()).buildObject();
        definition.setVariableId(expectedVariableDefinitionId);

        VariableReferenceType variableReference   = (new VariableReferenceTypeImplBuilder()).buildObject();
        variableReference.setVariableId(expectedVariableReferenceId);
        definition.setExpression(variableReference);
        
        assertXMLEquals(expectedChildElementsDOM, definition);
    }
}