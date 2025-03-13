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

package org.opensaml.messaging.handler.impl;

import java.util.List;
import java.util.Set;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.Pair;

/**
 * Unit test for {@link HttpServletRequestParametersValidationHandler}.
 */
public class HttpServletRequestParametersValidationHandlerTest {
    
    @DataProvider
    Object[][] requiredParamsSetterData() {
        return new Object[][] {
            new Object[] { Set.of(),
                    Set.of()},
            new Object[] { Set.of("  Foo  ", "  Bar  ", "   "),
                    Set.of("Foo", "Bar")},
        };
    }
    
    @Test(dataProvider="requiredParamsSetterData")
    public void requiredSetter(Set<String> params, Set<String> expected) throws Exception {
        HttpServletRequestParametersValidationHandler handler = new HttpServletRequestParametersValidationHandler();
        handler.setRequiredParameters(params);
        handler.setHttpServletRequestSupplier(() -> new MockHttpServletRequest());
        handler.initialize();
        
        Assert.assertEquals(handler.getRequiredParameters(), expected);
    }
    
    @DataProvider
    Object[][] uniqueParamsSetterData() {
        return new Object[][] {
            new Object[] { Set.of(),
                    Set.of()},
            new Object[] { Set.of("  Foo  ", "  Bar  ", "   "),
                    Set.of("Foo", "Bar")},
        };
    }
    
    @Test(dataProvider="uniqueParamsSetterData")
    public void uniqueSetter(Set<String> params, Set<String> expected) throws Exception {
        HttpServletRequestParametersValidationHandler handler = new HttpServletRequestParametersValidationHandler();
        handler.setUniqueParameters(params);
        handler.setHttpServletRequestSupplier(() -> new MockHttpServletRequest());
        handler.initialize();
        
        Assert.assertEquals(handler.getUniqueParameters(), expected);
    }
    
    @DataProvider
    Object[][] exclusiveParamsSetterData() {
        return new Object[][] {
            new Object[] { Set.of(Set.of()),
                    Set.of(Set.of())},
            new Object[] { Set.of(Set.of("  Foo  ", "  Bar  ", "   ")),
                    Set.of(Set.of("Foo", "Bar"))},
            new Object[] { Set.of(Set.of("  Foo  ", "  Bar  ", "   "), Set.of("  Baz   ", "   ", "   ABC  ")),
                    Set.of(Set.of("Foo", "Bar"), Set.of("Baz", "ABC"))},
        };
    }
    
    @Test(dataProvider="exclusiveParamsSetterData")
    public void exclusiveSetter(Set<Set<String>> params, Set<Set<String>> expected) throws Exception {
        HttpServletRequestParametersValidationHandler handler = new HttpServletRequestParametersValidationHandler();
        handler.setMutuallyExclusiveParameters(params);
        handler.setHttpServletRequestSupplier(() -> new MockHttpServletRequest());
        handler.initialize();
        
        Assert.assertEquals(handler.getMutuallyExclusiveParameters(), expected);
    }
    
    @DataProvider
    Object[][] requiredParamsEvalData() {
        return new Object[][] {
            new Object[] { List.of(),
                    Set.of(),
                    true},
            new Object[] { List.of(new Pair<>("Foo", new String[]{"abc"})),
                    Set.of(),
                    true},
            new Object[] { List.of(new Pair<>("Foo", new String[]{"abc"})),
                    Set.of("Foo"),
                    true},
            new Object[] { List.of(new Pair<>("Bar", new String[]{"def"})),
                    Set.of("Foo"),
                    false},
            new Object[] { List.of(),
                    Set.of("Foo"),
                    false},
        };
    }
    
    @Test(dataProvider="requiredParamsEvalData")
    public void requiredEval(List<Pair<String, String[]>> requestParams, Set<String> requiredParams, boolean valid) throws Exception {
        HttpServletRequestParametersValidationHandler handler = new HttpServletRequestParametersValidationHandler();
        handler.setRequiredParameters(requiredParams);
        evaluateRequest(handler, "required", requestParams, valid);
    }
    
