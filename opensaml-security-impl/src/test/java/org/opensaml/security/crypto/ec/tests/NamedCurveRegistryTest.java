/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.security.crypto.ec.tests;

import java.security.spec.ECParameterSpec;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.provider.ThreadLocalConfigurationPropertiesHolder;
import org.opensaml.security.config.GlobalNamedCurveRegistryInitializer;
import org.opensaml.security.crypto.ec.ECSupport;
import org.opensaml.security.crypto.ec.NamedCurve;
import org.opensaml.security.crypto.ec.NamedCurveRegistry;
import org.opensaml.security.crypto.ec.curves.Secp256r1;
import org.opensaml.security.crypto.ec.curves.Secp384r1;
import org.opensaml.security.crypto.ec.curves.Secp521r1;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 *
 */
public class NamedCurveRegistryTest extends BaseNamedCurveTest {
    
    @Test
    public void basicOps() throws Exception {
        Secp256r1 secp256r1 = new Secp256r1();
        secp256r1.initialize();
        Secp384r1 secp384r1 = new Secp384r1();
        secp384r1.initialize();
        Secp521r1 secp521r1 = new Secp521r1();
        secp521r1.initialize();
        
        // To test lookup by a different param spec object instance
        ECParameterSpec secp256r1ParamSpec = ECSupport.convert(ECNamedCurveTable.getParameterSpec(secp256r1.getObjectIdentifier()));
        ECParameterSpec secp384r1ParamSpec = ECSupport.convert(ECNamedCurveTable.getParameterSpec(secp384r1.getObjectIdentifier()));
        ECParameterSpec secp521r1ParamSpec = ECSupport.convert(ECNamedCurveTable.getParameterSpec(secp521r1.getObjectIdentifier()));
        Assert.assertNotNull(secp256r1ParamSpec);
        Assert.assertNotNull(secp384r1ParamSpec);
        Assert.assertNotNull(secp521r1ParamSpec);
        
        
        NamedCurveRegistry registry = new NamedCurveRegistry();
        
        Assert.assertTrue(registry.getRegisteredCurves().isEmpty());
        
        Assert.assertNull(registry.getByName(secp256r1.getName()));
        Assert.assertNull(registry.getByOID(secp256r1.getObjectIdentifier()));
        Assert.assertNull(registry.getByURI(secp256r1.getURI()));
        Assert.assertNull(registry.getByParameterSpec(secp256r1.getParameterSpec()));
        Assert.assertNull(registry.getByParameterSpec(secp256r1ParamSpec));
        
        registry.register(secp256r1);
        
        Assert.assertEquals(registry.getRegisteredCurves().size(), 1);
        Assert.assertEquals(registry.getRegisteredCurves().stream().map(NamedCurve::getName).collect(Collectors.toSet()), Set.of("secp256r1"));
        Assert.assertTrue(registry.getRegisteredCurves().contains(secp256r1));
        
        Assert.assertSame(registry.getByName(secp256r1.getName()), secp256r1);
        Assert.assertSame(registry.getByOID(secp256r1.getObjectIdentifier()), secp256r1);
        Assert.assertSame(registry.getByURI(secp256r1.getURI()), secp256r1);
        Assert.assertSame(registry.getByParameterSpec(secp256r1.getParameterSpec()), secp256r1);
        Assert.assertSame(registry.getByParameterSpec(secp256r1ParamSpec), secp256r1);
        
        // Re-registering a different instance shouldn't cause a duplicate, etc, even though the object instance will change
        secp256r1 = new Secp256r1();
        secp256r1.initialize();
        registry.register(secp256r1);
        Assert.assertEquals(registry.getRegisteredCurves().size(), 1);
        Assert.assertEquals(registry.getRegisteredCurves().stream().map(NamedCurve::getName).collect(Collectors.toSet()), Set.of("secp256r1"));
        Assert.assertTrue(registry.getRegisteredCurves().contains(secp256r1));
        
        registry.register(secp384r1);
        registry.register(secp521r1);
        
        Assert.assertEquals(registry.getRegisteredCurves().size(), 3);
        Assert.assertEquals(registry.getRegisteredCurves().stream().map(NamedCurve::getName).collect(Collectors.toSet()),
                CollectionSupport.setOf("secp256r1", "secp384r1", "secp521r1"));
        Assert.assertTrue(registry.getRegisteredCurves().contains(secp256r1));
        Assert.assertTrue(registry.getRegisteredCurves().contains(secp384r1));
        Assert.assertTrue(registry.getRegisteredCurves().contains(secp521r1));
        
        Assert.assertSame(registry.getByName(secp384r1.getName()), secp384r1);
        Assert.assertSame(registry.getByOID(secp384r1.getObjectIdentifier()), secp384r1);
        Assert.assertSame(registry.getByURI(secp384r1.getURI()), secp384r1);
        Assert.assertSame(registry.getByParameterSpec(secp384r1.getParameterSpec()), secp384r1);
        Assert.assertSame(registry.getByParameterSpec(secp384r1ParamSpec), secp384r1);
        
        Assert.assertSame(registry.getByName(secp521r1.getName()), secp521r1);
        Assert.assertSame(registry.getByOID(secp521r1.getObjectIdentifier()), secp521r1);
        Assert.assertSame(registry.getByURI(secp521r1.getURI()), secp521r1);
        Assert.assertSame(registry.getByParameterSpec(secp521r1.getParameterSpec()), secp521r1);
        Assert.assertSame(registry.getByParameterSpec(secp521r1ParamSpec), secp521r1);
        
        registry.deregister(secp521r1);
        
        Assert.assertEquals(registry.getRegisteredCurves().size(), 2);
        Assert.assertEquals(registry.getRegisteredCurves().stream().map(NamedCurve::getName).collect(Collectors.toSet()),
                CollectionSupport.setOf("secp256r1", "secp384r1"));
        Assert.assertTrue(registry.getRegisteredCurves().contains(secp256r1));
        Assert.assertTrue(registry.getRegisteredCurves().contains(secp384r1));
        Assert.assertFalse(registry.getRegisteredCurves().contains(secp521r1));
        
        Assert.assertNull(registry.getByName(secp521r1.getName()));
        Assert.assertNull(registry.getByOID(secp521r1.getObjectIdentifier()));
        Assert.assertNull(registry.getByURI(secp521r1.getURI()));
        Assert.assertNull(registry.getByParameterSpec(secp521r1.getParameterSpec()));
        Assert.assertNull(registry.getByParameterSpec(secp521r1ParamSpec));
        
        registry.deregisterByName(secp256r1.getName());
        registry.deregisterByOID(secp384r1.getObjectIdentifier());
        
        Assert.assertTrue(registry.getRegisteredCurves().isEmpty());
        
        registry.register(secp256r1);
        Assert.assertEquals(registry.getRegisteredCurves().size(), 1);
        Assert.assertTrue(registry.getRegisteredCurves().contains(secp256r1));
        registry.deregisterByURI(secp256r1.getURI());
        Assert.assertTrue(registry.getRegisteredCurves().isEmpty());
        Assert.assertFalse(registry.getRegisteredCurves().contains(secp256r1));
        
        registry.register(secp256r1);
        Assert.assertEquals(registry.getRegisteredCurves().size(), 1);
        Assert.assertTrue(registry.getRegisteredCurves().contains(secp256r1));
        registry.deregisterByParameterSpec(secp256r1ParamSpec);
        Assert.assertTrue(registry.getRegisteredCurves().isEmpty());
        Assert.assertFalse(registry.getRegisteredCurves().contains(secp256r1));
        
        registry.register(secp256r1);
        registry.register(secp384r1);
        Assert.assertEquals(registry.getRegisteredCurves().size(), 2);
        Assert.assertTrue(registry.getRegisteredCurves().contains(secp256r1));
        Assert.assertTrue(registry.getRegisteredCurves().contains(secp384r1));
        
        registry.clear();
        Assert.assertTrue(registry.getRegisteredCurves().isEmpty());
    }
    
