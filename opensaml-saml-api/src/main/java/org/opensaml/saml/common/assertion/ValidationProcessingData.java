/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.common.assertion;

import javax.annotation.Nonnull;

import net.shibboleth.shared.logic.Constraint;

/**
 * Convenience class for holding the {@link ValidationContext} used to validate
 * an assertion, along with the final {@link ValidationResult}.
 * 
 * <p>
 * This is useful for storing the pair of post-validation data items on the object metadata of an assertion.
 * </p>
 */
public class ValidationProcessingData {

    /** The validation context. */
    @Nonnull private ValidationContext context;

    /** The validation result. */
    @Nonnull private ValidationResult result;

    /**
     * Constructor.
     *
     * @param validationContext the validation context
     * @param validationResult the validation result
     */
    public ValidationProcessingData(@Nonnull final ValidationContext validationContext, 
            @Nonnull final ValidationResult validationResult) {
        context = Constraint.isNotNull(validationContext, "ValidationContext was null");
        result = Constraint.isNotNull(validationResult, "ValidationResult was null");
    }

    /**
     * Get the validation context.
     * 
     * @return the validation context
     */
    @Nonnull public ValidationContext getContext() {
        return context;
    }

    /**
     * Get the validation result.
     * 
     * @return the validation result
     */
    @Nonnull public ValidationResult getResult() {
        return result;
    }

}