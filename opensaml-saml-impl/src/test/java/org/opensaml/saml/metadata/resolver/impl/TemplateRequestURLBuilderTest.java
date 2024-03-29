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

package org.opensaml.saml.metadata.resolver.impl;

import java.util.function.Function;

import net.shibboleth.shared.resolver.CriteriaSet;

import org.apache.velocity.app.VelocityEngine;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.resolver.impl.TemplateRequestURLBuilder.EncodingStyle;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class TemplateRequestURLBuilderTest {
    
    private VelocityEngine engine;
    
    private TemplateRequestURLBuilder function;
    
    @BeforeClass
    public void setUp() {
        engine = net.shibboleth.shared.testing.VelocityEngine.newVelocityEngine();
    }
    
    @Test
    public void testEncodedQueryParam() {
        function = new TemplateRequestURLBuilder(engine, "http://metadata.example.org/?entity=${entityID}", EncodingStyle.form);
        
        Assert.assertEquals(function.apply(new CriteriaSet(new EntityIdCriterion("http://example.org/idp"))), "http://metadata.example.org/?entity=http%3A%2F%2Fexample.org%2Fidp");
    }
    
    @Test
    public void testEncodedPath() {
        function = new TemplateRequestURLBuilder(engine, "http://metadata.example.org/entities/${entityID}", EncodingStyle.path);
        
        Assert.assertEquals(function.apply(new CriteriaSet(new EntityIdCriterion("http://example.org/idp"))), "http://metadata.example.org/entities/http:%2F%2Fexample.org%2Fidp");
    }

    @Test
    public void testEncodedFragment() {
        function = new TemplateRequestURLBuilder(engine, "http://metadata.example.org/entities#${entityID}", EncodingStyle.fragment);
        
        Assert.assertEquals(function.apply(new CriteriaSet(new EntityIdCriterion("http://example.org/idp"))), "http://metadata.example.org/entities#http://example.org/idp");
    }

    @Test
    public void testWellKnownLocationStyle() {
        function = new TemplateRequestURLBuilder(engine, "${entityID}", EncodingStyle.none);
        
        Assert.assertEquals(function.apply(new CriteriaSet(new EntityIdCriterion("http://example.org/idp"))), "http://example.org/idp");
    }
    
    @Test
    public void testTransformer() {
        Function<String,String> transformer = String::toUpperCase;
        
        function = new TemplateRequestURLBuilder(engine, "${entityID}", EncodingStyle.none, transformer);
        
        Assert.assertEquals(function.apply(new CriteriaSet(new EntityIdCriterion("http://example.org/idp"))), "HTTP://EXAMPLE.ORG/IDP");
    }
    
    @Test
    public void testNullEntityID() {
        function = new TemplateRequestURLBuilder(engine, "http://metadata.example.org/?entity=${entityID}", EncodingStyle.form);
        Assert.assertNull(function.apply(null));
    }


}
