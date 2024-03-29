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

package org.opensaml.security.credential.criteria.impl;

import java.security.Key;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.logic.AbstractTriStatePredicate;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.criteria.KeyAlgorithmCriterion;
import org.slf4j.Logger;

/**
 * Instance of evaluable credential criteria for evaluating the credential key algorithm.
 */
public class EvaluableKeyAlgorithmCredentialCriterion extends AbstractTriStatePredicate<Credential> 
        implements EvaluableCredentialCriterion {

    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EvaluableKeyAlgorithmCredentialCriterion.class);

    /** Base criteria. */
    @Nonnull private final String keyAlgorithm;

    /**
     * Constructor.
     * 
     * @param criteria the criteria which is the basis for evaluation
     */
    public EvaluableKeyAlgorithmCredentialCriterion(@Nonnull final KeyAlgorithmCriterion criteria) {
        keyAlgorithm = Constraint.isNotNull(criteria, "Criterion instance cannot be null").getKeyAlgorithm();
    }

    /**
     * Constructor.
     * 
     * @param newKeyAlgorithm the criteria value which is the basis for evaluation
     */
    public EvaluableKeyAlgorithmCredentialCriterion(@Nonnull final String newKeyAlgorithm) {
        final String trimmed = StringSupport.trimOrNull(newKeyAlgorithm);
        keyAlgorithm = Constraint.isNotNull(trimmed, "Key algorithm cannot be null or empty");
    }

    /** {@inheritDoc} */
    public boolean test(@Nullable final Credential target) {
        if (target == null) {
            log.error("Credential target was null");
            return isNullInputSatisfies();
        }
        
        final Key key = getKey(target);
        if (key == null) {
            log.info("Could not evaluate criteria, credential contained no key");
            return isUnevaluableSatisfies();
        }
        
        final String algorithm = StringSupport.trimOrNull(key.getAlgorithm());
        if (algorithm == null) {
            log.info("Could not evaluate criteria, key does not specify an algorithm via getAlgorithm()");
            return isUnevaluableSatisfies();
        }

        return keyAlgorithm.equals(algorithm);
    }

    /**
     * Get the key contained within the credential.
     * 
     * @param credential the credential containing a key
     * @return the key from the credential
     */
    @Nullable private Key getKey(@Nonnull final Credential credential) {
        if (credential.getPublicKey() != null) {
            return credential.getPublicKey();
        } else if (credential.getSecretKey() != null) {
            return credential.getSecretKey();
        } else if (credential.getPrivateKey() != null) {
            // There should have been a corresponding public key, but just in case...
            return credential.getPrivateKey();
        } else {
            return null;
        }
    }
    
    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("EvaluableKeyAlgorithmCredentialCriterion [keyAlgorithm=");
        builder.append(keyAlgorithm);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return keyAlgorithm.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof EvaluableKeyAlgorithmCredentialCriterion other) {
            return keyAlgorithm.equals(other.keyAlgorithm);
        }

        return false;
    }

}