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

package org.opensaml.saml.saml2.metadata.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXSAnyAdapter;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.xmlsec.signature.Signature;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.collection.LazyList;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.xml.DOMTypeSupport;

/**
 * Component that adapts an instance of {@link XSAny} to the interface {@link RoleDescriptor}.
 */
public class RoleDescriptorXSAnyAdapter extends AbstractXSAnyAdapter implements RoleDescriptor {

    /**
     * Constructor.
     *
     * @param xsAny the instance to adapt
     */
    public RoleDescriptorXSAnyAdapter(@Nonnull final XSAny xsAny) {
        super(xsAny);
        getAdapted().getUnknownAttributes().registerID(new QName(RoleDescriptor.ID_ATTRIB_NAME));
    }

    /** {@inheritDoc} */
    @Nullable public String getSignatureReferenceID() {
        return getID();
    }

    /** {@inheritDoc} */
    public boolean isSigned() {
        return getSignature() != null;
    }

    /** {@inheritDoc} */
    @Nullable public Signature getSignature() {
        final List<XMLObject> xmlObjects = getAdapted().getUnknownXMLObjects(Signature.DEFAULT_ELEMENT_NAME);
        if (xmlObjects.isEmpty()) {
            return null;
        }
        return (Signature) xmlObjects.get(0);
    }

    /** {@inheritDoc} */
    public void setSignature(@Nullable final Signature newSignature) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public boolean isValid() {
        final Instant validUntil = getValidUntil();

        if (null == validUntil) {
            return true;
        }
        
        return Instant.now().isBefore(validUntil);
    }

    /** {@inheritDoc} */
    public Instant getValidUntil() {
        final String attr = getAdapted().getUnknownAttributes().get(RoleDescriptor.VALID_UNTIL_ATTRIB_QNAME);
        return attr != null ? DOMTypeSupport.stringToInstant(attr) : null;
    }

    /** {@inheritDoc} */
    public void setValidUntil(@Nullable final Instant validUntil) {
        if (validUntil != null) {
            getAdapted().getUnknownAttributes().put(RoleDescriptor.VALID_UNTIL_ATTRIB_QNAME,
                    DOMTypeSupport.instantToString(validUntil));
        } else {
            getAdapted().getUnknownAttributes().remove(RoleDescriptor.VALID_UNTIL_ATTRIB_QNAME);
        }
    }

    /** {@inheritDoc} */
    public Duration getCacheDuration() {
        final String attr = getAdapted().getUnknownAttributes().get(RoleDescriptor.CACHE_DURATION_ATTRIB_QNAME);
        return attr != null ? DOMTypeSupport.stringToDuration(attr) : null;
    }

