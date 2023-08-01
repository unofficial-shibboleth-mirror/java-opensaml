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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml1.core.AuthorityBinding;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * A concrete implementation of the {@link AuthorityBinding} interface.
 */
public class AuthorityBindingImpl extends AbstractXMLObject implements AuthorityBinding {

    /** The AuthorityKind. */
    @Nullable private QName authorityKind;

    /** The Location. */
    @Nullable private String location;

    /** The Binding. */
    @Nullable private String binding;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AuthorityBindingImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
   }
    
    /** {@inheritDoc} */
    @Nullable public QName getAuthorityKind() {
        return authorityKind;
    }

    /** {@inheritDoc} */
    public void setAuthorityKind(@Nullable final QName kind) {
        authorityKind = prepareAttributeValueForAssignment(AuthorityBinding.AUTHORITYKIND_ATTRIB_NAME, 
                authorityKind, kind);
    }

    /** {@inheritDoc} */
    @Nullable public String getLocation() {
        return location;
    }

    /** {@inheritDoc} */
    public void setLocation(@Nullable final String loc) {
        location = prepareForAssignment(location, loc);
    }

    /** {@inheritDoc} */
    @Nullable public String getBinding() {
        return binding;
    }

    /** {@inheritDoc} */
    public void setBinding(@Nullable final String newBinding) {
        binding = prepareForAssignment(binding, newBinding);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return null;
    }

}