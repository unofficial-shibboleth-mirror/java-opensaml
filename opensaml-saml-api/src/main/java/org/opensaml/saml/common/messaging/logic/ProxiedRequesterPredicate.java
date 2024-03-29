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

package org.opensaml.saml.common.messaging.logic;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProxiedRequesterContext;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Predicate that determines whether one of a set of candidates is contained in a
 * a {@link ProxiedRequesterContext} subcontext.
 * 
 * @since 3.4.0
 */
public class ProxiedRequesterPredicate implements Predicate<MessageContext> {

    /** Set of entityIDs to check for. */
    @Nonnull private final Set<String> entityIds;

    /**
     * Constructor.
     * 
     * @param ids the entityIDs to check for
     */
    public ProxiedRequesterPredicate(@Nonnull final Collection<String> ids) {
        Constraint.isNotNull(ids, "EntityID collection cannot be null");
        
        entityIds = new HashSet<>(StringSupport.normalizeStringCollection(ids));
    }
    
    /** {@inheritDoc} */
    public boolean test(@Nullable final MessageContext input) {
        
        final ProxiedRequesterContext ctx = input != null ? input.getSubcontext(ProxiedRequesterContext.class) : null;
        if (ctx != null) {
            return !Collections.disjoint(entityIds, ctx.getRequesters());
        }
        return false;
    }

}