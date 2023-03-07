/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.config;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.saml1.binding.artifact.SAML1ArtifactBuilderFactory;
import org.opensaml.saml.saml2.binding.artifact.SAML2ArtifactBuilderFactory;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * SAML-related configuration information.
 * 
 * <p>
 * The configuration instance to use would typically be retrieved from the
 * {@link org.opensaml.core.config.ConfigurationService}.
 * </p>
 * 
 */
public class SAMLConfiguration {
    
    /** Lowercase string function. */
    @Nonnull private static final Function<String, String> lowercaseFunction = new LowercaseFunction();

    /** SAML 1 Artifact factory. */
    @Nullable private SAML1ArtifactBuilderFactory saml1ArtifactBuilderFactory;

    /** SAML 2 Artifact factory. */
    @Nullable private SAML2ArtifactBuilderFactory saml2ArtifactBuilderFactory;
    
    /** The list of schemes allowed to appear in binding URLs when encoding a message. 
     * Defaults to 'http' and 'https'. */
    @Nonnull @NonnullElements @Unmodifiable @NotLive private List<String> allowedBindingURLSchemes;
    

    /**
     * Constructor.
     *
     */
    public SAMLConfiguration() {
        setAllowedBindingURLSchemes(CollectionSupport.listOf("http", "https"));
    }

    /**
     * Gets the artifact factory for the library.
     * 
     * @return artifact factory for the library
     */
    @Nullable public SAML1ArtifactBuilderFactory getSAML1ArtifactBuilderFactory() {
        return saml1ArtifactBuilderFactory;
    }

    /**
     * Sets the artifact factory for the library.
     * 
     * @param factory artifact factory for the library
     */
    public void setSAML1ArtifactBuilderFactory(@Nullable final SAML1ArtifactBuilderFactory factory) {
        saml1ArtifactBuilderFactory = factory;
    }

    /**
     * Gets the artifact factory for the library.
     * 
     * @return artifact factory for the library
     */
    @Nullable public SAML2ArtifactBuilderFactory getSAML2ArtifactBuilderFactory() {
        return saml2ArtifactBuilderFactory;
    }

    /**
     * Sets the artifact factory for the library.
     * 
     * @param factory artifact factory for the library
     */
    public void setSAML2ArtifactBuilderFactory(@Nullable final SAML2ArtifactBuilderFactory factory) {
        saml2ArtifactBuilderFactory = factory;
    }

    /**
     * Gets the unmodifiable list of schemes allowed to appear in binding URLs when encoding a message. 
     * 
     * <p>
     * All scheme values returned will be lowercased.
     * </p>
     * 
     * <p>
     * Defaults to 'http' and 'https'.
     * </p>
     * 
     * @return list of URL schemes allowed to appear in a message
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<String> getAllowedBindingURLSchemes() {
        return allowedBindingURLSchemes;
    }

    /**
     * Sets the list of schemes allowed to appear in binding URLs when encoding a message. 
     * 
     * <p>
     * The supplied list will be copied.  Values will be normalized: 1) strings will be trimmed, 
     * 2) nulls will be removed, and 3) all values will be lowercased.
     * </p>
     * 
     * <p>Note, the appearance of schemes such as 'javascript' may open the system up to attacks 
     * (e.g. cross-site scripting attacks).
     * </p>
     * 
     * @param schemes URL schemes allowed to appear in a message
     */
    public void setAllowedBindingURLSchemes(@Nullable final List<String> schemes) {
        if (schemes == null || schemes.isEmpty()) {
            allowedBindingURLSchemes = Collections.emptyList();
        } else {
            allowedBindingURLSchemes = StringSupport.normalizeStringCollection(schemes)
                    .stream()
                    .map(lowercaseFunction::apply)
                    .collect(Collectors.toUnmodifiableList());
        }
    }
    
    /**
     * Function to lowercase a string input.
     */
    private static class LowercaseFunction implements Function<String, String> {

        /** {@inheritDoc} */
        public String apply(final String input) {
            if (input == null) {
                return null;
            }
            return input.toLowerCase();
        }
        
    }
}