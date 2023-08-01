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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.saml2.core.Advice;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AssertionIDRef;
import org.opensaml.saml.saml2.core.AssertionURIRef;
import org.opensaml.saml.saml2.core.EncryptedAssertion;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * A concrete implementation of {@link Advice}.
 */
@SuppressWarnings("unchecked")
public class AdviceImpl extends AbstractXMLObject implements Advice {

    /** Children. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> indexedChildren;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AdviceImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        indexedChildren = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<XMLObject> getChildren() {
        return indexedChildren;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<XMLObject> getChildren(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) indexedChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AssertionIDRef> getAssertionIDReferences() {
        return (List<AssertionIDRef>) indexedChildren.subList(AssertionIDRef.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AssertionURIRef> getAssertionURIReferences() {
        return (List<AssertionURIRef>) indexedChildren.subList(AssertionURIRef.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Assertion> getAssertions() {
        return (List<Assertion>) indexedChildren.subList(Assertion.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<EncryptedAssertion> getEncryptedAssertions() {
        return (List<EncryptedAssertion>) indexedChildren.subList(EncryptedAssertion.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(indexedChildren);
    }

}