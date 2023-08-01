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
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.KANonce;
import org.opensaml.xmlsec.encryption.OriginatorKeyInfo;
import org.opensaml.xmlsec.encryption.RecipientKeyInfo;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link AgreementMethod}.
 */
public class AgreementMethodImpl extends AbstractXMLObject implements AgreementMethod {
    
    /** Algorithm attribute value. */
    @Nullable private String algorithm;
    
    /** KA-Nonce child element value. */
    @Nullable private KANonce kaNonce;
    
    /** OriginatorKeyInfo child element value. */
    @Nullable private OriginatorKeyInfo originatorKeyInfo;
    
    /** RecipientKeyInfo child element value. */
    @Nullable private RecipientKeyInfo recipientKeyInfo;
    
    /** List of wildcard &lt;any&gt; XMLObject children. */
    @Nonnull private IndexedXMLObjectChildrenList<XMLObject> xmlChildren;

    /**
     * Constructor.
     *
     * @param namespaceURI namespace URI
     * @param elementLocalName element local name
     * @param namespacePrefix namespace prefix
     */
    protected AgreementMethodImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        xmlChildren = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getAlgorithm() {
        return algorithm;
    }

    /** {@inheritDoc} */
    public void setAlgorithm(@Nullable final String newAlgorithm) {
        algorithm = prepareForAssignment(algorithm, newAlgorithm);
    }

    /** {@inheritDoc} */
    @Nullable public KANonce getKANonce() {
        return this.kaNonce;
    }

    /** {@inheritDoc} */
    public void setKANonce(@Nullable final KANonce newKANonce) {
        kaNonce = prepareForAssignment(kaNonce, newKANonce);
    }

    /** {@inheritDoc} */
    @Nullable public OriginatorKeyInfo getOriginatorKeyInfo() {
        return originatorKeyInfo;
    }

    /** {@inheritDoc} */
    public void setOriginatorKeyInfo(@Nullable final OriginatorKeyInfo newOriginatorKeyInfo) {
        originatorKeyInfo = prepareForAssignment(originatorKeyInfo, newOriginatorKeyInfo);
    }

    /** {@inheritDoc} */
    @Nullable public RecipientKeyInfo getRecipientKeyInfo() {
        return recipientKeyInfo;
    }

    /** {@inheritDoc} */
    public void setRecipientKeyInfo(@Nullable final RecipientKeyInfo newRecipientKeyInfo) {
        recipientKeyInfo = prepareForAssignment(recipientKeyInfo, newRecipientKeyInfo);
    }

    /** {@inheritDoc} */
    @Nonnull public List<XMLObject> getUnknownXMLObjects() {
        return xmlChildren;
    }
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull public List<XMLObject> getUnknownXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) xmlChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (kaNonce != null) {
            children.add(kaNonce);
        }
        
        children.addAll(xmlChildren);
        
        if (originatorKeyInfo != null) {
            children.add(originatorKeyInfo);
        }
        if (recipientKeyInfo != null) {
            children.add(recipientKeyInfo);
        }
        
        if (children.size() == 0) {
            return null;
        }
        
        return CollectionSupport.copyToList(children);
    }

}
