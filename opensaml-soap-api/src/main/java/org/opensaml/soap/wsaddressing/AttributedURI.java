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

package org.opensaml.soap.wsaddressing;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.schema.XSURI;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface for element of type &lt;wsa:AttributedURIType&gt;.
 * 
 * @see "WS-Addressing 1.0 - Core"
 * 
 */
public interface AttributedURI extends XSURI, AttributeExtensibleXMLObject, WSAddressingObject {
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "AttributedURIType"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = 
        new QName(WSAddressingConstants.WSA_NS, TYPE_LOCAL_NAME, WSAddressingConstants.WSA_PREFIX);

}
