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

package org.opensaml.saml.metadata.resolver.impl;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.slf4j.Logger;

/**
 * Function which examines an entity ID from supplied criteria and returns it as a metadata request URL 
 * if and only if the entity ID is an HTTP or HTTPS URL.
 */
public class HTTPEntityIDRequestURLBuilder implements Function<CriteriaSet, String> {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(HTTPEntityIDRequestURLBuilder.class);

    /** {@inheritDoc} */
    @Nullable public String apply(@Nullable final CriteriaSet criteria) {
        
        final EntityIdCriterion criterion = criteria != null ? criteria.get(EntityIdCriterion.class) : null;
        if (criterion == null) {
            log.trace("Criteria did not contain entity ID, unable to build request URL");
            return null;
        }
        final String entityID = criterion.getEntityId();
        if (entityID.toLowerCase().startsWith("http:") || entityID.toLowerCase().startsWith("https:")) {
            log.debug("Saw entityID with HTTP/HTTPS URL syntax, returning the entityID itself as request URL");
            return entityID;
        }
        log.debug("EntityID was not an HTTP or HTTPS URL, could not construct request URL on that basis");
        return null;
    }

}