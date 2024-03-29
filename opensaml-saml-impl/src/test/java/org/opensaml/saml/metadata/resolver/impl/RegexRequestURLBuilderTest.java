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

import org.opensaml.core.criterion.EntityIdCriterion;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.resolver.CriteriaSet;

@SuppressWarnings("javadoc")
public class RegexRequestURLBuilderTest {
    
    private RegexRequestURLBuilder function;
    
    @Test
    public void testBasic() {
        // Attempt to pluck out the domain name into match group $1.
        function = new RegexRequestURLBuilder("^https?://([a-zA-Z0-9\\.]+).*$", "http://metadata.example.org/query?domain=$1");
        
        Assert.assertEquals(function.apply(new CriteriaSet(new EntityIdCriterion("http://example.org"))), "http://metadata.example.org/query?domain=example.org");
        Assert.assertEquals(function.apply(new CriteriaSet(new EntityIdCriterion("http://example.org/"))), "http://metadata.example.org/query?domain=example.org");
        Assert.assertEquals(function.apply(new CriteriaSet(new EntityIdCriterion("http://example.org/idp"))), "http://metadata.example.org/query?domain=example.org");
        Assert.assertEquals(function.apply(new CriteriaSet(new EntityIdCriterion("http://example.org:443/idp"))), "http://metadata.example.org/query?domain=example.org");
        
        // These shouldn't match, so should return null.
        Assert.assertNull(function.apply(new CriteriaSet(new EntityIdCriterion("urn:test:foo"))));
        Assert.assertNull(function.apply(new CriteriaSet(new EntityIdCriterion("ftp://example.org"))));
    }

}
