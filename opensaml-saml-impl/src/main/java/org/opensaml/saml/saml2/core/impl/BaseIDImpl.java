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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.BaseID;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * Concrete implementation of {@link BaseID}.
 */
public abstract class BaseIDImpl extends AbstractXMLObject implements BaseID {

    /** Name Qualifier of BaseID. */
    @Nullable private String nameQualifier;

    /** SP Name Qualifier of Base. */
    @Nullable private String spNameQualfier;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected BaseIDImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getNameQualifier() {
        return nameQualifier;
    }

    /** {@inheritDoc} */
    public void setNameQualifier(@Nullable final String newNameQualifier) {
        nameQualifier = prepareForAssignment(nameQualifier, newNameQualifier);
    }

    /** {@inheritDoc} */
    @Nullable public String getSPNameQualifier() {
        return spNameQualfier;
    }

    /** {@inheritDoc} */
    public void setSPNameQualifier(@Nullable final String newSPNameQualifier) {
        spNameQualfier = prepareForAssignment(spNameQualfier, newSPNameQualifier);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return null;
    }

}