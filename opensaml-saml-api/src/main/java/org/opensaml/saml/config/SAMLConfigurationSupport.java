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
import org.opensaml.saml.common.binding.SAMLBindingSupport;
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
    
    /**
     * Get the flag indicating whether to enforce SAMLRequest message size limits in the decoders.
     * 
     * @return true if enforcement enabled, false if not
     */
    public static boolean isEnforceDecoderRequestSizeLimit() {
        return ConfigurationService.ensure(SAMLConfiguration.class).isEnforceDecoderRequestSizeLimit();
    }

    /**
     * Set the flag indicating whether to enforce SAMLRequest message size limits in the decoders.
     * 
     * @param flag true if enforcement enabled, false if not
     */
    public static void setEnforceDecoderRequestSizeLimit(final boolean flag) {
        ConfigurationService.ensure(SAMLConfiguration.class).setEnforceDecoderRequestSizeLimit(flag);
    }

    /**
     * Get the SAMLRequest message size limit.
     * 
     * @return the limit in bytes
     */
    @Nullable public static Integer getDecoderRequestSizeLimit() {
        return ConfigurationService.ensure(SAMLConfiguration.class).getDecoderRequestSizeLimit();
    }

    /**
     * Set the SAMLRequest message size limit.
     * 
     * @param limit the limit in bytes
     */
    public static void setDecoderRequestSizeLimit(@Nullable final Integer limit) {
        ConfigurationService.ensure(SAMLConfiguration.class).setDecoderRequestSizeLimit(limit);
    }

    /**
     * Get the flag indicating whether to enforce SAMLResponse message size limits in the decoders.
     * 
     * @return true if enforcement enabled, false if not
     */
    public static boolean isEnforceDecoderResponseSizeLimit() {
        return ConfigurationService.ensure(SAMLConfiguration.class).isEnforceDecoderResponseSizeLimit();
    }

    /**
     * Set the flag indicating whether to enforce SAMLResponse message size limits in the decoders.
     * 
     * @param flag true if enforcement enabled, false if not
     */
    public static void setEnforceDecoderResponseSizeLimit(final boolean flag) {
        ConfigurationService.ensure(SAMLConfiguration.class).setEnforceDecoderResponseSizeLimit(flag);
    }

    /**
     * Get the SAMLResponse message size limit.
     * 
     * @return the limit in bytes
     */
    @Nullable public static Integer getDecoderResponseSizeLimit() {
        return ConfigurationService.ensure(SAMLConfiguration.class).getDecoderResponseSizeLimit();
    }

    /**
     * Set the SAMLResponse message size limit.
     * 
     * @param limit the limit in bytes
     */
    public static void setDecoderResponseSizeLimit(@Nullable final Integer limit) {
        ConfigurationService.ensure(SAMLConfiguration.class).setDecoderResponseSizeLimit(limit);
    }

    /**
     * Get the flag indicating whether to enforce KeyInfo size limits in the decoders (currently POST SimpleSign only).
     * 
     * @return true if enforcement enabled, false if not
     */
    public static boolean isEnforceDecoderKeyInfoSizeLimit() {
        return ConfigurationService.ensure(SAMLConfiguration.class).isEnforceDecoderKeyInfoSizeLimit();
    }

    /**
     * Set the flag indicating whether to enforce KeyInfo size limits in the decoders (currently POST SimpleSign only).
     * 
     * @param flag true if enforcement enabled, false if not
     */
    public static void setEnforceDecoderKeyInfoSizeLimit(final boolean flag) {
        ConfigurationService.ensure(SAMLConfiguration.class).setEnforceDecoderKeyInfoSizeLimit(flag);
    }

    /**
     * Get the KeyInfo size limit (currently POST SimpleSign only). 
     * 
     * @return the limit in bytes
     */
    @Nullable public static Integer getDecoderKeyInfoSizeLimit() {
        return ConfigurationService.ensure(SAMLConfiguration.class).getDecoderKeyInfoSizeLimit();
    }

    /**
     * Set the KeyInfo size limit (currently POST SimpleSign only). 
     * 
     * @param limit the limit in bytes
     */
    public static void setDecoderKeyInfoSizeLimit(@Nullable final Integer limit) {
        ConfigurationService.ensure(SAMLConfiguration.class).setDecoderKeyInfoSizeLimit(limit);
    }
    
    /**
     * Get the flag indicating whether to estimate the inflated size of deflated data, or to do an exact computation.
     * 
     * <p>
     * For more details on usage see the documentation for
     * {@link SAMLBindingSupport#getDeflatedSize(String, boolean, Float)}.
     * </p>
     * 
     * @return true if estimation is enabled, false if not
     */
    public static boolean isDecoderEstimateInflatedSize() {
        return ConfigurationService.ensure(SAMLConfiguration.class).isDecoderEstimateInflatedSize();
    }

    /**
     * Set the flag indicating whether to estimate the inflated size of deflated data, or to do an exact computation.
     * 
     * <p>
     * For more details on usage see the documentation for
     * {@link SAMLBindingSupport#getDeflatedSize(String, boolean, Float)}.
     * </p>
     * 
     * @param flag true if estimate is enabled, false if not
     */
    public static void setDecoderEstimateInflatedSize(final boolean flag) {
        ConfigurationService.ensure(SAMLConfiguration.class).setDecoderEstimateInflatedSize(flag);
    }

    /**
     * Get the inflation factor to use when {@link #isDecoderEstimateInflatedSize()} is enabled.
     * 
     * <p>
     * For more details on usage see the documentation for
     * {@link SAMLBindingSupport#getDeflatedSize(String, boolean, Float)}.
     * </p>
     * 
     * @return the inflation factor
     */
    @Nullable public static Float getDecoderInflationFactor() {
        return ConfigurationService.ensure(SAMLConfiguration.class).getDecoderInflationFactor();
    }

    /**
     * Set the inflation factor to use when {@link #isDecoderEstimateInflatedSize()} is enabled.
     * 
     * <p>
     * For more details on usage see the documentation for
     * {@link SAMLBindingSupport#getDeflatedSize(String, boolean, Float)}.
     * </p>
     * 
     * @param inflationFactor the inflation factor
     */
    public static void setDecoderInflationFactor(@Nullable final Float inflationFactor) {
        ConfigurationService.ensure(SAMLConfiguration.class).setDecoderInflationFactor(inflationFactor);
    }
}