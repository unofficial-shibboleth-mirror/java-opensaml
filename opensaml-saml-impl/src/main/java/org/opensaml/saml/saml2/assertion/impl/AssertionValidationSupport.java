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

package org.opensaml.saml.saml2.assertion.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support methods for assertion validation.
 */
public final class AssertionValidationSupport {
    
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(AssertionValidationSupport.class);
    
    /** Constructor. */
    private AssertionValidationSupport() { }
    
    /**
     * Check an address from an assertion using valid values obtained from the validation context.
     * 
     * @param context the validation context
     * @param address the address to be evaluated
     * @param validAddressesParam the name of the context parameter holding the set of valid addresses
     * @param assertion the assertion which is the context for evaluation
     * @param description a brief description string used in logging messages
     * 
     * @return the validation result
     */
    @Nonnull public static ValidationResult checkAddress(@Nonnull final ValidationContext context,
            @Nullable final String address,
            @Nonnull final String validAddressesParam,
            @Nonnull final Assertion assertion,
            @Nonnull final String description) {
        
        if (address == null) {
            return ValidationResult.VALID;
        }

        LOG.debug("Evaluating {} value of: {}", description, address);

        final InetAddress[] confirmingAddresses;
        try {
            confirmingAddresses = InetAddress.getAllByName(address);
        } catch (final UnknownHostException e) {
            LOG.warn("The {} value '{}' in assertion '{}' can not be resolved to a valid set of IP address(s)",
                    description, address, assertion.getID());
            context.setValidationFailureMessage(String.format(
                    "%s '%s' is not resolvable to hostname or IP address", description, address));
            return ValidationResult.INDETERMINATE;
        }
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} was resolved to addresses: {}", description, Arrays.asList(confirmingAddresses));
        }

        final Set<InetAddress> validAddresses;
        try {
            validAddresses = (Set<InetAddress>) context.getStaticParameters().get(validAddressesParam);
        } catch (final ClassCastException e) {
            LOG.warn("The value of the static validation parameter '{}' was not a java.util.Set<InetAddress>",
                    validAddressesParam);
            context.setValidationFailureMessage(String.format("Unable to determine list of valid values for %s",
                    description));
            return ValidationResult.INDETERMINATE;
        }
        
        if (validAddresses == null || validAddresses.isEmpty()) {
            LOG.warn("Set of valid addresses was not available from the validation context, unable to evaluate {}",
                    description);
            context.setValidationFailureMessage(String.format("Unable to determine list of valid values for %s",
                    description));
            return ValidationResult.INDETERMINATE;
        }

        for (final InetAddress confirmingAddress : confirmingAddresses) {
            if (validAddresses.contains(confirmingAddress)) {
                LOG.debug("Matched {} '{}' to valid address", description, confirmingAddress.getHostAddress());
                return ValidationResult.VALID;
            }
        }
        
        LOG.debug("Failed to match {} to any supplied valid addresses: {}", description, validAddresses);

        context.setValidationFailureMessage(String.format(
                "%s for assertion '%s' did not match any valid addresses", description, assertion.getID()));
        return ValidationResult.INVALID;
    }
 
}
