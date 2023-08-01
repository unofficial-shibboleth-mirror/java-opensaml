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

package org.opensaml.xmlsec.config.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.opensaml.core.config.provider.ThreadLocalConfigurationPropertiesHolder;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.xmlsec.agreement.KeyAgreementParameter;
import org.opensaml.xmlsec.derivation.KeyDerivation;
import org.opensaml.xmlsec.derivation.impl.ConcatKDF;
import org.opensaml.xmlsec.derivation.impl.PBKDF2;
import org.opensaml.xmlsec.encryption.support.KeyAgreementEncryptionConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class DefaultSecurityConfigurationBootstrapTest {
    
    @Test
    public void testECDHDefaultKDF() {
        final Map<String, KeyAgreementEncryptionConfiguration> kaConfigs = DefaultSecurityConfigurationBootstrap.buildKeyAgreementConfigurations();
        Assert.assertTrue(kaConfigs.containsKey(JCAConstants.KEY_ALGO_EC));
        
        final KeyAgreementEncryptionConfiguration config = kaConfigs.get(JCAConstants.KEY_ALGO_EC);
        final Collection<KeyAgreementParameter> params = config.getParameters();
        assert params != null;
        
        final KeyDerivation keyDerivation = params.stream()
                .filter(KeyDerivation.class::isInstance)
                .map(KeyDerivation.class::cast)
                .findFirst().orElse(null);
        Assert.assertNotNull(keyDerivation);
        Assert.assertTrue(ConcatKDF.class.isInstance(keyDerivation));
    }

    @Test
    public void testECDHConcatKDF() {
        try {
            final Properties props = new Properties();
            props.setProperty("opensaml.config.ecdh.defaultKDF", "ConcatKDF");
            ThreadLocalConfigurationPropertiesHolder.setProperties(props);
            
            final Map<String, KeyAgreementEncryptionConfiguration> kaConfigs = DefaultSecurityConfigurationBootstrap.buildKeyAgreementConfigurations();
            Assert.assertTrue(kaConfigs.containsKey(JCAConstants.KEY_ALGO_EC));
        
            final KeyAgreementEncryptionConfiguration config = kaConfigs.get(JCAConstants.KEY_ALGO_EC);
            final Collection<KeyAgreementParameter> params = config.getParameters();
            assert params != null;
            final KeyDerivation keyDerivation = params.stream()
                    .filter(KeyDerivation.class::isInstance)
                    .map(KeyDerivation.class::cast)
                    .findFirst().orElse(null);
            Assert.assertNotNull(keyDerivation);
            Assert.assertTrue(ConcatKDF.class.isInstance(keyDerivation));
        } finally {
            ThreadLocalConfigurationPropertiesHolder.clear();
        }
        
    }
    
    @Test
    public void testECDHPBKDF2() {
        try {
            final Properties props = new Properties();
            props.setProperty("opensaml.config.ecdh.defaultKDF", "PBKDF2");
            ThreadLocalConfigurationPropertiesHolder.setProperties(props);
            
            final Map<String, KeyAgreementEncryptionConfiguration> kaConfigs = DefaultSecurityConfigurationBootstrap.buildKeyAgreementConfigurations();
            Assert.assertTrue(kaConfigs.containsKey(JCAConstants.KEY_ALGO_EC));
        
            final KeyAgreementEncryptionConfiguration config = kaConfigs.get(JCAConstants.KEY_ALGO_EC);
            final Collection<KeyAgreementParameter> params = config.getParameters();
            assert params != null;
            final KeyDerivation keyDerivation = params.stream()
                    .filter(KeyDerivation.class::isInstance)
                    .map(KeyDerivation.class::cast)
                    .findFirst().orElse(null);
            Assert.assertNotNull(keyDerivation);
            Assert.assertTrue(PBKDF2.class.isInstance(keyDerivation));
        } finally {
            ThreadLocalConfigurationPropertiesHolder.clear();
        }
        
    }
    
    @Test
    public void testECDHBadKDF() {
        try {
            final Properties props = new Properties();
            props.setProperty("opensaml.config.ecdh.defaultKDF", "BADBADBAD");
            ThreadLocalConfigurationPropertiesHolder.setProperties(props);
            
            final Map<String, KeyAgreementEncryptionConfiguration> kaConfigs = DefaultSecurityConfigurationBootstrap.buildKeyAgreementConfigurations();
            Assert.assertTrue(kaConfigs.containsKey(JCAConstants.KEY_ALGO_EC));
        
            final KeyAgreementEncryptionConfiguration config = kaConfigs.get(JCAConstants.KEY_ALGO_EC);
            final Collection<KeyAgreementParameter> params = config.getParameters();
            assert params != null;
            final KeyDerivation keyDerivation = params.stream()
                    .filter(KeyDerivation.class::isInstance)
                    .map(KeyDerivation.class::cast)
                    .findFirst().orElse(null);
            Assert.assertNull(keyDerivation);

        } finally {
            ThreadLocalConfigurationPropertiesHolder.clear();
        }
        
    }

}
