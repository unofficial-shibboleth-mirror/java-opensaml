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

import org.opensaml.core.xml.XMLObject;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.xml.XMLConstants;

/**
 * XSBoolean is the <code>xs:boolean</code> schema type.
 */
public interface XSBoolean extends XMLObject {

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "boolean"; 
            
    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(XMLConstants.XSD_NS, TYPE_LOCAL_NAME, XMLConstants.XSD_PREFIX);
    
    /**
     * Returns the XSBooleanValue value.
     * 
     * @return the {@link XSBooleanValue} value
     */
    @Nullable XSBooleanValue getValue();

    /**
     * Sets the XSBooleanValue value.
     * 
     * @param value The {@link XSBooleanValue} value
     */
    void setValue(@Nullable final XSBooleanValue value);
}