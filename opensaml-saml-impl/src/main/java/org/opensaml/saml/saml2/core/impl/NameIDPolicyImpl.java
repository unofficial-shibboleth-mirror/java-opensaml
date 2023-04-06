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

/**
 * 
 */

package org.opensaml.saml.saml2.core.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.saml2.core.NameIDPolicy;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * Concrete implementation of {@link NameIDPolicy}.
 */
public class NameIDPolicyImpl extends AbstractXMLObject implements NameIDPolicy {

    /** NameID Format URI. */
    @Nullable private String format;

    /** NameID Format URI. */
    @Nullable private String spNameQualifier;

    /** NameID Format URI. */
    @Nullable private XSBooleanValue allowCreate;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected NameIDPolicyImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getFormat() {
        return format;
    }

    /** {@inheritDoc} */
    public void setFormat(@Nullable final String newFormat) {
        format = prepareForAssignment(format, newFormat);

    }

    /** {@inheritDoc} */
    @Nullable public String getSPNameQualifier() {
        return spNameQualifier;
    }

    /** {@inheritDoc} */
    public void setSPNameQualifier(@Nullable final String newSPNameQualifier) {
        spNameQualifier = prepareForAssignment(spNameQualifier, newSPNameQualifier);

    }
    
    /** {@inheritDoc} */
    @Nullable public Boolean getAllowCreate() {
        if(allowCreate != null){
            return allowCreate.getValue();
        }
        
        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue getAllowCreateXSBoolean() {
        return allowCreate;
    }

    /** {@inheritDoc} */
    public void setAllowCreate(@Nullable final Boolean newAllowCreate){
        if(newAllowCreate != null){
            allowCreate = prepareForAssignment(allowCreate, new XSBooleanValue(newAllowCreate, false));
        }else{
            allowCreate = prepareForAssignment(allowCreate, null);
        }
    }
    
    /** {@inheritDoc} */
    public void setAllowCreate(@Nullable final XSBooleanValue newAllowCreate) {
        allowCreate = prepareForAssignment(allowCreate, newAllowCreate);

    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        // no children
        return null;
    }
    
}