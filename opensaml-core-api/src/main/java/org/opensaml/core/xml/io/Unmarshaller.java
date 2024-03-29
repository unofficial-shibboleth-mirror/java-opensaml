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

package org.opensaml.core.xml.io;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.w3c.dom.Element;

/**
 * Unmarshallers are used to unmarshall a W3C DOM element into a {@link org.opensaml.core.xml.XMLObject}.
 */
public interface Unmarshaller {

    /**
     * Unmarshalls the given W3C DOM element into a XMLObject.
     * 
     * @param element the DOM Element
     * 
     * @return the unmarshalled XMLObject
     * 
     * @throws UnmarshallingException thrown if an error occurs unmarshalling the DOM element into the XMLObject
     */
    @Nonnull XMLObject unmarshall(@Nonnull final Element element) throws UnmarshallingException;
}