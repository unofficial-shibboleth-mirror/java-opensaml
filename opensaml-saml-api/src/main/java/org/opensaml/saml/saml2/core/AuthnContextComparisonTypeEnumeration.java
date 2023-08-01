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

package org.opensaml.saml.saml2.core;

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Enumeration of {@link org.opensaml.saml.saml2.core.RequestedAuthnContext} comparison types.
 */
public enum AuthnContextComparisonTypeEnumeration {

    /** "exact" comparison type. */
    EXACT("exact"),

    /** "minimum" comparison type. */
    MINIMUM("minimum"),

    /** "maximum" comparison type. */
    MAXIMUM("maximum"),

    /** "better" comparison type. */
    BETTER("better");

    /** The comparison type string. */
    @Nonnull @NotEmpty private String comparisonType;

    /**
     * Constructor.
     * 
     * @param newComparisonType the comparison type string
     */
    private AuthnContextComparisonTypeEnumeration(@Nonnull @NotEmpty final String newComparisonType) {
        comparisonType = newComparisonType;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return comparisonType;
    }

}