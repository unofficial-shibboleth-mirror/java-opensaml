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

package org.opensaml.saml.ext.saml2mdrpi.impl;

import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.ext.saml2mdrpi.Publication;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * A concrete {@link Publication}.
 */
public class PublicationImpl extends AbstractXMLObject implements Publication {

    /** The publisher. */
    @Nullable private String publisher;

    /** The creation instant. */
    @Nullable private Instant creationInstant;

    /** The publicationId. */
    @Nullable private String publicationId;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected PublicationImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }


    /** {@inheritDoc} */
    @Nullable public String getPublisher() {
        return publisher;
    }

    /** {@inheritDoc} */
    public void setPublisher(@Nullable final String thePublisher) {
        publisher = prepareForAssignment(publisher, thePublisher);
    }

    /** {@inheritDoc} */
    @Nullable public Instant getCreationInstant() {
        return creationInstant;
    }

    /** {@inheritDoc} */
    public void setCreationInstant(@Nullable final Instant dateTime) {
        creationInstant = prepareForAssignment(creationInstant, dateTime);
    }

    /** {@inheritDoc} */
    @Nullable public String getPublicationId() {
        return publicationId;
    }

    /** {@inheritDoc} */
    public void setPublicationId(@Nullable final String id) {
        publicationId = prepareForAssignment(publicationId, id);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return null;
    }

}