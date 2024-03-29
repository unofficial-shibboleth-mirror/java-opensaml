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

package org.opensaml.spring.trust;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.SecurityException;
import org.opensaml.security.x509.PKIXTrustEvaluator;
import org.opensaml.security.x509.PKIXValidationInformation;
import org.opensaml.security.x509.PKIXValidationOptions;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.impl.BasicX509CredentialNameEvaluator;
import org.opensaml.security.x509.impl.CertPathPKIXTrustEvaluator;
import org.opensaml.security.x509.impl.PKIXX509CredentialTrustEngine;
import org.opensaml.security.x509.impl.StaticPKIXValidationInformationResolver;
import org.opensaml.security.x509.impl.X509CredentialNameEvaluator;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.support.GenericApplicationContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.AbstractInitializableComponent;
import net.shibboleth.shared.spring.util.ApplicationContextBuilder;

/**
 * Unit test for {@link StaticPKIXFactoryBean}.
 */
@SuppressWarnings("javadoc")
public class StaticPKIXFactoryBeanTest {

    @Test
    public void defaults() {
        final ApplicationContextBuilder contextBuilder = new ApplicationContextBuilder();
        contextBuilder.setUnresolvedServiceConfigurations(CollectionSupport.singletonList("org/opensaml/spring/trust/static-pkix-factory-defaults.xml"));
        
        final GenericApplicationContext context = contextBuilder.build();

        final PKIXX509CredentialTrustEngine trustEngine = context.getBean("StaticPKIXX509CredentialTrustEngine",
                PKIXX509CredentialTrustEngine.class);
        
        Assert.assertNotNull(trustEngine);
        
        Assert.assertTrue(StaticPKIXValidationInformationResolver.class.isInstance((trustEngine.getPKIXResolver())));
        
        Assert.assertTrue(CertPathPKIXTrustEvaluator.class.isInstance((trustEngine.getPKIXTrustEvaluator())));
        
        Assert.assertTrue(BasicX509CredentialNameEvaluator.class.isInstance((trustEngine.getX509CredentialNameEvaluator())));
    }
    
    @Test
    public void customPropertiesSuccess() {
        final ApplicationContextBuilder contextBuilder = new ApplicationContextBuilder();
        contextBuilder.setUnresolvedServiceConfigurations(CollectionSupport.singletonList("org/opensaml/spring/trust/static-pkix-factory-custom-success.xml"));
        
        final GenericApplicationContext context = contextBuilder.build();

        final PKIXX509CredentialTrustEngine trustEngine = context.getBean("StaticPKIXX509CredentialTrustEngine",
                PKIXX509CredentialTrustEngine.class);
        
        Assert.assertNotNull(trustEngine);
        
        Assert.assertTrue(StaticPKIXValidationInformationResolver.class.isInstance((trustEngine.getPKIXResolver())));
        
        Assert.assertTrue(MockPKIXTrustEvaluator.class.isInstance((trustEngine.getPKIXTrustEvaluator())));
        
        Assert.assertTrue(MockX509CredentialNameEvaluator.class.isInstance((trustEngine.getX509CredentialNameEvaluator())));
    }
    
    @Test(expectedExceptions=FatalBeanException.class)
    public void customPropertiesFailsValidation() {
        final ApplicationContextBuilder contextBuilder = new ApplicationContextBuilder();
        contextBuilder.setUnresolvedServiceConfigurations(CollectionSupport.singletonList("org/opensaml/spring/trust/static-pkix-factory-custom-failsValidation.xml"));
        
        contextBuilder.build();
    }
    
    
    // 
    // Helpers
    //
    
    public static class MockPKIXTrustEvaluator extends AbstractInitializableComponent implements PKIXTrustEvaluator {

        /** {@inheritDoc} */
        public boolean validate(@Nonnull final PKIXValidationInformation validationInfo,
                @Nonnull final X509Credential untrustedCredential) throws SecurityException {
            return false;
        }

        /** {@inheritDoc} */
        @Nonnull public PKIXValidationOptions getPKIXValidationOptions() {
            throw new UnsupportedOperationException();
        }
        
    }
    
    public static class MockX509CredentialNameEvaluator extends AbstractInitializableComponent implements X509CredentialNameEvaluator {

        /** {@inheritDoc} */
        public boolean evaluate(@Nonnull final X509Credential credential, @Nullable final Set<String> trustedNames)
                throws SecurityException {
            return false;
        }
        
    }
}
