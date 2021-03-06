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
import java.util.Collections;
import java.util.List;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.DHKeyValue;
import org.opensaml.xmlsec.encryption.Generator;
import org.opensaml.xmlsec.encryption.P;
import org.opensaml.xmlsec.encryption.PgenCounter;
import org.opensaml.xmlsec.encryption.Public;
import org.opensaml.xmlsec.encryption.Q;
import org.opensaml.xmlsec.encryption.Seed;

/**
 * Concrete implementation of {@link org.opensaml.xmlsec.encryption.DHKeyValue}.
 */
public class DHKeyValueImpl extends AbstractXMLObject implements DHKeyValue {
    
    /** P child element. */
    private P p;
    
    /** Q child element. */
    private Q q;
    
    /** Generator child element. */
    private Generator generator;
    
    /** Public element. */
    private Public publicChild;
    
    /** seed child element. */
    private Seed seed;
    
    /** pgenCounter child element. */
    private PgenCounter pgenCounter;

    /**
     * Constructor.
     *
     * @param namespaceURI namespace URI
     * @param elementLocalName local name
     * @param namespacePrefix namespace prefix
     */
    protected DHKeyValueImpl(final String namespaceURI, final String elementLocalName, final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public P getP() {
        return this.p;
    }

    /** {@inheritDoc} */
    public void setP(final P newP) {
        this.p = prepareForAssignment(this.p, newP);
    }

    /** {@inheritDoc} */
    public Q getQ() {
        return this.q;
    }

    /** {@inheritDoc} */
    public void setQ(final Q newQ) {
        this.q = prepareForAssignment(this.q, newQ);
    }

    /** {@inheritDoc} */
    public Generator getGenerator() {
        return this.generator;
    }

    /** {@inheritDoc} */
    public void setGenerator(final Generator newGenerator) {
        this.generator = prepareForAssignment(this.generator, newGenerator);
    }

    /** {@inheritDoc} */
    public Public getPublic() {
        return this.publicChild;
    }

    /** {@inheritDoc} */
    public void setPublic(final Public newPublic) {
        this.publicChild = prepareForAssignment(this.publicChild, newPublic);
    }

    /** {@inheritDoc} */
    public Seed getSeed() {
        return this.seed;
    }

    /** {@inheritDoc} */
    public void setSeed(final Seed newSeed) {
        this.seed = prepareForAssignment(this.seed, newSeed);
    }

    /** {@inheritDoc} */
    public PgenCounter getPgenCounter() {
        return this.pgenCounter;
    }

    /** {@inheritDoc} */
    public void setPgenCounter(final PgenCounter newPgenCounter) {
        this.pgenCounter = prepareForAssignment(this.pgenCounter, newPgenCounter);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (p != null) {
            children.add(p);
        }
        if (q!= null) {
            children.add(q);
        }
        if (generator != null) {
            children.add(generator);
        }
        if (publicChild != null) {
            children.add(publicChild);
        }
        if (seed != null) {
            children.add(seed);
        }
        if (pgenCounter != null) {
            children.add(pgenCounter);
        }
        
        if (children.size() == 0) {
            return null;
        }
        
        return Collections.unmodifiableList(children);
    }

}
