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

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * The wst:RequestSecurityTokenResponseCollection element.
 * 
 * @see "WS-Trust 1.3 Specification"
 * 
 */
public interface RequestSecurityTokenResponseCollection extends AttributeExtensibleXMLObject, WSTrustObject {

    /** Element local name. */
    @Nonnull @NotEmpty public static final String ELEMENT_LOCAL_NAME = "RequestSecurityTokenResponseCollection";

    /** Default element name. */
    @Nonnull public static final QName ELEMENT_NAME =
        new QName(WSTrustConstants.WST_NS, ELEMENT_LOCAL_NAME, WSTrustConstants.WST_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "RequestSecurityTokenResponseCollectionType"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = 
        new QName(WSTrustConstants.WST_NS, TYPE_LOCAL_NAME, WSTrustConstants.WST_PREFIX);

    /**
     * Returns the list of wst:RequestSecurityTokenResponse child elements.
     * 
     * @return The list of {@link RequestSecurityTokenResponse}s.
     */
    @Nonnull @Live public List<RequestSecurityTokenResponse> getRequestSecurityTokenResponses();

}
