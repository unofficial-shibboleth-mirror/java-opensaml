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

package org.opensaml.saml.saml1.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * This interface defines how the object representing a SAML 1 <code>AttributeDesignator</code> element behaves.
 */
public interface AttributeDesignator extends SAMLObject {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "AttributeDesignator";
    
    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML1_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "AttributeDesignatorType"; 
        
    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML1_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    
    /** Name for the AttributeName attribute. */
    @Nonnull @NotEmpty static final String ATTRIBUTENAME_ATTRIB_NAME = "AttributeName";

    /** Name for the AttributeNamespace attribute. */
    @Nonnull @NotEmpty static final String ATTRIBUTENAMESPACE_ATTRIB_NAME = "AttributeNamespace";

    /**
     * Get the contents of the AttributeName attribute.
     * 
     * @return the AttributeName attribute
     */
    @Nullable String getAttributeName();
    
    /**
     * Set the contents of the AttributeName attribute.
     * 
     * @param attributeName value to set
     */
    void setAttributeName(@Nullable final String attributeName);
    
    /**
     * Get the contents of the AttributeNamespace attribute.
     * 
     * @return the AttributeNamespace attribute
     */
    @Nullable String getAttributeNamespace();
    
    /**
     * Set the contents of the AttributeNamespace attribute.
     * 
     * @param attributeNamespace value to set
     */
    void setAttributeNamespace(@Nullable final String attributeNamespace);
    
}