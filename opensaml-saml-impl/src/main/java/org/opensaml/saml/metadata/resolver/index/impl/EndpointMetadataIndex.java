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

package org.opensaml.saml.metadata.resolver.index.impl;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.criterion.EndpointCriterion;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.StartsWithLocationCriterion;
import org.opensaml.saml.metadata.resolver.index.MetadataIndex;
import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.slf4j.Logger;

import com.google.common.base.MoreObjects;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.net.URLBuilder;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * An implementation of {@link MetadataIndex} which indexes entities by their role endpoint locations.
 * 
 * <p>
 * The indexed endpoint location keys are scoped by the containing {@link RoleDescriptor} type, {@link Endpoint} type,
 * and whether or not the endpoint value was a standard location ({@link Endpoint#getLocation()}) 
 * or a response location ({@link Endpoint#getResponseLocation()}).
 * </p>
 */
public class EndpointMetadataIndex implements MetadataIndex {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(EndpointMetadataIndex.class);
    
    /** The predicate which selects which endpoints to index. */
    @Nonnull private Predicate<Endpoint> endpointSelectionPredicate;
    
    /**
     * Constructor.
     * 
     * <p>
     * All entity descriptor endpoints will be indexed.
     * </p>
     */
    public EndpointMetadataIndex() {
        endpointSelectionPredicate = PredicateSupport.alwaysTrue();
    }
    
    /**
     * Constructor.
     *
     * @param endpointPredicate the predicate which selects which endpoints to index
     */
    public EndpointMetadataIndex(@Nonnull @ParameterName(name="endpointPredicate")
        final Predicate<Endpoint> endpointPredicate) {
        endpointSelectionPredicate = Constraint.isNotNull(endpointPredicate, 
                "Endpoint selection predicate may not be null");
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public Set<MetadataIndexKey> generateKeys(
            @Nonnull final EntityDescriptor descriptor) {
        Constraint.isNotNull(descriptor, "EntityDescriptor was null");
        final HashSet<MetadataIndexKey> result = new HashSet<>();
        for (final RoleDescriptor role : descriptor.getRoleDescriptors()) {
            QName roleType = role.getSchemaType();
            if (roleType == null) {
                roleType = role.getElementQName();
            }
            
            for (final Endpoint endpoint : role.getEndpoints()) {
                QName endpointType = endpoint.getSchemaType();
                if (endpointType == null) {
                    endpointType = endpoint.getElementQName();
                }
                
                if (endpointSelectionPredicate.test(endpoint)) {
                    final String location = StringSupport.trimOrNull(endpoint.getLocation());
                    if (location != null) {
                        log.trace("Indexing Endpoint: role '{}', endpoint type '{}', location '{}'", 
                                roleType, endpointType, location);
                        result.add(new EndpointMetadataIndexKey(roleType, endpointType, location, false));
                    }
                    final String responseLocation = StringSupport.trimOrNull(endpoint.getResponseLocation());
                    if (responseLocation != null) {
                        log.trace("Indexing response Endpoint - role '{}', endpoint type '{}', response location '{}'", 
                                roleType, endpointType, responseLocation);
                        result.add(new EndpointMetadataIndexKey(roleType, endpointType, responseLocation, true));
                    }
                }
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public Set<MetadataIndexKey> generateKeys(
            @Nullable final CriteriaSet criteriaSet) {
        final EntityRoleCriterion roleCrit = criteriaSet != null ? criteriaSet.get(EntityRoleCriterion.class) : null;
        final EndpointCriterion<Endpoint> endpointCrit =
            criteriaSet != null ? criteriaSet.get(EndpointCriterion.class) : null;
        
        if (roleCrit != null && endpointCrit != null) {
            final HashSet<MetadataIndexKey> result = new HashSet<>();
            result.addAll(processCriteria(criteriaSet, roleCrit.getRole(), endpointCrit.getEndpoint()));
            return result;
        }
        return null;
    }
    
    /**
     * Process the specified role and endpoint.
     * 
     * @param criteriaSet  the criteria being processed
     * @param roleType the type of role containing the endpoint
     * @param endpoint the endpoint to process
     * @return the set of metadata index keys for the endpoint
     */
    @Nonnull private Set<MetadataIndexKey> processCriteria(@Nullable final CriteriaSet criteriaSet, 
            @Nonnull final QName roleType, @Nonnull final Endpoint endpoint) {
        
        final HashSet<MetadataIndexKey> result = new HashSet<>();
        
        QName endpointType = endpoint.getSchemaType();
        if (endpointType == null) {
            endpointType = endpoint.getElementQName();
        }
        
        final String location = StringSupport.trimOrNull(endpoint.getLocation());
        if (location != null) {
            for (final String variant : processLocation(criteriaSet, location)) {
                assert variant != null;
                result.add(new EndpointMetadataIndexKey(roleType, endpointType, variant, false));
            }
        }
        final String responseLocation = StringSupport.trimOrNull(endpoint.getResponseLocation());
        if (responseLocation != null) {
            for (final String variant : processLocation(criteriaSet, responseLocation)) {
                assert variant != null;
                result.add(new EndpointMetadataIndexKey(roleType, endpointType, variant, true));
            }
        }
        
        return result;
    }

    /**
     * Process the specified location.
     * 
     * @param criteriaSet the criteria being processed
     * @param location the location to process
     * @return the variants of the location to be indexed 
     */
    @Nonnull private Set<String> processLocation(@Nullable final CriteriaSet criteriaSet,
            @Nonnull final String location) {
        boolean generateStartsWithVariants = false;
        final StartsWithLocationCriterion startsWithCrit =
                criteriaSet != null ? criteriaSet.get(StartsWithLocationCriterion.class) : null;
        if (startsWithCrit != null) {
            generateStartsWithVariants = startsWithCrit.isMatchStartsWith();
        }
        if (generateStartsWithVariants) {
            log.trace("Saw indication to produce path-trimmed key variants for startsWith eval from '{}'", location);
            final HashSet<String> result = new HashSet<>();
            result.add(location);
            log.trace("Produced value '{}'", location);
            try {
                String currentURL = null;
                final URLBuilder urlBuilder = new URLBuilder(location);
                String currentPath = MetadataIndexSupport.trimURLPathSegment(urlBuilder.getPath());
                while (currentPath != null) {
                    urlBuilder.setPath(currentPath);
                    currentURL = urlBuilder.buildURL();
                    result.add(currentURL);
                    log.trace("Produced value '{}'", currentURL);
                    currentPath = MetadataIndexSupport.trimURLPathSegment(urlBuilder.getPath());
                }
                urlBuilder.setPath(null);
                currentURL = urlBuilder.buildURL();
                result.add(currentURL);
                log.trace("Produced value '{}'", currentURL);
            } catch (final MalformedURLException e) {
                log.warn("Could not parse URL '{}', will not generate path segment variants", location, e);
            }
            return result;
        }
        
        return CollectionSupport.singleton(location);
    }



    /**
     * The default endpoint selection predicate, which evaluates an {@link Endpoint} using
     * a map of {@link QName} endpoint types, indexed by role type.
     */
    public static class DefaultEndpointSelectionPredicate implements Predicate<Endpoint> {
        
        /** The indexable endpoint types. */
        @Nonnull private Map<QName, Set<QName>> endpointTypes;
        
        /**
         * Constructor.
         */
        public DefaultEndpointSelectionPredicate() {
            endpointTypes = CollectionSupport.emptyMap();
        }
        
        /**
         * Constructor.
         *
         * @param indexableTypes a map controlling the types of endpoints to index
         */
        public DefaultEndpointSelectionPredicate(
                @ParameterName(name="indexableTypes") @Nonnull final Map<QName, Set<QName>> indexableTypes) {
            endpointTypes = Constraint.isNotNull(indexableTypes, "Indexable endpoint types map was null");
        }

        /** {@inheritDoc} */
        public boolean test(@Nullable final Endpoint endpoint) {
            if (endpoint == null) {
                return false;
            }
            
            final RoleDescriptor role = (RoleDescriptor) endpoint.getParent();
            if (role == null) {
                return false;
            }
            
            QName roleType = role.getSchemaType();
            if (roleType == null) {
                roleType = role.getElementQName();
            }
            
            QName endpointType = endpoint.getSchemaType();
            if (endpointType == null) {
                endpointType = endpoint.getElementQName();
            }
            
            final Set<QName> indexableEndpoints = endpointTypes.get(roleType);
            if (indexableEndpoints != null && indexableEndpoints.contains(endpointType)) {
                return true;
            }
            
            return false;
        }
        
    }
    
    /**
     * An implementation of {@link MetadataIndexKey} representing a single SAML metadata endpoint.
     */
    protected static class EndpointMetadataIndexKey implements MetadataIndexKey {
        
        /** Logger. */
        @Nonnull private final Logger log = LoggerFactory.getLogger(EndpointMetadataIndexKey.class);
        
        /** The role type. */
        @Nonnull private final QName role;
        
        /** The endpoint type. */
        @Nonnull private final QName endpoint;
        
        /** The location. */
        @Nonnull private final String location;
        
        /** Respone location flag. */
        private final boolean response;

        /** The canonicalized location. */
        @Nonnull private String canonicalizedLocation;
        
        /** Flag indicating whether canonicalized location is the simple lower case fallback strategy. */
        private boolean isCanonicalizedLowerCase;
        
        /**
         * Constructor.
         * 
         * @param roleType the role type
         * @param endpointType the endpoint type
         * @param endpointLocation the endpoint location
         * @param isResponse flag indicating whether location is a response or not
         */
        public EndpointMetadataIndexKey(@Nonnull final QName roleType, @Nonnull final QName endpointType,
                @Nonnull @NotEmpty final String endpointLocation, final boolean isResponse) {
            role = Constraint.isNotNull(roleType, "SAML role cannot be null");
            endpoint = Constraint.isNotNull(endpointType, "SAML endpoint type cannot be null");
            location = Constraint.isNotNull(StringSupport.trimOrNull(endpointLocation),
                    "SAML role cannot be null or empty");
            response = isResponse;
            
            try {
                canonicalizedLocation = MetadataIndexSupport.canonicalizeLocationURI(location);
            } catch (final MalformedURLException e) {
                // This is unlikely to happen on realistic real world inputs. If it does, don't be fatal, 
                // just switch to alternate strategy.
                log.warn("Input location '{}' was a malformed URL, switching to lower case strategy", 
                        location, e);
                canonicalizedLocation = location.toLowerCase();
                isCanonicalizedLowerCase = true;
            }
        }

        /**
         * Gets the entity role.
         * 
         * @return the entity role
         */
        @Nonnull public QName getRoleType() {
            return role;
        }
        
        /**
         * Gets the entity endpoint type.
         * 
         * @return the endpoint type
         */
        @Nonnull public QName getEndpointType() {
            return endpoint;
        }

        /**
         * Gets the location.
         * 
         * @return the location
         */
        @Nonnull public String getLocation() {
            return location;
        }
        
        /**
         * Gets the response location flag.
         * 
         * @return true if endpoint is a response location, false otherwise
         */
        public boolean isResponse() {
            return response;
        }
        
        /**
         * Get the canonicalized representation of the location, primarily for use in
         * {@link #hashCode()} and {@link #equals(Object)}.
         * 
         * @return the canonicalized source location
         */
        @Nonnull public String getCanonicalizedLocation() {
            return canonicalizedLocation;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("role", role)
                    .add("endpoint", endpoint)
                    .add("location", location)
                    .add("isResponse", response)
                    .add("canonicalizedLocation", canonicalizedLocation)
                    .add("isCanonicalizedLowerCase", isCanonicalizedLowerCase)
                    .toString();
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return Objects.hash(getRoleType(), getEndpointType(), getCanonicalizedLocation(), isResponse());
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj instanceof EndpointMetadataIndexKey other) {
                String thisLocation = this.canonicalizedLocation;
                String otherLocation = other.canonicalizedLocation;
                if (this.isCanonicalizedLowerCase != other.isCanonicalizedLowerCase) {
                    if (this.isCanonicalizedLowerCase) {
                        otherLocation = other.location.toLowerCase();
                    } else {
                        thisLocation = this.location.toLowerCase();
                    }
                }
                return this.role.equals(other.role) 
                        && this.endpoint.equals(other.endpoint) 
                        && thisLocation.equals(otherLocation) 
                        && this.response == other.response;
            }

            return false;
        }

    }

}