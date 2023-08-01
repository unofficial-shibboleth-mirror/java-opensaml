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

package org.opensaml.xmlsec.encryption.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.DerivedKey;
import org.opensaml.xmlsec.encryption.DerivedKeyName;
import org.opensaml.xmlsec.encryption.KeyDerivationMethod;
import org.opensaml.xmlsec.encryption.MasterKeyName;
import org.opensaml.xmlsec.encryption.ReferenceList;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link DerivedKey}.
 */
public class DerivedKeyImpl extends AbstractXMLObject implements DerivedKey {
    
    /** KeyDerivationMethod child element. */
    @Nullable private KeyDerivationMethod keyDerivationMethod;
    
    /** ReferenceList child element. */
    @Nullable private ReferenceList referenceList;
    
    /** DerivedKeyName child element. */
    @Nullable private DerivedKeyName derivedKeyName;
    
    /** MasterKeyName child element. */
    @Nullable private MasterKeyName masterKeyName;
    
    /** Recipient attribute. */
    @Nullable private String recipient;
    
    /** Id attribute. */
    @Nullable private String id;
    
    /** Type attribute. */
    @Nullable private String type;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace URI
     * @param elementLocalName local name
     * @param namespacePrefix namespace prefix
     */
    protected DerivedKeyImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public KeyDerivationMethod getKeyDerivationMethod() {
        return keyDerivationMethod;
    }

    /** {@inheritDoc} */
    public void setKeyDerivationMethod(@Nullable final KeyDerivationMethod method) {
        keyDerivationMethod = prepareForAssignment(keyDerivationMethod, method);
    }

    /** {@inheritDoc} */
    @Nullable public ReferenceList getReferenceList() {
        return referenceList;
    }

    /** {@inheritDoc} */
    public void setReferenceList(@Nullable final ReferenceList newReferenceList) {
        referenceList = prepareForAssignment(referenceList, newReferenceList);
    }

    /** {@inheritDoc} */
    @Nullable public DerivedKeyName getDerivedKeyName() {
        return derivedKeyName;
    }

    /** {@inheritDoc} */
    public void setDerivedKeyName(@Nullable final DerivedKeyName name) {
        derivedKeyName = prepareForAssignment(derivedKeyName, name);
    }

    /** {@inheritDoc} */
    @Nullable public MasterKeyName getMasterKeyName() {
        return masterKeyName;
    }

    /** {@inheritDoc} */
    public void setMasterKeyName(@Nullable final MasterKeyName name) {
        masterKeyName = prepareForAssignment(masterKeyName, name);
    }

    /** {@inheritDoc} */
    @Nullable public String getRecipient() {
        return recipient;
    }

    /** {@inheritDoc} */
    public void setRecipient(@Nullable final String newRecipient) {
        recipient = prepareForAssignment(recipient, newRecipient);
    }

    /** {@inheritDoc} */
    @Nullable public String getId() {
        return id;
    }

    /** {@inheritDoc} */
    public void setId(@Nullable final String newId) {
        final String oldID = id;
        id = prepareForAssignment(id, newId);
        registerOwnID(oldID, id);
    }

    /** {@inheritDoc} */
    @Nullable public String getType() {
        return type;
    }

    /** {@inheritDoc} */
    public void setType(@Nullable final String newType) {
        type = prepareForAssignment(type, newType);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (keyDerivationMethod != null) {
            children.add(keyDerivationMethod);
        }

        if (referenceList != null) {
            children.add(referenceList);
        }

        if (derivedKeyName != null) {
            children.add(derivedKeyName);
        }

        if (masterKeyName != null) {
            children.add(masterKeyName);
        }

        return CollectionSupport.copyToList(children);
    }

}