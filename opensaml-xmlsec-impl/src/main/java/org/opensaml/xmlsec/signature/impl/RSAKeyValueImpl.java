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
import org.opensaml.xmlsec.signature.Exponent;
import org.opensaml.xmlsec.signature.Modulus;
import org.opensaml.xmlsec.signature.RSAKeyValue;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link RSAKeyValue}.
 */
public class RSAKeyValueImpl extends AbstractXMLObject implements RSAKeyValue {
    
    /** Modulus child element value. */
    @Nullable private Modulus modulus;
    
    /** Exponent child element value. */
    @Nullable private Exponent exponent;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected RSAKeyValueImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public Modulus getModulus() {
        return this.modulus;
    }

    /** {@inheritDoc} */
    public void setModulus(@Nullable final Modulus newModulus) {
        this.modulus = prepareForAssignment(this.modulus, newModulus);
    }

    /** {@inheritDoc} */
    @Nullable public Exponent getExponent() {
        return this.exponent;
    }

    /** {@inheritDoc} */
    public void setExponent(@Nullable final Exponent newExponent) {
        this.exponent = prepareForAssignment(this.exponent, newExponent);

    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (modulus != null) {
            children.add(modulus);
        }
        if (exponent != null) {
            children.add(exponent);
        }
        
        return CollectionSupport.copyToList(children);
    }

}