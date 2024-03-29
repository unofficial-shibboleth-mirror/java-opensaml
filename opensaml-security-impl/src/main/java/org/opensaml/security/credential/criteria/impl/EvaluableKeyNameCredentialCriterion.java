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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.logic.AbstractTriStatePredicate;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.criteria.KeyNameCriterion;
import org.slf4j.Logger;


/**
 * Instance of evaluable credential criteria for evaluating credential key names.
 */
public class EvaluableKeyNameCredentialCriterion extends AbstractTriStatePredicate<Credential> 
        implements EvaluableCredentialCriterion {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EvaluableKeyNameCredentialCriterion.class);
    
    /** Base criteria. */
    @Nonnull private final String keyName;
    
    /**
     * Constructor.
     *
     * @param criteria the criteria which is the basis for evaluation
     */
    public EvaluableKeyNameCredentialCriterion(@Nonnull final KeyNameCriterion criteria) {
        keyName = Constraint.isNotNull(criteria, "Criterion instance cannot be null").getKeyName();
    }
    
    /**
     * Constructor.
     *
     * @param newKeyName the criteria value which is the basis for evaluation
     */
    public EvaluableKeyNameCredentialCriterion(@Nonnull final String newKeyName) {
        final String trimmed = StringSupport.trimOrNull(newKeyName);
        keyName = Constraint.isNotNull(trimmed, "Key name cannot be null or empty");
    }

    /** {@inheritDoc} */
    public boolean test(@Nullable final Credential target) {
        if (target == null) {
            log.error("Credential target was null");
            return isNullInputSatisfies();
        }
        if (target.getKeyNames().isEmpty()) {
            log.info("Could not evaluate criteria, credential contained no key names");
            return isUnevaluableSatisfies();
        }
        return target.getKeyNames().contains(keyName);
    }
    
    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("EvaluableKeyNameCredentialCriterion [keyName=");
        builder.append(keyName);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return keyName.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof EvaluableKeyNameCredentialCriterion other) {
            return keyName.equals(other.keyName);
        }

        return false;
    }

}