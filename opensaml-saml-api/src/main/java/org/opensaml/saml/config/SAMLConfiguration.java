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
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.saml1.binding.artifact.SAML1ArtifactBuilderFactory;
import org.opensaml.saml.saml2.binding.artifact.SAML2ArtifactBuilderFactory;

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

    /** SAML 1 Artifact factory. */
    @Nullable private SAML1ArtifactBuilderFactory saml1ArtifactBuilderFactory;

    /** SAML 2 Artifact factory. */
    @Nullable private SAML2ArtifactBuilderFactory saml2ArtifactBuilderFactory;
    
    /** The list of schemes allowed to appear in binding URLs when encoding a message. 
     * Defaults to 'http' and 'https'. */
    @Nonnull @Unmodifiable @NotLive private List<String> allowedBindingURLSchemes;
    
    /** Flag indicating whether to enforce SAMLRequest message size limits in the decoders. */
    private boolean enforceDecoderRequestSizeLimit;
    
    /** SAMLRequest message size limit. */
    @Nullable private Integer decoderRequestSizeLimit;
    
    /** Flag indicating whether to enforce SAMLResponse message size limits in the decoders. */
    private boolean enforceDecoderResponseSizeLimit;

    /** SAMLResponse message size limit. */
    @Nullable private Integer decoderResponseSizeLimit;

    /** Flag indicating whether to enforce KeyInfo size limits in the decoders (currently POST SimpleSign only). */
    private boolean enforceDecoderKeyInfoSizeLimit;

    /** KeyInfo size limit (currently POST SimpleSign only). */
    @Nullable private Integer decoderKeyInfoSizeLimit;

    /** Flag indicating whether to estimate the inflated size of deflated data, or to do an exact computation. */
    private boolean decoderEstimateInflatedSize;

    /** The inflation factor to use when {@link #isDecoderEstimateInflatedSize()} is enabled. */
    @Nullable private Float decoderInflationFactor;

    /**
     * Constructor.
     *
     */
    public SAMLConfiguration() {
        allowedBindingURLSchemes = CollectionSupport.listOf("http", "https");
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
    @Nonnull @Unmodifiable @NotLive public List<String> getAllowedBindingURLSchemes() {
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
            allowedBindingURLSchemes = CollectionSupport.emptyList();
        } else {
            allowedBindingURLSchemes = StringSupport.normalizeStringCollection(schemes)
                    .stream()
                    .filter(Objects::nonNull)
                    .map(String::toLowerCase)
                    .collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableList())).get();
        }
    }

    /**
     * Get the flag indicating whether to enforce SAMLRequest message size limits in the decoders.
     * 
     * @return true if enforcement enabled, false if not
     */
    public boolean isEnforceDecoderRequestSizeLimit() {
        return enforceDecoderRequestSizeLimit;
    }

    /**
     * Set the flag indicating whether to enforce SAMLRequest message size limits in the decoders.
     * 
     * @param flag true if enforcement enabled, false if not
     */
    public void setEnforceDecoderRequestSizeLimit(final boolean flag) {
        enforceDecoderRequestSizeLimit = flag;
    }

    /**
     * Get the SAMLRequest message size limit.
     * 
     * @return the limit in bytes
     */
    @Nullable public Integer getDecoderRequestSizeLimit() {
        return decoderRequestSizeLimit;
    }

    /**
     * Set the SAMLRequest message size limit.
     * 
     * @param limit the limit in bytes
     */
    public void setDecoderRequestSizeLimit(@Nullable final Integer limit) {
        decoderRequestSizeLimit = limit;
    }

    /**
     * Get the flag indicating whether to enforce SAMLResponse message size limits in the decoders.
     * 
     * @return true if enforcement enabled, false if not
     */
    public boolean isEnforceDecoderResponseSizeLimit() {
        return enforceDecoderResponseSizeLimit;
    }

    /**
     * Set the flag indicating whether to enforce SAMLResponse message size limits in the decoders.
     * 
     * @param flag true if enforcement enabled, false if not
     */
    public void setEnforceDecoderResponseSizeLimit(final boolean flag) {
        enforceDecoderResponseSizeLimit = flag;
    }

    /**
     * Get the SAMLResponse message size limit.
     * 
     * @return the limit in bytes
     */
    @Nullable public Integer getDecoderResponseSizeLimit() {
        return decoderResponseSizeLimit;
    }

    /**
     * Set the SAMLResponse message size limit.
     * 
     * @param limit the limit in bytes
     */
    public void setDecoderResponseSizeLimit(@Nullable final Integer limit) {
        decoderResponseSizeLimit = limit;
    }

    /**
     * Get the flag indicating whether to enforce KeyInfo size limits in the decoders (currently POST SimpleSign only).
     * 
     * @return true if enforcement enabled, false if not
     */
    public boolean isEnforceDecoderKeyInfoSizeLimit() {
        return enforceDecoderKeyInfoSizeLimit;
    }

    /**
     * Set the flag indicating whether to enforce KeyInfo size limits in the decoders (currently POST SimpleSign only).
     * 
     * @param flag true if enforcement enabled, false if not
     */
    public void setEnforceDecoderKeyInfoSizeLimit(final boolean flag) {
        enforceDecoderKeyInfoSizeLimit = flag;
    }

    /**
     * Get the KeyInfo size limit (currently POST SimpleSign only). 
     * 
     * @return the limit in bytes
     */
    @Nullable public Integer getDecoderKeyInfoSizeLimit() {
        return decoderKeyInfoSizeLimit;
    }

    /**
     * Set the KeyInfo size limit (currently POST SimpleSign only). 
     * 
     * @param limit the limit in bytes
     */
    public void setDecoderKeyInfoSizeLimit(@Nullable final Integer limit) {
        decoderKeyInfoSizeLimit = limit;
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
    public boolean isDecoderEstimateInflatedSize() {
        return decoderEstimateInflatedSize;
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
    public void setDecoderEstimateInflatedSize(final boolean flag) {
        decoderEstimateInflatedSize = flag;
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
    @Nullable public Float getDecoderInflationFactor() {
        return decoderInflationFactor;
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
    public void setDecoderInflationFactor(@Nullable final Float inflationFactor) {
        decoderInflationFactor = inflationFactor;
    }

}