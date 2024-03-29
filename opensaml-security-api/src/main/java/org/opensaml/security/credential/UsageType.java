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

package org.opensaml.security.credential;

import javax.annotation.Nonnull;

import net.shibboleth.shared.logic.Constraint;

/** Credential usage types. */
public enum UsageType {
    
    /** Key used for encryption processes. */
    ENCRYPTION("encryption"),
    
    /** Key used for signature processes including TLS/SSL. */
    SIGNING("signing"),
    
    /** Denotes that the purpose of the key was not specified. */
    UNSPECIFIED("unspecified");
    
    /** Enum string value. */
    @Nonnull private String value;
    
    /**
     * Constructor.
     *
     * @param v the enum string value
     */
    private UsageType(@Nonnull final String v) {
        value = Constraint.isNotNull(v, "UsageType string cannot be null");
    }
    
    /**
     * Get the enum string value.
     * 
     * @return the enum string value
     */
    @Nonnull public String getValue() {
        return value;
    }
   
}