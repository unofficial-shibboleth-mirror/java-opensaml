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

package org.opensaml.security.config.impl;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.testing.InitializerBaseTestCase;
import org.opensaml.security.x509.tls.ClientTLSValidationConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class ClientTLSValidationConfigurationInitializerTest extends InitializerBaseTestCase {
    
    @Test
    public void testInit() throws InitializationException {
        ClientTLSValidationConfiguration config = ConfigurationService.get(ClientTLSValidationConfiguration.class);
        Assert.assertNull(config, "Config was non-null");
        
        ClientTLSValidationConfiguratonInitializer initializer = new ClientTLSValidationConfiguratonInitializer();
        initializer.init();
        
        config = ConfigurationService.get(ClientTLSValidationConfiguration.class);
        assert config != null;
        
        Assert.assertNotNull(config.getCertificateNameOptions());
        
    }

}
