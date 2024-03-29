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

package org.opensaml.saml.saml2.common;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;

/**
 * Utility class for common SAML 2 operations.
 */
public final class SAML2Support {
    
    /** Private constructor to disallow instantiation. */
    private SAML2Support() { }

    /**
     * Checks to see if the given XMLObject is still valid. An XMLObject is valid if, and only if, itself and every
     * ancestral {@link TimeBoundSAMLObject} is valid.
     * 
     * @param xmlObject the XML object tree to check
     * 
     * @return true of the tree is valid, false if not
     */
    public static boolean isValid(@Nonnull final XMLObject xmlObject) {
        if (xmlObject instanceof TimeBoundSAMLObject) {
            final TimeBoundSAMLObject timeBoundObject = (TimeBoundSAMLObject) xmlObject;
            if (!timeBoundObject.isValid()) {
                return false;
            }
        }

        final XMLObject parent = xmlObject.getParent();
        if (parent != null) {
            return isValid(parent);
        }

        return true;
    }

    /**
     * Gets the earliest expiration instant for a XMLObject. This method traverses the tree of SAMLObject rooted at the
     * given object and calculates the earliest expiration as the earliest of the following two items:
     * <ul>
     * <li>the earliest validUntil time on a {@link TimeBoundSAMLObject}</li>
     * <li>the shortest duration on a {@link CacheableSAMLObject} added to the current time</li>
     * </ul>
     * 
     * @param xmlObject the XML object tree from which to get the earliest expiration time
     * 
     * @return the earliest expiration time
     */
    @Nullable public static Instant getEarliestExpiration(@Nullable final XMLObject xmlObject) {
        return getEarliestExpiration(xmlObject, null, Instant.now());
    }

    /**
     * Gets the earliest expiration instant within a metadata tree.
     * 
     * @param xmlObject the target XMLObject to evaluate
     * @param candidateTime the candidate earliest expiration instant
     * @param now when this method was called
     * 
     * @return the earliest expiration instant within a metadata tree. May be null if the input candiateTime 
     *          was null, otherwise will always be non-null.
     */
    @Nullable public static Instant getEarliestExpiration(@Nullable final XMLObject xmlObject, 
            @Nullable final Instant candidateTime, @Nonnull final Instant now) {
        
        Instant earliestExpiration = candidateTime;

        // Test duration based times
        if (xmlObject instanceof CacheableSAMLObject cacheable) {
            earliestExpiration = getEarliestExpirationFromCacheable(cacheable, earliestExpiration, now);
        }

        // Test instant based times
        if (xmlObject instanceof TimeBoundSAMLObject timebound) {
            earliestExpiration = getEarliestExpirationFromTimeBound(timebound, earliestExpiration);
        }

        // Inspect children
        if (xmlObject != null) {
            final List<XMLObject> children = xmlObject.getOrderedChildren();
            if (children != null && !children.isEmpty()) {
                for (final XMLObject child : children) {
                    earliestExpiration = getEarliestExpiration(child, earliestExpiration, now);
                }
            }
        }

        return earliestExpiration;
    }
    
    /**
     * Gets the earliest effective expiration instant of the specified cacheable SAML object and the specified 
     * candidate time.
     * 
     * @param cacheableObject the target XMLObject to evaluate
     * @param candidateTime the candidate earliest expiration instant
     * @param now when this method was called
     * 
     * @return the earliest effective expiration instant of the 2 targets. May be null if the input candiateTime 
     *          was null, otherwise will always be non-null.
     */
    @Nullable public static Instant getEarliestExpirationFromCacheable(
            @Nonnull final CacheableSAMLObject cacheableObject, @Nullable final Instant candidateTime,
            @Nonnull final Instant now) {
        
        Instant earliestExpiration = candidateTime;
        final Duration cacheDuration = cacheableObject.getCacheDuration();

        if (cacheDuration != null && !cacheDuration.isNegative()) {
            final Instant elementExpirationTime = now.plus(cacheDuration);
            if (earliestExpiration == null) {
                earliestExpiration = elementExpirationTime;
            } else {
                if (elementExpirationTime != null && elementExpirationTime.isBefore(earliestExpiration)) {
                    earliestExpiration = elementExpirationTime;
                }
            }
        }
        
        return earliestExpiration;
    }
    
    /**
     * Gets the earliest effective expiration instant of the specified time-bound SAML object and the specified 
     * candidate time.
     * 
     * @param timeBoundObject the target XMLObject to evaluate
     * @param candidateTime the earliest expiration instant
     * 
     * @return the earliest effective expiration instant of the 2 targets. May be null if the input candiateTime 
     *          was null, otherwise will always be non-null.
     */
    @Nullable public static Instant getEarliestExpirationFromTimeBound(
            @Nonnull final TimeBoundSAMLObject timeBoundObject, @Nullable final Instant candidateTime) {
        
        Instant earliestExpiration = candidateTime;
        
        final Instant elementExpirationTime = timeBoundObject.getValidUntil();
        if (earliestExpiration == null) {
            earliestExpiration = elementExpirationTime;
        } else {
            if (elementExpirationTime != null && elementExpirationTime.isBefore(earliestExpiration)) {
                earliestExpiration = elementExpirationTime;
            }
        }
        
        return earliestExpiration;
    }
    
}