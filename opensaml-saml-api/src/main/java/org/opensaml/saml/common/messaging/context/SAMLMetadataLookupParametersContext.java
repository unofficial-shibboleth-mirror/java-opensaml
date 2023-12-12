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

package org.opensaml.saml.common.messaging.context;

import java.util.function.BiConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.metadata.resolver.DetectDuplicateEntityIDs;

import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.Criterion;

/**
 * Context for operational parameters that influence the lookup of SAML metadata.
 */
public class SAMLMetadataLookupParametersContext extends BaseContext {
    
    /** The strategy for duplicate entityID detection. */
    @Nullable private DetectDuplicateEntityIDs detectDuplicateEntityIDs;
    
    /** An extender hook that can add additional {@link Criterion} objects. */
    @Nullable private BiConsumer<MessageContext,CriteriaSet> criteriaExtender;
    
    /**
     * Get the strategy for duplicate entityID detection.
     * 
     * @return strategy for duplicate entityID detection
     */
    @Nullable public DetectDuplicateEntityIDs getDetectDuplicateEntityIDs() {
        return detectDuplicateEntityIDs;
    }

    /**
     * Set the strategy for duplicate entityID detection.
     * 
     * @param strategy the strategy for duplicate entityID detection
     * 
     * @return this context
     */
    @Nonnull public SAMLMetadataLookupParametersContext setDetectDuplicateEntityIDs(
            @Nullable final DetectDuplicateEntityIDs strategy) {
        detectDuplicateEntityIDs = strategy;
        
        return this;
    }

    /**
     * Get a callable hook for extending the {@link CriteriaSet} used for metadata resolution.
     * 
     * @return callable hook
     * 
     * @since 5.1.0
     */
    @Nullable public BiConsumer<MessageContext,CriteriaSet> getCriteriaExtender() {
        return criteriaExtender;
    }

    /**
     * Set a callable hook for extending the {@link CriteriaSet} used for metadata resolution.
     * 
     * @param extender callable hook
     * 
     * @return this context
     * 
     * @since 5.1.0
     */
    @Nonnull public SAMLMetadataLookupParametersContext setCriteriaExtender(
            @Nullable final BiConsumer<MessageContext,CriteriaSet> extender) {
        criteriaExtender = extender;
        
        return this;
    }

}