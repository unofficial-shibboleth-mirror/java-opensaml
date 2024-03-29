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

package org.opensaml.xmlsec.impl;

import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.xmlsec.algorithm.AlgorithmRegistry;

import net.shibboleth.shared.logic.Constraint;

/**
 * A predicate which evaluates whether a cryptographic algorithm URI is effectively supported by
 * the runtime environment, as determined by {@link AlgorithmRegistry#isRuntimeSupported(String)}.
 */
public class AlgorithmRuntimeSupportedPredicate implements Predicate<String> {
    
    /** The algorithm registry instance. */
    @Nonnull private AlgorithmRegistry registry;
    
    /**
     * Constructor.
     *
     * @param algorithmRegistry the algorithm registry instance to use
     */
    public AlgorithmRuntimeSupportedPredicate(@Nonnull final AlgorithmRegistry algorithmRegistry) {
        registry = Constraint.isNotNull(algorithmRegistry, "AlgorithmRegistry may not be null");
    }

    /** {@inheritDoc} */
    public boolean test(@Nullable final String input) {
        return registry.isRuntimeSupported(input);
    }

}