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

package org.opensaml.saml.common.binding.artifact.impl;

import java.io.IOException;
import java.io.StringReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLRuntimeException;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.artifact.BasicSAMLArtifactMapEntry;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap.SAMLArtifactMapEntry;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap.SAMLArtifactMapEntryFactory;
import org.opensaml.storage.StorageSerializer;
import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.AbstractInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.xml.ElementSupport;
import net.shibboleth.shared.xml.ParserPool;
import net.shibboleth.shared.xml.QNameSupport;
import net.shibboleth.shared.xml.SerializeSupport;
import net.shibboleth.shared.xml.XMLParserException;

/**
 * A factory and {@link StorageSerializer} for instances of {@link BasicSAMLArtifactMapEntry}.
 * 
 * <p>This implements serialization of an entry by wrapping the XML-based message
 * in a parent element that tracks the additional associated data.</p>
 */
public class StorageServiceSAMLArtifactMapEntryFactory extends AbstractInitializableComponent
        implements SAMLArtifactMapEntryFactory, StorageSerializer<SAMLArtifactMapEntry> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(StorageServiceSAMLArtifactMapEntryFactory.class);

    /** XML parsing component. */
    @Nonnull private ParserPool parserPool;
    
    /** Constructor. */
    public StorageServiceSAMLArtifactMapEntryFactory() {
        parserPool = Constraint.isNotNull(XMLObjectProviderRegistrySupport.getParserPool(),
                "Default ParserPool was null");
    }

    /**
     * Gets the parser pool used to parse serialized data.
     * 
     * @return parser pool used to parse serialized data
     */
    @Nonnull public ParserPool getParserPool() {
        return parserPool;
    }

    /**
     * Sets the parser pool used to parse serialized data.
     * 
     * @param pool parser pool used to parse serialized data
     */
    public void setParserPool(@Nonnull final ParserPool pool) {
        checkSetterPreconditions();
        
        parserPool = Constraint.isNotNull(pool, "ParserPool cannot be null");
    }
    
    
    /** {@inheritDoc} */
    @Nonnull public SAMLArtifactMapEntry newEntry(@Nonnull @NotEmpty final String artifact,
            @Nonnull @NotEmpty final String issuerId, @Nonnull @NotEmpty final String relyingPartyId,
            @Nonnull final SAMLObject samlMessage) {
        
        try {
            return new BasicSAMLArtifactMapEntry(artifact, issuerId, relyingPartyId, samlMessage);
        } catch (final MarshallingException | UnmarshallingException e) {
            throw new XMLRuntimeException("Error creating BasicSAMLArtifactMapEntry", e);
        }
    }
    
    /** {@inheritDoc} */
    @Nonnull public String serialize(@Nonnull final SAMLArtifactMapEntry instance) throws IOException {        
        log.debug("Serializing SAMLArtifactMapEntry for storage");
        
        final Element marshalledMessage;
        try {
            marshalledMessage = XMLObjectSupport.marshall(instance.getSamlMessage());
        } catch (final MarshallingException e) {
            throw new IOException("Error marshalling SAML message", e);
        }
        
        final Element rootElement = marshalledMessage.getOwnerDocument().createElementNS(null, "Mapping");
        rootElement.setAttributeNS(null, "issuer", instance.getIssuerId());
        rootElement.setAttributeNS(null, "relyingParty", instance.getRelyingPartyId());
        rootElement.appendChild(marshalledMessage);
        
        final String serializedMessage = SerializeSupport.nodeToString(rootElement);
        
        if (log.isTraceEnabled()) {
            log.trace("Serialized SAMLArtifactMapEntry data is:");
            log.trace(serializedMessage);
        }
        
        return serializedMessage;
    }

    /** {@inheritDoc} */
    // Checkstyle: CyclomaticComplexity OFF
    @Nonnull public SAMLArtifactMapEntry deserialize(final long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nonnull @NotEmpty final String value, @Nullable final Long expiration)
                    throws IOException {
        log.debug("Deserializing artifact mapping data from stored string");

        if (log.isTraceEnabled()) {
            log.trace("Serialized SAMLArtifactMapEntry data is:");
            log.trace(value);
        }
        
        try {
            Element rootElement = null;
            try (final StringReader sr = new StringReader(value)) {
                rootElement = getParserPool().parse(sr).getDocumentElement();
            }
            final Node messageElement = rootElement.getFirstChild();
            if (!ElementSupport.isElementNamed(rootElement, null, "Mapping")) {
                throw new IOException("SAMLArtifactMapEntry XML not rooted by expected element");
            } else if (messageElement == null || messageElement.getNodeType() != Node.ELEMENT_NODE) {
                throw new IOException("SAMLArtifactMapEntry XML missing child element");
            }
            
            final String issuer = rootElement.getAttributeNS(null, "issuer");
            final String relyingParty = rootElement.getAttributeNS(null, "relyingParty");
            if (issuer == null || relyingParty == null) {
                throw new IOException("SAMLArtifactMapEntry XML missing issuer or relyingParty attributes");
            }
            
            final Unmarshaller unmarshaller = XMLObjectSupport.getUnmarshaller((Element) messageElement);
            if (unmarshaller == null) {
                throw new UnmarshallingException("Unable to obtain unmarshaller for element "
                        + QNameSupport.getNodeQName(rootElement.getFirstChild()));
            }
            
            final XMLObject message = unmarshaller.unmarshall((Element) rootElement.removeChild(messageElement));
            rootElement.getOwnerDocument().replaceChild(messageElement, rootElement);
            if (!(message instanceof SAMLObject)) {
                throw new IOException("SAMLArtifactMapEntry's XMLObject was not a SAML message");
            }
            
            return newEntry(key, issuer, relyingParty, (SAMLObject) message);
        } catch (final XMLParserException e) {
            throw new IOException("Error parsing XML into DOM", e);
        } catch (final UnmarshallingException e) {
            throw new IOException("Error unmarshalling DOM into SAMLObject", e);
        }
    }
    // Checkstyle: CyclomaticComplexity ON

}