    /** {@inheritDoc} */
    public void setCacheDuration(@Nullable final Duration duration) {
        if (duration != null) {
            getAdapted().getUnknownAttributes().put(RoleDescriptor.CACHE_DURATION_ATTRIB_QNAME,
                    DOMTypeSupport.durationToString(duration));
        } else {
            getAdapted().getUnknownAttributes().remove(RoleDescriptor.CACHE_DURATION_ATTRIB_QNAME);
        }
    }

    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return getAdapted().getUnknownAttributes();
    }

    /** {@inheritDoc} */
    @Nullable public String getID() {
        return getAdapted().getUnknownAttributes().get(new QName(RoleDescriptor.ID_ATTRIB_NAME));
    }

    /** {@inheritDoc} */
    public void setID(@Nullable final String newID) {
        getAdapted().getUnknownAttributes().put(new QName(RoleDescriptor.ID_ATTRIB_NAME), newID);
    }
    
    /**
     * Internal method for fetching the supported protocols as a list.
     * 
     * @return the list, possibly empty
     */
    @Nonnull private List<String> fetchSupportedProtocols() {
        final String rawValue = StringSupport.trimOrNull(getAdapted().getUnknownAttributes().get(
                new QName(RoleDescriptor.PROTOCOL_ENUMERATION_ATTRIB_NAME)));
        if (rawValue == null) {
            return new LazyList<String>();
        }
        return StringSupport.stringToList(rawValue, " ");
    }
    
    /**
     * Internal method for storing the list of supported protocols as a string.
     * 
     * @param protocols the list of protocols
     */
    private void storeSupportedProtocols(@Nonnull final List<String> protocols) {
       if (protocols.isEmpty()) {
          getAdapted().getUnknownAttributes().remove(new QName(RoleDescriptor.PROTOCOL_ENUMERATION_ATTRIB_NAME));
       } else {
           getAdapted().getUnknownAttributes().put(new QName(RoleDescriptor.PROTOCOL_ENUMERATION_ATTRIB_NAME),
                   StringSupport.listToStringValue(protocols, " "));
       }
    }

    /** {@inheritDoc} */
    @Nonnull @Unmodifiable @NotLive public List<String> getSupportedProtocols() {
        return CollectionSupport.copyToList(fetchSupportedProtocols());
    }

    /** {@inheritDoc} */
    public boolean isSupportedProtocol(@Nonnull final String protocol) {
        return fetchSupportedProtocols().contains(protocol);
    }

    /** {@inheritDoc} */
    public void addSupportedProtocol(@Nonnull final String protocol) {
        final List<String> protocols = fetchSupportedProtocols();
        protocols.add(protocol);
        storeSupportedProtocols(protocols);
    }

    /** {@inheritDoc} */
    public void removeSupportedProtocol(@Nonnull final String protocol) {
        final List<String> protocols = fetchSupportedProtocols();
        protocols.remove(protocol);
        storeSupportedProtocols(protocols);
    }

    /** {@inheritDoc} */
    public void removeSupportedProtocols(@Nonnull final Collection<String> protocolsToRemove) {
        final List<String> protocols = fetchSupportedProtocols();
        protocols.removeAll(protocolsToRemove);
        storeSupportedProtocols(protocols);
    }

    /** {@inheritDoc} */
    public void removeAllSupportedProtocols() {
        storeSupportedProtocols(CollectionSupport.emptyList());
    }

    /** {@inheritDoc} */
    @Nullable public String getErrorURL() {
        return getAdapted().getUnknownAttributes().get(new QName(RoleDescriptor.ERROR_URL_ATTRIB_NAME));
    }

    /** {@inheritDoc} */
    public void setErrorURL(@Nullable final String errorURL) {
        getAdapted().getUnknownAttributes().put(new QName(RoleDescriptor.ERROR_URL_ATTRIB_NAME), errorURL);
    }

    /** {@inheritDoc} */
    @Nullable public Extensions getExtensions() {
        final List<XMLObject> xmlObjects = getAdapted().getUnknownXMLObjects(Extensions.DEFAULT_ELEMENT_NAME);
        if (xmlObjects.isEmpty()) {
            return null;
        }

        return xmlObjects.stream()
                .filter(Extensions.class::isInstance)
                .map(Extensions.class::cast)
                .findFirst().get();
    }

    /** {@inheritDoc} */
    public void setExtensions(@Nullable final Extensions extensions) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<KeyDescriptor> getKeyDescriptors() {
        final List<XMLObject> xmlObjects = getAdapted().getUnknownXMLObjects(KeyDescriptor.DEFAULT_ELEMENT_NAME);
        // TODO: this returned list is immutable, which violates the API
        return xmlObjects.stream()
                .filter(KeyDescriptor.class::isInstance)
                .map(KeyDescriptor.class::cast)
                .toList();
    }

    /** {@inheritDoc} */
    @Nullable public Organization getOrganization() {
        final List<XMLObject> xmlObjects = getAdapted().getUnknownXMLObjects(Organization.DEFAULT_ELEMENT_NAME);
        if (xmlObjects.isEmpty()) {
            return null;
        }
        return xmlObjects.stream()
                .filter(Organization.class::isInstance)
                .map(Organization.class::cast)
                .findFirst().get();
    }

    /** {@inheritDoc} */
    public void setOrganization(@Nullable final Organization organization) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<ContactPerson> getContactPersons() {
        final List<XMLObject> xmlObjects = getAdapted().getUnknownXMLObjects(ContactPerson.DEFAULT_ELEMENT_NAME);
        // TODO: this returned list is immutable, which violates the API
        return xmlObjects.stream()
                .filter(ContactPerson.class::isInstance)
                .map(ContactPerson.class::cast)
                .toList();
    }

    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<Endpoint> getEndpoints() {
        final List<XMLObject> xmlObjects = getAdapted().getUnknownXMLObjects(Endpoint.DEFAULT_ELEMENT_NAME);
        return xmlObjects.stream()
                .filter(Endpoint.class::isInstance)
                .map(Endpoint.class::cast)
                .collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableList())).get();
    }

    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<Endpoint> getEndpoints(@Nonnull final QName type) {
        final List<XMLObject> xmlObjects = getAdapted().getUnknownXMLObjects(type);
        return xmlObjects.stream()
                .filter(Endpoint.class::isInstance)
                .map(Endpoint.class::cast)
                .collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableList())).get();
    }

}