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

import org.opensaml.xacml.ctx.StatusType;
import org.opensaml.xacml.impl.AbstractXACMLObjectBuilder;

/** Builder for {@link StatusType} objects. */
public class StatusTypeImplBuilder extends AbstractXACMLObjectBuilder<StatusType> {

    /** Constructor. */
    public StatusTypeImplBuilder() {
    }

    /** {@inheritDoc} */
    public StatusType buildObject() {
        return buildObject(StatusType.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    public StatusType buildObject(final String namespaceURI, final String localName, final String namespacePrefix) {
        return new StatusTypeImpl(namespaceURI, localName, namespacePrefix);
    }
}