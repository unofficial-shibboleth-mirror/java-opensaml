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

package org.opensaml.saml.saml2.profile.impl;

import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.saml2.assertion.messaging.AssertionValidationInput;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;

@SuppressWarnings("javadoc")
public class MockAssertionValidationContextBuilder implements Function<AssertionValidationInput, ValidationContext> {
    
    @Nonnull private Map<String,Object> staticParams;
    
    public MockAssertionValidationContextBuilder() {
       staticParams = CollectionSupport.emptyMap(); 
    }
    
    public MockAssertionValidationContextBuilder(@Nonnull final Map<String,Object> statics) {
        staticParams = Constraint.isNotNull(statics, "Static params were null");
    }

    /** {@inheritDoc} */
    @Nullable public ValidationContext apply(@Nullable final AssertionValidationInput t) {
        return new ValidationContext(staticParams);
    }

}
