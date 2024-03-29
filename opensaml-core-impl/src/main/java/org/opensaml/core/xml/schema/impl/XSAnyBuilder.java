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

package org.opensaml.core.xml.schema.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObjectBuilder;
import org.opensaml.core.xml.schema.XSAny;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Builder of {@link XSAnyImpl}s.
 */
public class XSAnyBuilder extends AbstractXMLObjectBuilder<XSAny> {

    /** {@inheritDoc} */
    @Override
    @Nonnull public XSAny buildObject(@Nullable final String namespaceURI, @Nonnull @NotEmpty final String localName,
            @Nullable final String namespacePrefix) {
        return new XSAnyImpl(namespaceURI, localName, namespacePrefix);
    }
}