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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.resolver.impl.MetadataQueryProtocolRequestURLBuilder.MetadataQueryProtocolURLBuilder;
import org.opensaml.saml.metadata.resolver.index.impl.SimpleStringCriterion;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import net.shibboleth.shared.resolver.CriteriaSet;

@SuppressWarnings("javadoc")
public class MetadataQueryProtocolRequestURLBuilderTest {
    
    private MetadataQueryProtocolRequestURLBuilder function;
    
    @Test
    public void testWithoutTrailingSlash() {
        function = new MetadataQueryProtocolRequestURLBuilder("http://metadata.example.org/service");
        
        Assert.assertEquals(function.apply(new CriteriaSet(new EntityIdCriterion("http://example.org/idp"))), "http://metadata.example.org/service/entities/http:%2F%2Fexample.org%2Fidp");
    }

    @Test
    public void testWithTrailingSlash() {
        function = new MetadataQueryProtocolRequestURLBuilder("http://metadata.example.org/service/");
        
        Assert.assertEquals(function.apply(new CriteriaSet(new EntityIdCriterion("http://example.org/idp"))), "http://metadata.example.org/service/entities/http:%2F%2Fexample.org%2Fidp");
    }
    
    @Test
    public void testNullCriteria() {
        function = new MetadataQueryProtocolRequestURLBuilder("http://metadata.example.org/service/");
        Assert.assertNull(function.apply(null));
    }
    
    @Test
    public void testSecondaryURLBuilders() {
        MetadataQueryProtocolURLBuilder foo = new MockURLBuilder("tags/foo");
        MetadataQueryProtocolURLBuilder bar = new MockURLBuilder("tags/bar");
        MetadataQueryProtocolURLBuilder noValue = new MockURLBuilder(null);
        
        function = new MetadataQueryProtocolRequestURLBuilder("http://metadata.example.org/service", Lists.newArrayList(foo,bar));
        Assert.assertEquals(function.apply(new CriteriaSet(new SimpleStringCriterion("will-be-ignored"))), "http://metadata.example.org/service/tags/foo");
        
        function = new MetadataQueryProtocolRequestURLBuilder("http://metadata.example.org/service", Lists.newArrayList(bar,foo));
        Assert.assertEquals(function.apply(new CriteriaSet(new SimpleStringCriterion("will-be-ignored"))), "http://metadata.example.org/service/tags/bar");
        
        function = new MetadataQueryProtocolRequestURLBuilder("http://metadata.example.org/service", Lists.newArrayList(noValue, foo, bar));
        Assert.assertEquals(function.apply(new CriteriaSet(new SimpleStringCriterion("will-be-ignored"))), "http://metadata.example.org/service/tags/foo");
    }
    
    // Test helpers
    
    public static class MockURLBuilder implements MetadataQueryProtocolURLBuilder {
        
        private String suffix;
        
        public MockURLBuilder(@Nullable String s) {
            suffix = s;
        }

        /** {@inheritDoc} */
        public String buildURL(@Nonnull String baseURL, @Nullable CriteriaSet criteria) {
            if (suffix == null) {
                return null;
            }
            return baseURL + suffix;
        }
        
    }

}
