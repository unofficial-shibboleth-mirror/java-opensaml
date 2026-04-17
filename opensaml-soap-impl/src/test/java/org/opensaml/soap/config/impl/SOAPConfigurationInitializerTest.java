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

package org.opensaml.soap.config.impl;

import java.util.Properties;

import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.provider.ThreadLocalConfigurationPropertiesHolder;
import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.soap.config.SOAPConfigurationSupport;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SOAPConfigurationInitializerTest extends OpenSAMLInitBaseTestCase {
    
    @Test
    public void testDecoderSizeLimitDefaults() {
        Assert.assertFalse(SOAPConfigurationSupport.isEnforceDecoderSizeLimit());
        Assert.assertEquals(SOAPConfigurationSupport.getDecoderSizeLimit(), 2*1024*1024);
    }

    @Test
    public void testDecoderSizeLimitCustomValues() throws InitializationException {
        try {
            Properties props = new Properties();
            props.setProperty(SOAPConfigurationInitializer.CONFIG_PROPERTY_ENFORCE_SIZE_LIMIT, "true");
            props.setProperty(SOAPConfigurationInitializer.CONFIG_PROPERTY_SIZE_LIMIT, "100");

            // Note that for testing the ThreadLocalConfigurationPropertiesSource is already available, see
            // opensaml-testing/src/main/resources/META-INF/services/org.opensaml.core.config.ConfigurationPropertiesSource.
            // So all we need to do is populate the thread local holder and then clear it when done.
            ThreadLocalConfigurationPropertiesHolder.setProperties(props);
            new SOAPConfigurationInitializer().init();

            Assert.assertTrue(SOAPConfigurationSupport.isEnforceDecoderSizeLimit());
            Assert.assertEquals(SOAPConfigurationSupport.getDecoderSizeLimit(), 100);

        } finally {
            ThreadLocalConfigurationPropertiesHolder.clear();
            new SOAPConfigurationInitializer().init();
            
            // Check that things reset properly for other tests
            Assert.assertFalse(SOAPConfigurationSupport.isEnforceDecoderSizeLimit());
            Assert.assertEquals(SOAPConfigurationSupport.getDecoderSizeLimit(), 2*1024*1024);
        }
    }

}
