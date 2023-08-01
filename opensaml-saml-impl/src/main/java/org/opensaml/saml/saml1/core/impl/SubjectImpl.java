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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.opensaml.saml.saml1.core.Subject;
import org.opensaml.saml.saml1.core.SubjectConfirmation;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Complete implementation of {@link Subject}.
 */
public class SubjectImpl extends AbstractXMLObject implements Subject {

    /** Contains the NameIdentifier inside the Subject. */
    @Nullable private NameIdentifier nameIdentifier;

    /** Contains the SubjectConfirmation inside the Subject. */
    @Nullable private SubjectConfirmation subjectConfirmation;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected SubjectImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public NameIdentifier getNameIdentifier() {
        return nameIdentifier;
    }

    /** {@inheritDoc} */
    public void setNameIdentifier(@Nullable final NameIdentifier name) {
        nameIdentifier = prepareForAssignment(nameIdentifier, name);
    }

    /** {@inheritDoc} */
    @Nullable public SubjectConfirmation getSubjectConfirmation() {
        return subjectConfirmation;
    }

    /** {@inheritDoc} */
    public void setSubjectConfirmation(@Nullable final SubjectConfirmation conf) {
        subjectConfirmation = prepareForAssignment(subjectConfirmation, conf);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {

        final List<XMLObject> list = new ArrayList<>(2);

        if (nameIdentifier != null) {
            list.add(nameIdentifier);
        }

        if (subjectConfirmation != null) {
            list.add(subjectConfirmation);
        }

        return CollectionSupport.copyToList(list);
    }
    
}