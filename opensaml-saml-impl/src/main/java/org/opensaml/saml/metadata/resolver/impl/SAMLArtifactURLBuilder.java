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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.common.binding.artifact.SAMLSourceIDArtifact;
import org.opensaml.saml.criterion.ArtifactCriterion;
import org.opensaml.saml.metadata.resolver.impl.MetadataQueryProtocolRequestURLBuilder.MetadataQueryProtocolURLBuilder;

import com.google.common.io.BaseEncoding;

import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * Implementation of {@link MetadataQueryProtocolURLBuilder} which understands {@link ArtifactCriterion}.
 */
public class SAMLArtifactURLBuilder implements MetadataQueryProtocolURLBuilder {
    
    /** Hex encoder. */
    @Nonnull private static final BaseEncoding HEX = BaseEncoding.base16().lowerCase();

    /** {@inheritDoc} */
    @Nullable public String buildURL(@Nonnull final String baseURL, @Nullable final CriteriaSet criteria) {
        
        final ArtifactCriterion artifactCriterion = criteria != null ? criteria.get(ArtifactCriterion.class) : null;
        if (artifactCriterion == null) {
            return null;
        }
        
        if (artifactCriterion.getArtifact() instanceof SAMLSourceIDArtifact art) {
            return buildFromSourceID(baseURL, art);
        }
        
        return null;
    }

    /**
     * Builder URL from SAML artifact source ID.
     * 
     * @param baseURL  the base URL
     * @param sourceIDArtifact the source ID artifact 
     * @return the request URL
     * 
     */
    @Nullable private String buildFromSourceID(@Nonnull final String baseURL, 
            @Nonnull final SAMLSourceIDArtifact sourceIDArtifact) {
        
        // We just statically escape "{sha1}" here
        return baseURL + "entities/" + "%7Bsha1%7D" + HEX.encode(sourceIDArtifact.getSourceID());
    }

}