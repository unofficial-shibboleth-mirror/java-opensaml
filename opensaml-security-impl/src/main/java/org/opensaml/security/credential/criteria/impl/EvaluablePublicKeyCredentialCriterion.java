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

import java.security.PublicKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.logic.AbstractTriStatePredicate;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.criteria.PublicKeyCriterion;
import org.slf4j.Logger;

/**
 * Instance of evaluable credential criteria for evaluating whether a credential contains a particular
 * public key.
 */
public class EvaluablePublicKeyCredentialCriterion extends AbstractTriStatePredicate<Credential> 
        implements EvaluableCredentialCriterion {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EvaluablePublicKeyCredentialCriterion.class);
    
    /** Base criteria. */
    @Nonnull private final PublicKey publicKey;
    
    /**
     * Constructor.
     *
     * @param criteria the criteria which is the basis for evaluation
     */
    public EvaluablePublicKeyCredentialCriterion(@Nonnull final PublicKeyCriterion criteria) {
        publicKey = Constraint.isNotNull(criteria, "Criterion instance cannot be null").getPublicKey();
    }
    
    /**
     * Constructor.
     *
     * @param newPublicKey the criteria value which is the basis for evaluation
     */
    public EvaluablePublicKeyCredentialCriterion(@Nonnull final PublicKey newPublicKey) {
        publicKey = Constraint.isNotNull(newPublicKey, "Public key cannot be null");
    }

    /** {@inheritDoc} */
    public boolean test(@Nullable final Credential target) {
        if (target == null) {
            log.error("Credential target was null");
            return isNullInputSatisfies();
        }
        
        final PublicKey key = target.getPublicKey();
        if (key == null) {
            log.info("Credential contained no public key, does not satisfy public key criteria");
            return false;
        }
        
        return publicKey.equals(key);
    }
    
    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("EvaluablePublicKeyCredentialCriterion [publicKey=");
        builder.append(publicKey);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return publicKey.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof EvaluablePublicKeyCredentialCriterion other) {
            return publicKey.equals(other.publicKey);
        }

        return false;
    }
}