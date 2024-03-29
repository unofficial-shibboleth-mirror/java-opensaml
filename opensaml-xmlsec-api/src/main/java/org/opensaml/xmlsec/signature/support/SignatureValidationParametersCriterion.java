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

package org.opensaml.xmlsec.signature.support;


import javax.annotation.Nonnull;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.Criterion;

import org.opensaml.xmlsec.SignatureValidationParameters;

/**
 * Criterion which holds an instance of {@link SignatureValidationParameters}.
 * 
 * <p>This criterion is often used with implementations of the {@link SignatureTrustEngine}.</p>
 */
public class SignatureValidationParametersCriterion implements Criterion {
    
    /** The SignatureValidationParameters instance. */
    @Nonnull private SignatureValidationParameters params;
    
    /**
     * Constructor.
     *
     * @param validationParams the signature validation parameters instance to wrap
     */
    public SignatureValidationParametersCriterion(@Nonnull final SignatureValidationParameters validationParams) {
       params = Constraint.isNotNull(validationParams, "SignatureValidationParameters instance was null"); 
    }
    
    /**
     * Get the signature validation parameters instance.
     * 
     * @return the parameters instance
     */
    @Nonnull public SignatureValidationParameters getSignatureValidationParameters() {
        return params;
    }
    
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("SignatureValidationParametersCriterion [params=");
        builder.append(params);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return params.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof SignatureValidationParametersCriterion other) {
            return params.equals(other.getSignatureValidationParameters());
        }

        return false;
    }

}
