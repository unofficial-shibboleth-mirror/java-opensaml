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

package org.opensaml.storage.impl;

import java.time.Duration;

import javax.annotation.Nonnull;

import org.opensaml.storage.EnumeratableStorageService;
import org.opensaml.storage.testing.StorageServiceTest;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/**
 * Test of {@link MemoryStorageService} implementation.
 */
public class MemoryStorageServiceTest extends StorageServiceTest {

    /** {@inheritDoc} */
    @Override
    @Nonnull protected EnumeratableStorageService getStorageService() {
        MemoryStorageService ss = new MemoryStorageService();
        ss.setId("test");
        ss.setCleanupInterval(Duration.ofSeconds(1));
        return ss;
    }
        
    /**
     * Test config.
     * 
     * @throws ComponentInitializationException
     */
    @Test
    public void validConfig() throws ComponentInitializationException {
        MemoryStorageService ss = new MemoryStorageService();
        ss.setId("test");
        ss.initialize();
        ss.destroy();
    }
    
}