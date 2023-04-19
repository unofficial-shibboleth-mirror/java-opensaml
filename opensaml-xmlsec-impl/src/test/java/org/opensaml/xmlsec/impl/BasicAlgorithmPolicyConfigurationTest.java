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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.opensaml.xmlsec.AlgorithmPolicyConfiguration.Precedence;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings({"javadoc", "null"})
public class BasicAlgorithmPolicyConfigurationTest {
    
    private BasicAlgorithmPolicyConfiguration config;
    
    @BeforeMethod
    public void setUp() {
        config = new BasicAlgorithmPolicyConfiguration();
    }
    
    @Test
    public void testDefaults() {
        assertEquals(config.isIncludeMerge(), false);
        assertNotNull(config.getIncludedAlgorithms());
        assertTrue(config.getIncludedAlgorithms().isEmpty());
        
        assertEquals(config.isExcludeMerge(), true);
        assertNotNull(config.getExcludedAlgorithms());
        assertTrue(config.getExcludedAlgorithms().isEmpty());
        
        assertEquals(config.getIncludeExcludePrecedence(), Precedence.INCLUDE);
    }
    
    @Test
    public void testValidWhitelist() {
        config.setIncludedAlgorithms(Arrays.asList("  A   ", null, "   B   ", null, "   C   "));
        
        assertEquals(config.getIncludedAlgorithms().size(), 3);
        assertTrue(config.getIncludedAlgorithms().contains("A"));
        assertTrue(config.getIncludedAlgorithms().contains("B"));
        assertTrue(config.getIncludedAlgorithms().contains("C"));
    }

    @Test
    public void testNullWhitelist() {
        config.setIncludedAlgorithms(null);
        assertNotNull(config.getIncludedAlgorithms());
        assertTrue(config.getIncludedAlgorithms().isEmpty());
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testWhitelistImmutable() {
        config.setIncludedAlgorithms(Arrays.asList("A", "B", "C"));
        config.getIncludedAlgorithms().add("D");
    }

    @Test
    public void testWhitelistMerge() {
        // Test default
        assertFalse(config.isIncludeMerge());
        
        config.setIncludeMerge(true);
        assertTrue(config.isIncludeMerge());
        
        config.setIncludeMerge(false);
        assertFalse(config.isIncludeMerge());
    }

    @Test
    public void testValidBlacklist() {
        config.setExcludedAlgorithms(Arrays.asList("   A   ", null, "   B   ", null, "   C   "));
        
        assertEquals(config.getExcludedAlgorithms().size(), 3);
        assertTrue(config.getExcludedAlgorithms().contains("A"));
        assertTrue(config.getExcludedAlgorithms().contains("B"));
        assertTrue(config.getExcludedAlgorithms().contains("C"));
    }
    
    @Test
    public void testNullBlacklist() {
        config.setExcludedAlgorithms(null);
        assertNotNull(config.getExcludedAlgorithms());
        assertTrue(config.getExcludedAlgorithms().isEmpty());
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testBlacklistImmutable() {
        config.setExcludedAlgorithms(Arrays.asList("A", "B", "C"));
        config.getExcludedAlgorithms().add("D");
    }
    
    @Test
    public void testBlacklistMerge() {
        // Test default
        assertTrue(config.isExcludeMerge());
        
        config.setExcludeMerge(false);
        assertFalse(config.isExcludeMerge());
        
        config.setExcludeMerge(true);
        assertTrue(config.isExcludeMerge());
    }

    @Test
    public void testValidPrecedence() {
        // Test default
        assertEquals(config.getIncludeExcludePrecedence(), Precedence.INCLUDE);
        
        config.setIncludeExcludePrecedence(Precedence.INCLUDE);
        assertEquals(config.getIncludeExcludePrecedence(), Precedence.INCLUDE);
        
        config.setIncludeExcludePrecedence(Precedence.EXCLUDE);
        assertEquals(config.getIncludeExcludePrecedence(), Precedence.EXCLUDE);
    }
    
}