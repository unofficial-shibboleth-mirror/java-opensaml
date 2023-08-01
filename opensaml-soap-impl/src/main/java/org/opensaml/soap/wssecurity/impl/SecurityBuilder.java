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

package org.opensaml.soap.wssecurity.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.soap.wssecurity.Security;

/**
 * SecurityBuilder.
 * 
 */
public class SecurityBuilder extends AbstractWSSecurityObjectBuilder<Security> {

    /** {@inheritDoc} */
    @Nonnull public Security buildObject() {
        return buildObject(Security.ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull public Security buildObject(@Nullable final String namespaceURI, @Nonnull final String localName,
            @Nullable final String namespacePrefix) {
        return new SecurityImpl(namespaceURI, localName, namespacePrefix);
    }

}
