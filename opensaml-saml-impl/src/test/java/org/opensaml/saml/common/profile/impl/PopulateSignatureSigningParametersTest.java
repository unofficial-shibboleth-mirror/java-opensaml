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

package org.opensaml.saml.common.profile.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.SignatureSigningParametersResolver;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** Unit test for {@link PopulateSignatureSigningParameters}. */
@SuppressWarnings({"null", "javadoc"})
public class PopulateSignatureSigningParametersTest extends OpenSAMLInitBaseTestCase {

    private ProfileRequestContext prc;
    
    private PopulateSignatureSigningParameters action;
    
    @BeforeMethod public void setUp() {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        action = new PopulateSignatureSigningParameters();
    }
    
    @Test(expectedExceptions=ComponentInitializationException.class)
    public void testConfig() throws ComponentInitializationException {
        action.initialize();
    }
    
    @Test public void testNoContext() throws Exception {
        action.setSignatureSigningParametersResolver(new MockResolver(false));
        action.initialize();
        
        prc.setOutboundMessageContext(null);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }
    
    @Test public void testResolverError() throws Exception {
        action.setSignatureSigningParametersResolver(new MockResolver(true));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_SEC_CFG);
    }    

    @Test public void testSuccess() throws Exception {
        action.setSignatureSigningParametersResolver(new MockResolver(false));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertNotNull(prc.ensureOutboundMessageContext().ensureSubcontext(
                SecurityParametersContext.class).getSignatureSigningParameters());
    }    

    @Test public void testCopy() throws Exception {
        // Test copy from PRC to MessageContext
        action.setSignatureSigningParametersResolver(new MockResolver(true));
        action.setExistingParametersContextLookupStrategy(new ChildContextLookup<>(SecurityParametersContext.class));
        action.setSecurityParametersContextLookupStrategy(
                new ChildContextLookup<>(SecurityParametersContext.class, true).compose(
                        new OutboundMessageContextLookup()));
        action.initialize();
        
        prc.ensureSubcontext(SecurityParametersContext.class).setSignatureSigningParameters(new SignatureSigningParameters());
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertSame(prc.ensureSubcontext(SecurityParametersContext.class).getSignatureSigningParameters(),
                prc.ensureOutboundMessageContext().ensureSubcontext(SecurityParametersContext.class).getSignatureSigningParameters());
    }    
    
    private class MockResolver implements SignatureSigningParametersResolver {

        private boolean throwException;
        
        public MockResolver(final boolean shouldThrow) {
            throwException = shouldThrow;
        }
        
        /** {@inheritDoc} */
        @Nonnull public Iterable<SignatureSigningParameters> resolve(@Nullable CriteriaSet criteria) throws ResolverException {
            return CollectionSupport.singletonList(Constraint.isNotNull(resolveSingle(criteria), "Resolver was null"));
        }

        /** {@inheritDoc} */
        @Nullable public SignatureSigningParameters resolveSingle(@Nullable CriteriaSet criteria) throws ResolverException {
            if (throwException) {
                throw new ResolverException();
            }
            
            assert criteria != null;
            Constraint.isNotNull(criteria.get(SignatureSigningConfigurationCriterion.class), "Criterion was null");
            return new SignatureSigningParameters();
        }
        
    }
    
}