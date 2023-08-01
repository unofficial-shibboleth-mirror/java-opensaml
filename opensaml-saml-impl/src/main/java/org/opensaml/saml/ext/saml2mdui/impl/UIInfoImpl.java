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

package org.opensaml.saml.ext.saml2mdui.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.ext.saml2mdui.Description;
import org.opensaml.saml.ext.saml2mdui.DisplayName;
import org.opensaml.saml.ext.saml2mdui.InformationURL;
import org.opensaml.saml.ext.saml2mdui.Keywords;
import org.opensaml.saml.ext.saml2mdui.Logo;
import org.opensaml.saml.ext.saml2mdui.PrivacyStatementURL;
import org.opensaml.saml.ext.saml2mdui.UIInfo;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link UIInfo}.
 */
@SuppressWarnings("unchecked")
public class UIInfoImpl extends AbstractXMLObject implements UIInfo {
    
    /** Children of the UIInfo. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> uiInfoChildren;
    
    /**
     * Constructor.
     * @param namespaceURI namespaceURI
     * @param elementLocalName elementLocalName
     * @param namespacePrefix namespacePrefix
     */
    protected UIInfoImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        
        uiInfoChildren = new IndexedXMLObjectChildrenList<>(this);
    }
    
    /** {@inheritDoc} */
    @Nonnull @Live public List<XMLObject> getXMLObjects() {
        return uiInfoChildren;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<XMLObject> getXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) uiInfoChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Description> getDescriptions() {
        return (List<Description>) uiInfoChildren.subList(Description.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<DisplayName> getDisplayNames() {
        return (List<DisplayName>) uiInfoChildren.subList(DisplayName.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Keywords> getKeywords() {
        return (List<Keywords>) uiInfoChildren.subList(Keywords.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<InformationURL> getInformationURLs() {
        return (List<InformationURL>) uiInfoChildren.subList(InformationURL.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Logo> getLogos() {
        return (List<Logo>) uiInfoChildren.subList(Logo.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<PrivacyStatementURL> getPrivacyStatementURLs() {
        return (List<PrivacyStatementURL>) uiInfoChildren.subList(PrivacyStatementURL.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(uiInfoChildren);
    }

}