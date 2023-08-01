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

package org.opensaml.saml.saml2.metadata.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.signature.KeyInfo;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link KeyDescriptor}.
 */
public class KeyDescriptorImpl extends AbstractXMLObject implements KeyDescriptor {

    /** Key usage type. */
    @Nullable private UsageType keyUseType;

    /** Key information. */
    @Nullable private KeyInfo keyInfo;

    /** Encryption methods supported by the entity. */
    @Nonnull private final XMLObjectChildrenList<EncryptionMethod> encryptionMethods;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected KeyDescriptorImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        encryptionMethods = new XMLObjectChildrenList<>(this);
        keyUseType = UsageType.UNSPECIFIED;
    }

    /** {@inheritDoc} */
    @Nullable public UsageType getUse() {
        return keyUseType;
    }

    /** {@inheritDoc} */
    public void setUse(@Nullable final UsageType newType) {
        if (newType != null) {
            keyUseType = prepareForAssignment(keyUseType, newType);
        } else {
            keyUseType = prepareForAssignment(keyUseType, UsageType.UNSPECIFIED);
        }
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
    @Nonnull @Live public List<EncryptionMethod> getEncryptionMethods() {
        return encryptionMethods;
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (keyInfo != null) {
            children.add(keyInfo);
        }
        
        children.addAll(encryptionMethods);

        return CollectionSupport.copyToList(children);
    }

}