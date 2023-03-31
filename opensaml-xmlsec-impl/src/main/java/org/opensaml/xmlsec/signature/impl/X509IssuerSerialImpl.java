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

package org.opensaml.xmlsec.signature.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.X509IssuerName;
import org.opensaml.xmlsec.signature.X509IssuerSerial;
import org.opensaml.xmlsec.signature.X509SerialNumber;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link X509IssuerSerial}.
 */
public class X509IssuerSerialImpl extends AbstractXMLObject implements X509IssuerSerial {
    
    /** X509IssuerName child element. */
    @Nullable private X509IssuerName issuerName;
    
    /** X509SerialNumber child element. */
    @Nullable private X509SerialNumber serialNumber;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected X509IssuerSerialImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public X509IssuerName getX509IssuerName() {
        return this.issuerName;
    }

    /** {@inheritDoc} */
    public void setX509IssuerName(@Nullable final X509IssuerName newX509IssuerName) {
        this.issuerName = prepareForAssignment(this.issuerName, newX509IssuerName);
    }

    /** {@inheritDoc} */
    @Nullable public X509SerialNumber getX509SerialNumber() {
        return this.serialNumber;
    }

    /** {@inheritDoc} */
    public void setX509SerialNumber(@Nullable final X509SerialNumber newX509SerialNumber) {
        this.serialNumber = prepareForAssignment(this.serialNumber, newX509SerialNumber);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (issuerName != null) {
            children.add(issuerName);
        }
        if (serialNumber != null) {
            children.add(serialNumber);
        }
        
        return CollectionSupport.copyToList(children);
    }

}