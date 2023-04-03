/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xmlsec.impl;

import static org.testng.Assert.*;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.AlgorithmPolicyConfiguration;
import org.opensaml.xmlsec.AlgorithmPolicyConfiguration.Precedence;
import org.opensaml.xmlsec.AlgorithmPolicyParameters;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test various aspects of the {@link AbstractSecurityParametersResolver} so don't have to test
 * them in all the individual subclasses.
 */
@SuppressWarnings("javadoc")
public class AbstractSecurityParametersResolverTest extends XMLObjectBaseTestCase {
    
    private DummyParametersResolver resolver;
    
    private BasicAlgorithmPolicyConfiguration config1, config2, config3;
    private AlgorithmPolicyConfigurationCriterion criterion;
    private CriteriaSet criteriaSet;
    private Set<String> set1, set2, set3;
    
    @BeforeMethod
    public void setUp() {
        resolver = new DummyParametersResolver();
        
        config1 = new BasicAlgorithmPolicyConfiguration();
        config2 = new BasicAlgorithmPolicyConfiguration();
        config3 = new BasicAlgorithmPolicyConfiguration();
        
        criterion = new AlgorithmPolicyConfigurationCriterion(config1, config2, config3);
        
        criteriaSet = new CriteriaSet(criterion);
        
        set1 = CollectionSupport.setOf("A", "B", "C", "D");
        set2 = CollectionSupport.setOf("X", "Y", "Z");
        set3 = CollectionSupport.setOf("foo", "bar", "baz");
    }
    
    @Test
    public void testBlacklistOnlyDefaults() throws ResolverException {
        config1.setExcludedAlgorithms(set1);
        config2.setExcludedAlgorithms(set2);
        
        final AlgorithmPolicyParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        final HashSet<String> control = new HashSet<>();
        control.addAll(set1);
        control.addAll(set2);
        
        assertTrue(params.getIncludedAlgorithms().equals(CollectionSupport.emptySet()));
        assertTrue(params.getExcludedAlgorithms().equals(control));
    }
    
    @Test
    public void testBlacklistOnlyNoMerge() throws ResolverException {
        config1.setExcludedAlgorithms(set1);
        config1.setExcludeMerge(false);
        config2.setExcludedAlgorithms(set2);
        
        final AlgorithmPolicyParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertTrue(params.getIncludedAlgorithms().equals(CollectionSupport.emptySet()));
        assertTrue(params.getExcludedAlgorithms().equals(set1));
    }
    
    @Test
    public void testBlacklistOnlyWithSimpleMerge() throws ResolverException {
        config1.setExcludedAlgorithms(set1);
        config1.setExcludeMerge(true);
        config2.setExcludedAlgorithms(set2);
        
        final AlgorithmPolicyParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        HashSet<String> control = new HashSet<>();
        control.addAll(set1);
        control.addAll(set2);
        
        assertTrue(params.getIncludedAlgorithms().equals(CollectionSupport.emptySet()));
        assertTrue(params.getExcludedAlgorithms().equals(control));
    }
    
    @Test
    public void testBlacklistOnlyWithTransitiveMerge() throws ResolverException {
        config1.setExcludedAlgorithms(set1);
        config1.setExcludeMerge(true);
        config2.setExcludeMerge(true);
        config3.setExcludedAlgorithms(set3);
        
        final AlgorithmPolicyParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        HashSet<String> control = new HashSet<>();
        control.addAll(set1);
        control.addAll(set3);
        
        assertTrue(params.getIncludedAlgorithms().equals(CollectionSupport.emptySet()));
        assertTrue(params.getExcludedAlgorithms().equals(control));
    }
    
    @Test
    public void testWhitelistOnlyDefaults() throws ResolverException {
        config1.setIncludedAlgorithms(set1);
        config2.setIncludedAlgorithms(set2);
        
        final AlgorithmPolicyParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertTrue(params.getIncludedAlgorithms().equals(set1));
        assertTrue(params.getExcludedAlgorithms().equals(CollectionSupport.emptySet()));
    }
    
    @Test
    public void testWhitelistOnlyWithSimpleMerge() throws ResolverException {
        config1.setIncludedAlgorithms(set1);
        config1.setIncludeMerge(true);
        config2.setIncludedAlgorithms(set2);
        
        final AlgorithmPolicyParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        HashSet<String> control = new HashSet<>();
        control.addAll(set1);
        control.addAll(set2);
        
        assertTrue(params.getIncludedAlgorithms().equals(control));
        assertTrue(params.getExcludedAlgorithms().equals(CollectionSupport.emptySet()));
    }
    
