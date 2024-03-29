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

package org.opensaml.xmlsec.impl;

import static org.testng.Assert.assertNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.shibboleth.shared.logic.ConstraintViolationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.criterion.DecryptionConfigurationCriterion;
import org.opensaml.xmlsec.criterion.DecryptionRecipientsCriterion;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.InlineEncryptedKeyResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings({"javadoc", "null"})
public class BasicDecryptionParametersResolverTest {
    
    private BasicDecryptionParametersResolver resolver;
    
    private CriteriaSet criteriaSet;
    
    private DecryptionConfigurationCriterion criterion;
    
    private BasicDecryptionConfiguration config1, config2, config3;
    
    private KeyInfoCredentialResolver controlKeyInfoResolver1, controlKeyInfoResolver2, controlKeyInfoResolver3;
    
    private EncryptedKeyResolver controlEncKeyResolver1, controlEncKeyResolver2, controlEncKeyResolver3;
    
    
    @BeforeClass
    public void buildResolvers() {
        controlKeyInfoResolver1 = new StaticKeyInfoCredentialResolver(new ArrayList<>());
        controlKeyInfoResolver2 = new StaticKeyInfoCredentialResolver(new ArrayList<>());
        controlKeyInfoResolver3 = new StaticKeyInfoCredentialResolver(new ArrayList<>());
        
        controlEncKeyResolver1 = new InlineEncryptedKeyResolver();
        controlEncKeyResolver2 = new InlineEncryptedKeyResolver();
        controlEncKeyResolver3 = new InlineEncryptedKeyResolver();
    }
    
    @BeforeMethod
    public void setUp() {
        resolver = new BasicDecryptionParametersResolver();
        
        config1 = new BasicDecryptionConfiguration();
        config2 = new BasicDecryptionConfiguration();
        config3 = new BasicDecryptionConfiguration();
        
        criterion = new DecryptionConfigurationCriterion(config1, config2, config3);
        
        criteriaSet = new CriteriaSet(criterion);
    }
    
    @Test
    public void testResolveDataKeyInfoCredentialResolver() throws ResolverException {
        KeyInfoCredentialResolver kiResolver;
        
        kiResolver = resolver.resolveDataKeyInfoCredentialResolver(criteriaSet);
        Assert.assertNull(kiResolver);
        
        config1.setDataKeyInfoCredentialResolver(controlKeyInfoResolver1);
        config2.setDataKeyInfoCredentialResolver(controlKeyInfoResolver2);
        config3.setDataKeyInfoCredentialResolver(controlKeyInfoResolver3);
        
        kiResolver = resolver.resolveDataKeyInfoCredentialResolver(criteriaSet);
        Assert.assertTrue(kiResolver == controlKeyInfoResolver1);
        
        config1.setDataKeyInfoCredentialResolver(null);
        
        kiResolver = resolver.resolveDataKeyInfoCredentialResolver(criteriaSet);
        Assert.assertTrue(kiResolver == controlKeyInfoResolver2);
        
        config2.setDataKeyInfoCredentialResolver(null);
        
        kiResolver = resolver.resolveDataKeyInfoCredentialResolver(criteriaSet);
        Assert.assertTrue(kiResolver == controlKeyInfoResolver3);
    }

    @Test
    public void testResolveKEKKeyInfoCredentialResolver() throws ResolverException {
        KeyInfoCredentialResolver kiResolver;
        
        kiResolver = resolver.resolveKEKKeyInfoCredentialResolver(criteriaSet);
        Assert.assertNull(kiResolver);
        
        config1.setKEKKeyInfoCredentialResolver(controlKeyInfoResolver1);
        config2.setKEKKeyInfoCredentialResolver(controlKeyInfoResolver2);
        config3.setKEKKeyInfoCredentialResolver(controlKeyInfoResolver3);
        
        kiResolver = resolver.resolveKEKKeyInfoCredentialResolver(criteriaSet);
        Assert.assertTrue(kiResolver == controlKeyInfoResolver1);
        
        config1.setKEKKeyInfoCredentialResolver(null);
        
        kiResolver = resolver.resolveKEKKeyInfoCredentialResolver(criteriaSet);
        Assert.assertTrue(kiResolver == controlKeyInfoResolver2);
        
        config2.setKEKKeyInfoCredentialResolver(null);
        
        kiResolver = resolver.resolveKEKKeyInfoCredentialResolver(criteriaSet);
        Assert.assertTrue(kiResolver == controlKeyInfoResolver3);
    }

