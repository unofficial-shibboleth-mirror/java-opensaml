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

import java.util.Arrays;
import java.util.List;

import org.opensaml.xmlsec.WhitelistBlacklistConfiguration.Precedence;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.logic.ConstraintViolationException;

@SuppressWarnings("removal")
public class BasicWhitelistBlacklistConfigurationTest {
    
    private BasicWhitelistBlacklistConfiguration config;
    
    @BeforeMethod
    public void setUp() {
        config = new BasicWhitelistBlacklistConfiguration();
    }
    
    @Test
    public void testDefaults() {
        assertEquals(config.isWhitelistMerge(), false);
        assertNotNull(config.getWhitelistedAlgorithms());
        assertTrue(config.getWhitelistedAlgorithms().isEmpty());
        
        assertEquals(config.isBlacklistMerge(), true);
        assertNotNull(config.getBlacklistedAlgorithms());
        assertTrue(config.getBlacklistedAlgorithms().isEmpty());
        
        assertEquals(config.getWhitelistBlacklistPrecedence(), Precedence.WHITELIST);
    }
    
    @Test
    public void testValidWhitelist() {
        config.setWhitelistedAlgorithms(Arrays.asList("  A   ", null, "   B   ", null, "   C   "));
        
        assertEquals(config.getWhitelistedAlgorithms().size(), 3);
        assertTrue(config.getWhitelistedAlgorithms().contains("A"));
        assertTrue(config.getWhitelistedAlgorithms().contains("B"));
        assertTrue(config.getWhitelistedAlgorithms().contains("C"));
    }

    @Test
    public void testNullWhitelist() {
        config.setWhitelistedAlgorithms(null);
        assertNotNull(config.getWhitelistedAlgorithms());
        assertTrue(config.getWhitelistedAlgorithms().isEmpty());
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testWhitelistImmutable() {
        config.setWhitelistedAlgorithms(List.of("A", "B", "C"));
        config.getWhitelistedAlgorithms().add("D");
    }

    @Test
    public void testWhitelistMerge() {
        // Test default
        assertFalse(config.isWhitelistMerge());
        
        config.setWhitelistMerge(true);
        assertTrue(config.isWhitelistMerge());
        
        config.setWhitelistMerge(false);
        assertFalse(config.isWhitelistMerge());
    }

    @Test
    public void testValidBlacklist() {
        config.setBlacklistedAlgorithms(Arrays.asList("   A   ", null, "   B   ", null, "   C   "));
        
        assertEquals(config.getBlacklistedAlgorithms().size(), 3);
        assertTrue(config.getBlacklistedAlgorithms().contains("A"));
        assertTrue(config.getBlacklistedAlgorithms().contains("B"));
        assertTrue(config.getBlacklistedAlgorithms().contains("C"));
    }
    
    @Test
    public void testNullBlacklist() {
        config.setBlacklistedAlgorithms(null);
        assertNotNull(config.getBlacklistedAlgorithms());
        assertTrue(config.getBlacklistedAlgorithms().isEmpty());
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void testBlacklistImmutable() {
        config.setBlacklistedAlgorithms(List.of("A", "B", "C"));
        config.getBlacklistedAlgorithms().add("D");
    }
    
    @Test
    public void testBlacklistMerge() {
        // Test default
        assertTrue(config.isBlacklistMerge());
        
        config.setBlacklistMerge(false);
        assertFalse(config.isBlacklistMerge());
        
        config.setBlacklistMerge(true);
        assertTrue(config.isBlacklistMerge());
    }

    @Test
    public void testValidPrecedence() {
        // Test default
        assertEquals(config.getWhitelistBlacklistPrecedence(), Precedence.WHITELIST);
        
        config.setWhitelistBlacklistPrecedence(Precedence.WHITELIST);
        assertEquals(config.getWhitelistBlacklistPrecedence(), Precedence.WHITELIST);
        
        config.setWhitelistBlacklistPrecedence(Precedence.BLACKLIST);
        assertEquals(config.getWhitelistBlacklistPrecedence(), Precedence.BLACKLIST);
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testNullPrecedence() {
        config.setWhitelistBlacklistPrecedence(null);
    }
    
}
