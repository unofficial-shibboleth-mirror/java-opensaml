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

package org.opensaml.saml.saml2.profile.impl;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.saml2.profile.impl.ValidateAssertions.AssertionValidationInput;

import net.shibboleth.shared.logic.Constraint;

/**
 *
 */
public class MockAssertionValidationContextBuilder implements Function<AssertionValidationInput, ValidationContext> {
    
    private Map<String,Object> staticParams;
    
    public MockAssertionValidationContextBuilder() {
       staticParams = Collections.emptyMap(); 
    }
    
    public MockAssertionValidationContextBuilder(Map<String,Object> statics) {
        staticParams = Constraint.isNotNull(statics, "Static params were null");
    }

    /** {@inheritDoc} */
    public ValidationContext apply(AssertionValidationInput t) {
        return new ValidationContext(staticParams);
    }

}
