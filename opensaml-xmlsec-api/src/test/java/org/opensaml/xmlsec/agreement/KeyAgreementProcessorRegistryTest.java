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

package org.opensaml.xmlsec.agreement;

import java.util.Set;

import javax.annotation.Nonnull;

import org.opensaml.security.credential.Credential;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Unit test for {@link KeyAgreementProcessorRegistry}.
 */
public class KeyAgreementProcessorRegistryTest {
    
    /** Basic tests */
    @Test
    public void basic() {
        KeyAgreementProcessorRegistry registry = new KeyAgreementProcessorRegistry();
        
        Assert.assertEquals(registry.getRegisteredAlgorithms().size(), 0);
        
        KeyAgreementProcessor foo1 = new MockProcessorFoo();
        KeyAgreementProcessor foo2 = new MockProcessorFoo();
        KeyAgreementProcessor bar = new MockProcessorBar();
        
        registry.register(foo1);
        Assert.assertEquals(registry.getRegisteredAlgorithms().size(), 1);
        Assert.assertEquals(registry.getRegisteredAlgorithms(), CollectionSupport.singleton("urn:test:KeyAgreementProcessor:Foo"));
        Assert.assertSame(registry.getProcessor("urn:test:KeyAgreementProcessor:Foo"), foo1);
        
        registry.register(foo2);
        Assert.assertEquals(registry.getRegisteredAlgorithms().size(), 1);
        Assert.assertEquals(registry.getRegisteredAlgorithms(), Set.of("urn:test:KeyAgreementProcessor:Foo"));
        Assert.assertSame(registry.getProcessor("urn:test:KeyAgreementProcessor:Foo"), foo2);
        
        registry.register(bar);
        Assert.assertEquals(registry.getRegisteredAlgorithms().size(), 2);
        Assert.assertEquals(registry.getRegisteredAlgorithms(), CollectionSupport.setOf("urn:test:KeyAgreementProcessor:Foo", "urn:test:KeyAgreementProcessor:Bar"));
        Assert.assertSame(registry.getProcessor("urn:test:KeyAgreementProcessor:Foo"), foo2);
        Assert.assertSame(registry.getProcessor("urn:test:KeyAgreementProcessor:Bar"), bar);
        
        registry.deregister("urn:test:KeyAgreementProcessor:Foo");
        Assert.assertEquals(registry.getRegisteredAlgorithms().size(), 1);
        Assert.assertEquals(registry.getRegisteredAlgorithms(), CollectionSupport.singleton("urn:test:KeyAgreementProcessor:Bar"));
        Assert.assertNull(registry.getProcessor("urn:test:KeyAgreementProcessor:Foo"));
        Assert.assertSame(registry.getProcessor("urn:test:KeyAgreementProcessor:Bar"), bar);
        
        registry.clear();
        Assert.assertEquals(registry.getRegisteredAlgorithms().size(), 0);
        Assert.assertEquals(registry.getRegisteredAlgorithms(), Set.of());
        Assert.assertNull(registry.getProcessor("urn:test:KeyAgreementProcessor:Foo"));
        Assert.assertNull(registry.getProcessor("urn:test:KeyAgreementProcessor:Bar"));
    }
    
    /**
     * Mock processor.
     */
    public static class MockProcessorFoo implements KeyAgreementProcessor {

        /** {@inheritDoc} */
        @Nonnull public String getAlgorithm() {
            return "urn:test:KeyAgreementProcessor:Foo";
        }

        /** {@inheritDoc} */
        @Nonnull public KeyAgreementCredential execute(@Nonnull final Credential publicCredential,
                @Nonnull final String keyAlgorithm, @Nonnull final KeyAgreementParameters parameters)
                        throws KeyAgreementException {
            throw new UnsupportedOperationException();
        }
    }
    
    /**
     * Mock processor.
     */
    public static class MockProcessorBar implements KeyAgreementProcessor {

        /** {@inheritDoc} */
        @Nonnull public String getAlgorithm() {
            return "urn:test:KeyAgreementProcessor:Bar";
        }

        /** {@inheritDoc} */
        @Nonnull public KeyAgreementCredential execute(@Nonnull final Credential publicCredential,
                @Nonnull final String keyAlgorithm, @Nonnull final KeyAgreementParameters parameters)
                        throws KeyAgreementException {
            throw new UnsupportedOperationException();
        }
    }
}