    @Test
    public void testResolveEncryptedKeyResolver() throws ResolverException {
        EncryptedKeyResolver encKeyResolver;
        
        encKeyResolver = resolver.resolveEncryptedKeyResolver(criteriaSet);
        Assert.assertNull(encKeyResolver);
        
        config1.setEncryptedKeyResolver(controlEncKeyResolver1);
        config2.setEncryptedKeyResolver(controlEncKeyResolver2);
        config3.setEncryptedKeyResolver(controlEncKeyResolver3);
        
        encKeyResolver = resolver.resolveEncryptedKeyResolver(criteriaSet);
        Assert.assertTrue(encKeyResolver == controlEncKeyResolver1);
        
        config1.setEncryptedKeyResolver(null);
        
        encKeyResolver = resolver.resolveEncryptedKeyResolver(criteriaSet);
        Assert.assertTrue(encKeyResolver == controlEncKeyResolver2);
        
        config2.setEncryptedKeyResolver(null);
        
        encKeyResolver = resolver.resolveEncryptedKeyResolver(criteriaSet);
        Assert.assertTrue(encKeyResolver == controlEncKeyResolver3);
    }
    
    @Test
    public void testNoRecipients() throws ResolverException {
        final DecryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;

        Assert.assertNull(params.getRecipients());
    }
    
    @Test
    public void testRecipientsFromConfig() throws ResolverException {
        config1.setRecipients(Set.of("A", "B", "C"));
        
        final DecryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        final Set<String> recipients = params.getRecipients();
        assert recipients != null;

        Assert.assertNotNull(recipients);
        Assert.assertTrue(recipients.equals(Set.of("A", "B", "C")));
    }
    
    @Test
    public void testRecipientsFromCriterion() throws ResolverException {
        criteriaSet.add(new DecryptionRecipientsCriterion(Set.of("X", "Y", "Z")));
        
        final DecryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;

        final Set<String> recipients = params.getRecipients();
        assert recipients != null;

        Assert.assertNotNull(recipients);
        Assert.assertTrue(recipients.equals(Set.of("X", "Y", "Z")));
    }
    
    @Test
    public void testRecipientsFromConfigAndCriterion() throws ResolverException {
        config2.setRecipients(Set.of("A", "B", "C"));
        criteriaSet.add(new DecryptionRecipientsCriterion(Set.of("X", "Y", "Z")));
        
        final DecryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;

        final Set<String> recipients = params.getRecipients();
        assert recipients != null;

        Assert.assertNotNull(recipients);
        Assert.assertTrue(recipients.equals(Set.of("A", "B", "C", "X", "Y", "Z")));
    }
    
    @Test
    public void testResolve() throws ResolverException {
        config1.setExcludedAlgorithms(List.of("foo", "bar"));
        config1.setDataKeyInfoCredentialResolver(controlKeyInfoResolver1);
        config1.setKEKKeyInfoCredentialResolver(controlKeyInfoResolver1);
        config1.setEncryptedKeyResolver(controlEncKeyResolver1);
        
        Iterable<DecryptionParameters> paramsIter = resolver.resolve(criteriaSet);
        Assert.assertNotNull(paramsIter);
        
        Iterator<DecryptionParameters> iterator = paramsIter.iterator();
        Assert.assertNotNull(iterator);
        
        Assert.assertTrue(iterator.hasNext());
        
        DecryptionParameters params =iterator.next();
        
        Assert.assertNotNull(params);
        Assert.assertTrue(params.getDataKeyInfoCredentialResolver() == controlKeyInfoResolver1);
        Assert.assertTrue(params.getKEKKeyInfoCredentialResolver() == controlKeyInfoResolver1);
        Assert.assertTrue(params.getEncryptedKeyResolver() == controlEncKeyResolver1);
        Assert.assertTrue(params.getIncludedAlgorithms().isEmpty());
        Assert.assertEquals(params.getExcludedAlgorithms().size(), 2);
        Assert.assertTrue(params.getExcludedAlgorithms().contains("foo"));
        Assert.assertTrue(params.getExcludedAlgorithms().contains("bar"));
        
        Assert.assertFalse(iterator.hasNext());
    }
    
    @Test
    public void testResolveSingle() throws ResolverException {
        config1.setExcludedAlgorithms(List.of("foo", "bar"));
        config1.setDataKeyInfoCredentialResolver(controlKeyInfoResolver1);
        config1.setKEKKeyInfoCredentialResolver(controlKeyInfoResolver1);
        config1.setEncryptedKeyResolver(controlEncKeyResolver1);
        
        final DecryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        Assert.assertTrue(params.getDataKeyInfoCredentialResolver() == controlKeyInfoResolver1);
        Assert.assertTrue(params.getKEKKeyInfoCredentialResolver() == controlKeyInfoResolver1);
        Assert.assertTrue(params.getEncryptedKeyResolver() == controlEncKeyResolver1);
        Assert.assertTrue(params.getIncludedAlgorithms().isEmpty());
        Assert.assertEquals(params.getExcludedAlgorithms().size(), 2);
        Assert.assertTrue(params.getExcludedAlgorithms().contains("foo"));
        Assert.assertTrue(params.getExcludedAlgorithms().contains("bar"));
    }
    
    @Test
    public void testNullCriteriaSet() throws ResolverException {
        assertNull(resolver.resolveSingle(null));
    }

    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testAbsentCriterion() throws ResolverException {
        resolver.resolve(new CriteriaSet());
    }

}
