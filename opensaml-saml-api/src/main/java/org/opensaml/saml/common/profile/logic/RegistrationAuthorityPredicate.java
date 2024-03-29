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

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.ext.saml2mdrpi.RegistrationInfo;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Predicate to determine whether one of a set of names matches an entity's
 * {@link RegistrationInfo#getRegistrationAuthority()}.
 */
public class RegistrationAuthorityPredicate  extends AbstractRegistrationInfoPredicate {

    /** Authorities to match on. */
    @Nonnull private final Set<String> authorities;
    
    /**
     * Constructor.
     * 
     * @param names the authority names to test for
     */
    public RegistrationAuthorityPredicate(@Nullable @ParameterName(name="names") final Collection<String> names) {
        authorities = CollectionSupport.copyToSet(StringSupport.normalizeStringCollection(names));
    }

    /**
     * Get the authority name criteria.
     * 
     * @return  the authority name criteria
     */
    @Nonnull @Unmodifiable @NotLive public Set<String> getAuthorities() {
        return authorities;
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doApply(@Nonnull final RegistrationInfo info) {
        return authorities.contains(info.getRegistrationAuthority());
    }
    
}