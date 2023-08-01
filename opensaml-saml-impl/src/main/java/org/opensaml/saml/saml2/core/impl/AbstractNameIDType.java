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

package org.opensaml.saml.saml2.core.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.schema.impl.XSStringImpl;
import org.opensaml.saml.saml2.core.NameIDType;

/**
 * Abstract implementation of {@link NameIDType}.
 */
public class AbstractNameIDType extends XSStringImpl implements NameIDType {
    
    /** Name Qualifier of the Name ID. */
    @Nullable private String nameQualifier;

    /** SP Name Qualifier of the Name ID. */
    @Nullable private String spNameQualifier;

    /** Format of the Name ID. */
    @Nullable private String format;

    /** SP ProvidedID of the NameID. */
    @Nullable private String spProvidedID;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AbstractNameIDType(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getNameQualifier() {
        return nameQualifier;
    }

    /** {@inheritDoc} */
    public void setNameQualifier(@Nullable final String newNameQualifier) {
        this.nameQualifier = prepareForAssignment(this.nameQualifier, newNameQualifier);
    }

    /** {@inheritDoc} */
    @Nullable public String getSPNameQualifier() {
        return spNameQualifier;
    }

    /** {@inheritDoc} */
    public void setSPNameQualifier(@Nullable final String newSPNameQualifier) {
        this.spNameQualifier = prepareForAssignment(this.spNameQualifier, newSPNameQualifier);
    }

    /** {@inheritDoc} */
    @Nullable public String getFormat() {
        return format;
    }

    /** {@inheritDoc} */
    public void setFormat(@Nullable final String newFormat) {
        this.format = prepareForAssignment(this.format, newFormat);
    }

    /** {@inheritDoc} */
    @Nullable public String getSPProvidedID() {
        return spProvidedID;
    }

    /** {@inheritDoc} */
    public void setSPProvidedID(@Nullable final String newSPProvidedID) {
        this.spProvidedID = prepareForAssignment(this.spProvidedID, newSPProvidedID);
    }
    
}