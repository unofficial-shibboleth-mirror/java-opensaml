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

package org.opensaml.soap.soap11.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.soap.common.AbstractExtensibleSOAPObject;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Header;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link org.opensaml.soap.soap11.Envelope}.
 */
public class EnvelopeImpl extends AbstractExtensibleSOAPObject implements Envelope {

    /** SOAP header. */
    @Nullable private Header header;

    /** SOAP body. */
    @Nullable private Body body;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    protected EnvelopeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public Header getHeader() {
        return header;
    }

    /** {@inheritDoc} */
    public void setHeader(@Nullable final Header newHeader) {
        header = prepareForAssignment(header, newHeader);
    }

    /** {@inheritDoc} */
    @Nullable public Body getBody() {
        return body;
    }

    /** {@inheritDoc} */
    public void setBody(@Nullable final Body newBody) {
        body = prepareForAssignment(body, newBody);
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (header != null) {
            children.add(header);
        }
        
        if (body != null) {
            children.add(body);
        }
        
        final List<XMLObject> superKids = super.getOrderedChildren();
        if (superKids != null) {
            children.addAll(superKids);
        }

        return children.isEmpty() ? null : CollectionSupport.copyToList(children);
    }

}