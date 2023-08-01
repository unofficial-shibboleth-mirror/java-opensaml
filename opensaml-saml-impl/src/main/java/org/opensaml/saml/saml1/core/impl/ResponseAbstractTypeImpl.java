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

import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSignableSAMLObject;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.ResponseAbstractType;
import org.opensaml.xmlsec.signature.Signature;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Abstract implementation of {@link ResponseAbstractType} Object.
 */
public abstract class ResponseAbstractTypeImpl extends AbstractSignableSAMLObject implements ResponseAbstractType {

    /** Contains the ID. */
    @Nullable private String id;

    /** Message version. */
    @Nullable private SAMLVersion version;

    /** Contents of the InResponseTo attribute. */
    @Nullable private String inResponseTo;

    /** Contents of the IssueInstant attribute. */
    @Nullable private Instant issueInstant;

    /** Contents of the Recipient attribute. */
    @Nullable private String recipient;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected ResponseAbstractTypeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        version = SAMLVersion.VERSION_11;
    }

    /** {@inheritDoc} */
    @Nullable public String getID() {
        return id;
    }

    /** {@inheritDoc} */
    public void setID(@Nullable final String newID) {
        final String oldID = id;
        id = prepareForAssignment(id, newID);
        registerOwnID(oldID, id);
    }

    /** {@inheritDoc} */
    @Nullable public String getInResponseTo() {
        return inResponseTo;
    }

    /** {@inheritDoc} */
    public void setInResponseTo(@Nullable final String to) {
        inResponseTo = prepareForAssignment(inResponseTo, to);
    }

    /** {@inheritDoc} */
    @Nullable public SAMLVersion getVersion() {
        return version;
    }
    
    /** {@inheritDoc} */
    public void setVersion(@Nullable final SAMLVersion newVersion) {
        version = prepareForAssignment(version, newVersion);
    }

    /** {@inheritDoc} */
    @Nullable public Instant getIssueInstant() {
        return issueInstant;
    }

    /** {@inheritDoc} */
    public void setIssueInstant(@Nullable final Instant date) {
        issueInstant = prepareForAssignment(issueInstant, date);
    }

    /** {@inheritDoc} */
    @Nullable public String getRecipient() {
        return recipient;
    }

    /** {@inheritDoc} */
    public void setRecipient(@Nullable final String recip) {
        recipient = prepareForAssignment(recipient, recip);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getSignatureReferenceID(){
        return id;
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        
        final Signature sig = getSignature();
        if (sig != null) {
            return CollectionSupport.singletonList(sig);
        }
        
        return null;
    }

}