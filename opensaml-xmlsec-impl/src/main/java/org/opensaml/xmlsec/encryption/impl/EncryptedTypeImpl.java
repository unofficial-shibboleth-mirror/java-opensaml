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
import org.opensaml.xmlsec.encryption.CipherData;
import org.opensaml.xmlsec.encryption.EncryptedType;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.EncryptionProperties;
import org.opensaml.xmlsec.signature.KeyInfo;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Abstract implementation of {@link EncryptedType}.
 */
public abstract class EncryptedTypeImpl extends AbstractXMLObject implements EncryptedType {
    
    /** id attribute value. */
    @Nullable private String id;
    
    /** Type attribute value. */
    @Nullable private String type;
    
    /** MimeType attribute value. */
    @Nullable private String mimeType;
    
    /** Encoding attribute value. */
    @Nullable private String encoding;
    
    /** EncryptionMethod child element. */
    @Nullable private EncryptionMethod encryptionMethod;
    
    /** EncryptionMethod child element. */
    @Nullable private KeyInfo keyInfo;
    
    /** CipherData child element. */
    @Nullable private CipherData cipherData;
    
    /** EncryptionProperties child element. */
    @Nullable private EncryptionProperties encryptionProperties;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI namespace URI
     * @param elementLocalName local name
     * @param namespacePrefix namespace prefix
     */
    protected EncryptedTypeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getID() {
        return id;
    }
    
    /** {@inheritDoc} */
    public void setID(@Nullable final String newID) {
        final String oldID = id;
        id = prepareForAssignment(id, newID);
        registerOwnID(oldID, id);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getType() {
        return type;
    }
    
    /** {@inheritDoc} */
    public void setType(@Nullable final String newType) {
        type = prepareForAssignment(type, newType);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getMimeType() {
        return mimeType;
    }
    
    /** {@inheritDoc} */
    public void setMimeType(@Nullable final String newMimeType) {
        mimeType = prepareForAssignment(mimeType, newMimeType);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getEncoding() {
        return encoding;
    }
    
    /** {@inheritDoc} */
    public void setEncoding(@Nullable final String newEncoding) {
        encoding = prepareForAssignment(encoding, newEncoding);
    }

    /** {@inheritDoc} */
    @Nullable public EncryptionMethod getEncryptionMethod() {
        return encryptionMethod;
    }

    /** {@inheritDoc} */
    public void setEncryptionMethod(@Nullable final EncryptionMethod newEncryptionMethod) {
        encryptionMethod = prepareForAssignment(encryptionMethod, newEncryptionMethod);
    }

    /** {@inheritDoc} */
    @Nullable public KeyInfo getKeyInfo() {
        return keyInfo;
    }

    /** {@inheritDoc} */
    public void setKeyInfo(@Nullable final KeyInfo newKeyInfo) {
        keyInfo = prepareForAssignment(keyInfo, newKeyInfo);
    }

    /** {@inheritDoc} */
    @Nullable public CipherData getCipherData() {
        return cipherData;
    }

    /** {@inheritDoc} */
    public void setCipherData(@Nullable final CipherData newCipherData) {
        cipherData = prepareForAssignment(cipherData, newCipherData);
    }

    /** {@inheritDoc} */
    @Nullable public EncryptionProperties getEncryptionProperties() {
        return encryptionProperties;
    }

    /** {@inheritDoc} */
    public void setEncryptionProperties(@Nullable final EncryptionProperties newEncryptionProperties) {
        encryptionProperties = prepareForAssignment(encryptionProperties, newEncryptionProperties);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (encryptionMethod != null) {
            children.add(encryptionMethod);
        }
        if (keyInfo != null) {
            children.add(keyInfo);
        }
        if (cipherData != null) {
            children.add(cipherData);
        }
        if (encryptionProperties!= null) {
            children.add(encryptionProperties);
        }
        
        return CollectionSupport.copyToList(children);
    }

}