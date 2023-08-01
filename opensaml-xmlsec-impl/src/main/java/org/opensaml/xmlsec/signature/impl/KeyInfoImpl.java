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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.signature.DEREncodedKeyValue;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyInfoReference;
import org.opensaml.xmlsec.signature.KeyName;
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.MgmtData;
import org.opensaml.xmlsec.signature.PGPData;
import org.opensaml.xmlsec.signature.RetrievalMethod;
import org.opensaml.xmlsec.signature.SPKIData;
import org.opensaml.xmlsec.signature.X509Data;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link KeyInfo}.
 */
@SuppressWarnings("unchecked")
public class KeyInfoImpl extends AbstractXMLObject implements KeyInfo {
    
    /** The list of XMLObject child elements. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> indexedChildren;
    
    /** The Id attribute value. */
    @Nullable private String id;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected KeyInfoImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        indexedChildren = new IndexedXMLObjectChildrenList<>(this);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getID() {
        return id;
    }

    /** {@inheritDoc} */
    public void setID(@Nullable final String newID) {
        final String oldID = id;
        this.id = prepareForAssignment(id, newID);
        registerOwnID(oldID, id);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<XMLObject> getXMLObjects() {
        return indexedChildren;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<XMLObject> getXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) indexedChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<KeyName> getKeyNames() {
        return (List<KeyName>) indexedChildren.subList(KeyName.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<KeyValue> getKeyValues() {
        return (List<KeyValue>) indexedChildren.subList(KeyValue.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<DEREncodedKeyValue> getDEREncodedKeyValues() {
        return (List<DEREncodedKeyValue>) indexedChildren.subList(DEREncodedKeyValue.DEFAULT_ELEMENT_NAME);
    }
    
    /** {@inheritDoc} */
    @Nonnull @Live public List<RetrievalMethod> getRetrievalMethods() {
        return (List<RetrievalMethod>) indexedChildren.subList(RetrievalMethod.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<KeyInfoReference> getKeyInfoReferences() {
        return (List<KeyInfoReference>) indexedChildren.subList(KeyInfoReference.DEFAULT_ELEMENT_NAME);
    }
    
    /** {@inheritDoc} */
    @Nonnull @Live public List<X509Data> getX509Datas() {
        return (List<X509Data>) indexedChildren.subList(X509Data.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<PGPData> getPGPDatas() {
        return (List<PGPData>) indexedChildren.subList(PGPData.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<SPKIData> getSPKIDatas() {
        return (List<SPKIData>) indexedChildren.subList(SPKIData.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<MgmtData> getMgmtDatas() {
        return (List<MgmtData>) indexedChildren.subList(MgmtData.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AgreementMethod> getAgreementMethods() {
        return (List<AgreementMethod>) indexedChildren.subList(AgreementMethod.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<EncryptedKey> getEncryptedKeys() {
        return (List<EncryptedKey>) indexedChildren.subList(EncryptedKey.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(indexedChildren);
    }

}
