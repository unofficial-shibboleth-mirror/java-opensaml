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

package org.opensaml.storage.testing;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.opensaml.storage.EnumeratableStorageService;
import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.StorageService;
import org.opensaml.storage.VersionMismatchException;
import org.opensaml.storage.annotation.Context;
import org.opensaml.storage.annotation.Expiration;
import org.opensaml.storage.annotation.Key;
import org.opensaml.storage.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.component.DestructableComponent;
import net.shibboleth.shared.component.InitializableComponent;

/**
 * Test of {@link StorageService} implementations.
 */
@SuppressWarnings({"javadoc", "null"})
public abstract class StorageServiceTest {
    
    protected SecureRandom random;
    
    protected EnumeratableStorageService shared;

    /**
     * Returns a fresh service instance to test.
     * 
     * @return  a new instance
     */
    @Nonnull protected abstract EnumeratableStorageService getStorageService();
    
    /** Called to init a thread in preparation to run a test. */
    protected void threadInit() {
        
    }

    @BeforeClass
    protected void setUp() throws ComponentInitializationException {
        random = new SecureRandom();
        shared = getStorageService();
        if (shared instanceof InitializableComponent) {
            ((InitializableComponent) shared).initialize();
        }
    }
    
    @AfterClass
    protected void tearDown() {
        if (shared instanceof DestructableComponent) {
            ((DestructableComponent) shared).destroy();
        }
    }
    
    @Test(threadPoolSize = 10, invocationCount = 10)
    public void strings() throws IOException {
        threadInit();
        
        final String context = Long.toString(random.nextLong());
        
        for (int i = 1; i <= 100; i++) {
            boolean result = shared.create(context, Integer.toString(i), Integer.toString(i + 1), System.currentTimeMillis() + 300000);
            Assert.assertTrue(result);
        }
        
        final List<String> keylist = new ArrayList<>();
        Iterable<String> keys = shared.getContextKeys(context);
        keys.forEach(keylist::add);
        Assert.assertEquals(keylist.size(), 100);
        
        for (int i = 1; i <= 100; i++) {
            StorageRecord<?> rec = shared.read(context, Integer.toString(i));
            assert rec!=null;
            Assert.assertEquals(rec.getValue(), Integer.toString(i + 1));
        }

        for (int i = 1; i <= 100; i++) {
            boolean result = shared.update(context, Integer.toString(i), Integer.toString(i + 2), System.currentTimeMillis() + 300000);
            Assert.assertTrue(result);
        }

        for (int i = 1; i <= 100; i++) {
            StorageRecord<?> rec = shared.read(context, Integer.toString(i));
            assert rec!=null;
            Assert.assertEquals(rec.getValue(), Integer.toString(i + 2));
        }

        for (int i = 1; i <= 100; i++) {
            boolean result = shared.create(context, Integer.toString(i), Integer.toString(i + 1), null);
            Assert.assertFalse(result, "createString should have failed");
        }        
        
        for (int i = 1; i <= 100; i++) {
            shared.delete(context, Integer.toString(i));
            StorageRecord<?> rec = shared.read(context, Integer.toString(i));
            Assert.assertNull(rec);
        }
        
        keylist.clear();
        keys = shared.getContextKeys(context);
        keys.forEach(keylist::add);
        Assert.assertEquals(keylist.size(), 0);
    }

    @Test
    public void expiration() throws IOException, InterruptedException {
        threadInit();
        
        final String context = Long.toString(random.nextLong());
        
        for (int i = 1; i <= 100; i++) {
            shared.create(context, Integer.toString(i), Integer.toString(i + 1), System.currentTimeMillis() + 5000);
        }

        Thread.sleep(5150);
        
        for (int i = 1; i <= 100; i++) {
            StorageRecord<?> rec = shared.read(context, Integer.toString(i));
            Assert.assertNull(rec);
        }
        
        Assert.assertFalse(shared.getContextKeys(context).iterator().hasNext());
    }
    
    @Test
    public void updates() throws IOException, VersionMismatchException {
        threadInit();
        
        final String key = "key";
        final String context = Long.toString(random.nextLong());
        
        shared.create(context, key, "foo", null);
        
        assertEquals(shared.updateWithVersion(1, context, key, "bar", null), 2);
        
        try {
            shared.updateWithVersion(1, context, key, "baz", null);
            Assert.fail("updateStringWithVersion should have failed");
        } catch (VersionMismatchException e) {
            // expected
        }
        
        StorageRecord<?> rec = shared.read(context, key);
        assert rec!=null;
        Assert.assertEquals(rec.getVersion(), 2);
    }
    
    @Test
    public void update() throws ComponentInitializationException, IOException {
        final String context = Long.toString(random.nextLong());
        final String value = Long.toString(random.nextLong());
        final String newValue = Long.toString(random.nextLong());
        final Long expiration = System.currentTimeMillis() + 10000;

        shared.create(context, context, value, expiration);
        StorageRecord<Object> rec = shared.read(context, context);
        assert rec!=null;
        assertEquals(rec.getValue(), value);
        assertEquals(rec.getExpiration(), expiration);

        shared.updateExpiration(context, context, expiration+50000);
        rec = shared.read(context, context);
        assert rec!=null;
        assertEquals(rec.getValue(), value);
        assertNotEquals(rec.getExpiration(), expiration);

        shared.update(context, context, newValue, expiration+100000);
        rec = shared.read(context, context);
        assert rec!=null;
        assertEquals(rec.getValue(), newValue);
        assertNotEquals(rec.getExpiration(), expiration);
    }

    @Test
    public void objects() throws IOException, InterruptedException {
        threadInit();
        
        final AnnotatedObject o1 = new AnnotatedObject();
        final AnnotatedObject o2 = new AnnotatedObject();
        
        o1.generate();
        shared.create(o1);
        
        o2.setContext(o1.getContext());
        o2.setKey(o1.getKey());
        Assert.assertSame(o2, shared.read(o2));
        Assert.assertEquals(o1.getValue(), o2.getValue());
        
        o2.setValue("foo");
        o2.setExpiration(System.currentTimeMillis() + 10000);
        shared.update(o2);
        
        shared.read(o1);
        Assert.assertEquals(o1.getValue(), "foo");
        Assert.assertEquals(o1.getExpiration(), o2.getExpiration());
        
        Thread.sleep(10100);
        
        Assert.assertNull(shared.read(o2));
    }
    
    @Context("context")
    @Key("key")
    @Value("value")
    @Expiration("expiration")
    private class AnnotatedObject {

        private String context;
        private String key;
        private String value;
        private Long expiration;
        
        public void generate() {
            context = Long.toString(random.nextLong());
            key = Long.toString(random.nextLong());
            value = Long.toString(random.nextLong());
            expiration = System.currentTimeMillis() + 60000;
        }
        
        public String getContext() {
            return context;
        }
        
        public void setContext(String c) {
            context = c;
        }
        
        public String getKey() {
            return key;
        }
        
        public void setKey(String k) {
            key = k;
        }
        
        public String getValue() {
            return value;
        }
        
        public void setValue(String val) {
            value = val;
        }
        
        public long getExpiration() {
            return expiration;
        }
        
        public void setExpiration(long exp) {
            expiration = exp;
        }
        
    }
}