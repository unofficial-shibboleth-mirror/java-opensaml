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

package org.opensaml.core.xml.config.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.util.Properties;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.config.provider.ThreadLocalConfigurationPropertiesHolder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;

/**
 * A class which provides basic testing for the InitializationService.
 */
public class InitializationServiceTest {
    
    @BeforeMethod
    protected void setUp() throws Exception {
        Properties props = new Properties();
        
        props.setProperty(ConfigurationService.PROPERTY_PARTITION_NAME, this.getClass().getName());
        
        ThreadLocalConfigurationPropertiesHolder.setProperties(props);
    }

    @AfterMethod
    protected void tearDown() throws Exception {
        ThreadLocalConfigurationPropertiesHolder.clear();
    }

    /**
     * Unit test.
     * 
     * @throws InitializationException
     */
    @Test
    public void testProviderInit() throws InitializationException {
        XMLObjectProviderRegistry registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        Assert.assertNull(registry, "Registry was non-null");        
        
        InitializationService.initialize();
        
        registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        Assert.assertNotNull(registry, "Registry was null");        
    }

}
