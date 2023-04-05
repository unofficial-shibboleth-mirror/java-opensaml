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

package org.opensaml.saml.ext.saml2mdquery.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.ext.saml2mdquery.QueryDescriptorType;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.impl.RoleDescriptorImpl;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * Concrete implementation of {@link QueryDescriptorType}.
 */
public abstract class QueryDescriptorTypeImpl extends RoleDescriptorImpl implements QueryDescriptorType {

    /** WantAssertionSigned attribute value. */
    @Nullable private XSBooleanValue wantAssertionsSigned;
    
    /** Supported NameID formats. */
    @Nonnull private final XMLObjectChildrenList<NameIDFormat> nameIDFormats;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected QueryDescriptorTypeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        
        nameIDFormats = new XMLObjectChildrenList<>(this);
    }
    
    /** {@inheritDoc} */
    @Nullable public Boolean getWantAssertionsSigned() {
        if (wantAssertionsSigned != null) {
            return wantAssertionsSigned.getValue();
        }
        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    public void setWantAssertionsSigned(@Nullable final Boolean newWantAssertionsSigned) {
        if (newWantAssertionsSigned != null) {
            wantAssertionsSigned = prepareForAssignment(wantAssertionsSigned, 
                    new XSBooleanValue(newWantAssertionsSigned, false));
        } else {
            wantAssertionsSigned = prepareForAssignment(wantAssertionsSigned, null);
        }
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue getWantAssertionsSignedXSBoolean(){
        return wantAssertionsSigned;
    }
    
    /** {@inheritDoc} */
    public void setWantAssertionsSigned(@Nullable final XSBooleanValue newWantAssertionsSigned){
        wantAssertionsSigned = prepareForAssignment(wantAssertionsSigned, newWantAssertionsSigned);
    }
    
    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<NameIDFormat> getNameIDFormat(){
        return nameIDFormats;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        children.addAll(super.getOrderedChildren());
        children.addAll(nameIDFormats);
        
        return Collections.unmodifiableList(children);
    }

}