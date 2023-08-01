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

/** XMLObject representing XML Digital Signature 1.1 DEREncodedKeyValue element. */
public interface DEREncodedKeyValue extends XSBase64Binary {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "DEREncodedKeyValue";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SignatureConstants.XMLSIG11_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, SignatureConstants.XMLSIG11_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "DEREncodedKeyValueType"; 
        
    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
            new QName(SignatureConstants.XMLSIG11_NS, TYPE_LOCAL_NAME, SignatureConstants.XMLSIG11_PREFIX);
    
    /** Id attribute name. */
    @Nonnull @NotEmpty static final String ID_ATTRIB_NAME = "Id";

    /**
     * Get the Id attribute value.
     * 
     * @return the Id attribute value
     */
    @Nullable String getID();

    /**
     * Set the Id attribute value.
     * 
     * @param newID the new Id attribute value
     */
    void setID(@Nullable final String newID);
    
}