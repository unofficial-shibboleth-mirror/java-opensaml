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

package org.opensaml.security.x509.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.security.x509.PKIXValidationInformation;
import org.opensaml.security.x509.PKIXValidationInformationResolver;
import org.opensaml.security.x509.TrustedNamesCriterion;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/**
 * An implementation of {@link PKIXValidationInformationResolver} which always returns a static, fixed set of
 * information.
 */
public class StaticPKIXValidationInformationResolver implements PKIXValidationInformationResolver {

    /** The PKIX validation information to return. */
    @Nonnull private final List<PKIXValidationInformation> pkixInfo;

    /** The set of trusted names to return. */
    @Nonnull private final Set<String> trustedNames;
    
    /** Flag indicating whether dynamic trusted names should be extracted from criteria set. */
    private boolean supportDynamicTrustedNames;
    
    /**
     * Constructor.
     * 
     * <p>Dynamic trusted names will not be supported.</p>
     * 
     * @param info list of PKIX validation information to return
     * @param names set of trusted names to return
     */
    public StaticPKIXValidationInformationResolver(
            @Nullable @ParameterName(name="info") final List<PKIXValidationInformation> info,
            @Nullable @ParameterName(name="names") final Set<String> names) {
        this(info, names, false);
    }

    /**
     * Constructor.
     * 
     * @param info list of PKIX validation information to return
     * @param names set of trusted names to return
     * @param supportDynamicNames whether resolver should support dynamic extraction of trusted names
     *        from an instance of {@link TrustedNamesCriterion} in the criteria set
     */
    public StaticPKIXValidationInformationResolver(
            @Nullable @ParameterName(name="info") final List<PKIXValidationInformation> info,
            @Nullable @ParameterName(name="names") final Set<String> names,
            @ParameterName(name="supportDynamicNames") final boolean supportDynamicNames) {
        if (info != null) {
            pkixInfo = CollectionSupport.copyToList(info);
        } else {
            pkixInfo = CollectionSupport.emptyList();
        }

        if (names != null) {
            trustedNames = CollectionSupport.copyToSet(names);
        } else {
            trustedNames = CollectionSupport.emptySet();
        }
        
        supportDynamicTrustedNames = supportDynamicNames;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public Set<String> resolveTrustedNames(@Nullable final CriteriaSet criteriaSet) throws ResolverException {
        if (criteriaSet == null) {
            return CollectionSupport.copyToSet(trustedNames);
        }
        
        final HashSet<String> temp = new HashSet<>(trustedNames);
        final EntityIdCriterion entityIDCriterion = criteriaSet.get(EntityIdCriterion.class);
        if (entityIDCriterion != null) {
            temp.add(entityIDCriterion.getEntityId());
        }
        
        if (supportDynamicTrustedNames) {
            final TrustedNamesCriterion trustedNamesCriterion = criteriaSet.get(TrustedNamesCriterion.class);
            if (trustedNamesCriterion != null) {
                temp.addAll(trustedNamesCriterion.getTrustedNames());
            }
        }
        
        return CollectionSupport.copyToSet(temp);
    }

    /** {@inheritDoc} */
    @Override
    public boolean supportsTrustedNameResolution() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public Iterable<PKIXValidationInformation> resolve(@Nullable final CriteriaSet criteria)
            throws ResolverException {
        return pkixInfo;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public PKIXValidationInformation resolveSingle(@Nullable final CriteriaSet criteria)
            throws ResolverException {
        if (!pkixInfo.isEmpty()) {
            return pkixInfo.get(0);
        }
        return null;
    }

}