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

package org.opensaml.saml.common.profile;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.NameIDType;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Abstract base class for simple implementations of {@link NameIdentifierGenerator}.
 * 
 * <p>
 * This class is suitable for implementing generators that produce simple kinds of identifiers. It supports various
 * options controlling the inclusion of qualifier attributes.
 * </p>
 * 
 * <p>
 * Subclasses must override one of {@link #doGenerate(ProfileRequestContext)} or
 * {@link #getIdentifier(ProfileRequestContext)}.
 * </p>
 * 
 * @param <NameIdType> type of object produced
 */
public abstract class AbstractNameIdentifierGenerator<NameIdType extends SAMLObject>
        extends AbstractIdentifiableInitializableComponent
        implements FormatSpecificNameIdentifierGenerator<NameIdType>, Predicate<ProfileRequestContext> {

    /** A predicate indicating whether the component applies to a request. */
    @Nonnull private Predicate<ProfileRequestContext> activationCondition;

    /** Optional lookup function for obtaining default NameQualifier. */
    @Nullable private Function<ProfileRequestContext,String> defaultIdPNameQualifierLookupStrategy;
    
    /** Optional lookup function for obtaining default SPNameQualifier. */
    @Nullable private Function<ProfileRequestContext,String> defaultSPNameQualifierLookupStrategy;
    
    /** Flag allowing qualifier(s) to be omitted when they would match defaults or are not set. */
    private boolean omitQualifiers;

    /** The identifier Format supported. */
    @Nonnull @NotEmpty private String format;

    /** Explicit NameQualifier, if any. */
    @Nullable private String idpNameQualifier;

    /** Explicit SPNameQualifier, if any. */
    @Nullable private String spNameQualifier;

    /** SPProvidedID, if any. */
    @Nullable private String spProvidedId;

    /** Constructor. */
    protected AbstractNameIdentifierGenerator() {
        activationCondition = PredicateSupport.alwaysTrue();
        format = NameIDType.UNSPECIFIED;
    }

    /**
     * Set an activation condition that determines whether to run or not.
     * 
     * @param condition an activation condition
     */
    public void setActivationCondition(@Nonnull final Predicate<ProfileRequestContext> condition) {
        checkSetterPreconditions();

        activationCondition = Constraint.isNotNull(condition, "Predicate cannot be null");
    }
    
    /**
     * Get the lookup strategy to obtain the default IdP NameQualifier.
     * 
     * @return lookup strategy
     */
    @Nullable public Function<ProfileRequestContext,String> getDefaultIdPNameQualifierLookupStrategy() {
        return defaultIdPNameQualifierLookupStrategy;
    }
    
    /**
     * Set the lookup strategy to obtain the default IdP NameQualifier.
     * 
     * @param strategy lookup strategy
     */
    public void setDefaultIdPNameQualifierLookupStrategy(
            @Nullable final Function<ProfileRequestContext,String> strategy) {
        checkSetterPreconditions();
        
        defaultIdPNameQualifierLookupStrategy = strategy;
    }

    /**
     * Get the lookup strategy to obtain the default SPNameQualifier.
     * 
     * @return lookup strategy
     */
    @Nullable public Function<ProfileRequestContext,String> getDefaultSPNameQualifierLookupStrategy() {
        return defaultSPNameQualifierLookupStrategy;
    }
    
    /**
     * Set the lookup strategy to obtain the default SPNameQualifier.
     * 
     * @param strategy lookup strategy
     */
    public void setDefaultSPNameQualifierLookupStrategy(
            @Nullable final Function<ProfileRequestContext,String> strategy) {
        checkSetterPreconditions();
        
        defaultSPNameQualifierLookupStrategy = strategy;
    }

    /**
     * Get whether to omit NameQualifier/SPNameQualifier attributes if the qualifiers are not explicitly set or are set
     * to values matching the IdP and relying party names respectively.
     * 
     * @return whether to omit qualifiers
     */
    public boolean isOmitQualifiers() {
        return omitQualifiers;
    }

    /**
     * Set whether to omit NameQualifier/SPNameQualifier attributes if the qualifiers are not explicitly set or are set
     * to values matching the IdP and relying party names respectively.
     * 
     * @param flag flag to set
     */
    public void setOmitQualifiers(final boolean flag) {
        checkSetterPreconditions();

        omitQualifiers = flag;
    }

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getFormat() {
        return format;
    }

    /**
     * Set the Format attribute supported.
     * 
     * @param f format to set
     */
    public void setFormat(@Nonnull @NotEmpty final String f) {
        checkSetterPreconditions();

        format = Constraint.isNotNull(StringSupport.trimOrNull(f), "Format cannot be null or empty");
    }

    /**
     * Get the NameQualifier attribute.
     * 
     * @return the qualifier attribute
     */
    @Nullable public String getIdPNameQualifier() {
        return idpNameQualifier;
    }

    /**
     * Set the NameQualifier attribute.
     * 
     * <p>
     * If not set, and {@link #isOmitQualifiers()} is false, then the value used will be derived from the IdP identity.
     * </p>
     * 
     * @param qualifier qualifier to set
     */
    public void setIdPNameQualifier(@Nullable final String qualifier) {
        checkSetterPreconditions();

        idpNameQualifier = StringSupport.trimOrNull(qualifier);
    }

    /**
     * Get the SPNameQualifier attribute.
     * 
     * @return the qualifier attribute
     */
    @Nullable public String getSPNameQualifier() {
        return spNameQualifier;
    }

    /**
     * Set the SPNameQualifier attribute.
     * 
     * <p>
     * If not set, and {@link #isOmitQualifiers()} is false, then the value used will be derived from the relying party
     * identity.
     * </p>
     * 
     * @param qualifier qualifier to set
     */
    public void setSPNameQualifier(@Nullable final String qualifier) {
        checkSetterPreconditions();

        spNameQualifier = StringSupport.trimOrNull(qualifier);
    }

    /**
     * Get the SPProvidedID attribute.
     * 
     * @return the secondary ID attribute
     */
    @Nullable public String getSPProvidedID() {
        return spProvidedId;
    }

    /**
     * Set the SPProvidedID attribute.
     * 
     * @param id value to set
     */
    public void setSPProvidedId(@Nullable final String id) {
        checkSetterPreconditions();

        spProvidedId = id;
    }

    /** {@inheritDoc} */
    public boolean test(@Nullable final ProfileRequestContext input) {
        return activationCondition.test(input);
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public NameIdType generate(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull @NotEmpty final String theFormat) throws SAMLException {
        checkComponentActive();

        if (!Objects.equals(format, theFormat)) {
            throw new SAMLException("The format to generate does not match the value configured");
        } else if (!test(profileRequestContext)) {
            return null;
        }
        return doGenerate(profileRequestContext);
    }

    /**
     * Override this method to fully control the generation process.
     * 
     * @param profileRequestContext current profile request context
     * @return the generated object
     * @throws SAMLException if an error occurs
     */
    @Nullable protected abstract NameIdType doGenerate(@Nonnull final ProfileRequestContext profileRequestContext)
            throws SAMLException;

    /**
     * Override this method to reuse this implementation of {@link #doGenerate(ProfileRequestContext)}, and return the
     * identifier to be included as the value of the eventual element.
     * 
     * @param profileRequestContext current profile request context
     * 
     * @return the generated identifier
     * @throws SAMLException if an error occurs
     */
    @Nullable protected String getIdentifier(@Nonnull final ProfileRequestContext profileRequestContext)
            throws SAMLException {
        return null;
    }

    /**
     * Get the effective NameQualifier to apply based on the properties set and the current request.
     * 
     * @param profileRequestContext current profile context
     * 
     * @return the effective NameQualifier to set, or null
     */
    @Nullable protected String getEffectiveIdPNameQualifier(
            @Nonnull final ProfileRequestContext profileRequestContext) {
        if (idpNameQualifier != null) {
            if (omitQualifiers) {
                final Function<ProfileRequestContext,String> strategy = defaultIdPNameQualifierLookupStrategy;
                if (strategy == null || !Objects.equals(idpNameQualifier, strategy.apply(profileRequestContext))) {
                    return idpNameQualifier;
                }
                return null;
            }
            return idpNameQualifier;
        } else if (!omitQualifiers && defaultIdPNameQualifierLookupStrategy != null) {
            return defaultIdPNameQualifierLookupStrategy.apply(profileRequestContext);
        } else {
            return null;
        }
    }

    /**
     * Get the effective SPNameQualifier to apply based on the properties set and the current request.
     * 
     * @param profileRequestContext current profile context
     * 
     * @return the effective NameQualifier to set, or null
     */
    @Nullable protected String getEffectiveSPNameQualifier(@Nonnull final ProfileRequestContext profileRequestContext) {
        if (spNameQualifier != null) {
            if (omitQualifiers) {
                final Function<ProfileRequestContext,String> strategy = defaultSPNameQualifierLookupStrategy;
                if (strategy == null || !Objects.equals(spNameQualifier, strategy.apply(profileRequestContext))) {
                    return spNameQualifier;
                }
                return null;
            }
            return spNameQualifier;
        } else if (!omitQualifiers && defaultSPNameQualifierLookupStrategy != null) {
            return defaultSPNameQualifierLookupStrategy.apply(profileRequestContext);
        } else {
            return null;
        }
    }

}