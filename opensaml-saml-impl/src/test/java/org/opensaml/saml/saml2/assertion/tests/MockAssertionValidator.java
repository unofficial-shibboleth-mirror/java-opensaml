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

package org.opensaml.saml.saml2.assertion.tests;

import java.util.Map;

import javax.annotation.Nonnull;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.SAML20AssertionValidator;
import org.opensaml.saml.saml2.core.Assertion;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.logic.Constraint;

@SuppressWarnings("javadoc")
public class MockAssertionValidator extends SAML20AssertionValidator {
    
    private Map<Assertion, Object> resultsMap;

    public MockAssertionValidator(Map<Assertion, Object> results) {
        super(CollectionSupport.emptyList(), CollectionSupport.emptyList(), CollectionSupport.emptyList(), null, null, null);
        resultsMap = Constraint.isNotNull(results, "Results map was null");
    }

    /** {@inheritDoc} */
    @Nonnull public ValidationResult validate(@Nonnull final Assertion assertion,
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        Object result = resultsMap.get(assertion);
        
        if (Throwable.class.isInstance(result)) {
            Throwable throwable = Throwable.class.cast(result);
            
            if (AssertionValidationException.class.isInstance(throwable)) {
                throw AssertionValidationException.class.cast(throwable);
            }
            if (RuntimeException.class.isInstance(throwable)) {
                throw RuntimeException.class.cast(throwable);
            }
            if (Error.class.isInstance(throwable)) {
                throw Error.class.cast(throwable);
            }
            if (Exception.class.isInstance(throwable)) {
                throw new AssertionValidationException(Exception.class.cast(throwable));
            }
            throw new RuntimeException(throwable);
        }
        
        if (ValidationResult.class.isInstance(result)) {
            ValidationResult vr = ValidationResult.class.cast(result);
            if (!ValidationResult.VALID.equals(vr)) {
                context.getValidationFailureMessages().add("Mock validation was not valid");
            }
            return vr;
        }
        
        if (Pair.class.isInstance(result)) {
            Pair<ValidationResult,String> pair = Pair.class.cast(result);
            if (!ValidationResult.VALID.equals(pair.getFirst())) {
                context.getValidationFailureMessages().add(pair.getSecond());
            }
            return Constraint.isNotNull(pair.getFirst(), "ValidationResult was null");
        }
        
        throw new IllegalArgumentException(String.format("Invalid result type supplied in mock results map for Assertion '%s': %s",
                assertion, result));
    }
    
}