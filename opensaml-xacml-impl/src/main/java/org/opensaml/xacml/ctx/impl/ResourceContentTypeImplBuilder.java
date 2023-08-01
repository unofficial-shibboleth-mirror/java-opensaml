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

package org.opensaml.xacml.ctx.impl;

import org.opensaml.xacml.ctx.ResourceContentType;
import org.opensaml.xacml.impl.AbstractXACMLObjectBuilder;

/** Builder for {@link ResourceContentType} objects. */
public class ResourceContentTypeImplBuilder extends AbstractXACMLObjectBuilder<ResourceContentType> {

    /** Constructor. */
    public ResourceContentTypeImplBuilder() {

    }

    /** {@inheritDoc} */
    public ResourceContentType buildObject(final String namespaceURI, final String localName,
            final String namespacePrefix) {
        return new ResourceContentTypeImpl(namespaceURI, localName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public ResourceContentType buildObject() {
        return buildObject(ResourceContentType.DEFAULT_ELEMENT_NAME);
    }
}