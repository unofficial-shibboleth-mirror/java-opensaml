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

import java.util.Collection;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;

/**
 * Predicate which implements an algorithm URI inclusion policy.
 * 
 */
public class IncludedAlgorithmsPredicate implements Predicate<String> {
    
    /** Included algorithms. */
    @Nonnull private Collection<String> includes;
    
    /**
     * Constructor.
     *
     * @param algorithms collection of included algorithms
     */
    public IncludedAlgorithmsPredicate(@Nonnull final Collection<String> algorithms) {
        includes = CollectionSupport.copyToSet(Constraint.isNotNull(algorithms, "Inclusions may not be null"));
    }

    /** {@inheritDoc} */
    public boolean test(@Nullable final String input) {
        if (input == null) {
            throw new IllegalArgumentException("Algorithm URI to evaluate may not be null");
        }
        return includes.isEmpty() || includes.contains(input);
    }
    
}