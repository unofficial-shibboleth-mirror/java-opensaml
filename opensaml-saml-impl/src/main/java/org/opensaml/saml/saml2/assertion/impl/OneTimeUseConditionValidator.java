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

package org.opensaml.saml.saml2.assertion.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.namespace.QName;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.ConditionValidator;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.OneTimeUse;
import org.opensaml.storage.ReplayCache;
import org.slf4j.Logger;

/**
 * {@link ConditionValidator} used for {@link OneTimeUse} conditions.
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * </p>
 * <ul>
 * <li>
 * {@link SAML2AssertionValidationParameters#COND_ONE_TIME_USE_EXPIRES}:
 * Optional. If not supplied, defaults to the validator-wide value supplied at construction, or
 * the default value, as retrieved via {@link #getReplayCacheExpires()}.
 * </li>
 * </ul>
 * 
 * <p>
 * Supports the following {@link ValidationContext} dynamic parameters:
 * </p>
 * <ul>
 *   <li>None.</li>
 * </ul>
 */
@ThreadSafe
public class OneTimeUseConditionValidator implements ConditionValidator {
    
    /** Cache context name. */
    @Nonnull @NotEmpty public static final String CACHE_CONTEXT = OneTimeUseConditionValidator.class.getName();
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(OneTimeUseConditionValidator.class);
    
    /** Replay cache used to track which assertions have been used. */
    @Nonnull private final ReplayCache replayCache;
    
    /** Time for disposal of value from cache. */
    @Nonnull private final Duration replayCacheExpires;

    /**
     * Constructor.
     * 
     * @param replay reply cache used to track which assertions have been used
     * @param expires time for disposal of tracked assertion from the replay cache.
     *      May be null, then defaults to 8 hours
     */
    public OneTimeUseConditionValidator(@Nonnull final ReplayCache replay, @Nullable final Duration expires) {
        replayCache = Constraint.isNotNull(replay, "Replay cache was null");
        
        if (expires == null) {
            replayCacheExpires = Duration.ofHours(8);
        } else if (expires.isNegative()) {
            replayCacheExpires = Duration.ofHours(8);
            log.warn("Supplied value for replay cache expires '{}' was negative, using default expiration", 
                    replayCacheExpires);
        } else {
            replayCacheExpires = expires;
        }
    }

    /** {@inheritDoc} */
    @Nonnull public QName getServicedCondition() {
        return OneTimeUse.DEFAULT_ELEMENT_NAME;
    }

    /** {@inheritDoc} */
    @Nonnull public ValidationResult validate(@Nonnull final Condition condition, @Nonnull final Assertion assertion,
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        if (!(condition instanceof OneTimeUse) 
                && !Objects.equals(condition.getElementQName(), getServicedCondition())) {
            context.getValidationFailureMessages().add(
                    String.format("Condition '%s' of type '%s' in assertion '%s' was not an '%s' condition",
                            condition.getElementQName(), condition.getSchemaType(), assertion.getID(),
                            getServicedCondition()));
            return ValidationResult.INDETERMINATE;
        }
        
        if (!replayCache.check(CACHE_CONTEXT, getCacheValue(assertion), getExpires(assertion, context))) {
            context.getValidationFailureMessages().add(String.format(
                    "Assertion '%s' has a one time use condition and has been used before", assertion.getID()));
            return ValidationResult.INVALID;
        }

        return ValidationResult.VALID;
    }
    
    /**
     * Get the configured validator cache expiration interval.
     * 
     * @return the configured cache expiration interval
     */
    @Nonnull protected Duration getReplayCacheExpires() {
        return replayCacheExpires;
    }
    
    /**
     * Get the one-time use expiration time for the assertion being evaluated.
     * 
     * <p>
     * Defaults to <code>Instant.now().plus(getReplayCacheExpires())</code>.
     * </p>
     * 
     * <p>
     * A subclass might override this to base expiration on data from the assertion or the validation context.
     * </p>
     * 
     * @param assertion the SAML 2 Assertion being evaluated
     * @param context the current validation context
     * 
     * @return the effective one-time use expiration for the assertion being evaluated
     */
    @Nonnull protected Instant getExpires(final Assertion assertion, final ValidationContext context) {
        Duration expires = null;
        final Object raw = context.getStaticParameters().get(
                SAML2AssertionValidationParameters.COND_ONE_TIME_USE_EXPIRES);
        if (raw instanceof Duration) {
            expires = (Duration) raw;
        }
        
        log.debug("Saw one-time use cache expires context param: {}", expires);
        
        Duration suppliedExpiration = null;
        
        if (expires == null || expires.isZero()) {
            suppliedExpiration = getReplayCacheExpires();
        } else if (expires.isNegative()) {
            log.warn("Supplied context param for replay cache expires '{}' was negative, using configured expiration", 
                    expires);
            suppliedExpiration = getReplayCacheExpires();
        } else {
            suppliedExpiration = expires;
        }
             
        log.debug("Effective one-time use cache expires of: {}", suppliedExpiration);
        
        final Instant computedExpiration = Instant.now().plus(suppliedExpiration);
        log.debug("Computed one-time use cache effective expiration time of: {}", computedExpiration);
        return computedExpiration;
    }

    /**
     * Get the string value which will be tracked in the cache for purposes of one-time use detection.
     * 
     * @param assertion the SAML 2 Assertion to evaluate
     * 
     * @return the cache value
     * 
     * @throws AssertionValidationException thrown if there is a problem calculating the cached value
     */
    @Nonnull protected String getCacheValue(@Nonnull final Assertion assertion) throws AssertionValidationException {
        String issuer = null;
        final Issuer issuerObject = assertion.getIssuer();
        if (issuerObject != null) {
            issuer = StringSupport.trimOrNull(issuerObject.getValue());
        }
        
        if (issuer == null) {
            issuer = "NoIssuer";
        }
        
        String id = StringSupport.trimOrNull(assertion.getID());
        if (id == null) {
            id = "NoID";
        }
        
        final String value = String.format("%s--%s", issuer, id);
        log.debug("Generated one-time use cache value of: {}", value);
        return value;
    }
   
}