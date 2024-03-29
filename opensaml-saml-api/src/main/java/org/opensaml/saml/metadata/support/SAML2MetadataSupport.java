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

package org.opensaml.saml.metadata.support;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.saml2.metadata.IndexedEndpoint;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Utility helper class for SAML 2 metadata objects.
 */
public final class SAML2MetadataSupport {
    
    /** Logger. */
    @Nonnull private static final Logger LOG = LoggerFactory.getLogger(SAML2MetadataSupport.class);
    
    /** Constructor. */
    private SAML2MetadataSupport() { }

    /**
     * Select the default {@link IndexedEndpoint} from a list of candidates.
     * 
     * <p>
     * The algorithm used is:
     * </p>
     * <ol>
     * <li>Select the first endpoint with an explicit <code>isDefault=true</code></li>
     * <li>Select the first endpoint with no explicit <code>isDefault</code></li>
     * <li>Select the first endpoint</li>
     * </ol>
     * 
     * @param candidates the list of candidate indexed endpoints
     * @return the selected candidate (or null if the list is null or empty)
     * 
     * @param <T> the subtype of IndexedType
     * 
     */
    @Nullable public static <T extends IndexedEndpoint> T getDefaultIndexedEndpoint(final List<T> candidates) {
        LOG.debug("Selecting default IndexedEndpoint");
        
        if (candidates == null || candidates.isEmpty()) {
            LOG.debug("IndexedEndpoint list was null or empty, returning null");
            return null;
        }
        
        T firstNoDefault = null;
        for (final T endpoint : candidates) {
            final Boolean isDefault = endpoint.isDefault();
            if (isDefault != null && isDefault) {
                LOG.debug("Selected IndexedEndpoint with explicit isDefault of true");
                return endpoint;
            }
            
            // This records the first element whose isDefault is not explicitly false
            if (firstNoDefault == null && endpoint.isDefaultXSBoolean() == null) {
                firstNoDefault = endpoint;
            }
        }
        
        if (firstNoDefault != null) {
            LOG.debug("Selected first IndexedEndpoint with no explicit isDefault");
            return firstNoDefault;
        }
        LOG.debug("Selected first IndexedEndpoint with explicit isDefault of false");
        return candidates.get(0);
        
    }
    
}