    @DataProvider
    Object[][] uniqueParamsEvalData() {
        return new Object[][] {
            new Object[] { List.of(),
                    Set.of(),
                    true},
            new Object[] { List.of(),
                    Set.of("Foo"),
                    true},
            new Object[] { List.of(new Pair<>("Foo", new String[]{"abc"})),
                    Set.of("Foo"),
                    true},
            new Object[] { List.of(new Pair<>("Foo", new String[]{"abc"})),
                    Set.of(),
                    true},
            new Object[] { List.of(new Pair<>("Foo", new String[]{"abc", "def"})),
                    Set.of(),
                    true},
            new Object[] { List.of(new Pair<>("Bar", new String[]{"abc", "def"})),
                    Set.of("Foo"),
                    true},
            new Object[] { List.of(new Pair<>("Foo", new String[]{"abc", "def"})),
                    Set.of("Foo"),
                    false},
        };
    }
    
    @Test(dataProvider="uniqueParamsEvalData")
    public void uniqueEval(List<Pair<String, String[]>> requestParams, Set<String> uniqueParams, boolean valid) throws Exception {
        HttpServletRequestParametersValidationHandler handler = new HttpServletRequestParametersValidationHandler();
        handler.setUniqueParameters(uniqueParams);
        evaluateRequest(handler, "unique", requestParams, valid);
    }
    
    @DataProvider
    Object[][] mutuallyExclusiveParamsEvalData() {
        return new Object[][] {
            new Object[] { List.of(),
                    Set.of(),
                    true},
            new Object[] { List.of(new Pair<>("Foo", new String[]{"abc"})),
                    Set.of(),
                    true},
            new Object[] { List.of(new Pair<>("Foo", new String[]{"abc",}), new Pair<>("Bar", new String[] {"def"})),
                    Set.of(),
                    true},
            new Object[] { List.of(new Pair<>("Foo", new String[]{"abc",}), new Pair<>("Bar", new String[] {"def"})),
                    Set.of(Set.of("Foo")),
                    true},
            new Object[] { List.of(new Pair<>("Foo", new String[]{"abc"})),
                    Set.of(Set.of("Foo", "Bar")),
                    true},
            new Object[] { List.of(new Pair<>("Foo", new String[]{"abc",}), new Pair<>("Baz", new String[] {"def"})),
                    Set.of(Set.of("Foo", "Bar")),
                    true},
            new Object[] { List.of(new Pair<>("Foo", new String[]{"abc",}), new Pair<>("Baz", new String[] {"def"})),
                    Set.of(Set.of("Foo", "Bar"), Set.of("Bar", "Baz")),
                    true},
            new Object[] { List.of(new Pair<>("Foo", new String[]{"abc",}), new Pair<>("Bar", new String[] {"def"})),
                    Set.of(Set.of("Foo", "Bar")),
                    false},
        };
    }
    
    @Test(dataProvider="mutuallyExclusiveParamsEvalData")
    public void mutuallyExclusiveEval(List<Pair<String, String[]>> requestParams, Set<Set<String>> exclusiveParams, boolean valid) throws Exception {
        HttpServletRequestParametersValidationHandler handler = new HttpServletRequestParametersValidationHandler();
        handler.setMutuallyExclusiveParameters(exclusiveParams);
        evaluateRequest(handler, "exclusive", requestParams, valid);
    }
    
    private void evaluateRequest(HttpServletRequestParametersValidationHandler handler, String desc,
            List<Pair<String, String[]>> requestParams, boolean valid) throws Exception{

        MockHttpServletRequest request = new MockHttpServletRequest();
        for (Pair<String,String[]> requestParam : requestParams) {
            request.addParameter(requestParam.getFirst(), requestParam.getSecond()); 
        }

        handler.setHttpServletRequestSupplier(() -> request);
        handler.initialize();

        MessageContext messageContext = new MessageContext();

        try {
            handler.invoke(messageContext);
            if (!valid) {
                Assert.fail(String.format("Request evaled to valid on invalid %s params", desc));
            }
        } catch (MessageHandlerException e) {
            if (valid) {
                Assert.fail(String.format("Request evaled to invaid on valid %s params", desc));
            }
        }
    }

}
