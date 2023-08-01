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

package org.opensaml.saml.saml1.core.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.schema.impl.XSStringImpl;
import org.opensaml.saml.saml1.core.NameIdentifier;

/**
 * Complete implementation of {@link NameIdentifier}.
 */
public class NameIdentifierImpl extends XSStringImpl implements NameIdentifier {

    /** Contents of the NameQualifierAttribute. */
    @Nullable private String nameQualifier;

    /** Contents of the Format. */
    @Nullable private String format;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected NameIdentifierImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getNameQualifier() {
        return nameQualifier;
    }

    /** {@inheritDoc} */
    @Nullable public String getFormat() {
        return this.format;
    }
    
    /** {@inheritDoc} */
    public void setNameQualifier(@Nullable final String qualifier) {
        nameQualifier = prepareForAssignment(nameQualifier, qualifier);
    }

    /** {@inheritDoc} */
    public void setFormat(@Nullable final String fmt) {
        format = prepareForAssignment(format, fmt);
    }

}