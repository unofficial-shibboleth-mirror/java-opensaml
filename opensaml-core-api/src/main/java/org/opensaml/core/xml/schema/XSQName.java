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

package org.opensaml.core.xml.schema;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.xml.XMLConstants;

import org.opensaml.core.xml.XMLObject;

/**
 * XMLObject that represents an XML schema QName content bearing element.
 */
public interface XSQName extends XMLObject {

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "QName"; 
        
    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(XMLConstants.XSD_NS, TYPE_LOCAL_NAME, XMLConstants.XSD_PREFIX);
    
    /**
     * Gets the QName content of the element.
     * 
     * @return the QName content of the element
     */
    @Nullable QName getValue();
    
    /**
     * Sets the QName content of the element.
     * 
     * @param newValue the QName content of the element
     */
    void setValue(@Nullable final QName newValue);
}