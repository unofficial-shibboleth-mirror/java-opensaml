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

/**
 * 
 */

package org.opensaml.saml.saml2.core.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.SubjectLocality;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * A concrete implementation of {@link AuthnStatement}.
 */
public class AuthnStatementImpl extends AbstractXMLObject implements AuthnStatement {

    /** Subject Locality of the Authentication Statement. */
    @Nullable private SubjectLocality subjectLocality;

    /** Authentication Context of the Authentication Statement. */
    @Nullable private AuthnContext authnContext;

    /** Time of the authentication. */
    @Nullable private Instant authnInstant;

    /** Index of the session. */
    @Nullable private String sessionIndex;

    /** Time at which the session ends. */
    @Nullable private Instant sessionNotOnOrAfter;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AuthnStatementImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public SubjectLocality getSubjectLocality() {
        return subjectLocality;
    }

    /** {@inheritDoc} */
    public void setSubjectLocality(@Nullable final SubjectLocality newSubjectLocality) {
        subjectLocality = prepareForAssignment(subjectLocality, newSubjectLocality);
    }

    /** {@inheritDoc} */
    @Nullable public AuthnContext getAuthnContext() {
        return authnContext;
    }

    /** {@inheritDoc} */
    public void setAuthnContext(@Nullable final AuthnContext newAuthnContext) {
        authnContext = prepareForAssignment(authnContext, newAuthnContext);
    }

    /** {@inheritDoc} */
    @Nullable public Instant getAuthnInstant() {
        return authnInstant;
    }

    /** {@inheritDoc} */
    public void setAuthnInstant(@Nullable final Instant newAuthnInstant) {
        authnInstant = prepareForAssignment(authnInstant, newAuthnInstant);
    }

    /** {@inheritDoc} */
    @Nullable public String getSessionIndex() {
        return sessionIndex;
    }

    /** {@inheritDoc} */
    public void setSessionIndex(@Nullable final String newSessionIndex) {
        sessionIndex = prepareForAssignment(sessionIndex, newSessionIndex);
    }

    /** {@inheritDoc} */
    @Nullable public Instant getSessionNotOnOrAfter() {
        return sessionNotOnOrAfter;
    }

    /** {@inheritDoc} */
    public void setSessionNotOnOrAfter(@Nullable final Instant newSessionNotOnOrAfter) {
        sessionNotOnOrAfter = prepareForAssignment(sessionNotOnOrAfter, newSessionNotOnOrAfter);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (subjectLocality != null) {
            children.add(subjectLocality);
        }
        
        if (authnContext != null) {
            children.add(authnContext);
        }
        
        return CollectionSupport.copyToList(children);
    }
    
}