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

/**
 * 
 */
package org.opensaml.saml.saml1.core;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Description of the behaviour of the <code> AttributeQuery </code> element.
 */
public interface AttributeQuery extends SubjectQuery {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "AttributeQuery";
    
    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML10P_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "AttributeQueryType"; 
        
    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML10P_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
    
    /** AuthenticationMethod attribute name. */
    @Nonnull @NotEmpty static final String RESOURCE_ATTRIB_NAME = "Resource"; 

    /**
     * Get list of AttributeDesignators.
     * 
     * @return the list of AttributeDesignators
     */
    @Nonnull @Live List<AttributeDesignator> getAttributeDesignators();
    
    /**
     * Get Resource attribute.
     * 
     * @return the attribute value
     */
    @Nullable String getResource();
    
    /**
     * Set Resource attribute.
     * 
     * @param resource value to set
     */
    void setResource(@Nullable final String resource);

}