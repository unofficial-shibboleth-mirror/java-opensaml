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

package org.opensaml.spring.credential;

import org.opensaml.security.x509.BasicX509Credential;
import org.springframework.context.support.GenericApplicationContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.spring.util.ApplicationContextBuilder;

/**
 * Unit test for {@link BasicX509CredentialFactoryBean}.
 */
public class BasicX509CredentialFactoryBeanTest {

    /**
     * Test.
     */
    @Test public void bean() {
        final ApplicationContextBuilder contextBuilder = new ApplicationContextBuilder();
        contextBuilder.setUnresolvedServiceConfigurations(CollectionSupport.singletonList("org/opensaml/spring/credential/bean.xml"));
        
        final GenericApplicationContext context = contextBuilder.build();
        
         final BasicX509Credential cred1 = context.getBean("Credential", BasicX509Credential.class);
        
         final BasicX509Credential cred2 = context.getBean("EncCredential", BasicX509Credential.class);
         
         Assert.assertEquals("http://example.org/enc", cred2.getEntityId()); 
         
         final byte[] cb1 = Constraint.isNotNull(cred1.getPrivateKey(), "Private key was null").getEncoded();
         final byte[] cb2 = Constraint.isNotNull(cred2.getPrivateKey(), "Private key was null").getEncoded();
         
         Assert.assertEquals(cb1.length, cb2.length);

         for (int i = 0; i< cb1.length; i++)Assert.assertEquals(cb1[i], cb2[i]);
         
         Assert.assertEquals(cred2.getPublicKey(), cred2.getPublicKey());
    }

}