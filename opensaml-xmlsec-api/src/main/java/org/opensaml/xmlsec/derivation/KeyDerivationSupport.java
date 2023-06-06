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

package org.opensaml.xmlsec.derivation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.xmlsec.algorithm.AlgorithmSupport;

import net.shibboleth.shared.logic.Constraint;

/**
 * Support key derivation operations.
 */
public final class KeyDerivationSupport {
    
    /** Constructor. */
    private KeyDerivationSupport() { }

    /**
     * Get the JCA key algorithm which corresponds to the specified algorithm URI.
     * 
     * @param algorithmURI the algorithm URI with which the derived key will be user
     * 
     * @return the JCA key algorithm
     * 
     * @throws KeyDerivationException if key algorithm could not be determined
     */
    @Nonnull public static String getJCAKeyAlgorithm(@Nonnull final String algorithmURI)
            throws KeyDerivationException {
        Constraint.isNotNull(algorithmURI, "Algorithm URI was null");
        
        final String jcaKeyAlgorithm = AlgorithmSupport.getKeyAlgorithm(algorithmURI);
        if (jcaKeyAlgorithm == null) {
            throw new KeyDerivationException("Could not determine JCA key algorithm from URI: " + algorithmURI);
        }
        return jcaKeyAlgorithm;
    }
    
    /**
     * Get the effective key length based on the specified algorithm URI and the specified key length, if present.
     * 
     * <p>
     * If the algorithm URI implies a key length and the specified key length is non-null, the lengths must
     * match or an exception will be thrown.  If the algorithm URI does not imply a key length and the specified 
     * length is null, and exception will be thrown.
     * </p>
     * 
     * @param algorithmURI the algorithm URI with which the derived key will be used
     * @param specifiedKeyLength an explicitly specified key length
     * 
     * @return the effective key length
     * 
     * @throws KeyDerivationException if algorithm and specified key lengths are not consistent
     */
    public static int getEffectiveKeyLength(@Nonnull final String algorithmURI,
            @Nullable final Integer specifiedKeyLength) throws KeyDerivationException {
        Constraint.isNotNull(algorithmURI, "Algorithm URI was null");
        
        final Integer algoKeyLength = AlgorithmSupport.getKeyLength(algorithmURI);
        if (algoKeyLength == null) {
            if (specifiedKeyLength == null) {
                throw new KeyDerivationException(String.format("Could not determine algorithm key length from URI '%s'"
                        + "and no length was specified", algorithmURI));
            }
            return specifiedKeyLength;
        } 
        
        if (specifiedKeyLength != null && ! specifiedKeyLength.equals(algoKeyLength)) {
            throw new KeyDerivationException(String.format("Algorithm URI '%s' key length (%d) "
                    + "does not match specified (%d)", algorithmURI, algoKeyLength, specifiedKeyLength));
        }
        
        return algoKeyLength;
    }
    
}