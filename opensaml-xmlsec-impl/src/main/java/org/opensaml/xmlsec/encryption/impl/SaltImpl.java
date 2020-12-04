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

import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.OtherSource;
import org.opensaml.xmlsec.encryption.Salt;
import org.opensaml.xmlsec.encryption.Specified;

/**
 * Concrete implementation of {@link org.opensaml.xmlsec.encryption.Salt}.
 */
public class SaltImpl extends AbstractXMLObject implements Salt {
    
    /** Specified child element value. */
    private Specified specified;
    
    /** OtherSource child element value. */
    private OtherSource otherSource;
    
    /**
     * Constructor.
     *
     * @param namespaceURI namespace URI
     * @param elementLocalName local name
     * @param namespacePrefix namespace prefix
     */
    protected SaltImpl(final String namespaceURI, final String elementLocalName,
            final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public Specified getSpecified() {
        return specified;
    }

    /** {@inheritDoc} */
    public void setSpecified(@Nullable final Specified newSpecified) {
        specified = prepareForAssignment(specified, newSpecified);
    }

    /** {@inheritDoc} */
    @Nullable public OtherSource getOtherSource() {
        return otherSource;
    }

    /** {@inheritDoc} */
    public void setOtherSource(@Nullable final OtherSource newOtherSource) {
        otherSource = prepareForAssignment(otherSource, newOtherSource);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (specified != null) {
            children.add(specified);
        }
        if (otherSource != null) {
            children.add(otherSource);
        }
        
        if (children.size() == 0) {
            return null;
        }
        
        return Collections.unmodifiableList(children);
    }

}