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

import javax.annotation.Nullable;

import org.opensaml.security.SecurityException;
import org.opensaml.security.trust.TrustEngine;

import net.shibboleth.shared.resolver.CriteriaSet;

@SuppressWarnings("javadoc")
public class MockTrustEngine<T> implements TrustEngine<T> {

    private final boolean result;
    
    /**
     * Constructor.
     * 
     * @param retVal ...
     */
    public MockTrustEngine(boolean retVal) {
        result = retVal;
    }
    
    /** {@inheritDoc} */
    @Override public boolean validate(@Nullable final T token, @Nullable CriteriaSet trustBasisCriteria) throws SecurityException {
        return result;
    }

}