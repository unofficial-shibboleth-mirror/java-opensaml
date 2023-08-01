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
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.ManageNameIDRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NewEncryptedID;
import org.opensaml.saml.saml2.core.NewID;
import org.opensaml.saml.saml2.core.Terminate;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * A concrete implementation of {@link ManageNameIDRequest}.
 */
public class ManageNameIDRequestImpl extends RequestAbstractTypeImpl implements ManageNameIDRequest {

    /** NameID child element. */
    @Nullable private NameID nameID;

    /** EncryptedID child element. */
    @Nullable private EncryptedID encryptedID;

    /** NewID child element. */
    @Nullable private NewID newID;

    /** NameID child element. */
    @Nullable private NewEncryptedID newEncryptedID;

    /** Terminate child element. */
    @Nullable private Terminate terminate;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected ManageNameIDRequestImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
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
        return encryptedID;
    }

    /** {@inheritDoc} */
    public void setEncryptedID(@Nullable final EncryptedID newEncID) {
        encryptedID = prepareForAssignment(encryptedID, newEncID);
    }

    /** {@inheritDoc} */
    @Nullable public NewID getNewID() {
        return newID;
    }

    /** {@inheritDoc} */
    public void setNewID(@Nullable final NewID newNewID) {
        newID = prepareForAssignment(newID, newNewID);
    }

    /** {@inheritDoc} */
    @Nullable public NewEncryptedID getNewEncryptedID() {
        return newEncryptedID;
    }

    /** {@inheritDoc} */
    public void setNewEncryptedID(@Nullable final NewEncryptedID newNewEncryptedID) {
        newEncryptedID = prepareForAssignment(newEncryptedID, newNewEncryptedID);
    }

    /** {@inheritDoc} */
    @Nullable public Terminate getTerminate() {
        return terminate;
    }

    /** {@inheritDoc} */
    public void setTerminate(@Nullable final Terminate newTerminate) {
        terminate = prepareForAssignment(terminate, newTerminate);
    }

    /** {@inheritDoc} */
    @Override
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        final List<XMLObject> superKids = super.getOrderedChildren();
        if (superKids != null) {
            children.addAll(superKids);
        }

        if (nameID != null) {
            children.add(nameID);
        }
        if (encryptedID != null) {
            children.add(encryptedID);
        }
        if (newID != null) {
            children.add(newID);
        }
        if (newEncryptedID != null) {
            children.add(newEncryptedID);
        }
        if (terminate != null) {
            children.add(terminate);
        }

        return CollectionSupport.copyToList(children);
    }

}