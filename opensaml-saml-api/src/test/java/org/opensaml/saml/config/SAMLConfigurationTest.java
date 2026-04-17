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

package org.opensaml.saml.config;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

@SuppressWarnings("javadoc")
public class SAMLConfigurationTest {
    
    @Test
    public void testBindingURLSchemes() {
        List<String> schemes = null;
        SAMLConfiguration config = new SAMLConfiguration();
        
        // Test default values
        schemes = config.getAllowedBindingURLSchemes();
        Assert.assertEquals(schemes.size(), 2);
        Assert.assertTrue(schemes.contains("http"));
        Assert.assertTrue(schemes.contains("https"));
        
        // Test normalization 
        config.setAllowedBindingURLSchemes(Lists.newArrayList("   HTTP  ",  null, "  HTTPS  ", "FooBar", "     "));
        schemes = config.getAllowedBindingURLSchemes();
        Assert.assertEquals(schemes.size(), 3);
        Assert.assertTrue(schemes.contains("http"));
        Assert.assertTrue(schemes.contains("https"));
        Assert.assertTrue(schemes.contains("foobar"));
    }
    
    @Test
    public void testDecoderLimits() {
        SAMLConfiguration config = new SAMLConfiguration();

        // Test default values
        Assert.assertFalse(config.isEnforceDecoderRequestSizeLimit());
        Assert.assertNull(config.getDecoderRequestSizeLimit());

        Assert.assertFalse(config.isEnforceDecoderResponseSizeLimit());
        Assert.assertNull(config.getDecoderResponseSizeLimit());

        Assert.assertFalse(config.isEnforceDecoderKeyInfoSizeLimit());
        Assert.assertNull(config.getDecoderKeyInfoSizeLimit());

        Assert.assertFalse(config.isDecoderEstimateInflatedSize());
        Assert.assertNull(config.getDecoderInflationFactor());
        

        // Test setters 
        config.setEnforceDecoderRequestSizeLimit(true);
        config.setDecoderRequestSizeLimit(100);

        config.setEnforceDecoderResponseSizeLimit(true);
        config.setDecoderResponseSizeLimit(200);

        config.setEnforceDecoderKeyInfoSizeLimit(true);
        config.setDecoderKeyInfoSizeLimit(300);

        config.setDecoderEstimateInflatedSize(true);
        config.setDecoderInflationFactor(1.75f);

        Assert.assertTrue(config.isEnforceDecoderRequestSizeLimit());
        Assert.assertEquals(config.getDecoderRequestSizeLimit(), 100);

        Assert.assertTrue(config.isEnforceDecoderResponseSizeLimit());
        Assert.assertEquals(config.getDecoderResponseSizeLimit(), 200);

        Assert.assertTrue(config.isEnforceDecoderKeyInfoSizeLimit());
        Assert.assertEquals(config.getDecoderKeyInfoSizeLimit(), 300);

        Assert.assertTrue(config.isDecoderEstimateInflatedSize());
        Assert.assertEquals(config.getDecoderInflationFactor(), 1.75f);
    }
    

}