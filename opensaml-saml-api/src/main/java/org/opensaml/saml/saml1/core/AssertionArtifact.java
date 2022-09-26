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

package org.opensaml.saml.saml1.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.DeprecationSupport;
import net.shibboleth.shared.primitive.DeprecationSupport.ObjectType;

import org.opensaml.core.xml.schema.XSString;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

/**
 * This interface is for the SAML1 <code> AssertionArtifact </code> element.
 */
public interface AssertionArtifact extends XSString, SAMLObject {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "AssertionArtifact";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML10P_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
    
    /**
     * Get artifact value.
     * 
     * @return the artifact value
     */
    @Deprecated(forRemoval=true, since="4.0.0")
    @Nullable default String getAssertionArtifact() {
        DeprecationSupport.warn(ObjectType.METHOD, "getAssertionArtifact", AssertionArtifact.class.toString(),
                "getValue");
        return getValue();
    }

    /**
     * Set artifact value.
     * 
     * @param value new artifact value
     */
    @Deprecated(forRemoval=true, since="4.0.0")
    default void setAssertionArtifact(@Nullable final String value) {
        DeprecationSupport.warn(ObjectType.METHOD, "setAssertionArtifact", AssertionArtifact.class.toString(),
                "setValue");
        setValue(value);
    }

}