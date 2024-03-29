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

package org.opensaml.saml.common.profile.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.EntityGroupName;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.AffiliateMember;
import org.opensaml.saml.saml2.metadata.AffiliationDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/**
 * Predicate to determine whether one of a set of names matches any of an entity's containing
 * {@link org.opensaml.saml.saml2.metadata.EntitiesDescriptor} groups. 
 */
public class EntityGroupNamePredicate implements Predicate<EntityDescriptor> {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EntityGroupNamePredicate.class);
    
    /** Groups to match on. */
    @Nonnull private final Set<String> groupNames;
    
    /** A supplemental resolver to allow for {@link AffiliationDescriptor} lookup. */
    @Nullable private MetadataResolver metadataResolver;
    
    /** Pre-created criteria sets for metadata lookup. */
    @Nullable private Collection<CriteriaSet> criteriaSets;
    
    /**
     * Constructor.
     * 
     * @param names the group names to test for
     */
    public EntityGroupNamePredicate(@Nullable @ParameterName(name="names") final Collection<String> names) {
        this(names, null);
    }
    
    /**
     * Constructor.
     * 
     * @param names the group names to test for
     * @param resolver metadata resolver for affiliation support
     * 
     * @since 3.4.0
     */
    public EntityGroupNamePredicate(@Nullable @ParameterName(name="names") final Collection<String> names,
            @Nullable @ParameterName(name="resolver") final MetadataResolver resolver) {
        
        groupNames = CollectionSupport.copyToSet(StringSupport.normalizeStringCollection(names));
        
        metadataResolver = resolver;
        if (resolver != null) {
            criteriaSets = new ArrayList<>(groupNames.size());
            for (final String name : groupNames) {
                assert name != null;
                assert criteriaSets != null;
                criteriaSets.add(new CriteriaSet(new EntityIdCriterion(name)));
            }
        }
    }

    /**
     * Get the group name criteria.
     * 
     * @return  the group name criteria
     */
    @Nonnull @Unmodifiable @NotLive public Set<String> getGroupNames() {
        return groupNames;
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    public boolean test(@Nullable final EntityDescriptor input) {
        
        if (input == null) {
            log.debug("Input was null, condition is false");
            return false;
        }
        
        for (final EntityGroupName group : input.getObjectMetadata().get(EntityGroupName.class)) {
            if (groupNames.contains(group.getName())) {
                log.debug("Found matching group '{}' attached to entity '{}'", group.getName(), input.getEntityID());
                return true;
            }
        }
        
        if (metadataResolver != null) {
            assert criteriaSets != null;
            for (final CriteriaSet criteria : criteriaSets) {
                try {
                    assert metadataResolver != null;
                    final EntityDescriptor affiliation = metadataResolver.resolveSingle(criteria);
                    if (affiliation != null) {
                        final AffiliationDescriptor descriptor = affiliation.getAffiliationDescriptor();
                        if (descriptor != null) {
                            for (final AffiliateMember member : descriptor.getMembers()) {
                                final String uri = member.getURI();
                                if (uri != null && uri.equals(input.getEntityID())) {
                                    log.debug("Found AffiliationDescriptor '{}' membership for entity '{}'",
                                            affiliation.getEntityID(), input.getEntityID());
                                    return true;
                                }
                            }
                        }
                    }
                } catch (final ResolverException e) {
                    log.warn("Metadata lookup for AffiliationDescriptor failed", e);
                }
            }
        }
        
        log.debug("No group match found for entity '{}'", input.getEntityID());
        return false;
    }
// Checkstyle: CyclomaticComplexity ON
    
}