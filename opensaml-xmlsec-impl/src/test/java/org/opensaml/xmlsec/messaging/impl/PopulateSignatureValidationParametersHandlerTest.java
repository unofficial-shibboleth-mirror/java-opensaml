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

package org.opensaml.xmlsec.messaging.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.SignatureValidationParametersResolver;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.criterion.SignatureValidationConfigurationCriterion;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/** Unit test for {@link PopulateSignatureValidationParametersHandler}. */
@SuppressWarnings({"javadoc", "null"})
public class PopulateSignatureValidationParametersHandlerTest extends OpenSAMLInitBaseTestCase {

    private MessageContext messageContext;
    
    private PopulateSignatureValidationParametersHandler handler;
    
    @BeforeMethod public void setUp() {
        messageContext = new MessageContext();
        handler = new PopulateSignatureValidationParametersHandler();
    }
    
    @Test(expectedExceptions=ComponentInitializationException.class)
    public void testConfig() throws ComponentInitializationException {
        handler.initialize();
    }
    
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testResolverError() throws Exception {
        handler.setSignatureValidationParametersResolver(new MockResolver(true));
        handler.initialize();
        
        handler.invoke(messageContext);
    }    

    @Test public void testSuccess() throws Exception {
        handler.setSignatureValidationParametersResolver(new MockResolver(false));
        handler.initialize();
        
        handler.invoke(messageContext);
        Assert.assertNotNull(messageContext.ensureSubcontext(SecurityParametersContext.class).getSignatureValidationParameters());
    }    
    
    private class MockResolver implements SignatureValidationParametersResolver {

        private boolean throwException;
        
        public MockResolver(final boolean shouldThrow) {
            throwException = shouldThrow;
        }
        
        /** {@inheritDoc} */
        @Nonnull public Iterable<SignatureValidationParameters> resolve(@Nullable final CriteriaSet criteria) throws ResolverException {
            final var params = resolveSingle(criteria);
            if (params != null) {
                return CollectionSupport.singletonList(params);
            }
            return CollectionSupport.emptyList();
        }

        /** {@inheritDoc} */
        @Nullable public SignatureValidationParameters resolveSingle(@Nullable final CriteriaSet criteria) throws ResolverException {
            if (throwException) {
                throw new ResolverException();
            }
            
            assert criteria != null;
            Constraint.isNotNull(criteria.get(SignatureValidationConfigurationCriterion.class), "Criterion was null");
            return new SignatureValidationParameters();
        }
        
    }
    
}