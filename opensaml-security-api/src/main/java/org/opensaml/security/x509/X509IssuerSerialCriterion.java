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

package org.opensaml.security.x509;

import java.math.BigInteger;

import javax.annotation.Nonnull;
import javax.security.auth.x500.X500Principal;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.Criterion;

/**
 * An implementation of {@link Criterion} which specifies criteria based on
 * X.509 certificate issuer name and serial number.
 */
public final class X509IssuerSerialCriterion implements Criterion {
    
    /** X.509 certificate issuer name. */
    @Nonnull private X500Principal issuerName;
    
    /** X.509 certificate serial number. */
    @Nonnull private BigInteger serialNumber;
    
    /**
     * Constructor.
     *
     * @param issuer certificate issuer name
     * @param serial certificate serial number
     */
    public X509IssuerSerialCriterion(@Nonnull final X500Principal issuer, @Nonnull final BigInteger serial) {
        issuerName = Constraint.isNotNull(issuer, "Issuer principal criteria value cannot be null");
        serialNumber = Constraint.isNotNull(serial, "Serial number criteria value cannot be null");
    }
    
    /** Get the issuer name.
     * 
     * @return Returns the issuer name.
     */
    @Nonnull public X500Principal getIssuerName() {
        return issuerName;
    }

    /**
     * Set the issuer name.
     * 
     * @param issuer The issuer name to set.
     */
    public void setIssuerName(@Nonnull final X500Principal issuer) {
        issuerName = Constraint.isNotNull(issuer, "Issuer principal criteria value cannot be null");
    }

    /**
     * Get the serial number.
     * 
     * @return Returns the serial number.
     */
    @Nonnull public BigInteger getSerialNumber() {
        return serialNumber;
    }

    /**
     * Set the serial number.
     * 
     * @param serial The serial number to set.
     */
    public void setSerialNumber(@Nonnull final BigInteger serial) {
        serialNumber = Constraint.isNotNull(serial, "Serial number criteria value cannot be null");
    }
    
    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BindingCriterion [issuerName=");
        builder.append(issuerName.getName());
        builder.append(", serialNumber=");
        builder.append(serialNumber);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        int result = 17;
        result = result*37 + issuerName.hashCode();
        result = result*37 + serialNumber.hashCode();
        return result;
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof X509IssuerSerialCriterion other) {
            return issuerName.equals(other.issuerName) && serialNumber.equals(other.serialNumber);
        }

        return false;
    }

}