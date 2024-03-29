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

package org.opensaml.saml.saml1.profile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLException;
import org.opensaml.saml.common.profile.NameIdentifierGenerator;
import org.opensaml.saml.saml1.core.NameIdentifier;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/** Specialization of {@link NameIdentifierGenerator} for SAML 1.x. */
public interface SAML1NameIdentifierGenerator extends NameIdentifierGenerator<NameIdentifier> {

    /** {@inheritDoc} */
    @Nullable NameIdentifier generate(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull @NotEmpty final String format) throws SAMLException;
    
}