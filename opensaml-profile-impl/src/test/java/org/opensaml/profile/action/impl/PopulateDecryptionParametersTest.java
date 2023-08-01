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

package org.opensaml.profile.action.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.DecryptionParametersResolver;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.criterion.DecryptionConfigurationCriterion;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** Unit test for {@link PopulateDecryptionParameters}. */
@SuppressWarnings("javadoc")
public class PopulateDecryptionParametersTest extends OpenSAMLInitBaseTestCase {

    private ProfileRequestContext prc;
    
    private PopulateDecryptionParameters action;
    
    @BeforeMethod public void setUp() {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        action = new PopulateDecryptionParameters();
    }
    
    @Test(expectedExceptions=ComponentInitializationException.class)
    public void testConfig() throws ComponentInitializationException {
        action.initialize();
    }
    
    @Test public void testNoContext() throws Exception {
        action.setDecryptionParametersResolver(new MockResolver(false));
        action.initialize();
        
        prc.setInboundMessageContext(null);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_PROFILE_CTX);
    }
    
    @Test public void testResolverError() throws Exception {
        action.setDecryptionParametersResolver(new MockResolver(true));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_SEC_CFG);
    }    

    @Test public void testSuccess() throws Exception {
        action.setDecryptionParametersResolver(new MockResolver(false));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final MessageContext mc = prc.getInboundMessageContext();
        assert mc != null;
        final SecurityParametersContext ctx = mc.getSubcontext(SecurityParametersContext.class);
        assert ctx != null;
        Assert.assertNotNull(ctx.getDecryptionParameters());
    }    
    
    private class MockResolver implements DecryptionParametersResolver {

        private boolean throwException;
        
        public MockResolver(final boolean shouldThrow) {
            throwException = shouldThrow;
        }
        
        /** {@inheritDoc} */
        @Nonnull public Iterable<DecryptionParameters> resolve(@Nullable final CriteriaSet criteria) throws ResolverException {
            return CollectionSupport.singletonList(Constraint.isNotNull(resolveSingle(criteria), "Parameters were null"));
        }

        /** {@inheritDoc} */
        @Nullable public DecryptionParameters resolveSingle(@Nullable CriteriaSet criteria) throws ResolverException {
            if (throwException) {
                throw new ResolverException();
            }
            
            if (criteria != null) {
                Constraint.isNotNull(criteria.get(DecryptionConfigurationCriterion.class), "Criterion was null");
                return new DecryptionParameters();
            }
            
            return null;
        }
        
    }
    
}