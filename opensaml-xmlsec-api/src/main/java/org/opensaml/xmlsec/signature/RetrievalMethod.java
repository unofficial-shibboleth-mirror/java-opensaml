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

package org.opensaml.xmlsec.signature;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * XMLObject representing XML Digital Signature, version 20020212, RetrievalMethod element.
 */
public interface RetrievalMethod extends XMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "RetrievalMethod";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SignatureConstants.XMLSIG_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, SignatureConstants.XMLSIG_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "RetrievalMethodType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SignatureConstants.XMLSIG_NS, TYPE_LOCAL_NAME,
            SignatureConstants.XMLSIG_PREFIX);

    /** URI attribute name. */
    @Nonnull @NotEmpty static final String URI_ATTRIB_NAME = "URI";

    /** Type attribute name. */
    @Nonnull @NotEmpty static final String TYPE_ATTRIB_NAME = "Type";

    /**
     * Get the URI attribute value.
     * 
     * @return the URI attribute value
     */
    @Nullable String getURI();

    /**
     * Set the URI attribute value.
     * 
     * @param newURI the new URI attribute value
     */
    void setURI(@Nullable final String newURI);

    /**
     * Set the Type attribute value.
     * 
     * @return the Type attribute value
     */
    @Nullable String getType();

    /**
     * Set the Type attribute value.
     * 
     * @param newType the new Type attribute value
     */
    void setType(@Nullable final String newType);

    /**
     * Get the Transforms child element.
     * 
     * @return the Transforms child element
     */
    @Nullable Transforms getTransforms();

    /**
     * Set the Transforms child element.
     * 
     * @param newTransforms the new Transforms child element
     */
    void setTransforms(@Nullable final Transforms newTransforms);

}