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

package org.opensaml.saml.config.impl;

import java.util.Properties;

import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.provider.ThreadLocalConfigurationPropertiesHolder;
import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.saml.config.SAMLConfigurationSupport;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class SAMLConfigurationInitializerTest extends OpenSAMLInitBaseTestCase {
    
    @Test
    public void testDecoderSizeLimitDefaults() {

        Assert.assertFalse(SAMLConfigurationSupport.isEnforceDecoderRequestSizeLimit());
        Assert.assertEquals(SAMLConfigurationSupport.getDecoderRequestSizeLimit(), 1*1024*1024);

        Assert.assertFalse(SAMLConfigurationSupport.isEnforceDecoderResponseSizeLimit());
        Assert.assertEquals(SAMLConfigurationSupport.getDecoderResponseSizeLimit(), 2*1024*1024);

        Assert.assertFalse(SAMLConfigurationSupport.isEnforceDecoderKeyInfoSizeLimit());
        Assert.assertEquals(SAMLConfigurationSupport.getDecoderKeyInfoSizeLimit(), 2*1024*1024);

        Assert.assertTrue(SAMLConfigurationSupport.isDecoderEstimateInflatedSize());
        Assert.assertEquals(SAMLConfigurationSupport.getDecoderInflationFactor(), 1.75f);
    }

    @Test
    public void testDecoderSizeLimitCustomValues() throws InitializationException {
        try {
            Properties props = new Properties();
            props.setProperty(SAMLConfigurationInitializer.CONFIG_PROPERTY_ENFORCE_REQUEST_SIZE_LIMIT, "true");
            props.setProperty(SAMLConfigurationInitializer.CONFIG_PROPERTY_REQUEST_SIZE_LIMIT, "100");

            props.setProperty(SAMLConfigurationInitializer.CONFIG_PROPERTY_ENFORCE_RESPONSE_SIZE_LIMIT, "true");
            props.setProperty(SAMLConfigurationInitializer.CONFIG_PROPERTY_RESPONSE_SIZE_LIMIT, "200");

            props.setProperty(SAMLConfigurationInitializer.CONFIG_PROPERTY_ENFORCE_KEYINFO_SIZE_LIMIT, "true");
            props.setProperty(SAMLConfigurationInitializer.CONFIG_PROPERTY_KEYINFO_SIZE_LIMIT, "300");
            
            props.setProperty(SAMLConfigurationInitializer.CONFIG_PROPERTY_DEFLATE_ESTIMATE, "false");
            props.setProperty(SAMLConfigurationInitializer.CONFIG_PROPERTY_DEFLATE_INFLATION_FACTOR, "2.5");

            // Note that for testing the ThreadLocalConfigurationPropertiesSource is already available, see
            // opensaml-testing/src/main/resources/META-INF/services/org.opensaml.core.config.ConfigurationPropertiesSource.
            // So all we need to do is populate the thread local holder and then clear it when done.
            ThreadLocalConfigurationPropertiesHolder.setProperties(props);
            new SAMLConfigurationInitializer().init();

            Assert.assertTrue(SAMLConfigurationSupport.isEnforceDecoderRequestSizeLimit());
            Assert.assertEquals(SAMLConfigurationSupport.getDecoderRequestSizeLimit(), 100);

            Assert.assertTrue(SAMLConfigurationSupport.isEnforceDecoderResponseSizeLimit());
            Assert.assertEquals(SAMLConfigurationSupport.getDecoderResponseSizeLimit(), 200);

            Assert.assertTrue(SAMLConfigurationSupport.isEnforceDecoderKeyInfoSizeLimit());
            Assert.assertEquals(SAMLConfigurationSupport.getDecoderKeyInfoSizeLimit(), 300);

            Assert.assertFalse(SAMLConfigurationSupport.isDecoderEstimateInflatedSize());
            Assert.assertEquals(SAMLConfigurationSupport.getDecoderInflationFactor(), 2.5f);
        } finally {
            ThreadLocalConfigurationPropertiesHolder.clear();
            new SAMLConfigurationInitializer().init();
            
            // Check that things reset properly for other tests
            Assert.assertFalse(SAMLConfigurationSupport.isEnforceDecoderRequestSizeLimit());
            Assert.assertEquals(SAMLConfigurationSupport.getDecoderRequestSizeLimit(), 1*1024*1024);

            Assert.assertFalse(SAMLConfigurationSupport.isEnforceDecoderResponseSizeLimit());
            Assert.assertEquals(SAMLConfigurationSupport.getDecoderResponseSizeLimit(), 2*1024*1024);

            Assert.assertFalse(SAMLConfigurationSupport.isEnforceDecoderKeyInfoSizeLimit());
            Assert.assertEquals(SAMLConfigurationSupport.getDecoderKeyInfoSizeLimit(), 2*1024*1024);

            Assert.assertTrue(SAMLConfigurationSupport.isDecoderEstimateInflatedSize());
            Assert.assertEquals(SAMLConfigurationSupport.getDecoderInflationFactor(), 1.75f);
        }
    }

}
