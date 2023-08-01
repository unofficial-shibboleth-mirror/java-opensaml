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

package org.opensaml.saml.saml1.core.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.AssertionIDReference;
import org.opensaml.saml.saml1.core.Evidence;
import org.opensaml.saml.saml1.core.Evidentiary;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of the {@link Evidence} interface.
 */
@SuppressWarnings("unchecked")
public class EvidenceImpl extends AbstractXMLObject implements Evidence {

    /** The Evidentiary child elements. */
    @Nonnull private final IndexedXMLObjectChildrenList<Evidentiary> evidence;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected EvidenceImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        evidence = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AssertionIDReference> getAssertionIDReferences() {
        final QName qname = new QName(SAMLConstants.SAML1_NS, AssertionIDReference.DEFAULT_ELEMENT_LOCAL_NAME);
        return (List<AssertionIDReference>) evidence.subList(qname);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Assertion> getAssertions() {
        final QName qname = new QName(SAMLConstants.SAML1_NS, Assertion.DEFAULT_ELEMENT_LOCAL_NAME);
        return (List<Assertion>) evidence.subList(qname);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Evidentiary> getEvidence() {
        return evidence;
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(evidence);
    }
    
}