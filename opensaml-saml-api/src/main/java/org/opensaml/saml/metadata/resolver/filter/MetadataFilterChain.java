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

package org.opensaml.saml.metadata.resolver.filter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

import org.opensaml.core.xml.XMLObject;
import org.slf4j.Logger;

/**
 * A filter that allows the composition of {@link MetadataFilter}s. Filters will be executed on the given metadata
 * document in the order they were added to the chain.
 */
public class MetadataFilterChain extends AbstractMetadataFilter {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(MetadataFilterChain.class);
    
    /** Registered filters. */
    @Nonnull private List<MetadataFilter> filters;

    /**
     * Constructor.
     */
    public MetadataFilterChain() {
        filters = new ArrayList<>();
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable public final XMLObject filter(@Nullable final XMLObject xmlObject,
            @Nonnull final MetadataFilterContext context) throws FilterException {
        if (xmlObject == null) {
            return null;
        }
        
        synchronized (filters) {
            if (filters == null || filters.isEmpty()) {
                log.debug("No filters configured, nothing to do");
                return xmlObject;
            }
            
            XMLObject current = xmlObject;
            for (final MetadataFilter filter : filters) {
                if (current == null) {
                    return null;
                }
                log.debug("Applying filter {}", filter.getClass().getName());
                current = filter.filter(current, context);
            }
            
            return current;
        }
    }

    /**
     * Get the list of {@link MetadataFilter}s that make up this chain.
     * 
     * @return the filters that make up this chain
     */
    @Nonnull @Live public List<MetadataFilter> getFilters() {
        return filters;
    }

    /**
     * Set the list of {@link MetadataFilter}s that make up this chain.
     * 
     * @param newFilters list of {@link MetadataFilter}s that make up this chain
     */
    public void setFilters(@Nonnull final List<MetadataFilter> newFilters) {
        Constraint.isNotNull(newFilters, "Filter collection cannot be null");
        
        filters = new ArrayList<>(newFilters);
    }

}