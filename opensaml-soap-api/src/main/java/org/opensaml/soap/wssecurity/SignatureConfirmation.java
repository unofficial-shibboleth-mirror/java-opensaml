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

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * The &lt;wsse11:SignatureConfirmation&gt; element.
 */
public interface SignatureConfirmation extends IdBearing, WSSecurityObject {
    
    /** Element local name. */
    @Nonnull @NotEmpty public static final String ELEMENT_LOCAL_NAME = "SignatureConfirmation";

    /** Qualified element name. */
    @Nonnull public static final QName ELEMENT_NAME =
        new QName(WSSecurityConstants.WSSE11_NS, ELEMENT_LOCAL_NAME, WSSecurityConstants.WSSE11_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "SignatureConfirmationType"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = 
        new QName(WSSecurityConstants.WSSE11_NS, TYPE_LOCAL_NAME, WSSecurityConstants.WSSE11_PREFIX);
    
    /** The Value attribute name. */
    @Nonnull @NotEmpty public static final String VALUE_ATTRIB_NAME = "Value";
    
    /**
     * Returns the Value attribute value.
     * 
     * @return the Value attribute value or <code>null</code>.
     */
    @Nullable public String getValue();

    /**
     * Sets the Value attribute value.
     * 
     * @param newValue the Value attribute value.
     */
    public void setValue(@Nullable final String newValue);

}
