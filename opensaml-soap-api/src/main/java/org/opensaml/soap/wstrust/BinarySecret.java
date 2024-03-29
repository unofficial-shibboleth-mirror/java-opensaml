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

package org.opensaml.soap.wstrust;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.schema.XSBase64Binary;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * The wst:BinarySecret element.
 * 
 * @see Entropy
 * @see "WS-Trust 1.3, Chapter 3.3 Binary Secrets."
 * 
 */
public interface BinarySecret extends XSBase64Binary, AttributeExtensibleXMLObject, WSTrustObject {

    /** Element local name. */
    @Nonnull @NotEmpty public static final String ELEMENT_LOCAL_NAME = "BinarySecret";

    /** Default element name. */
    @Nonnull public static final QName ELEMENT_NAME =
        new QName(WSTrustConstants.WST_NS, ELEMENT_LOCAL_NAME, WSTrustConstants.WST_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "BinarySecretType"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = 
        new QName(WSTrustConstants.WST_NS, TYPE_LOCAL_NAME, WSTrustConstants.WST_PREFIX);

    /** wst:BinarySecret/@Type attribute local name. */
    @Nonnull @NotEmpty public static final String TYPE_ATTRIB_NAME = "Type";

    /** Type attribute AsymmetricKey URI. */
    @Nonnull @NotEmpty public static final String TYPE_ASYMMETRIC_KEY = WSTrustConstants.WST_NS + "/AsymmetricKey";

    /** Type attribute SymmetricKey URI. */
    @Nonnull @NotEmpty public static final String TYPE_SYMMETRIC_KEY = WSTrustConstants.WST_NS + "/SymmetricKey";

    /** Type attribute Nonce URI. */
    @Nonnull @NotEmpty public static final String TYPE_NONCE = WSTrustConstants.WST_NS + "/Nonce";

    /**
     * Returns the wst:BinarySecret/@Type attribute value.
     * 
     * @return the Type attribute value.
     */
    @Nullable public String getType();

    /**
     * Sets the wst:BinarySecret/@Type attribute value.
     * 
     * @param type the Type attribute value to set.
     */
    public void setType(@Nullable final String type);

}
