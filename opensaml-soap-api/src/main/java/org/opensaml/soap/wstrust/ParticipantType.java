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

import org.opensaml.core.xml.XMLObject;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface ParticipantType complex type.
 * 
 */
public interface ParticipantType extends WSTrustObject {
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "ParticipantType"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = 
        new QName(WSTrustConstants.WST_NS, TYPE_LOCAL_NAME, WSTrustConstants.WST_PREFIX);
    
    /**
     * Get the unknown child element.
     * 
     * @return the child element
     */
    @Nullable public XMLObject getUnknownXMLObject();
    
    /**
     * Set the unknown child element.
     * 
     * @param unknownObject the new child element
     */
    public void setUnknownXMLObject(@Nullable final XMLObject unknownObject);

}
