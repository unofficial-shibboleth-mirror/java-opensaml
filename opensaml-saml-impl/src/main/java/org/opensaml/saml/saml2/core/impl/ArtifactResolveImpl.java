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

package org.opensaml.saml.saml2.core.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.Artifact;
import org.opensaml.saml.saml2.core.ArtifactResolve;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link ArtifactResolve}.
 */
public class ArtifactResolveImpl extends RequestAbstractTypeImpl implements ArtifactResolve {

    /** Artifact child element. */
    @Nullable private Artifact artifact;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace uri
     * @param elementLocalName element name
     * @param namespacePrefix namespace prefix
     */
    protected ArtifactResolveImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public Artifact getArtifact() {
        return this.artifact;
    }

    /** {@inheritDoc} */
    public void setArtifact(@Nullable final Artifact newArtifact) {
        this.artifact = prepareForAssignment(this.artifact, newArtifact);
    }

    /** {@inheritDoc} */
    @Override
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        final List<XMLObject> superKids = super.getOrderedChildren();
        if (superKids != null) {
            children.addAll(superKids);
        }

        if (artifact != null) {
            children.add(artifact);
        }

        return CollectionSupport.copyToList(children);
    }

}