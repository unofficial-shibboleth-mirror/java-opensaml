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

package org.opensaml.saml.ext.saml2mdui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSString;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.DeprecationSupport;
import net.shibboleth.shared.primitive.DeprecationSupport.ObjectType;

/**
 * IPHint.
 *
 * See IdP Discovery and Login UI Metadata Extension Profile.
 *
 * @author Rod Widdowson August 2010
 * 
 * The &lt;IPHint&gt; element specifies a set of [CIDR] blocks associated with, 
 *  or serviced by, the entity.  Both IPv4 and IPv6 CIDR blocks MUST be supported.
 */
public interface IPHint extends SAMLObject, XSString {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "IPHint";
    
    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MDUI_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MDUI_PREFIX);
    
    /**
     * Gets the hint.
     * 
     * @return the hint
     */
    @Deprecated(forRemoval=true, since="4.0.0")
    @Nullable default String getHint() {
        DeprecationSupport.warn(ObjectType.METHOD, "getHint", IPHint.class.toString(), "getValue");
        return getValue();
    }
    
    /**
     * Sets the hint.
     * 
     * @param value hint
     */
    @Deprecated(forRemoval=true, since="4.0.0")
    default void setHint(@Nullable final String value) {
        DeprecationSupport.warn(ObjectType.METHOD, "setHint", IPHint.class.toString(), "setValue");
        setValue(value);
    }

}