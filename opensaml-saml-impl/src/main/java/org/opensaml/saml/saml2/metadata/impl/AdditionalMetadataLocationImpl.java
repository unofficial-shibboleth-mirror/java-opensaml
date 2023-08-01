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
import org.opensaml.saml.saml2.metadata.AdditionalMetadataLocation;

/**
 * Concrete implementation of {@link AdditionalMetadataLocation}.
 */
public class AdditionalMetadataLocationImpl extends XSURIImpl implements AdditionalMetadataLocation {

    /** Namespace scope of the root metadata element at the location. */
    @Nullable private String namespace;

    /**
     * Constructor.
     * 
     * @param namespaceURI the URI of the name space
     * @param elementLocalName the local name
     * @param namespacePrefix the prefix name space
     */
    protected AdditionalMetadataLocationImpl(@Nullable final String namespaceURI,
            @Nonnull final String elementLocalName, @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getNamespaceURI() {
        return namespace;
    }

    /** {@inheritDoc} */
    public void setNamespaceURI(@Nullable final String namespaceURI) {
        namespace = prepareForAssignment(namespace, namespaceURI);
    }

}