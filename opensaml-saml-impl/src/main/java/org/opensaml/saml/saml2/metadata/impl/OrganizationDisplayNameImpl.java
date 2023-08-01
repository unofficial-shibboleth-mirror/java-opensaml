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

/**
 * 
 */

package org.opensaml.saml.saml2.metadata.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.saml2.metadata.OrganizationDisplayName;

/**
 * Concrete implementation of {@link OrganizationDisplayName}.
 */
public class OrganizationDisplayNameImpl extends LocalizedNameImpl implements OrganizationDisplayName {

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace
     * @param elementLocalName the name
     * @param namespacePrefix the prefix
     */
    protected OrganizationDisplayNameImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

}