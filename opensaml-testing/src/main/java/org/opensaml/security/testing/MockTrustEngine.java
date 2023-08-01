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

public class MockTrustEngine<TokenType> implements TrustEngine<TokenType> {
    
    private boolean trusted;
    
    private Throwable throwable;

    public MockTrustEngine(boolean flag) {
        trusted = flag;
    }

    public MockTrustEngine(Throwable t) {
        throwable = t;
    }

    /** {@inheritDoc} */
    public boolean validate(@Nonnull TokenType token, @Nullable CriteriaSet trustBasisCriteria) throws SecurityException {
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
