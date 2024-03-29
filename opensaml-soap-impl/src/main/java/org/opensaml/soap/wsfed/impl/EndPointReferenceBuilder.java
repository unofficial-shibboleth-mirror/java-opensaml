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

package org.opensaml.soap.wsfed.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObjectBuilder;
import org.opensaml.soap.wsfed.EndPointReference;
import org.opensaml.soap.wsfed.WSFedConstants;
import org.opensaml.soap.wsfed.WSFedObjectBuilder;

/** Builder of {@link EndPointReferenceImpl} objects. */
public class EndPointReferenceBuilder extends AbstractXMLObjectBuilder<EndPointReference> implements
        WSFedObjectBuilder<EndPointReference> {

    /** {@inheritDoc} */
    @Nonnull public EndPointReference buildObject() {
        return buildObject(WSFedConstants.WSADDRESS_NS, EndPointReference.DEFAULT_ELEMENT_LOCAL_NAME,
                WSFedConstants.WSADDRESS_PREFIX);
    }

    /** {@inheritDoc} */
    @Nonnull public EndPointReference buildObject(@Nullable final String namespaceURI, @Nonnull final String localName,
            @Nullable final String namespacePrefix) {
        return new EndPointReferenceImpl(namespaceURI, localName, namespacePrefix);
    }
}