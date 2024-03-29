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

package org.opensaml.saml.saml2.metadata.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.schema.impl.XSURIImpl;
import org.opensaml.saml.saml2.metadata.AffiliateMember;

/**
 * Concrete implementation of {@link AffiliateMember}.
 */
public class AffiliateMemberImpl extends XSURIImpl implements AffiliateMember {
    
    /**
     * Constructor.
     *
     * @param namespaceURI namespace
     * @param elementLocalName local name
     * @param namespacePrefix prefix
     */
    protected AffiliateMemberImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Override
    public void setURI(@Nullable final String uri) {
        if (uri != null && uri.length() > 1024) {
            throw new IllegalArgumentException("Member ID can not exceed 1024 characters in length");
        }

        super.setURI(uri);
    }
    
}