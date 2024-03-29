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

import javax.annotation.Nonnull;
import javax.security.auth.x500.X500Principal;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.Criterion;

/**
 * An implementation of {@link Criterion} which specifies criteria based on
 * X.509 certificate subject name.
 */
public final class X509SubjectNameCriterion implements Criterion {
    
    /** X.509 certificate subject name. */
    @Nonnull private X500Principal subjectName;
    
    /**
     * Constructor.
     *
     * @param subject certificate subject name
     */
    public X509SubjectNameCriterion(@Nonnull final X500Principal subject) {
        subjectName = Constraint.isNotNull(subject, "Subject principal criteria value cannot be null");
    }

    /**
     * Get the subject name.
     * 
     * @return Returns the subject name
     */
    @Nonnull public X500Principal getSubjectName() {
        return subjectName;
    }

    /**
     * Set the subject name.
     * 
     * @param subject The subject name
     */
    public void setSubjectName(@Nonnull final X500Principal subject) {
        subjectName = Constraint.isNotNull(subject, "Subject principal criteria value cannot be null");
    }
    
    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("X509SubjectNameCriterion [subjectName=");
        builder.append(subjectName.getName());
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return subjectName.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof X509SubjectNameCriterion other) {
            return subjectName.equals(other.subjectName);
        }

        return false;
    }

}