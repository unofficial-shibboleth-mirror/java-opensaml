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

/**
 * 
 */

package org.opensaml.core.xml.mock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObjectBuilder;

/**
 * Builder of {@link org.opensaml.core.xml.mock.SimpleXMLObject}s.
 */
public class SimpleXMLObjectBuilder extends AbstractXMLObjectBuilder<SimpleXMLObject> {

    /**
     * Default builder method.
     * 
     * @return new object
     */
    @Nonnull public SimpleXMLObject buildObject(){
        return buildObject(SimpleXMLObject.NAMESPACE, SimpleXMLObject.LOCAL_NAME, SimpleXMLObject.NAMESPACE_PREFIX);
    }

    /** {@inheritDoc} */
    @Nonnull public SimpleXMLObject buildObject(@Nullable final String namespaceURI, @Nonnull final String localName,
            @Nullable final String namespacePrefix) {
        return new SimpleXMLObject(namespaceURI, localName, namespacePrefix);
    }

}