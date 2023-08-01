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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Support methods for assertion validation.
 */
public final class AssertionValidationSupport {
    
    /** Logger. */
    @Nonnull private static final Logger LOG = LoggerFactory.getLogger(AssertionValidationSupport.class);
    
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
            context.getValidationFailureMessages().add(String.format(
                    "%s '%s' is not resolvable to hostname or IP address", description, address));
            return ValidationResult.INDETERMINATE;
        }
        
        if (LOG.isDebugEnabled()) {
            assert confirmingAddresses != null;
            LOG.debug("{} was resolved to addresses: {}", description, CollectionSupport.listOf(confirmingAddresses));
        }

        final Set<InetAddress> validAddresses;
        try {
            validAddresses = (Set<InetAddress>) context.getStaticParameters().get(validAddressesParam);
        } catch (final ClassCastException e) {
            context.getValidationFailureMessages().add(
                    String.format("Unable to determine list of valid values for %s", description));
            return ValidationResult.INDETERMINATE;
        }
        
        if (validAddresses == null || validAddresses.isEmpty()) {
            context.getValidationFailureMessages().add(String.format(
                    "Set of valid addresses was not available from the validation context, unable to evaluate %s",
                            description));
            return ValidationResult.INDETERMINATE;
        }

        for (final InetAddress confirmingAddress : confirmingAddresses) {
            if (validAddresses.contains(confirmingAddress)) {
                LOG.debug("Matched {} '{}' to valid address", description, confirmingAddress.getHostAddress());
                return ValidationResult.VALID;
            }
        }
        
        context.getValidationFailureMessages().add(
                String.format("%s for assertion '%s' did not match supplied valid addresses: %s",
                        description, assertion.getID(), validAddresses));
        return ValidationResult.INVALID;
    }
 
}