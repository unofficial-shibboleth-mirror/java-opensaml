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

package org.opensaml.storage.impl.client;

import java.io.IOException;

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

/** Unit test for {@link PopulateClientStorageSaveContext}. */
@SuppressWarnings("javadoc")
public class PopulateClientStorageSaveContextTest extends AbstractBaseClientStorageServiceTest {

    private ProfileRequestContext prc;
    
    private PopulateClientStorageSaveContext action;

    @BeforeClass public void setUpClass() throws ComponentInitializationException {
        init();
    }

    @BeforeMethod public void setUp() {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        action = new PopulateClientStorageSaveContext();
    }
        
    @Test public void testNoServices() throws ComponentInitializationException {
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, PopulateClientStorageSaveContext.SAVE_NOT_NEEDED);
        Assert.assertNull(prc.getSubcontext(ClientStorageSaveContext.class));
    }
 
    @Test public void testUnloaded() throws ComponentInitializationException {
        action.setStorageServices(CollectionSupport.singletonList(getStorageService()));
        action.initialize();
        
        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());

        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, PopulateClientStorageSaveContext.SAVE_NOT_NEEDED);
        
        Assert.assertNull(prc.getSubcontext(ClientStorageSaveContext.class));
    }

    @Test public void testClean() throws ComponentInitializationException {
        final ClientStorageService ss = getStorageService();
        action.setStorageServices(CollectionSupport.singletonList(ss));
        action.initialize();
        
        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());

        ss.load(null, ClientStorageSource.HTML_LOCAL_STORAGE);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, PopulateClientStorageSaveContext.SAVE_NOT_NEEDED);
        
        Assert.assertNull(prc.getSubcontext(ClientStorageSaveContext.class));
    }

    @Test public void testDirty() throws ComponentInitializationException, IOException {
        final ClientStorageService ss = getStorageService();
        action.setStorageServices(CollectionSupport.singletonList(ss));
        action.initialize();
        
        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());

        ss.load(null, ClientStorageSource.HTML_LOCAL_STORAGE);
        ss.create("context", "key", "value", null);
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final ClientStorageSaveContext saveCtx = prc.getSubcontext(ClientStorageSaveContext.class); 
        assert saveCtx != null;
        Assert.assertTrue(saveCtx.isSourceRequired(ClientStorageSource.HTML_LOCAL_STORAGE));
        Assert.assertEquals(saveCtx.getStorageOperations().size(), 1);
        
        final ClientStorageServiceOperation op = saveCtx.getStorageOperations().iterator().next();
        Assert.assertEquals(op.getStorageServiceID(), ss.getId());
        Assert.assertEquals(op.getKey(), ss.getStorageName());
        Assert.assertEquals(op.getStorageSource(), ClientStorageSource.HTML_LOCAL_STORAGE);
    }

    @Test public void testInvalidBecomesClean() throws ComponentInitializationException, IOException {
        final ClientStorageService ss = getStorageService();
        action.setStorageServices(CollectionSupport.singletonList(ss));
        action.initialize();

        HttpServletRequestResponseContext.loadCurrent(new MockHttpServletRequest(), new MockHttpServletResponse());

        ss.load("invalid encrypted data", ClientStorageSource.HTML_LOCAL_STORAGE);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        final ClientStorageSaveContext saveCtx = prc.getSubcontext(ClientStorageSaveContext.class);
        assert saveCtx != null;
        Assert.assertTrue(saveCtx.isSourceRequired(ClientStorageSource.HTML_LOCAL_STORAGE));
        Assert.assertEquals(saveCtx.getStorageOperations().size(), 1);

        final ClientStorageServiceOperation op = saveCtx.getStorageOperations().iterator().next();
        Assert.assertEquals(op.getStorageServiceID(), ss.getId());
        Assert.assertEquals(op.getKey(), ss.getStorageName());
        Assert.assertNull(op.getValue());
        Assert.assertEquals(op.getStorageSource(), ClientStorageSource.HTML_LOCAL_STORAGE);

        prc.removeSubcontext(ClientStorageSaveContext.class);

        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, PopulateClientStorageSaveContext.SAVE_NOT_NEEDED);

        Assert.assertNull(prc.getSubcontext(ClientStorageSaveContext.class));
    }
   
}