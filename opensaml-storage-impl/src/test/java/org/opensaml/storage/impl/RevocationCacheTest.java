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

package org.opensaml.storage.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Random;

import org.opensaml.storage.impl.client.ClientStorageService;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.storage.RevocationCache;

/**
 * Tests for {@link RevocationCache}
 */
public class RevocationCacheTest {
    
    private MemoryStorageService storageService;
    
    private RevocationCache revocationCache;

    @BeforeMethod
    protected void setUp() throws ComponentInitializationException {
    
        storageService = new MemoryStorageService();
        storageService.setId("test");
        storageService.setCleanupInterval(Duration.ZERO);
        storageService.initialize();
        
        revocationCache = new RevocationCache();
        revocationCache.setEntryExpiration(Duration.ofMillis(500));
        revocationCache.setStorage(storageService);
        revocationCache.initialize();
    }
    
    @AfterMethod
    protected void tearDown() {
        revocationCache.destroy();
        storageService.destroy();
    }
    
    @Test
    public void testInit() {
        revocationCache = new RevocationCache();
        try {
            revocationCache.setStorage(null);
            Assert.fail("Null StorageService should have caused constraint violation");
        } catch (final Exception e) {
        }

        try {
            revocationCache.setStorage(new ClientStorageService());
            
            Assert.fail("ClientStorageService should have caused constraint violation");
        } catch (final Exception e) {
        }
    }
    
    
    @Test
    public void testStrictSetter() throws ComponentInitializationException {
        Assert.assertFalse(revocationCache.isStrict());
        revocationCache = new RevocationCache();
        revocationCache.setStorage(storageService);
        revocationCache.setStrict(true);
        revocationCache.initialize();
        Assert.assertTrue(revocationCache.isStrict());
    }
    
    @Test (expectedExceptions = ConstraintViolationException.class)
    public void testExpirationSetter() {
        //Must be positive
        revocationCache = new RevocationCache();
        revocationCache.setEntryExpiration(Duration.ZERO);
    }
    
    @Test 
    public void testStorageGetter() {
        Assert.assertEquals(storageService, revocationCache.getStorage());
    }
    
    @Test 
    public void testRevocationSuccess() {
        Assert.assertFalse(revocationCache.isRevoked("context", "item"));
        Assert.assertTrue(revocationCache.revoke("context", "item"));
        Assert.assertTrue(revocationCache.isRevoked("context", "item"));
    }
    
    @Test 
    public void testRevocationSuccessLongContext() throws ComponentInitializationException {
        storageService = new MemoryStorageService();
        storageService.setId("test");
        storageService.setContextSize(50);
        storageService.setCleanupInterval(Duration.ZERO);
        storageService.initialize();
        
        revocationCache = new RevocationCache();
        revocationCache.setStorage(storageService);
        revocationCache.initialize();
        
        final byte[] array = new byte[storageService.getCapabilities().getContextSize()*2];
        new Random().nextBytes(array);
        final String context = new String(array, Charset.forName("UTF-8"));
        Assert.assertTrue(context.length()>storageService.getCapabilities().getContextSize());
        Assert.assertTrue(revocationCache.isRevoked(context, "item"));
        Assert.assertFalse(revocationCache.revoke(context, "item"));
    }
    
    @Test 
    public void testRevocationSuccessLongLongItem() throws ComponentInitializationException {
        storageService = new MemoryStorageService();
        storageService.setId("test");
        storageService.setCleanupInterval(Duration.ZERO);
        storageService.setKeySize(50);
        storageService.initialize();
        revocationCache = new RevocationCache();
        revocationCache.setStorage(storageService);
        revocationCache.initialize();
        final byte[] array = new byte[storageService.getCapabilities().getKeySize()*2];
        new Random().nextBytes(array);
        final String item = new String(array, Charset.forName("UTF-8"));
        Assert.assertTrue(item.length()>storageService.getCapabilities().getKeySize());
        Assert.assertFalse(revocationCache.isRevoked("context", item));
        Assert.assertTrue(revocationCache.revoke("context", item));
        Assert.assertTrue(revocationCache.isRevoked("context", item));
    }
    
    @Test 
    public void testRevocationExpirationSuccess() throws InterruptedException {
        //Test expiration of entry (500ms)
        Assert.assertFalse(revocationCache.isRevoked("context", "item"));
        Assert.assertTrue(revocationCache.revoke("context", "item"));
        Thread.sleep(600L);
        Assert.assertFalse(revocationCache.isRevoked("context", "item"));
        //Test rolling window, second revoke updates expiration past original 500ms
        Assert.assertTrue(revocationCache.revoke("context", "item"));
        Thread.sleep(300L);
        Assert.assertTrue(revocationCache.revoke("context", "item"));
        Thread.sleep(300L);
        Assert.assertTrue(revocationCache.isRevoked("context", "item"));
    }

    @Test
    public void testRevokedRecordFetch() throws IOException {
        Assert.assertTrue(revocationCache.revoke("context", "item", "value", Duration.ofHours(1)));
        Assert.assertNull(revocationCache.getRevocationRecord("context", "item2"));
        Assert.assertEquals(revocationCache.getRevocationRecord("context", "item"), "value");
        
    }
    
}