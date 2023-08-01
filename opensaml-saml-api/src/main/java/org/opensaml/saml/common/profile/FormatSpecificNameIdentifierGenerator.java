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

package org.opensaml.saml.common.profile;

import javax.annotation.Nonnull;

import org.opensaml.saml.common.SAMLObject;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Specialized type of {@link NameIdentifierGenerator} that is locked to a specific Format
 * of identifier.
 * 
 * <p>Generators without this property are only usable as a "fallback" when a specific
 * generator isn't available for a particular Format.</p>
 * 
 * @param <NameIdType>  type of object produced
 */
public interface FormatSpecificNameIdentifierGenerator<NameIdType extends SAMLObject>
        extends NameIdentifierGenerator<NameIdType> {

    /**
     * Get the identifier format associated with this component.
     * 
     * @return  identifier format
     */
    @Nonnull @NotEmpty String getFormat();
}