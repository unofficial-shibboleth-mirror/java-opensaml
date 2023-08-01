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

package org.opensaml.saml.config;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.saml.saml1.binding.artifact.SAML1ArtifactBuilderFactory;
import org.opensaml.saml.saml2.binding.artifact.SAML2ArtifactBuilderFactory;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * Helper class for working with the registered instance of {@link SAMLConfiguration}, as obtained from
 * the {@link ConfigurationService}.
 * 
 */
public final class SAMLConfigurationSupport {
    
    /** Constructor. */
    private SAMLConfigurationSupport() {}

    /**
     * Gets the artifact factory for the library.
     * 
     * @return artifact factory for the library
     */
    @Nullable public static SAML1ArtifactBuilderFactory getSAML1ArtifactBuilderFactory() {
        return ConfigurationService.ensure(SAMLConfiguration.class).getSAML1ArtifactBuilderFactory();
    }

    /**
     * Sets the artifact factory for the library.
     * 
     * @param factory artifact factory for the library
     */
    public static void setSAML1ArtifactBuilderFactory(@Nullable final SAML1ArtifactBuilderFactory factory) {
        ConfigurationService.ensure(SAMLConfiguration.class).setSAML1ArtifactBuilderFactory(factory);
    }

    /**
     * Gets the artifact factory for the library.
     * 
     * @return artifact factory for the library
     */
    @Nullable public static SAML2ArtifactBuilderFactory getSAML2ArtifactBuilderFactory() {
        return ConfigurationService.ensure(SAMLConfiguration.class).getSAML2ArtifactBuilderFactory();
    }

    /**
     * Sets the artifact factory for the library.
     * 
     * @param factory artifact factory for the library
     */
    public static void setSAML2ArtifactBuilderFactory(@Nullable final SAML2ArtifactBuilderFactory factory) {
        ConfigurationService.ensure(SAMLConfiguration.class).setSAML2ArtifactBuilderFactory(factory);
    }
    
    /**
     * Get the allowed URL schemes.
     * 
     * @return the list of allowed URL schemes
     */
    @Nonnull @Unmodifiable @NotLive public static List<String> getAllowedBindingURLSchemes() {
        return ConfigurationService.ensure(SAMLConfiguration.class).getAllowedBindingURLSchemes();
    }
    
    /**
     * Set the allowed URL schemes.
     * 
     * @param schemes the new list of allowed URL schemes
     */
    public static void setAllowedBindingURLSchemes(@Nullable final List<String>schemes) {
        ConfigurationService.ensure(SAMLConfiguration.class).setAllowedBindingURLSchemes(schemes);
    }
}