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

package org.opensaml.security.x509;

import javax.annotation.Nonnull;
import javax.security.auth.x500.X500Principal;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface for implementations which handle parsing and serialization of X.500 names
 * represented by {@link X500Principal}.
 */
public interface X500DNHandler {
    
    /** Specifies the string format specified in RFC 1779. */
    @Nonnull @NotEmpty static final String FORMAT_RFC1779 = X500Principal.RFC1779;
    
    /** Specifies the string format specified in RFC 2253. */
    @Nonnull @NotEmpty static final String FORMAT_RFC2253 = X500Principal.RFC2253;
    
    /**
     * Parse the string representation of a name and build a new principal instance.
     * 
     * @param name the name string to parse
     * @return a new principal instance
     * 
     * @throws IllegalArgumentException if the name value can not be parsed by the implementation
     */
    @Nonnull X500Principal parse(@Nonnull final String name);
    
    /**
     * Parse the ASN.1 DER encoding representation of a name and build a new principal instance.
     * 
     * @param name a distinguished name in ASN.1 DER encoded form
     * @return a new principal instance
     * 
     * @throws IllegalArgumentException if the name value can not be parsed by the implementation
     */
    @Nonnull X500Principal parse(@Nonnull final byte[] name);
    
    /**
     * Returns a string representation of the X.500 distinguished name using the default format
     * as defined in the underlying implementation.
     * 
     * @param principal the principal name instance to serialize
     * @return the serialized string name
     */
    @Nonnull String getName(@Nonnull final X500Principal principal);
    
    /**
     * Returns a string representation of the X.500 distinguished name using the specified format.
     * 
     * The values and meanings of the format specifier are implementation dependent. Constants for
     * two common standard formats are provided here as {@link #FORMAT_RFC1779} and {@link #FORMAT_RFC2253};
     * 
     * @param principal the principal name instance to serialize
     * @param format the format specifier of the resulting serialized string name
     * @return the serialized string name
     * 
     * @throws IllegalArgumentException if the specified format is not understood by the implementation
     */
    @Nonnull String getName(@Nonnull final X500Principal principal, @Nonnull final String format);
    
    /**
     * Returns the distinguished name in ASN.1 DER encoded form.
     *  
     * @param principal the principal name instance to serialize
     * @return the serialized name in ASN.1 DER encoded form
     */
    @Nonnull byte[] getEncoded(@Nonnull final X500Principal principal);
    
    /**
     * Clone the handler. Implementations which maintain instance-specific configuration data, etc,
     * should implement this appropriately, possibly also implementing {@link Cloneable}.
     * 
     * @return the cloned handler
     */
    @Nonnull X500DNHandler clone();

}