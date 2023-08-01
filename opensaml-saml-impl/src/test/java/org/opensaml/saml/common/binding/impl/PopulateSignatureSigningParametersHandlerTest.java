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

package org.opensaml.saml.common.binding.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.ParentProfileRequestContextLookup;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.SignatureSigningParametersResolver;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/** Unit test for {@link PopulateSignatureSigningParametersHandler}. */
@SuppressWarnings("javadoc")
public class PopulateSignatureSigningParametersHandlerTest extends OpenSAMLInitBaseTestCase {

    private ProfileRequestContext prc;
    
    private PopulateSignatureSigningParametersHandler handler;
    
    @BeforeMethod public void setUp() {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        handler = new PopulateSignatureSigningParametersHandler();
    }
    
    @Test(expectedExceptions=ComponentInitializationException.class)
    public void testConfig() throws ComponentInitializationException {
        handler.initialize();
    }
    
    public void testNoContext() throws Exception {
        handler.setSignatureSigningParametersResolver(new MockResolver(false));
        handler.initialize();
        
        prc.setOutboundMessageContext(null);
        
        handler.invoke(prc.ensureOutboundMessageContext());
        Assert.assertNull(prc.ensureOutboundMessageContext().ensureSubcontext(
                SecurityParametersContext.class).getSignatureSigningParameters());
    }
    
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testResolverError() throws Exception {
        handler.setSignatureSigningParametersResolver(new MockResolver(true));
        handler.initialize();
        
        handler.invoke(prc.ensureOutboundMessageContext());
    }    

    @Test
    public void testSuccess() throws Exception {
        handler.setSignatureSigningParametersResolver(new MockResolver(false));
        handler.initialize();
        
        handler.invoke(prc.ensureOutboundMessageContext());
        Assert.assertNotNull(prc.ensureOutboundMessageContext().ensureSubcontext(
                SecurityParametersContext.class).getSignatureSigningParameters());
    }    

    @Test
    public void testCopy() throws Exception {
        // Test copy from PRC to MessageContext
        handler.setSignatureSigningParametersResolver(new MockResolver(true));
        handler.setExistingParametersContextLookupStrategy(
                new ChildContextLookup<>(SecurityParametersContext.class).compose(
                        new ParentProfileRequestContextLookup<>()));
        handler.setSecurityParametersContextLookupStrategy(
                new ChildContextLookup<MessageContext,SecurityParametersContext>(SecurityParametersContext.class, true));
        handler.initialize();
        
        prc.ensureSubcontext(SecurityParametersContext.class).setSignatureSigningParameters(new SignatureSigningParameters());
        
        handler.invoke(prc.ensureOutboundMessageContext());
        Assert.assertSame(prc.ensureSubcontext(SecurityParametersContext.class).getSignatureSigningParameters(),
                prc.ensureOutboundMessageContext().ensureSubcontext(SecurityParametersContext.class).getSignatureSigningParameters());
    }    
    
    private class MockResolver implements SignatureSigningParametersResolver {

        private boolean throwException;
        
        public MockResolver(final boolean shouldThrow) {
            throwException = shouldThrow;
        }
        
        /** {@inheritDoc} */
        @Nonnull public Iterable<SignatureSigningParameters> resolve(@Nullable final CriteriaSet criteria) throws ResolverException {
            final SignatureSigningParameters params = resolveSingle(criteria);
            return params != null ? CollectionSupport.singletonList(params) : CollectionSupport.emptyList();
        }

        /** {@inheritDoc} */
        @Nullable public SignatureSigningParameters resolveSingle(@Nullable final CriteriaSet criteria) throws ResolverException {
            if (throwException) {
                throw new ResolverException();
            }
            
            if (criteria == null || criteria.get(SignatureSigningConfigurationCriterion.class) == null) {
                return null;
            }
            return new SignatureSigningParameters();
        }
        
    }
    
}