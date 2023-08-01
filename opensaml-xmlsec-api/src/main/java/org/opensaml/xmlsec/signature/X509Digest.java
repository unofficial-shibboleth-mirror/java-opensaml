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

package org.opensaml.xmlsec.signature;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSBase64Binary;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * XMLObject representing XML Digital Signature 1.1 X509Digest element.
 */
public interface X509Digest extends XSBase64Binary {
    
    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "X509Digest";
    
    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SignatureConstants.XMLSIG11_NS, DEFAULT_ELEMENT_LOCAL_NAME, SignatureConstants.XMLSIG11_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "X509DigestType"; 
        
    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
            new QName(SignatureConstants.XMLSIG11_NS, TYPE_LOCAL_NAME, SignatureConstants.XMLSIG11_PREFIX);
    
    /** Algorithm attribute name. */
    @Nonnull @NotEmpty static final String ALGORITHM_ATTRIB_NAME = "Algorithm";
    
    /**
     * Get the Algorithm URI attribute value.
     *  
     * @return the Algorithm URI attribute value
     */
    @Nullable String getAlgorithm();
    
    /**
     * 
     * Set the Algorithm URI attribute value.
     *  
     * @param newAlgorithm the new Algorithm URI attribute value
     */
    void setAlgorithm(@Nullable final String newAlgorithm);
    
}