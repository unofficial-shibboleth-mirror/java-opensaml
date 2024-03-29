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

package org.opensaml.security.x509.tls.impl;

import java.util.ArrayList;
import java.util.Iterator;

import net.shibboleth.shared.logic.ConstraintViolationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.trust.impl.ExplicitX509CertificateTrustEngine;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.tls.CertificateNameOptions;
import org.opensaml.security.x509.tls.ClientTLSValidationConfigurationCriterion;
import org.opensaml.security.x509.tls.ClientTLSValidationParameters;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class BasicClientTLSValidationParametersResolverTest {

    private BasicClientTLSValidationParametersResolver resolver;

    private CriteriaSet criteriaSet;

    private ClientTLSValidationConfigurationCriterion criterion;

    private BasicClientTLSValidationConfiguration config1, config2, config3;

    private TrustEngine<? super X509Credential> controlTrustEngine1, controlTrustEngine2, controlTrustEngine3;
    
    private CertificateNameOptions controlNameOpts1, controlNameOpts2, controlNameOpts3;

    @BeforeClass public void buildTrustEnginesAndNameOpts() {
        CredentialResolver credResolver = new StaticCredentialResolver(new ArrayList<Credential>());

        controlTrustEngine1 = new ExplicitX509CertificateTrustEngine(credResolver);
        controlTrustEngine2 = new ExplicitX509CertificateTrustEngine(credResolver);
        controlTrustEngine3 = new ExplicitX509CertificateTrustEngine(credResolver);
        
        controlNameOpts1 = new CertificateNameOptions();
        controlNameOpts2 = new CertificateNameOptions();
        controlNameOpts3 = new CertificateNameOptions();
    }

    @BeforeMethod public void setUp() {
        resolver = new BasicClientTLSValidationParametersResolver();

        config1 = new BasicClientTLSValidationConfiguration();
        config2 = new BasicClientTLSValidationConfiguration();
        config3 = new BasicClientTLSValidationConfiguration();

        criterion = new ClientTLSValidationConfigurationCriterion(config1, config2, config3);

        criteriaSet = new CriteriaSet(criterion);
    }

    @Test public void testResolveTrustEngine() {
        TrustEngine<? super X509Credential> trustEngine;

        trustEngine = resolver.resolveTrustEngine(criteriaSet);
        Assert.assertNull(trustEngine);

        config1.setX509TrustEngine(controlTrustEngine1);
        config2.setX509TrustEngine(controlTrustEngine2);
        config3.setX509TrustEngine(controlTrustEngine3);

        trustEngine = resolver.resolveTrustEngine(criteriaSet);
        Assert.assertTrue(trustEngine == controlTrustEngine1);

        config1.setX509TrustEngine(null);

        trustEngine = resolver.resolveTrustEngine(criteriaSet);
        Assert.assertTrue(trustEngine == controlTrustEngine2);

        config2.setX509TrustEngine(null);

        trustEngine = resolver.resolveTrustEngine(criteriaSet);
        Assert.assertTrue(trustEngine == controlTrustEngine3);
    }
    
    @Test public void testResolveNameOptions() {
        CertificateNameOptions nameOpts;

        nameOpts = resolver.resolveNameOptions(criteriaSet);
        Assert.assertNull(nameOpts);

        config1.setCertificateNameOptions(controlNameOpts1);
        config2.setCertificateNameOptions(controlNameOpts2);
        config3.setCertificateNameOptions(controlNameOpts3);

        nameOpts = resolver.resolveNameOptions(criteriaSet);
        Assert.assertTrue(nameOpts == controlNameOpts1);

        config1.setCertificateNameOptions(null);

        nameOpts = resolver.resolveNameOptions(criteriaSet);
        Assert.assertTrue(nameOpts == controlNameOpts2);

        config2.setCertificateNameOptions(null);

        nameOpts = resolver.resolveNameOptions(criteriaSet);
        Assert.assertTrue(nameOpts == controlNameOpts3);
    }

    @Test public void testResolve() throws ResolverException {
        config1.setX509TrustEngine(controlTrustEngine1);
        config1.setCertificateNameOptions(controlNameOpts1);

        Iterable<ClientTLSValidationParameters> paramsIter = resolver.resolve(criteriaSet);
        Assert.assertNotNull(paramsIter);

        Iterator<ClientTLSValidationParameters> iterator = paramsIter.iterator();
        Assert.assertNotNull(iterator);

        Assert.assertTrue(iterator.hasNext());

        final ClientTLSValidationParameters params = iterator.next();

        Assert.assertNotNull(params);
        Assert.assertTrue(params.getX509TrustEngine() == controlTrustEngine1);
        Assert.assertTrue(params.getCertificateNameOptions() == controlNameOpts1);

        Assert.assertFalse(iterator.hasNext());
    }

    @Test public void testResolveSingle() throws ResolverException {
        config1.setX509TrustEngine(controlTrustEngine1);
        config1.setCertificateNameOptions(controlNameOpts1);

        final ClientTLSValidationParameters params = resolver.resolveSingle(criteriaSet);

        assert params != null;
        Assert.assertTrue(params.getX509TrustEngine() == controlTrustEngine1);
        Assert.assertTrue(params.getCertificateNameOptions() == controlNameOpts1);
    }

    @Test(expectedExceptions = ConstraintViolationException.class) public void testNullCriteriaSet()
            throws ResolverException {
        resolver.resolve(null);
    }

    @Test(expectedExceptions = ConstraintViolationException.class) public void testAbsentCriterion()
            throws ResolverException {
        resolver.resolve(new CriteriaSet());
    }

}
