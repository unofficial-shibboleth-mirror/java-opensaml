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

package org.opensaml.xmlsec.signature.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.DHKeyValue;
import org.opensaml.xmlsec.signature.DSAKeyValue;
import org.opensaml.xmlsec.signature.ECKeyValue;
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.RSAKeyValue;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link KeyValue}.
 */
public class KeyValueImpl extends AbstractXMLObject implements KeyValue {
 
    /** DHKeyValue child element. */
    @Nullable private DHKeyValue dhKeyValue;
    
    /** DSAKeyValue child element. */
    @Nullable private DSAKeyValue dsaKeyValue;
    
    /** RSAKeyValue child element. */
    @Nullable private RSAKeyValue rsaKeyValue;

    /** ECKeyValue child element. */
    @Nullable private ECKeyValue ecKeyValue;
    
    /** Wildcard &lt;any&gt; XMLObject child element. */
    @Nullable private XMLObject unknownXMLObject;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected KeyValueImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public DHKeyValue getDHKeyValue() {
        return dhKeyValue;
    }

    /** {@inheritDoc} */
    public void setDHKeyValue(@Nullable final DHKeyValue newDHKeyValue) {
        dhKeyValue = prepareForAssignment(dhKeyValue, newDHKeyValue);
    }

    /** {@inheritDoc} */
    @Nullable public DSAKeyValue getDSAKeyValue() {
        return dsaKeyValue;
    }

    /** {@inheritDoc} */
    public void setDSAKeyValue(@Nullable final DSAKeyValue newDSAKeyValue) {
        dsaKeyValue = prepareForAssignment(dsaKeyValue, newDSAKeyValue);
    }

    /** {@inheritDoc} */
    @Nullable public RSAKeyValue getRSAKeyValue() {
        return rsaKeyValue;
    }

    /** {@inheritDoc} */
    public void setRSAKeyValue(@Nullable final RSAKeyValue newRSAKeyValue) {
        rsaKeyValue = prepareForAssignment(rsaKeyValue, newRSAKeyValue);
    }

    /** {@inheritDoc} */
    @Nullable public ECKeyValue getECKeyValue() {
        return ecKeyValue;
    }

    /** {@inheritDoc} */
    public void setECKeyValue(@Nullable final ECKeyValue newECKeyValue) {
        ecKeyValue = prepareForAssignment(ecKeyValue, newECKeyValue);
    }
    
    /** {@inheritDoc} */
    @Nullable public XMLObject getUnknownXMLObject() {
        return unknownXMLObject;
    }

    /** {@inheritDoc} */
    public void setUnknownXMLObject(@Nullable final XMLObject newXMLObject) {
        unknownXMLObject = prepareForAssignment(unknownXMLObject, newXMLObject);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (dsaKeyValue != null) {
            children.add(dsaKeyValue);
        }
        if (rsaKeyValue != null) {
            children.add(rsaKeyValue);
        }
        if (ecKeyValue != null) {
            children.add(ecKeyValue);
        }
        if (dhKeyValue != null) {
            children.add(dhKeyValue);
        }
        if (unknownXMLObject != null) {
            children.add(unknownXMLObject);
        }
        
        return CollectionSupport.copyToList(children);
    }

}