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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.CipherReference;
import org.opensaml.xmlsec.encryption.Transforms;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link CipherReference}.
 */
public class CipherReferenceImpl extends AbstractXMLObject implements CipherReference {

    /** URI attribute value. */
    @Nullable private String uri;

    /** Transforms child element value. */
    @Nullable private Transforms transforms;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace URI
     * @param elementLocalName local name
     * @param namespacePrefix namespace prefix
     */
    protected CipherReferenceImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getURI() {
        return uri;
    }

    /** {@inheritDoc} */
    public void setURI(@Nullable final String newURI) {
        uri = prepareForAssignment(uri, newURI);
    }

    /** {@inheritDoc} */
    @Nullable public Transforms getTransforms() {
        return transforms;
    }

    /** {@inheritDoc} */
    public void setTransforms(@Nullable final Transforms newTransforms) {
        transforms = prepareForAssignment(transforms, newTransforms);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {

        if (transforms != null) {
            return CollectionSupport.singletonList(transforms);
        }
        
        return null;
    }

}
