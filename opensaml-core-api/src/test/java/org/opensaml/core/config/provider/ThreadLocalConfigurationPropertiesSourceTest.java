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

package org.opensaml.core.config.provider;

import java.util.Properties;

import org.opensaml.core.config.ConfigurationProperties;
import org.opensaml.core.config.ConfigurationPropertiesSource;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test {@link ThreadLocalConfigurationPropertiesSource}.
 */
public class ThreadLocalConfigurationPropertiesSourceTest {
    
    /** The source test to test. */
    private ConfigurationPropertiesSource source;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        final Properties props = new Properties();
        
        props.setProperty("opensaml.config.partitionName", "myapp-threadlocal");
        props.setProperty("opensaml.initializer.foo.flag", "false");
        
        ThreadLocalConfigurationPropertiesHolder.setProperties(props);
    }

    @AfterMethod
    protected void tearDown() throws Exception {
        ThreadLocalConfigurationPropertiesHolder.clear();
    }
    
    /**
     *  Test basic retrieval of properties from properties source.
     */
    @Test
    public void testSource() {
        source = new ThreadLocalConfigurationPropertiesSource();
        final ConfigurationProperties props = source.getProperties();
        assert props != null;
        
        Assert.assertEquals(props.getProperty("opensaml.config.partitionName"), "myapp-threadlocal", "Incorrect property value");
        Assert.assertEquals(props.getProperty("opensaml.initializer.foo.flag"), "false", "Incorrect property value");
    }

}
