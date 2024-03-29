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

package org.opensaml.security.x509.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.security.x509.PKIXValidationInformation;
import org.opensaml.security.x509.TrustedNamesCriterion;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

@SuppressWarnings("javadoc")
public class StaticPKIXValidationInformationResolverTest {
    
    @Test
    public void testDynamicNamesDisabled() throws ResolverException {
        final HashSet<String> staticTrustedNames = new HashSet<>(Arrays.asList("foo", "bar"));
        final ArrayList<PKIXValidationInformation> staticPKIXInfo = new ArrayList<>();
        staticPKIXInfo.add(new BasicPKIXValidationInformation(null, null, 1));
        
        final StaticPKIXValidationInformationResolver resolver =
                new StaticPKIXValidationInformationResolver(staticPKIXInfo, staticTrustedNames);
        
        Assert.assertTrue(resolver.resolve(null).iterator().hasNext());
        Assert.assertTrue(resolver.resolve(new CriteriaSet()).iterator().hasNext());
        
        Assert.assertTrue(resolver.supportsTrustedNameResolution());
        
        Set<String> trustedNamesResult = null;
        
        trustedNamesResult = resolver.resolveTrustedNames(null);
        Assert.assertNotNull(trustedNamesResult);
        Assert.assertEquals(trustedNamesResult.size(), staticTrustedNames.size());
        Assert.assertTrue(trustedNamesResult.containsAll(staticTrustedNames));
        
        trustedNamesResult = resolver.resolveTrustedNames(new CriteriaSet());
        Assert.assertNotNull(trustedNamesResult);
        Assert.assertEquals(trustedNamesResult.size(), staticTrustedNames.size());
        Assert.assertTrue(trustedNamesResult.containsAll(staticTrustedNames));
        
        final HashSet<String> dynamicTrustedNames = new HashSet<>(Arrays.asList("abc", "xyz"));
        trustedNamesResult = resolver.resolveTrustedNames(new CriteriaSet(new TrustedNamesCriterion(dynamicTrustedNames)));
        Assert.assertNotNull(trustedNamesResult);
        Assert.assertEquals(trustedNamesResult.size(), staticTrustedNames.size());
        Assert.assertTrue(trustedNamesResult.containsAll(staticTrustedNames));
    }
    
    @Test
    public void testDynamicNamesEnabled() throws ResolverException {
        final HashSet<String> staticTrustedNames = new HashSet<>(Arrays.asList("foo", "bar"));
        final ArrayList<PKIXValidationInformation> staticPKIXInfo = new ArrayList<>();
        staticPKIXInfo.add(new BasicPKIXValidationInformation(null, null, 1));
        
        final StaticPKIXValidationInformationResolver resolver =
                new StaticPKIXValidationInformationResolver(staticPKIXInfo, staticTrustedNames, true);
        
        Assert.assertTrue(resolver.resolve(null).iterator().hasNext());
        Assert.assertTrue(resolver.resolve(new CriteriaSet()).iterator().hasNext());
        
        Assert.assertTrue(resolver.supportsTrustedNameResolution());
        
        Set<String> trustedNamesResult = null;
        
        trustedNamesResult = resolver.resolveTrustedNames(null);
        Assert.assertNotNull(trustedNamesResult);
        Assert.assertEquals(trustedNamesResult.size(), staticTrustedNames.size());
        Assert.assertTrue(trustedNamesResult.containsAll(staticTrustedNames));
        
        trustedNamesResult = resolver.resolveTrustedNames(new CriteriaSet());
        Assert.assertNotNull(trustedNamesResult);
        Assert.assertEquals(trustedNamesResult.size(), staticTrustedNames.size());
        Assert.assertTrue(trustedNamesResult.containsAll(staticTrustedNames));
        
        final HashSet<String> dynamicTrustedNames = new HashSet<>(Arrays.asList("abc", "xyz"));
        trustedNamesResult = resolver.resolveTrustedNames(new CriteriaSet(new TrustedNamesCriterion(dynamicTrustedNames)));
        Assert.assertNotNull(trustedNamesResult);
        Assert.assertEquals(trustedNamesResult.size(), staticTrustedNames.size() + dynamicTrustedNames.size());
        Assert.assertTrue(trustedNamesResult.containsAll(staticTrustedNames));
        Assert.assertTrue(trustedNamesResult.containsAll(dynamicTrustedNames));
    }
    
    @Test
    public void testEntityIdIncluded() throws ResolverException {
        final HashSet<String> staticTrustedNames = new HashSet<>(Arrays.asList("foo", "bar"));
        final ArrayList<PKIXValidationInformation> staticPKIXInfo = new ArrayList<>();
        staticPKIXInfo.add(new BasicPKIXValidationInformation(null, null, 1));
        
        final StaticPKIXValidationInformationResolver resolver =
                new StaticPKIXValidationInformationResolver(staticPKIXInfo, staticTrustedNames, true);
        
        Set<String> trustedNamesResult = null;
        
        CriteriaSet criteriaSet = new CriteriaSet(new EntityIdCriterion("myEntity"));
        trustedNamesResult = resolver.resolveTrustedNames(criteriaSet);
        Assert.assertEquals(trustedNamesResult.size(), staticTrustedNames.size() + 1);
        Assert.assertTrue(trustedNamesResult.containsAll(staticTrustedNames));
        Assert.assertTrue(trustedNamesResult.contains("myEntity"));
        
        final HashSet<String> dynamicTrustedNames = new HashSet<>(Arrays.asList("abc", "xyz"));
        criteriaSet = new CriteriaSet(new EntityIdCriterion("myEntity"), new TrustedNamesCriterion(dynamicTrustedNames));
        trustedNamesResult = resolver.resolveTrustedNames(criteriaSet);
        Assert.assertEquals(trustedNamesResult.size(), staticTrustedNames.size() + dynamicTrustedNames.size() + 1);
        Assert.assertTrue(trustedNamesResult.containsAll(staticTrustedNames));
        Assert.assertTrue(trustedNamesResult.containsAll(dynamicTrustedNames));
        Assert.assertTrue(trustedNamesResult.contains("myEntity"));
    }


}
