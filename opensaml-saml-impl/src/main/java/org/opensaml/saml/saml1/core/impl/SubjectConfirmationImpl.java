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
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml1.core.ConfirmationMethod;
import org.opensaml.saml.saml1.core.SubjectConfirmation;
import org.opensaml.xmlsec.signature.KeyInfo;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link SubjectConfirmation}.
 */
public class SubjectConfirmationImpl extends AbstractXMLObject implements SubjectConfirmation {

    /** Contains the list of ConfirmationMethods. */
    @Nonnull private final XMLObjectChildrenList<ConfirmationMethod> confirmationMethods;

    /** Contains the SubjectConfirmationData element. */
    @Nullable private XMLObject subjectConfirmationData;

    /** Contains the KeyInfo element. */
    @Nullable private KeyInfo keyInfo;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected SubjectConfirmationImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        confirmationMethods = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<ConfirmationMethod> getConfirmationMethods() {
        return confirmationMethods;
    }
    
    /** {@inheritDoc} */
    @Nullable public XMLObject getSubjectConfirmationData() {
        return subjectConfirmationData;
    }
    
    /** {@inheritDoc} */
    public void setSubjectConfirmationData(@Nullable final XMLObject data) {
        subjectConfirmationData = prepareForAssignment(subjectConfirmationData, data);
    }

    /** {@inheritDoc} */
    @Nullable public KeyInfo getKeyInfo() {
        return keyInfo;
    }

    /** {@inheritDoc} */
    public void setKeyInfo(@Nullable final KeyInfo info) {
        keyInfo = prepareForAssignment(keyInfo, info);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {

        final List<XMLObject> list = new ArrayList<>(confirmationMethods.size() + 1);

        list.addAll(confirmationMethods);

        if (subjectConfirmationData != null) {
            list.add(subjectConfirmationData);
        }

        if (keyInfo != null) {
            list.add(keyInfo);
        }

        return CollectionSupport.copyToList(list);
    }
    
}