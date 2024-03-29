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

package org.opensaml.core.testing;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import java.util.Properties;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.provider.ThreadLocalConfigurationPropertiesHolder;

/**
 * An abstract base class for initializer tests which ensures are using a unique configuration
 * partition via a thread-local properties source.
 */
public abstract class InitializerBaseTestCase {
    
    /**
     * Test set up.
     * 
     * @throws Exception on error
     */
    @BeforeMethod
    protected void setUp() throws Exception {
        final Properties props = new Properties();
        
        props.setProperty(ConfigurationService.PROPERTY_PARTITION_NAME, this.getClass().getName());
        
        ThreadLocalConfigurationPropertiesHolder.setProperties(props);
    }

    /**
     * Test tear down.
     * 
     * @throws Exception on error
     */
    @AfterMethod
    protected void tearDown() throws Exception {
        ThreadLocalConfigurationPropertiesHolder.clear();
    }

}
