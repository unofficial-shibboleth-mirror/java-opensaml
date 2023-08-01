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

package org.opensaml.saml.saml2.core.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.BaseID;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link SubjectConfirmation}.
 */
public class SubjectConfirmationImpl extends AbstractXMLObject implements SubjectConfirmation {

    /** BaseID child element. */
    @Nullable private BaseID baseID;

    /** NameID child element. */
    @Nullable private NameID nameID;

    /** EncryptedID child element. */
    @Nullable private EncryptedID encryptedID;
    
    /** SubjectConfirmationData of the Confirmation. */
    @Nullable private SubjectConfirmationData subjectConfirmationData;

    /** Method of the Confirmation. */
    @Nullable private String method;

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
    }

    /** {@inheritDoc} */
    @Nullable public BaseID getBaseID() {
        return baseID;
    }

    /** {@inheritDoc} */
    public void setBaseID(@Nullable final BaseID newBaseID) {
        baseID = prepareForAssignment(baseID, newBaseID);
    }

    /** {@inheritDoc} */
    @Nullable public NameID getNameID() {
        return nameID;
    }

    /** {@inheritDoc} */
    public void setNameID(@Nullable final NameID newNameID) {
        nameID = prepareForAssignment(nameID, newNameID);
    }

    /** {@inheritDoc} */
    @Nullable public EncryptedID getEncryptedID() {
        return this.encryptedID;
    }

    /** {@inheritDoc} */
    public void setEncryptedID(@Nullable final EncryptedID newEncryptedID) {
        this.encryptedID = prepareForAssignment(this.encryptedID, newEncryptedID);
    }

    /** {@inheritDoc} */
    @Nullable public SubjectConfirmationData getSubjectConfirmationData() {
        return subjectConfirmationData;
    }

    /** {@inheritDoc} */
    public void setSubjectConfirmationData(@Nullable final SubjectConfirmationData newSubjectConfirmationData) {
        this.subjectConfirmationData = prepareForAssignment(this.subjectConfirmationData, newSubjectConfirmationData);

    }

    /** {@inheritDoc} */
    @Nullable public String getMethod() {
        return method;
    }

    /** {@inheritDoc} */
    public void setMethod(@Nullable final String newMethod) {
        this.method = prepareForAssignment(this.method, newMethod);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (baseID != null) {
            children.add(baseID);
        }

        if (nameID != null) {
            children.add(nameID);
        }
        
        if (encryptedID != null) {
            children.add(encryptedID);
        }

        if (subjectConfirmationData != null) {
            children.add(subjectConfirmationData);
        }

        return CollectionSupport.copyToList(children);
    }
    
}