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
import org.opensaml.xmlsec.encryption.DHKeyValue;
import org.opensaml.xmlsec.encryption.Generator;
import org.opensaml.xmlsec.encryption.P;
import org.opensaml.xmlsec.encryption.PgenCounter;
import org.opensaml.xmlsec.encryption.Public;
import org.opensaml.xmlsec.encryption.Q;
import org.opensaml.xmlsec.encryption.Seed;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link DHKeyValue}.
 */
public class DHKeyValueImpl extends AbstractXMLObject implements DHKeyValue {
    
    /** P child element. */
    @Nullable private P p;
    
    /** Q child element. */
    @Nullable private Q q;
    
    /** Generator child element. */
    @Nullable private Generator generator;
    
    /** Public element. */
    @Nullable private Public publicChild;
    
    /** seed child element. */
    @Nullable private Seed seed;
    
    /** pgenCounter child element. */
    @Nullable private PgenCounter pgenCounter;

    /**
     * Constructor.
     *
     * @param namespaceURI namespace URI
     * @param elementLocalName local name
     * @param namespacePrefix namespace prefix
     */
    protected DHKeyValueImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
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
    @Nullable public Generator getGenerator() {
        return generator;
    }

    /** {@inheritDoc} */
    public void setGenerator(@Nullable final Generator newGenerator) {
        generator = prepareForAssignment(generator, newGenerator);
    }

    /** {@inheritDoc} */
    @Nullable public Public getPublic() {
        return publicChild;
    }

    /** {@inheritDoc} */
    public void setPublic(@Nullable final Public newPublic) {
        publicChild = prepareForAssignment(publicChild, newPublic);
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
        
        return CollectionSupport.copyToList(children);
    }

}
