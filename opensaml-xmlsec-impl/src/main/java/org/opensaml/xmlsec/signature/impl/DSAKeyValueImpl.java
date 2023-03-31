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
import org.opensaml.xmlsec.signature.DSAKeyValue;
import org.opensaml.xmlsec.signature.G;
import org.opensaml.xmlsec.signature.J;
import org.opensaml.xmlsec.signature.P;
import org.opensaml.xmlsec.signature.PgenCounter;
import org.opensaml.xmlsec.signature.Q;
import org.opensaml.xmlsec.signature.Seed;
import org.opensaml.xmlsec.signature.Y;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link DSAKeyValue}.
 */
public class DSAKeyValueImpl extends AbstractXMLObject implements DSAKeyValue {
    
    /** P child element. */
    @Nullable private P p;
    
    /** Q child element. */
    @Nullable private Q q;
    
    /** G child element. */
    @Nullable private G g;
    
    /** Y child element. */
    @Nullable private Y y;
    
    /** J child element. */
    @Nullable private J j;
    
    /** Seed child element. */
    @Nullable private Seed seed;
    
    /** PgenCounter child element. */
    @Nullable private PgenCounter pgenCounter;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected DSAKeyValueImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public P getP() {
        return p;
    }

    /** {@inheritDoc} */
    public void setP(@Nullable final P newP) {
        p = prepareForAssignment(p, newP);
    }

    /** {@inheritDoc} */
    @Nullable public Q getQ() {
        return q;
    }

    /** {@inheritDoc} */
    public void setQ(@Nullable final Q newQ) {
        q = prepareForAssignment(q, newQ);
    }

    /** {@inheritDoc} */
    @Nullable public G getG() {
        return g;
    }

    /** {@inheritDoc} */
    public void setG(@Nullable final G newG) {
        g = prepareForAssignment(g, newG);
    }

    /** {@inheritDoc} */
    @Nullable public Y getY() {
        return y;
    }

    /** {@inheritDoc} */
    public void setY(@Nullable final Y newY) {
        y = prepareForAssignment(y, newY);
    }

    /** {@inheritDoc} */
    @Nullable public J getJ() {
        return j;
    }

    /** {@inheritDoc} */
    public void setJ(@Nullable final J newJ) {
        j = prepareForAssignment(j, newJ);
    }

    /** {@inheritDoc} */
    @Nullable public Seed getSeed() {
        return seed;
    }

    /** {@inheritDoc} */
    public void setSeed(@Nullable final Seed newSeed) {
        seed = prepareForAssignment(seed, newSeed);
    }

    /** {@inheritDoc} */
    @Nullable public PgenCounter getPgenCounter() {
        return pgenCounter;
    }

    /** {@inheritDoc} */
    public void setPgenCounter(@Nullable final PgenCounter newPgenCounter) {
        pgenCounter = prepareForAssignment(pgenCounter, newPgenCounter);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (p != null) {
            children.add(p);
        }
        if (q!= null) {
            children.add(q);
        }
        if (g != null) {
            children.add(g);
        }
        if (y != null) {
            children.add(y);
        }
        if (j != null) {
            children.add(j);
        }
        if (seed!= null) {
            children.add(seed);
        }
        if (pgenCounter != null) {
            children.add(pgenCounter);
        }
        
        return CollectionSupport.copyToList(children);
    }

}