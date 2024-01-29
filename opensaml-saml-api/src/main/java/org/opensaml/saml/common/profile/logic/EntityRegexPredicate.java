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

package org.opensaml.saml.common.profile.logic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.saml2.metadata.EntityDescriptor;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.logic.Constraint;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Predicate that matches {@link EntityDescriptor#getEntityID()} against a regular exression.
 */
public class EntityRegexPredicate implements Predicate<EntityDescriptor> {
    
    /** Regular expression to test. */
    @Nonnull private final Pattern pattern;
    
    /**
     * Constructor.
     * 
     * @param exp regular expression to check for
     */
    public EntityRegexPredicate(@Nonnull @ParameterName(name="exp") final Pattern exp) {
        pattern = Constraint.isNotNull(exp, "Pattern cannot be null");
    }
    
    /** {@inheritDoc} */
    public boolean test(@Nullable final EntityDescriptor input) {
        
        if (input == null || input.getEntityID() == null) {
            return false;
        }
        
        return pattern.matcher(input.getEntityID()).matches();
    }

}