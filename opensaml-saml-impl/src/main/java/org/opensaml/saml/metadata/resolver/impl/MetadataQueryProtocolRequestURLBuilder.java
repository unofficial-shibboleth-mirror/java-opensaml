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

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.slf4j.Logger;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.CriteriaSet;

//TODO reference to protocol spec pending in Javadoc.

/**
 * Function which produces a URL according to the Metadata Query Protocol (MDQ) specification.
 * 
 * <p>
 * Support for building request URLs per the MDQ SAML profile based on an {@link EntityIdCriterion} is built-in.
 * </p>
 * 
 * <p>
 * Support for building request URLs via other criteria may be specified via ordered instances 
 * of {@link MetadataQueryProtocolURLBuilder}. These are evaluated in the supplied order,
 * and the first non-null result will be returned.
 * </p>
 */
public class MetadataQueryProtocolRequestURLBuilder implements Function<CriteriaSet, String> {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(MetadataQueryProtocolRequestURLBuilder.class);
    
    /** The request base URL per the specification. */
    @Nonnull @NotEmpty private String base;
    
    /** Function which transforms the entityID prior to substitution into the URL. */
    @Nullable private Function<String, String> transformer;
    
    /** Path escaper for escaping the input value inserted into the URL path. */
    @SuppressWarnings("null")
    @Nonnull private final Escaper pathEscaper = UrlEscapers.urlPathSegmentEscaper();
    
    /** List of secondary URL builders. */
    @Nullable private List<MetadataQueryProtocolURLBuilder> urlBuilders;
    
    /**
     * Constructor.
     *
     * @param baseURL the base URL for the metadata responder
     */
    public MetadataQueryProtocolRequestURLBuilder(
            @ParameterName(name="baseURL") @Nonnull @NotEmpty final String baseURL) {
        this(baseURL, null, null);
    }
    
    /**
     * Constructor.
     *
     * @param baseURL the base URL for the metadata responder
     * @param transform function which transforms the entityID prior to URL construction substitution, may be null
     */
    public MetadataQueryProtocolRequestURLBuilder(
            @ParameterName(name="baseURL") @Nonnull @NotEmpty final String baseURL,
            @ParameterName(name="transform") @Nullable final Function<String,String> transform) {
        this(baseURL, transform, null);
    }
    
    /**
     * Constructor.
     *
     * @param baseURL the base URL for the metadata responder
     * @param secondaryURLBuilders the list of secondary URL builders, may be null
     */
    public MetadataQueryProtocolRequestURLBuilder(
            @ParameterName(name="baseURL") @Nonnull @NotEmpty final String baseURL,
            @ParameterName(name="secondaryURLBuilders")
            @Nullable final List<MetadataQueryProtocolURLBuilder> secondaryURLBuilders) {
        this(baseURL, null, secondaryURLBuilders);
    }
    
    /**
     * Constructor.
     *
     * @param baseURL the base URL for the metadata responder
     * @param transform function which transforms the entityID prior to URL construction substitution, may be null
     * @param secondaryURLBuilders the list of secondary URL builders, may be null
     */
    public MetadataQueryProtocolRequestURLBuilder(
            @ParameterName(name="baseURL") @Nonnull @NotEmpty final String baseURL, 
            @ParameterName(name="transform") @Nullable final Function<String,String> transform,
            @ParameterName(name="secondaryURLBuilders")
            @Nullable final List<MetadataQueryProtocolURLBuilder> secondaryURLBuilders
            ) {
        base = Constraint.isNotNull(StringSupport.trimOrNull(baseURL), "Base URL was null or empty");
        if (!base.endsWith("/")) {
            log.debug("Base URL did not end in a trailing '/', one will be added");
            base = base + "/";
        }
        log.debug("Effective base URL value was: {}", base);
        
        transformer = transform;
        
        if (secondaryURLBuilders != null) {
            urlBuilders = CollectionSupport.copyToList(secondaryURLBuilders);
        }
    }

    /**
     * Gets the base URL configured into function.
     * 
     * @return base URL
     * 
     * @since 5.0.0
     */
    @Nonnull @NotEmpty public String getBaseURL() {
        return base;
    }
    
    /** {@inheritDoc} */
    @Nullable public String apply(@Nullable final CriteriaSet criteria) {
        final EntityIdCriterion criterion = criteria != null ? criteria.get(EntityIdCriterion.class) : null;

        if (criterion != null) {
            log.debug("Criteria contained entity ID, building on that basis");
            return buildFromEntityID(criterion.getEntityId());
        } else if (urlBuilders != null) {
            log.debug("Criteria did not contain entity ID, attempting to build using secondary URL builders");
            return buildFromSecondaryLookups(criteria);
        } else {
            log.debug("Criteria did not contain entity ID and no secondary URL builders were configured");
            return null;
        }
    }
    
    /**
     * Build request URL from entityID.
     * 
     * @param inputEntityID the entityID
     * @return the request URL, or null
     */
    @Nullable private String buildFromEntityID(@Nonnull final String inputEntityID) {
        String entityID = inputEntityID;
        if (transformer != null) {
            entityID = transformer.apply(inputEntityID);
            log.debug("Transformed entityID is '{}'", entityID);
            if (entityID == null) {
                log.debug("Transformed entityID was null");
                return null;
            }
        }
        
        final String result = base +  "entities/" + pathEscaper.escape(entityID);
        log.debug("From entityID '{}' and base URL '{}', built request URL: {}", 
                entityID, base, result);
        return result;
    }

    /**
     * Build request URL from secondary lookup criteria.
     * 
     * @param criteria the criteria
     * @return the request URL, or null
     */
    @Nullable private String buildFromSecondaryLookups(@Nullable final CriteriaSet criteria) {
        if (urlBuilders != null) {
            for (final MetadataQueryProtocolURLBuilder builder : urlBuilders) {
                final String url = builder.buildURL(base, criteria);
                log.debug("Secondary URL builder '{}' produced URL: {}", builder.getClass().getName(), url);
                if (url != null) {
                    return url;
                }
            }
        }
        log.debug("No configured secondary URL builders produced a non-null request URL");
        return null;
    }
    
    /**
     * Interface for a component which builds a Metadata Query Protocol request URL from a base URL
     * and criteria.
     */
    public interface MetadataQueryProtocolURLBuilder {
        
        /**
         * Build a request URL using the supplied base service URL and criteria.
         * 
         * @param baseURL the service base URL
         * @param criteria the criteria
         * @return a URL based on the supplied inputs, or null if the implementation did not support
         *     or understand any of the supplied criteria 
         */
        @Nullable String buildURL(@Nonnull final String baseURL, @Nullable final CriteriaSet criteria);
        
    }

}