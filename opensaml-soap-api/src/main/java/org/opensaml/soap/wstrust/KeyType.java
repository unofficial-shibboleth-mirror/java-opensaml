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
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSURI;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * The wst:KeyType element.
 * 
 * @see "WS-Trust 1.3, Chapter 9.2 Key and Encryption Requirements."
 * 
 */
public interface KeyType extends XSURI, WSTrustObject {

    /** Element local name. */
    @Nonnull @NotEmpty public static final String ELEMENT_LOCAL_NAME = "KeyType";

    /** Default element name. */
    @Nonnull public static final QName ELEMENT_NAME =
        new QName(WSTrustConstants.WST_NS, ELEMENT_LOCAL_NAME, WSTrustConstants.WST_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "KeyTypeOpenEnum"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = 
        new QName(WSTrustConstants.WST_NS, TYPE_LOCAL_NAME, WSTrustConstants.WST_PREFIX);

    /** The KeyType PublicKey URI. */
    @Nonnull @NotEmpty public static final String PUBLIC_KEY = WSTrustConstants.WST_NS + "/PublicKey";

    /** The KeyType SymmetricKey URI. */
    @Nonnull @NotEmpty public static final String SYMMETRIC_KEY = WSTrustConstants.WST_NS + "/SymmetricKey";

    /** The KeyType Bearer URI. */
    @Nonnull @NotEmpty public static final String BEARER = WSTrustConstants.WST_NS + "/Bearer";

}
