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

package org.opensaml.storage.impl.client;

import java.io.IOException;
import java.util.concurrent.locks.Lock;

import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.StorageService;
import org.opensaml.storage.impl.client.ClientStorageService.ClientStorageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.net.UrlEscapers;

import jakarta.servlet.http.Cookie;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.servlet.impl.HttpServletRequestResponseContext;
import net.shibboleth.shared.servlet.impl.ThreadLocalHttpServletRequestSupplier;

/** Unit test for {@link LoadClientStorageServices}. */
@SuppressWarnings("javadoc")
public class LoadClientStorageServicesTest extends AbstractBaseClientStorageServiceTest {

    private ProfileRequestContext prc;
    private ClientStorageLoadContext loadCtx;
    
    private LoadClientStorageServices action;

    @BeforeClass public void setUpClass() throws ComponentInitializationException {
        init();
    }

    @BeforeMethod public void setUp() {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        loadCtx = prc.getOrCreateSubcontext(ClientStorageLoadContext.class);
        loadCtx.getStorageKeys().add(STORAGE_NAME);
        
        action = new LoadClientStorageServices();
        action.setHttpServletRequestSupplier(new ThreadLocalHttpServletRequestSupplier());
        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());
    }

    @Test public void testNoContext() throws ComponentInitializationException {
        action.setStorageServices(CollectionSupport.singletonList(getStorageService()));
        action.initialize();
        
        prc.removeSubcontext(loadCtx);
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_PROFILE_CTX);
    }

    @Test public void testNoKeys() throws ComponentInitializationException {
        action.initialize();
                
        loadCtx.getStorageKeys().clear();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    @Test public void testNoServices() throws ComponentInitializationException {
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }
 
    @Test public void testEmpty() throws ComponentInitializationException, IOException {
        final ClientStorageService ss = getStorageService();

        final Lock lock = ss.getLock().readLock();
        try {
            lock.lock();
            ss.getContextMap();
            Assert.fail("getContextMap should have failed for unloaded storage service");
        } catch(final IOException e) {
            
        } finally {
            lock.unlock();
        }

        action.setStorageServices(CollectionSupport.singletonList(ss));
        action.initialize();

        final Cookie cookie = new Cookie("bar", "ignored");
        final MockHttpServletRequest request = (MockHttpServletRequest) HttpServletRequestResponseContext.getRequest();
        assert request != null;
        request.setCookies(cookie);
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertNull(loadCtx.getParent());
        
        try {
            lock.lock();
            Assert.assertTrue(ss.getContextMap().isEmpty());
        } finally {
            lock.unlock();
        }
    }

    @Test public void testInvalid() throws ComponentInitializationException, IOException {
        final ClientStorageService ss = getStorageService();
        action.setStorageServices(CollectionSupport.singletonList(ss));
        action.initialize();

        final Cookie cookie = new Cookie(STORAGE_NAME, "error");
        final MockHttpServletRequest request = (MockHttpServletRequest) HttpServletRequestResponseContext.getRequest();
        assert request != null;
        request.setCookies(cookie);
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertNull(loadCtx.getParent());
        
        final Lock lock = ss.getLock().readLock();
        try {
            lock.lock();
            Assert.assertTrue(ss.getContextMap().isEmpty());
        } finally {
            lock.unlock();
        }
    }

    @Test public void testCookieLoad() throws ComponentInitializationException, IOException {
        final ClientStorageService ss = getStorageService();
        ss.load(null, ClientStorageSource.COOKIE);
        ss.create("context1", "key1", "value1", null);
        ss.create("context1", "key2", "value2", null);
        ss.create("context2", "key", "value", null);
        
        final ClientStorageServiceOperation saved = ss.save();
        Assert.assertNotNull(saved);

        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());

        Assert.assertFalse(ss.isLoaded());
        
        assert saved != null;
        final Cookie cookie = new Cookie("foo", UrlEscapers.urlFormParameterEscaper().escape(saved.getValue()));
        final MockHttpServletRequest request = (MockHttpServletRequest) HttpServletRequestResponseContext.getRequest();
        assert request != null;
        request.setCookies(cookie);

        action.setUseLocalStorage(true);
        action.setStorageServices(CollectionSupport.singletonList(ss));
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        checkStorageContent(ss);
    }

    @Test public void testFormLoad() throws ComponentInitializationException, IOException {
        final ClientStorageService ss = getStorageService();
        ss.load(null, ClientStorageSource.COOKIE);
        ss.create("context1", "key1", "value1", null);
        ss.create("context1", "key2", "value2", null);
        ss.create("context2", "key", "value", null);
        
        final ClientStorageServiceOperation saved = ss.save();
        assert saved != null;

        final String value = saved.getValue();
        assert value != null;
        
        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());

        Assert.assertFalse(ss.isLoaded());
        
        final MockHttpServletRequest request = (MockHttpServletRequest) HttpServletRequestResponseContext.getRequest();
        assert request != null;
        request.setParameter(LoadClientStorageServices.SUPPORT_FORM_FIELD, "true");
        request.setParameter(LoadClientStorageServices.SUCCESS_FORM_FIELD + '.' + ss.getStorageName(), "true");
        request.setParameter(LoadClientStorageServices.VALUE_FORM_FIELD + '.' + ss.getStorageName(), value);

        action.setUseLocalStorage(true);
        action.setStorageServices(CollectionSupport.singletonList(ss));
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        checkStorageContent(ss);
    }

    private void checkStorageContent(final StorageService ss) throws IOException {
        Assert.assertNull(loadCtx.getParent());

        StorageRecord<?> record = ss.read("context1", "key1");
        assert record != null;
        Assert.assertEquals(record.getValue(), "value1");
        Assert.assertNull(record.getExpiration());
        
        record = ss.read("context1", "key2");
        assert record != null;
        Assert.assertEquals(record.getValue(), "value2");
        Assert.assertNull(record.getExpiration());

        record = ss.read("context2", "key");
        assert record != null;
        Assert.assertEquals(record.getValue(), "value");
        Assert.assertNull(record.getExpiration());
    }
    
}
