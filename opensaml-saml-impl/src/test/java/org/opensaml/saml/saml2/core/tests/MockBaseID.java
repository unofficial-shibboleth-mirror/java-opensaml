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

package org.opensaml.saml.saml2.core.tests;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.saml.saml2.core.BaseID;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * Mock BaseID class for testing purposes.
 */
public class MockBaseID extends AbstractXMLObject implements BaseID, XSString {
    
    /** String content. */
    @Nullable private String content;
    
    /** Name qualifier. */
    @Nullable private String nameQualifier;
    
    /** SP name qualifier. */
    @Nullable private String spNameQualifier;
    
    /** Constructor. */
    public MockBaseID() {
        this(
                BaseID.DEFAULT_ELEMENT_NAME.getNamespaceURI(), 
                BaseID.DEFAULT_ELEMENT_LOCAL_NAME,
                BaseID.DEFAULT_ELEMENT_NAME.getPrefix());
    }

    /**
     * Constructor.
     *
     * @param namespaceURI ...
     * @param elementLocalName ...
     * @param namespacePrefix ...
     */
    protected MockBaseID(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getNameQualifier() {
        return nameQualifier;
    }

    /** {@inheritDoc} */
    @Nullable public String getSPNameQualifier() {
        return spNameQualifier;
    }

    /** {@inheritDoc} */
    public void setNameQualifier(@Nullable final String newNameQualifier) {
        nameQualifier = prepareForAssignment(nameQualifier, newNameQualifier);
    }

    /** {@inheritDoc} */
    public void setSPNameQualifier(@Nullable final String newSPNameQualifier) {
        spNameQualifier = prepareForAssignment(spNameQualifier, newSPNameQualifier);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return null;
    }

    /** {@inheritDoc} */
    @Nullable public String getValue() {
        return content;
    }

    /** {@inheritDoc} */
    public void setValue(@Nullable final String newValue) {
        content = prepareForAssignment(content, newValue);
    }
}
