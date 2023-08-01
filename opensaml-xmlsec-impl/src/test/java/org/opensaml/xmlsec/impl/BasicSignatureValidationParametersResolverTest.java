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

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.shibboleth.shared.logic.ConstraintViolationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.criterion.SignatureValidationConfigurationCriterion;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings({"javadoc", "null"})
public class BasicSignatureValidationParametersResolverTest {
    
    private BasicSignatureValidationParametersResolver resolver;
    
    private CriteriaSet criteriaSet;
    
    private SignatureValidationConfigurationCriterion criterion;
    
    private BasicSignatureValidationConfiguration config1, config2, config3;
    
    private SignatureTrustEngine controlTrustEngine1, controlTrustEngine2, controlTrustEngine3;
    
    @BeforeClass
    public void buildTrustEngines() {
        CredentialResolver credResolver = new StaticCredentialResolver(new ArrayList<Credential>());
        KeyInfoCredentialResolver keyInfoResolver = new StaticKeyInfoCredentialResolver(new ArrayList<Credential>());
        
        controlTrustEngine1 = new ExplicitKeySignatureTrustEngine(credResolver, keyInfoResolver);
        controlTrustEngine2 = new ExplicitKeySignatureTrustEngine(credResolver, keyInfoResolver);
        controlTrustEngine3 = new ExplicitKeySignatureTrustEngine(credResolver, keyInfoResolver);
    }
    
    @BeforeMethod
    public void setUp() {
        resolver = new BasicSignatureValidationParametersResolver();
        
        config1 = new BasicSignatureValidationConfiguration();
        config2 = new BasicSignatureValidationConfiguration();
        config3 = new BasicSignatureValidationConfiguration();
        
        criterion = new SignatureValidationConfigurationCriterion(config1, config2, config3);
        
        criteriaSet = new CriteriaSet(criterion);
    }
    
    @Test
    public void testResolveSignatureTrustEngine() {
        SignatureTrustEngine trustEngine;
        
        trustEngine = resolver.resolveSignatureTrustEngine(criteriaSet);
        assertNull(trustEngine);
        
        config1.setSignatureTrustEngine(controlTrustEngine1);
        config2.setSignatureTrustEngine(controlTrustEngine2);
        config3.setSignatureTrustEngine(controlTrustEngine3);
        
        trustEngine = resolver.resolveSignatureTrustEngine(criteriaSet);
        assertTrue(trustEngine == controlTrustEngine1);
        
        config1.setSignatureTrustEngine(null);
        
        trustEngine = resolver.resolveSignatureTrustEngine(criteriaSet);
        assertTrue(trustEngine == controlTrustEngine2);
        
        config2.setSignatureTrustEngine(null);
        
        trustEngine = resolver.resolveSignatureTrustEngine(criteriaSet);
        assertTrue(trustEngine == controlTrustEngine3);
    }

    @Test
    public void testResolve() throws ResolverException {
        config1.setExcludedAlgorithms(List.of("foo", "bar"));
        config1.setSignatureTrustEngine(controlTrustEngine1);
        
        Iterable<SignatureValidationParameters> paramsIter = resolver.resolve(criteriaSet);
        assertNotNull(paramsIter);
        
        Iterator<SignatureValidationParameters> iterator = paramsIter.iterator();
        assertNotNull(iterator);
        
        assertTrue(iterator.hasNext());
        
        final SignatureValidationParameters params =iterator.next();
        
        assertNotNull(params);
        assertTrue(params.getSignatureTrustEngine() == controlTrustEngine1);
        assertTrue(params.getIncludedAlgorithms().isEmpty());
        assertEquals(params.getExcludedAlgorithms().size(), 2);
        assertTrue(params.getExcludedAlgorithms().contains("foo"));
        assertTrue(params.getExcludedAlgorithms().contains("bar"));
        
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testResolveSingle() throws ResolverException {
        config1.setExcludedAlgorithms(List.of("foo", "bar"));
        config1.setSignatureTrustEngine(controlTrustEngine1);
        
        final SignatureValidationParameters params = resolver.resolveSingle(criteriaSet);
        
        assert params != null;
        assertTrue(params.getSignatureTrustEngine() == controlTrustEngine1);
        assertTrue(params.getIncludedAlgorithms().isEmpty());
        assertEquals(params.getExcludedAlgorithms().size(), 2);
        assertTrue(params.getExcludedAlgorithms().contains("foo"));
        assertTrue(params.getExcludedAlgorithms().contains("bar"));
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
