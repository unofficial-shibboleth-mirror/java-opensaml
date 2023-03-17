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

import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.storage.impl.client.ClientStorageService.ClientStorageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.servlet.impl.HttpServletRequestResponseContext;

/** Unit test for {@link SaveCookieBackedClientStorageServices}. */
@SuppressWarnings("javadoc")
public class SaveCookieBackedClientStorageServicesTest extends AbstractBaseClientStorageServiceTest {

    private ProfileRequestContext prc;

    private ClientStorageSaveContext saveCtx;
    
    private SaveCookieBackedClientStorageServices action;

    @BeforeClass public void setUpClass() throws ComponentInitializationException {
        init();
    }

    @BeforeMethod public void setUp() {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        saveCtx = prc.ensureSubcontext(ClientStorageSaveContext.class);
        action = new SaveCookieBackedClientStorageServices();
    }
        
    @Test public void testNoServices() throws ComponentInitializationException {
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    @Test public void testNoContext() throws ComponentInitializationException {
        action.setStorageServices(CollectionSupport.singletonList(getStorageService()));
        action.initialize();
        
        prc.removeSubcontext(saveCtx);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_PROFILE_CTX);
    }
    
    @Test public void testNoCookieSources() throws ComponentInitializationException {
        final ClientStorageService ss = getStorageService();
        action.setStorageServices(CollectionSupport.singletonList(ss));
        action.initialize();
        
        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());
        
        saveCtx.getStorageOperations().add(
                new ClientStorageServiceOperation(ss.getId(), ss.getStorageName(), "value", ClientStorageSource.HTML_LOCAL_STORAGE));
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertNull(saveCtx.getParent());
        
        final MockHttpServletResponse response = (MockHttpServletResponse) HttpServletRequestResponseContext.getResponse();
        assert response != null;
        
        Assert.assertEquals(response.getCookies().length, 0);
    }
    
    @Test public void testSave() throws ComponentInitializationException, IOException {
        final ClientStorageService ss = getStorageService();
        action.setStorageServices(CollectionSupport.singletonList(ss));
        action.initialize();
        
        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());

        saveCtx.getStorageOperations().add(
                new ClientStorageServiceOperation(ss.getId(), ss.getStorageName(), "the value", ClientStorageSource.COOKIE));

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertNull(saveCtx.getParent());
        
        final MockHttpServletResponse response = (MockHttpServletResponse) HttpServletRequestResponseContext.getResponse();
        assert response != null;
        Assert.assertEquals(response.getCookies().length, 1);
        Assert.assertEquals(response.getCookies()[0].getName(), ss.getStorageName());
        Assert.assertEquals(response.getCookies()[0].getValue(), "the+value");
    }
    
}