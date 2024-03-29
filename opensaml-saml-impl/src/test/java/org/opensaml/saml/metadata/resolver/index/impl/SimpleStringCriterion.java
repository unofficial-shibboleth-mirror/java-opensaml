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

package org.opensaml.saml.metadata.resolver.index.impl;

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.Criterion;

import com.google.common.base.MoreObjects;

@SuppressWarnings("javadoc")
public class SimpleStringCriterion implements Criterion {
    @Nonnull @NotEmpty private final String value;

    public SimpleStringCriterion(@Nonnull @NotEmpty final String newValue) {
        value = Constraint.isNotNull(StringSupport.trimOrNull(newValue), "Value cannot be null or empty");
    }

    @Nonnull @NotEmpty public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(value).toString();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof SimpleStringCriterion) {
            return value.equals(((SimpleStringCriterion) obj).value);
        }

        return false;
    }
}