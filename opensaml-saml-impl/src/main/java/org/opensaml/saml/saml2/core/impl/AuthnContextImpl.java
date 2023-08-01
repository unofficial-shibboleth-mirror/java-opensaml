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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml2.core.AuthenticatingAuthority;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextDecl;
import org.opensaml.saml.saml2.core.AuthnContextDeclRef;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * A concrete implemenation of {@link AuthnContext}.
 */
public class AuthnContextImpl extends AbstractXMLObject implements AuthnContext {

    /** URI of the Context Class. */
    @Nullable private AuthnContextClassRef authnContextClassRef;

    /** Declaration of the Authentication Context. */
    @Nullable private AuthnContextDecl authnContextDecl;

    /** URI of the Declaration of the Authentication Context. */
    @Nullable private AuthnContextDeclRef authnContextDeclRef;

    /** List of the Authenticating Authorities. */
    @Nonnull private final XMLObjectChildrenList<AuthenticatingAuthority> authenticatingAuthority;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AuthnContextImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        authenticatingAuthority = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public AuthnContextClassRef getAuthnContextClassRef() {
        return authnContextClassRef;
    }

    /** {@inheritDoc} */
    public void setAuthnContextClassRef(@Nullable final AuthnContextClassRef newAuthnContextClassRef) {
        authnContextClassRef = prepareForAssignment(authnContextClassRef, newAuthnContextClassRef);
    }

    /** {@inheritDoc} */
    @Nullable public AuthnContextDecl getAuthContextDecl() {
        return authnContextDecl;
    }

    /** {@inheritDoc} */
    public void setAuthnContextDecl(@Nullable final AuthnContextDecl newAuthnContextDecl) {
        authnContextDecl = prepareForAssignment(authnContextDecl, newAuthnContextDecl);
    }

    /** {@inheritDoc} */
    @Nullable public AuthnContextDeclRef getAuthnContextDeclRef() {
        return authnContextDeclRef;
    }

    /** {@inheritDoc} */
    public void setAuthnContextDeclRef(@Nullable final AuthnContextDeclRef newAuthnContextDeclRef) {
        authnContextDeclRef = prepareForAssignment(authnContextDeclRef, newAuthnContextDeclRef);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AuthenticatingAuthority> getAuthenticatingAuthorities() {
        return authenticatingAuthority;
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (authnContextClassRef != null) {
            children.add(authnContextClassRef);
        }
        
        if (authnContextDecl != null) {
            children.add(authnContextDecl);
        } 
        
        if (authnContextDeclRef != null) {
            children.add(authnContextDeclRef);
        }
        
        children.addAll(authenticatingAuthority);

        return CollectionSupport.copyToList(children);
    }
    
}