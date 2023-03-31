/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.CarriedKeyName;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.ReferenceList;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link EncryptedKey}.
 */
public class EncryptedKeyImpl extends EncryptedTypeImpl implements EncryptedKey {
    
    /** Recipient value. */
    @Nullable private String recipient;
    
    /** CarriedKeyName value. */
    @Nullable private CarriedKeyName carriedKeyName;
    
    /** ReferenceList value. */
    @Nullable private ReferenceList referenceList;

    /**
     * Constructor.
     *
     * @param namespaceURI namespace URI
     * @param elementLocalName local name
     * @param namespacePrefix namespace prefix
     */
    protected EncryptedKeyImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
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
    @Nullable public ReferenceList getReferenceList() {
        return referenceList;
    }

    /** {@inheritDoc} */
    public void setReferenceList(@Nullable final ReferenceList newReferenceList) {
        referenceList = prepareForAssignment(referenceList, newReferenceList);
    }

    /** {@inheritDoc} */
    @Nullable  public CarriedKeyName getCarriedKeyName() {
        return carriedKeyName;
    }

    /** {@inheritDoc} */
    public void setCarriedKeyName(@Nullable final CarriedKeyName newCarriedKeyName) {
        carriedKeyName = prepareForAssignment(carriedKeyName, newCarriedKeyName);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (super.getOrderedChildren() != null) {
            children.addAll(super.getOrderedChildren());
        }
       
        if (referenceList != null) {
            children.add(referenceList);
        }
        if (carriedKeyName != null) {
            children.add(carriedKeyName);
        }
        
        return CollectionSupport.copyToList(children);
    }

}
