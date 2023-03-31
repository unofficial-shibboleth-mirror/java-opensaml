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

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.CipherData;
import org.opensaml.xmlsec.encryption.CipherReference;
import org.opensaml.xmlsec.encryption.CipherValue;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link CipherData}.
 */
public class CipherDataImpl extends AbstractXMLObject implements CipherData {
    
    /** CipherValue child element. */
    @Nullable private CipherValue cipherValue;
    
    /** CipherReference child element. */
    @Nullable private CipherReference cipherReference;

    /**
     * Constructor.
     *
     * @param namespaceURI namespace URI
     * @param elementLocalName local name
     * @param namespacePrefix namespace prefix
     */
    protected CipherDataImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public CipherValue getCipherValue() {
        return cipherValue;
    }

    /** {@inheritDoc} */
    public void setCipherValue(@Nullable final CipherValue newCipherValue) {
        cipherValue = prepareForAssignment(cipherValue, newCipherValue);
    }

    /** {@inheritDoc} */
    @Nullable public CipherReference getCipherReference() {
        return cipherReference;
    }

    /** {@inheritDoc} */
    public void setCipherReference(@Nullable final CipherReference newCipherReference) {
        cipherReference = prepareForAssignment(cipherReference, newCipherReference);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (cipherValue != null) {
            children.add(cipherValue);
        }
        if (cipherReference != null) {
            children.add(cipherReference);
        }
        
        return CollectionSupport.copyToList(children);
    }

}
