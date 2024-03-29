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

package org.opensaml.xacml.ctx.impl;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.xacml.ctx.DecisionType;
import org.opensaml.xacml.ctx.DecisionType.DECISION;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.xacml.ctx.DecisionType}.
 */
public class DecisionTest extends XMLObjectProviderBaseTestCase {

    private DecisionType.DECISION expectedDecision;


    /**
     * Constructor
     */
    public DecisionTest() {
        singleElementFile = "/org/opensaml/xacml/ctx/impl/Decision.xml";

        expectedDecision = DECISION.Indeterminate;
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        DecisionType decision = (DecisionType) unmarshallElement(singleElementFile);

        Assert.assertEquals(decision.getDecision(), expectedDecision);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        DecisionType decision = (new DecisionTypeImplBuilder()).buildObject();

        decision.setDecision(expectedDecision);
        assertXMLEquals(expectedDOM, decision);
    }

}