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

package org.opensaml.xacml.ctx.provider.impl;

import java.util.Arrays;
import java.util.Collections;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.xacml.ctx.DecisionType;
import org.opensaml.xacml.ctx.DecisionType.DECISION;
import org.opensaml.xacml.ctx.ResultType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.ObligationType;
import org.opensaml.xacml.policy.ObligationsType;

/** Unit test for {@link ObligationService}. */
public class ObligationServiceTest extends XMLObjectBaseTestCase {

    private ObligationProcessingContext processingCtx;

    int count;

    @BeforeMethod
    protected void setUp() throws Exception {
        count = 0;

        ResultType result = (ResultType) builderFactory.getBuilder(ResultType.TYPE_NAME).buildObject(
                ResultType.DEFAULT_ELEMENT_NAME);
        result.setResourceId("urn:example.org:resource1");

        DecisionType decision = (DecisionType) builderFactory.getBuilder(DecisionType.TYPE_NAME).buildObject(
                DecisionType.DEFAULT_ELEMENT_NAME);
        decision.setDecision(DECISION.Permit);
        result.setDecision(decision);

        ObligationsType obligations = (ObligationsType) builderFactory.getBuilder(ObligationsType.SCHEMA_TYPE_NAME)
                .buildObject(ObligationsType.DEFAULT_ELEMENT_QNAME);
        result.setObligations(obligations);

        ObligationType obligation = (ObligationType) builderFactory.getBuilder(ObligationType.SCHEMA_TYPE_NAME)
                .buildObject(ObligationType.DEFAULT_ELEMENT_QNAME);
        obligation.setFulfillOn(EffectType.Permit);
        obligation.setObligationId("add1");
        obligations.getObligations().add(obligation);
        
        obligation = (ObligationType) builderFactory.getBuilder(ObligationType.SCHEMA_TYPE_NAME)
        .buildObject(ObligationType.DEFAULT_ELEMENT_QNAME);
        obligation.setFulfillOn(EffectType.Permit);
        obligation.setObligationId("add2");
        obligations.getObligations().add(obligation);

        obligation = (ObligationType) builderFactory.getBuilder(ObligationType.SCHEMA_TYPE_NAME).buildObject(
                ObligationType.DEFAULT_ELEMENT_QNAME);
        obligation.setFulfillOn(EffectType.Permit);
        obligation.setObligationId("multiply1");
        obligations.getObligations().add(obligation);
        
        obligation = (ObligationType) builderFactory.getBuilder(ObligationType.SCHEMA_TYPE_NAME).buildObject(
                ObligationType.DEFAULT_ELEMENT_QNAME);
        obligation.setFulfillOn(EffectType.Permit);
        obligation.setObligationId("multiply2");
        obligations.getObligations().add(obligation);

        processingCtx = new ObligationProcessingContext(result);
    }

    @Test
    public void testObligationService() throws Exception {
        ObligationService obligSrvc = new ObligationService();

        // Test basic precedence ordering
        obligSrvc.addObligationhandler(new AdditiveObligationHandler("add1", 1, 1));
        obligSrvc.addObligationhandler(new MultiplicitiveObligationHandler("multiply1", 2, 2));
        
        // Test lexical ordering when two handlers have the same precedence
        obligSrvc.addObligationhandler(Arrays.asList(
                new AdditiveObligationHandler("add2", 3, 2),
                new MultiplicitiveObligationHandler("multiply2", 3, 2)));

        obligSrvc.addObligationhandler(Collections.EMPTY_LIST);
        
        BaseObligationHandler toDel = new AdditiveObligationHandler("doo", -1, 3);
        obligSrvc.addObligationhandler(toDel);
        Assert.assertEquals(obligSrvc.getObligationHandlers().size(), 5);
        obligSrvc.removeObligationHandler(toDel);
        Assert.assertEquals(obligSrvc.getObligationHandlers().size(), 4);
        
        obligSrvc.processObligations(processingCtx);

        Assert.assertEquals(count, 8);
    }

    public class AdditiveObligationHandler extends BaseObligationHandler {

        private int addFactor;

        public AdditiveObligationHandler(String id, int precedence, int addFactor) {
            super(id, precedence);
            this.addFactor = addFactor;
        }

        /** {@inheritDoc} */
        public void evaluateObligation(ObligationProcessingContext context, ObligationType obligation)
                throws ObligationProcessingException {
            count += addFactor;
        }

    }

    public class MultiplicitiveObligationHandler extends BaseObligationHandler {

        int multiplyFactor;

        public MultiplicitiveObligationHandler(String id, int precedence, int multiplyFactor) {
            super(id, precedence);
            this.multiplyFactor = multiplyFactor;
        }

        /** {@inheritDoc} */
        public void evaluateObligation(ObligationProcessingContext context, ObligationType obligation)
                throws ObligationProcessingException {
            count *= multiplyFactor;
        }

    }
}
