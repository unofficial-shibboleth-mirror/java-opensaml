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

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.x509.PKIXValidationOptions;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Specialization of {@link PKIXValidationOptions} which specifies options specific to a
 * {@link org.opensaml.security.x509.PKIXTrustEvaluator} based on the Java CertPath API.
 */
public class CertPathPKIXValidationOptions extends PKIXValidationOptions {
    
    /** Force RevocationEnabled flag. */
    private boolean forceRevocationEnabled;
   
    /** Value for RevocationEnabled when forced. */
    private boolean revocationEnabled;
    
    /** Disable policy mapping flag. */
    private boolean policyMappingInhibit;

    /** Flag for disallowing the "any" policy OID. */
    private boolean anyPolicyInhibit;

    /** Acceptable policy OIDs. */
    @Nonnull private Set<String> initialPolicies;
    
    /** Constructor. */
    public CertPathPKIXValidationOptions() {
        forceRevocationEnabled = false;
        revocationEnabled = true;
        policyMappingInhibit = false;
        anyPolicyInhibit = false;
        initialPolicies = CollectionSupport.emptySet();
    }
    
    /**
     * If true, the revocation behavior of the underlying CertPath provider will be forced to the
     * value supplied by {@link #isRevocationEnabled()}. If false, the revocation behavior
     * of the underlying provider will be determined by the PKIXTrustEvaluator implementation.
     * 
     * <p>Default is: <b>false</b></p>
     * 
     * @return whether to force revocation behavior
     */
    public boolean isForceRevocationEnabled() {
        return forceRevocationEnabled;
    }

    /**
     * If true, the revocation behavior of the underlying CertPath provider will be forced to the
     * value supplied by {@link #isRevocationEnabled()}. If false, the revocation behavior
     * of the underlying provider will be determined by the PKIXTrustEvaluator implementation.
     * 
     * <p>Default is: <b>false</b></p>
     * 
     * @param flag whether to force revocation behavior
     */
    public void setForceRevocationEnabled(final boolean flag) {
        forceRevocationEnabled = flag;
    }

    /**
     * If {@link #isForceRevocationEnabled()} is true, the revocation behavior of the underlying CertPath Provider
     * will be forced to this value. If the former is false, the revocation behavior
     * of the underlying provider will be determined by the PKIXTrustEvaluator implementation.
     * 
     * <p>Default is: <b>true</b></p>
     * 
     * @return whether to force revocation if forcing is enabled
     */
    public boolean isRevocationEnabled() {
        return revocationEnabled;
    }

    /**
     * If {@link #isForceRevocationEnabled()} is true, the revocation behavior of the underlying CertPath Provider
     * will be forced to this value. If the former is false, the revocation behavior
     * of the underlying provider will be determined by the PKIXTrustEvaluator implementation.
     * 
     * <p>Default is: <b>true</b></p>
     * 
     * @param flag whether to force revocation if forcing is enabled
     */
    public void setRevocationEnabled(final boolean flag) {
        revocationEnabled = flag;
    }

    /**
     * Returns the value of the policy mapping inhibited flag of the underlying CertPath Provider.
     * 
     * @return Returns the policyMappingInhibit boolean.
     */
    public boolean isPolicyMappingInhibited() {
        return policyMappingInhibit;
    }

    /**
     * Sets the policy mapping inhibited flag for the underlying CertPath Provider.
     * See also RFC 5280, section 6.1.1 (e).
     * 
     * <p>Default is: <b>false</b></p>
     * 
     * @param flag the policyMappingInhibit boolean to set.
     */
    public void setPolicyMappingInhibit(final boolean flag) {
        policyMappingInhibit = flag;
    }

    /**
     * Returns the value of the any policy inhibited flag of the underlying CertPath Provider.
     * 
     * @return Returns the anyPolicyInhibit boolean.
     */
    public boolean isAnyPolicyInhibited() {
        return anyPolicyInhibit;
    }

    /**
     * Sets the any policy inhibited flag for the underlying CertPath Provider.
     * See also RFC 5280, section 6.1.1 (g).
     * 
     * <p>Default is: <b>false</b></p>
     * 
     * @param flag the anyPolicyInhibit boolean to set.
     */
    public void setAnyPolicyInhibit(final boolean flag) {
        anyPolicyInhibit = flag;
     }

    /**
     * Returns the set of initial policies (OID strings) of the underlying CertPath Provider.
     * See also RFC 5280, section 6.1.1 (c).
     * 
     * @return Returns the initialPolicies set.
     */
    @Nonnull @Unmodifiable @NotLive public Set<String> getInitialPolicies() {
        return initialPolicies;
    }

    /**
     * Sets the initial policy identifiers (OID strings) for the underlying CertPath Provider,
     * i.e. those policies that are acceptable to the certificate user.
     * See also RFC 5280, section 6.1.1 (c).
     * 
     * @param newPolicies the initial set of policy identifiers (OID strings)
     */
    public void setInitialPolicies(@Nullable final Set<String> newPolicies) {
        initialPolicies = newPolicies != null ? CollectionSupport.copyToSet(newPolicies) : CollectionSupport.emptySet();
    }
    
}