    @Test
    public void testWhitelistOnlyWithTransitiveMerge() throws ResolverException {
        config1.setIncludedAlgorithms(set1);
        config1.setIncludeMerge(true);
        config2.setIncludeMerge(true);
        config3.setIncludedAlgorithms(set3);
        
        final AlgorithmPolicyParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        HashSet<String> control = new HashSet<>();
        control.addAll(set1);
        control.addAll(set3);
        
        assertTrue(params.getIncludedAlgorithms().equals(control));
        assertTrue(params.getExcludedAlgorithms().equals(CollectionSupport.emptySet()));
    }
    
    @Test
    public void testPrecedence() throws ResolverException {
        config1.setIncludedAlgorithms(set1);
        config1.setExcludedAlgorithms(set2);
        
        config1.setIncludeExcludePrecedence(Precedence.INCLUDE);
        
        AlgorithmPolicyParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertTrue(params.getIncludedAlgorithms().equals(set1));
        assertTrue(params.getExcludedAlgorithms().equals(CollectionSupport.emptySet()));
        
        config1.setIncludeExcludePrecedence(Precedence.EXCLUDE);
        
        params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertTrue(params.getIncludedAlgorithms().equals(CollectionSupport.emptySet()));
        assertTrue(params.getExcludedAlgorithms().equals(set2));
    }

    
    @Test
    public void testResolvePredicate() {
        Predicate<String> predicate;
        
        config1.setIncludedAlgorithms(set1);
        config1.setExcludedAlgorithms(set2);
        
        config1.setIncludeExcludePrecedence(Precedence.INCLUDE);
        
        predicate = resolver.resolveIncludeExcludePredicate(criteriaSet, CollectionSupport.listOf(config1, config2, config3));
        
        // Note: Have effective whitelist based on set1
        
        assertTrue(predicate.test("A"));
        assertTrue(predicate.test("B"));
        assertTrue(predicate.test("C"));
        assertTrue(predicate.test("D"));
        
        assertFalse(predicate.test("X"));
        assertFalse(predicate.test("Y"));
        assertFalse(predicate.test("Z"));
        assertFalse(predicate.test("foo"));
        assertFalse(predicate.test("bar"));
        assertFalse(predicate.test("bax"));
        
        config1.setIncludeExcludePrecedence(Precedence.EXCLUDE);
        
        predicate = resolver.resolveIncludeExcludePredicate(criteriaSet, CollectionSupport.listOf(config1, config2, config3));
        
        // Note: Have effective blacklist based on set2
        
        assertTrue(predicate.test("A"));
        assertTrue(predicate.test("B"));
        assertTrue(predicate.test("C"));
        assertTrue(predicate.test("D"));
        assertTrue(predicate.test("foo"));
        assertTrue(predicate.test("bar"));
        assertTrue(predicate.test("bax"));
        
        assertFalse(predicate.test("X"));
        assertFalse(predicate.test("Y"));
        assertFalse(predicate.test("Z"));
    }
    
    @Test
    public void testResolveEffectiveWhitelist() {
        Collection<String> whitelist;
        
        whitelist = resolver.resolveEffectiveIncludes(criteriaSet, criterion.getConfigurations());
        assertTrue(whitelist.isEmpty());
        
        config1.setIncludedAlgorithms(set1);
        config2.setIncludedAlgorithms(set2);
        config3.setIncludedAlgorithms(set3);   
        
        whitelist = resolver.resolveEffectiveIncludes(criteriaSet, criterion.getConfigurations());
        assertTrue(whitelist.containsAll(set1));
        assertFalse(whitelist.containsAll(set2));
        assertFalse(whitelist.containsAll(set3));
        
        config1.setIncludeMerge(true);
        
        whitelist = resolver.resolveEffectiveIncludes(criteriaSet, criterion.getConfigurations());
        
        assertTrue(whitelist.containsAll(set1));
        assertTrue(whitelist.containsAll(set2));
        assertFalse(whitelist.containsAll(set3));
        
        config1.setIncludeMerge(true);
        config2.setIncludeMerge(true);
        
        whitelist = resolver.resolveEffectiveIncludes(criteriaSet, criterion.getConfigurations());
        
        assertTrue(whitelist.containsAll(set1));
        assertTrue(whitelist.containsAll(set2));
        assertTrue(whitelist.containsAll(set3));
        
        
        // Set 1 and 2 empty
        config1.setIncludedAlgorithms(new HashSet<String>());
        config2.setIncludedAlgorithms(new HashSet<String>());
        
        config1.setIncludeMerge(true);
        config2.setIncludeMerge(true);
        
        whitelist = resolver.resolveEffectiveIncludes(criteriaSet, criterion.getConfigurations());
        
        assertFalse(whitelist.containsAll(set1));
        assertFalse(whitelist.containsAll(set2));
        assertTrue(whitelist.containsAll(set3));
    }
    
