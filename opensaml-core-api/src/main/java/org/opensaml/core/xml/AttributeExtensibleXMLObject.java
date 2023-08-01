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

package org.opensaml.core.xml;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.util.AttributeMap;

/**
 * An interface for XMLObjects that represent DOM elements that support 
 * the XML Schema anyAttribute construct.
 */
public interface AttributeExtensibleXMLObject extends XMLObject {

    /**
     * Gets a mutable map of the attributes.  The map key is the namespace qualified name of the attribute, 
     * the map value is the value of the attribute.
     * 
     * @return a map of the attributes
     */
    @Nonnull AttributeMap getUnknownAttributes();
    
}