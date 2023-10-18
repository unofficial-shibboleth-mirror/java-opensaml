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

package org.opensaml.security.testing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.SecurityException;
import org.opensaml.security.trust.TrustEngine;

import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * Mock trust engine.
 *
 * @param <TokenType> token type for trust engine
 */
public class MockTrustEngine<TokenType> implements TrustEngine<TokenType> {
    
    /** Trusted flag. */
    private boolean trusted;
    
    /** Exception to raise. */
    @Nullable private Throwable throwable;

    /**
     * Constructor.
     *
     * @param flag flag controlling whether the trust engine mock should accept the input or not.
     */
    public MockTrustEngine(final boolean flag) {
        trusted = flag;
    }

    /**
     * Constructor.
     *
     * @param t exception to raise from mock.
     */
    public MockTrustEngine(@Nullable final Throwable t) {
        throwable = t;
    }

    /** {@inheritDoc} */
    public boolean validate(@Nonnull final TokenType token, @Nullable final CriteriaSet trustBasisCriteria)
            throws SecurityException {
        if (throwable != null) {
            if (SecurityException.class.isInstance(throwable)) {
                throw SecurityException.class.cast(throwable);
            } else if (RuntimeException.class.isInstance(throwable)) {
                throw RuntimeException.class.cast(throwable);
            } else if (Error.class.isInstance(throwable)){
                throw Error.class.cast(throwable);
            } else {
                throw new RuntimeException(throwable);
            }
        }
        
        return trusted;
    }

}