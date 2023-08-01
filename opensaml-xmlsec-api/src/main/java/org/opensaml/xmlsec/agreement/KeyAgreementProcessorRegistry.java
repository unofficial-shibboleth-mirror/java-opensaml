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

package org.opensaml.xmlsec.agreement;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * A component which provides access to registered instances of {@link KeyAgreementProcessor}.
 */
public class KeyAgreementProcessorRegistry {
    
    /** The registered processors. */
    @Nonnull private Map<String, KeyAgreementProcessor> processors;
    
    /** Constructor. */
    public KeyAgreementProcessorRegistry() {
        processors = new HashMap<>();
    }
    
    /**
     * Register a processor. 
     * 
     * <p>
     * Any existing processor registered for the given algorithm will be overwritten.
     * </p>
     * 
     * @param processor the processor to register
     */
    public void register(@Nonnull final KeyAgreementProcessor processor) {
        Constraint.isNotNull(processor, "KeyAgreementProcessor was null");
        Constraint.isNotNull(processor.getAlgorithm(), "KeyAgreementProcessor algorithm was null");
        processors.put(processor.getAlgorithm(), processor);
    }

    /**
     * Deregister a processor.
     * 
     * @param algorithm the algorithm of the processor to deregister
     */
    public void deregister(@Nonnull final String algorithm) {
        final String alg = Constraint.isNotNull(StringSupport.trimOrNull(algorithm),
                "KeyAgreementProcessor algorithm was null or empty");
        processors.remove(alg);
    }
    
    /**
     * Clear all registered processors.
     */
    public void clear() {
        processors.clear();
    }
    
    /**
     * Get the set of all registered algorithms.
     * 
     * @return the set of registered algorithms
     */
    @SuppressWarnings("null")
    @Nonnull @Unmodifiable @NotLive public Set<String> getRegisteredAlgorithms() {
        return CollectionSupport.copyToSet(processors.keySet());
    }
    
    /**
     * Get the processor registered for the specified algorithm.
     * 
     * @param algorithm the processor algorithm
     * 
     * @return the processor registered for that algorithm
     */
    @Nullable public KeyAgreementProcessor getProcessor(@Nonnull final String algorithm) {
        final String alg = Constraint.isNotNull(StringSupport.trimOrNull(algorithm),
                "KeyAgreementProcessor algorithm was null or empty");
        return processors.get(alg);
    }

}