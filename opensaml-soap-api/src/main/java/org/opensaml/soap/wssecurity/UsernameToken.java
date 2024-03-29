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

package org.opensaml.soap.wssecurity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.ElementExtensibleXMLObject;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * The &lt;wsse:UsernameToken&gt; element.
 * 
 * @see "WS-Security UsernameToken Profile 1.1"
 * 
 */
public interface UsernameToken extends IdBearing, AttributeExtensibleXMLObject, 
        ElementExtensibleXMLObject, WSSecurityObject {
    
    /** Element local name. */
    @Nonnull @NotEmpty public static final String ELEMENT_LOCAL_NAME = "UsernameToken";

    /** Qualified element name. */
    @Nonnull public static final QName ELEMENT_NAME =
        new QName(WSSecurityConstants.WSSE_NS, ELEMENT_LOCAL_NAME, WSSecurityConstants.WSSE_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "UsernameTokenType"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = 
        new QName(WSSecurityConstants.WSSE_NS, TYPE_LOCAL_NAME, WSSecurityConstants.WSSE_PREFIX);

    /**
     * Returns the &lt;wsse:Username&gt; child element.
     * 
     * @return the {@link Username} child element.
     */
    @Nullable public Username getUsername();

    /**
     * Sets the &lt;wsse:Username&gt; child element.
     * 
     * @param username
     *            the {@link Username} child element to set.
     */
    public void setUsername(@Nullable final Username username);

}