    @Test
    public void globalRegistry() {
        NamedCurveRegistry registry = ECSupport.getGlobalNamedCurveRegistry();
        Assert.assertNotNull(registry);
        
        // Test that it has at least the 3 main ones.
        Assert.assertTrue(registry.getRegisteredCurves().size() >= 3);
        Set<String> curveNames = registry.getRegisteredCurves().stream().map(NamedCurve::getName).collect(Collectors.toSet());
        Assert.assertTrue(curveNames.contains("secp256r1"));
        Assert.assertTrue(curveNames.contains("secp384r1"));
        Assert.assertTrue(curveNames.contains("secp521r1"));
    }
    
    @Test
    public void registerBouncyCastleCurves() throws Exception {
        String propName = GlobalNamedCurveRegistryInitializer.CONFIG_PROPERTY_REGISTER_BOUNCY_CASTLE_CURVES;
        NamedCurveRegistry origRegistry = ECSupport.getGlobalNamedCurveRegistry();
        try {
            Properties props = new Properties();
            props.setProperty(propName, "true");
            ThreadLocalConfigurationPropertiesHolder.setProperties(props);
            
            new GlobalNamedCurveRegistryInitializer().init();
            NamedCurveRegistry bcRegistry = ECSupport.getGlobalNamedCurveRegistry();
            Assert.assertNotSame(bcRegistry, origRegistry);
            
            Set<NamedCurve> origCurves = origRegistry.getRegisteredCurves();
            Set<NamedCurve> bcCurves = bcRegistry.getRegisteredCurves();
            Assert.assertTrue(bcCurves.size() > origCurves.size());
            
            Set<String> origOIDs = origCurves.stream().map(NamedCurve::getObjectIdentifier).collect(Collectors.toSet());
            Set<String> bcOIDs = bcCurves.stream().map(NamedCurve::getObjectIdentifier).collect(Collectors.toSet());
            Assert.assertTrue(bcOIDs.containsAll(origOIDs));
        } finally {
            ThreadLocalConfigurationPropertiesHolder.clear();
            ConfigurationService.register(NamedCurveRegistry.class, origRegistry);
        }
    }

}
