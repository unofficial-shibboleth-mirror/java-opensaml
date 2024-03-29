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

import javax.annotation.Nonnull;

import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.net.CookieManager;
import net.shibboleth.shared.resource.Resource;
import net.shibboleth.shared.security.DataSealer;
import net.shibboleth.shared.security.impl.BasicKeystoreKeyStrategy;
import net.shibboleth.shared.servlet.impl.ThreadLocalHttpServletRequestSupplier;
import net.shibboleth.shared.servlet.impl.ThreadLocalHttpServletResponseSupplier;
import net.shibboleth.shared.spring.resource.ResourceHelper;

/** Base class for client storage tests. */
public class AbstractBaseClientStorageServiceTest {

    /** Storage name. */
    @Nonnull @NotEmpty public static final String STORAGE_NAME = "foo";
    
    private Resource keystoreResource;
    private Resource versionResource;

    protected void init() throws ComponentInitializationException {
        ClassPathResource resource = new ClassPathResource("/org/opensaml/storage/impl/SealerKeyStore.jks");
        Assert.assertTrue(resource.exists());
        keystoreResource = ResourceHelper.of(resource);

        resource = new ClassPathResource("/org/opensaml/storage/impl/SealerKeyStore.kver");
        Assert.assertTrue(resource.exists());
        versionResource = ResourceHelper.of(resource);
    }

    @Nonnull protected ClientStorageService getStorageService() throws ComponentInitializationException {
        final ClientStorageService ss = new ClientStorageService();
        ss.setId("test");
        ss.setStorageName(STORAGE_NAME);

        final CookieManager cm = new CookieManager();
        cm.setHttpServletRequestSupplier(new ThreadLocalHttpServletRequestSupplier());
        cm.setHttpServletResponseSupplier(new ThreadLocalHttpServletResponseSupplier());
        cm.initialize();
        ss.setCookieManager(cm);

        final BasicKeystoreKeyStrategy strategy = new BasicKeystoreKeyStrategy();
        strategy.setKeyAlias("secret");
        strategy.setKeyPassword("kpassword");
        strategy.setKeystorePassword("password");
        strategy.setKeystoreResource(keystoreResource);
        strategy.setKeyVersionResource(versionResource);

        final DataSealer sealer = new DataSealer();
        sealer.setKeyStrategy(strategy);

        try {
            strategy.initialize();
            sealer.initialize();
        } catch (ComponentInitializationException e) {
            Assert.fail(e.getMessage());
        }

        ss.setDataSealer(sealer);
        
        ss.setHttpServletRequestSupplier(new ThreadLocalHttpServletRequestSupplier());
        ss.initialize();
        
        return ss;
    }
    
}
