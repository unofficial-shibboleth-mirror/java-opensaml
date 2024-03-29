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

package org.opensaml.profile.action;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.opensaml.profile.context.ProfileRequestContext;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Base class for conditional profile actions.
 * 
 * <p>A condition does not represent a situation in which an error should be raised, but that normal
 * processing should continue and the action simply doesn't apply, so a false condition does not
 * raise a non-proceed event.</p>
 */
public abstract class AbstractConditionalProfileAction extends AbstractProfileAction {
    
    /** Condition dictating whether to run or not. */
    @Nonnull private Predicate<ProfileRequestContext> activationCondition;
    
    /** Constructor. */
    public AbstractConditionalProfileAction() {
        activationCondition = PredicateSupport.alwaysTrue();
    }
    
    /**
     * Get activation condition indicating whether action should execute.
     * 
     * @return  activation condition
     */
    @Nonnull public Predicate<ProfileRequestContext> getActivationCondition() {
        return activationCondition;
    }

    /**
     * Set activation condition indicating whether action should execute.
     * 
     * @param condition predicate to apply
     */
    public void setActivationCondition(@Nonnull final Predicate<ProfileRequestContext> condition) {
        checkSetterPreconditions();
        
        activationCondition = Constraint.isNotNull(condition, "Predicate cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (activationCondition.test(profileRequestContext)) {
            return super.doPreExecute(profileRequestContext);
        }
        LoggerFactory.getLogger(AbstractConditionalProfileAction.class).debug(
                "{} Activation condition for action returned false", getLogPrefix());
        return false;
    }

}