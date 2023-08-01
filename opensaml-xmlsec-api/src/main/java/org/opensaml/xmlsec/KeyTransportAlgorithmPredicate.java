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

package org.opensaml.xmlsec;

import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.credential.Credential;

import net.shibboleth.shared.logic.Constraint;

/**
 * An interface for predicates which allow evaluation of a candidate key transport algorithm relative to
 * a candidate data encryption algorithm and/or a candidate key transport credential.
 */
public interface KeyTransportAlgorithmPredicate extends Predicate<KeyTransportAlgorithmPredicate.SelectionInput> {
    
    /**
     * Input class for instances of {@link KeyTransportAlgorithmPredicate}.
     */
    static final class SelectionInput {
        
        /** The candidate key transport algorithm. */
        @Nonnull private final String keyTransportAlgorithm;
        
        /** The candidate data encryption algorithm. */
        @Nullable private final String dataEncryptionAlgorithm;
        
        /** The candidate key transport credential. */
        @Nullable private final Credential keyTransportCredential;
        
        /**
         * Constructor.
         *
         * @param keyTransportAlgorithmCandidate the candidate key transport algorithm
         * @param dataEncryptionAlgorithmCandidate the candidate data encryption algorithm
         * @param keyTransportCredentialCandidate the candidate key transport credential
         */
        public SelectionInput(@Nonnull final String keyTransportAlgorithmCandidate, 
                @Nullable final String dataEncryptionAlgorithmCandidate, 
                @Nullable final Credential keyTransportCredentialCandidate) {
            
            keyTransportAlgorithm = Constraint.isNotNull(keyTransportAlgorithmCandidate, 
                    "Key transport algorithm candidate was not supplied");
            dataEncryptionAlgorithm = dataEncryptionAlgorithmCandidate;
            keyTransportCredential = keyTransportCredentialCandidate;
        }

        /**
         * Get the candidate key transport algorithm.
         * 
         * @return the algorithm
         */
        @Nonnull public String getKeyTransportAlgorithm() {
            return keyTransportAlgorithm;
        }

        /**
         * Get the candidate data encryption algorithm.
         * 
         * @return the algorithm, may be null
         */
        @Nullable public String getDataEncryptionAlgorithm() {
            return dataEncryptionAlgorithm;
        }

        /**
         * Get the candidate key transport credential.
         * 
         * @return the credential, may be null
         */
        @Nullable public Credential getKeyTransportCredential() {
            return keyTransportCredential;
        }
        
    }
    
}