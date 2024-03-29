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

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.security.credential.Credential;
import org.slf4j.Logger;

import com.google.common.base.Strings;

/**
 * Instance of evaluable credential criteria for evaluating a credential's entityID.
 */
public class EvaluableEntityIDCredentialCriterion extends AbstractTriStatePredicate<Credential> 
        implements EvaluableCredentialCriterion {

    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EvaluableEntityIDCredentialCriterion.class);

    /** Base criteria. */
    @Nonnull private final String entityID;

    /**
     * Constructor.
     * 
     * @param criteria the criteria which is the basis for evaluation
     */
    public EvaluableEntityIDCredentialCriterion(@Nonnull final EntityIdCriterion criteria) {
        entityID = Constraint.isNotNull(criteria, "Criterion instance may not be null").getEntityId();
    }

    /**
     * Constructor.
     * 
     * @param entity the criteria value which is the basis for evaluation
     */
    public EvaluableEntityIDCredentialCriterion(@Nonnull final String entity) {
        final String trimmed = StringSupport.trimOrNull(entity);
        entityID = Constraint.isNotNull(trimmed, "EntityID criteria cannot be null or empty");
    }

    /** {@inheritDoc} */
    public boolean test(@Nullable final Credential target) {
        if (target == null) {
            log.error("Credential target was null");
            return isNullInputSatisfies();
        } else if (Strings.isNullOrEmpty(target.getEntityId())) {
            log.info("Could not evaluate criteria, credential contained no entityID");
            return isUnevaluableSatisfies();
        }
        return entityID.equals(target.getEntityId());
    }
    
    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("EvaluableEntityIDCredentialCriterion [entityID=");
        builder.append(entityID);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return entityID.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof EvaluableEntityIDCredentialCriterion other) {
            return entityID.equals(other.entityID);
        }

        return false;
    }

}