    @Test
    public void testResolveEffectiveBlacklist() {
        Collection<String> blacklist;
        
        blacklist = resolver.resolveEffectiveExcludes(criteriaSet, criterion.getConfigurations());
        assertTrue(blacklist.isEmpty());
        
        config1.setExcludedAlgorithms(set1);
        config2.setExcludedAlgorithms(set2);
        config3.setExcludedAlgorithms(set3);   
        
        blacklist = resolver.resolveEffectiveExcludes(criteriaSet, criterion.getConfigurations());
        assertTrue(blacklist.containsAll(set1));
        assertTrue(blacklist.containsAll(set2));
        assertTrue(blacklist.containsAll(set3));
        
        config2.setExcludeMerge(false);
        
        blacklist = resolver.resolveEffectiveExcludes(criteriaSet, criterion.getConfigurations());
        
        assertTrue(blacklist.containsAll(set1));
        assertTrue(blacklist.containsAll(set2));
        assertFalse(blacklist.containsAll(set3));
        
        config1.setExcludeMerge(false);
        config2.setExcludeMerge(false);
        
        blacklist = resolver.resolveEffectiveExcludes(criteriaSet, criterion.getConfigurations());
        
        assertTrue(blacklist.containsAll(set1));
        assertFalse(blacklist.containsAll(set2));
        assertFalse(blacklist.containsAll(set3));
        
        
        // Set 1 and 2 empty
        config1.setExcludedAlgorithms(new HashSet<String>());
        config2.setExcludedAlgorithms(new HashSet<String>());
        
        config1.setExcludeMerge(true);
        config2.setExcludeMerge(true);
        
        blacklist = resolver.resolveEffectiveExcludes(criteriaSet, criterion.getConfigurations());
        
        assertFalse(blacklist.containsAll(set1));
        assertFalse(blacklist.containsAll(set2));
        assertTrue(blacklist.containsAll(set3));
    }
    
    @Test
    public void testResolveEffectivePrecedence() {
        AlgorithmPolicyConfiguration.Precedence precedence;
        
        config1.setIncludeExcludePrecedence(Precedence.INCLUDE);
        precedence = resolver.resolveIncludeExcludePrecedence(criteriaSet, criterion.getConfigurations());
        assertEquals(precedence, AlgorithmPolicyConfiguration.Precedence.INCLUDE);
        
        config1.setIncludeExcludePrecedence(Precedence.EXCLUDE);
        precedence = resolver.resolveIncludeExcludePrecedence(criteriaSet, criterion.getConfigurations());
        assertEquals(precedence, AlgorithmPolicyConfiguration.Precedence.EXCLUDE);
    }
    
    @Test
    public void testKeyInfoGeneratorLookup() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair kp = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null);
        Credential cred = CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate());
        NamedKeyInfoGeneratorManager manager;
        
        manager = new NamedKeyInfoGeneratorManager();
        manager.setUseDefaultManager(false);
        manager.registerDefaultFactory(new BasicKeyInfoGeneratorFactory());
        assertNotNull(resolver.lookupKeyInfoGenerator(cred, manager, null));
        assertNull(resolver.lookupKeyInfoGenerator(cred, manager, "test"));
        
        manager = new NamedKeyInfoGeneratorManager();
        manager.setUseDefaultManager(true);
        manager.registerDefaultFactory(new BasicKeyInfoGeneratorFactory());
        assertNotNull(resolver.lookupKeyInfoGenerator(cred, manager, null));
        assertNotNull(resolver.lookupKeyInfoGenerator(cred, manager, "test"));
        
        manager = new NamedKeyInfoGeneratorManager();
        manager.registerFactory("test", new BasicKeyInfoGeneratorFactory());
        assertNull(resolver.lookupKeyInfoGenerator(cred, manager, null));
        assertNotNull(resolver.lookupKeyInfoGenerator(cred, manager, "test"));
        
        assertNull(resolver.lookupKeyInfoGenerator(cred, null, null));
        assertNull(resolver.lookupKeyInfoGenerator(cred, null, "test"));
    }
    
    
    /* Supporting classes */
    
    /** Concrete class used for testing the abstract class. */
    public class DummyParametersResolver extends AbstractSecurityParametersResolver<AlgorithmPolicyParameters> {

        /** {@inheritDoc} */
        @Nonnull
        public Iterable<AlgorithmPolicyParameters> resolve(@Nullable final CriteriaSet criteria) throws ResolverException {
            final AlgorithmPolicyParameters params = resolveSingle(criteria);
            if (params != null) {
                return CollectionSupport.singletonList(params);
            }
            return CollectionSupport.emptyList();
        }

        /** {@inheritDoc} */
        @Nullable
        public AlgorithmPolicyParameters resolveSingle(@Nullable final CriteriaSet criteria) throws ResolverException {
            
            assert criteria != null;
            final AlgorithmPolicyParameters params = new AlgorithmPolicyParameters();
            resolveAndPopulateIncludesExcludes(params, criteria, 
                    Constraint.isNotNull(criteria.get(AlgorithmPolicyConfigurationCriterion.class), "Criterion").getConfigurations());
            return params;
        }
        
    }

}
