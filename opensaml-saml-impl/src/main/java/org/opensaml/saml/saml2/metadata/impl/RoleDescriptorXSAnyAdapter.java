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
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
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
    public RoleDescriptorXSAnyAdapter(XSAny xsAny) {
        super(xsAny);
        getAdapted().getUnknownAttributes().registerID(new QName(RoleDescriptor.ID_ATTRIB_NAME));
    }

    /** {@inheritDoc} */
    @Override
    public String getSignatureReferenceID() {
        return getID();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSigned() {
        return getSignature() != null;
    }

    /** {@inheritDoc} */
    @Override
    public Signature getSignature() {
        final List<XMLObject> xmlObjects = getAdapted().getUnknownXMLObjects(Signature.DEFAULT_ELEMENT_NAME);
        if (xmlObjects.isEmpty()) {
            return null;
        }
        return (Signature) xmlObjects.get(0);
    }

    /** {@inheritDoc} */
    @Override
    public void setSignature(Signature newSignature) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValid() {
        final Instant validUntil = getValidUntil();

        if (null == validUntil) {
            return true;
        }
        
        return Instant.now().isBefore(validUntil);
    }

    /** {@inheritDoc} */
    @Override
    public Instant getValidUntil() {
        return DOMTypeSupport.stringToInstant(getAdapted().getUnknownAttributes().get(
                RoleDescriptor.VALID_UNTIL_ATTRIB_QNAME));
    }

    /** {@inheritDoc} */
    @Override
    public void setValidUntil(Instant validUntil) {
        getAdapted().getUnknownAttributes().put(RoleDescriptor.VALID_UNTIL_ATTRIB_QNAME,
                DOMTypeSupport.instantToString(validUntil));
    }

    /** {@inheritDoc} */
    @Override
    public Duration getCacheDuration() {
        return DOMTypeSupport.stringToDuration(getAdapted().getUnknownAttributes().get(
                RoleDescriptor.CACHE_DURATION_ATTRIB_QNAME));
    }

    /** {@inheritDoc} */
    @Override
    public void setCacheDuration(Duration duration) {
        getAdapted().getUnknownAttributes().put(RoleDescriptor.CACHE_DURATION_ATTRIB_QNAME,
                DOMTypeSupport.durationToString(duration));
    }

    /** {@inheritDoc} */
    @Override
    public AttributeMap getUnknownAttributes() {
        return getAdapted().getUnknownAttributes();
    }

    /** {@inheritDoc} */
    @Override
    public String getID() {
        return getAdapted().getUnknownAttributes().get(new QName(RoleDescriptor.ID_ATTRIB_NAME));
    }

    /** {@inheritDoc} */
    @Override
    public void setID(String newID) {
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
    @Override
    public List<String> getSupportedProtocols() {
        return CollectionSupport.copyToList(fetchSupportedProtocols());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSupportedProtocol(String protocol) {
        return fetchSupportedProtocols().contains(protocol);
    }

    /** {@inheritDoc} */
    @Override
    public void addSupportedProtocol(String protocol) {
        final List<String> protocols = fetchSupportedProtocols();
        protocols.add(protocol);
        storeSupportedProtocols(protocols);
    }

    /** {@inheritDoc} */
    @Override
    public void removeSupportedProtocol(String protocol) {
        final List<String> protocols = fetchSupportedProtocols();
        protocols.remove(protocol);
        storeSupportedProtocols(protocols);
    }

    /** {@inheritDoc} */
    @Override
    public void removeSupportedProtocols(Collection<String> protocolsToRemove) {
        final List<String> protocols = fetchSupportedProtocols();
        protocols.removeAll(protocolsToRemove);
        storeSupportedProtocols(protocols);
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllSupportedProtocols() {
        storeSupportedProtocols(Collections.emptyList());
    }

    /** {@inheritDoc} */
    @Override
    public String getErrorURL() {
        return getAdapted().getUnknownAttributes().get(new QName(RoleDescriptor.ERROR_URL_ATTRIB_NAME));
    }

    /** {@inheritDoc} */
    @Override
    public void setErrorURL(String errorURL) {
        getAdapted().getUnknownAttributes().put(new QName(RoleDescriptor.ERROR_URL_ATTRIB_NAME), errorURL);
    }

    /** {@inheritDoc} */
    @Override
    public Extensions getExtensions() {
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
    @Override
    public void setExtensions(Extensions extensions) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public List<KeyDescriptor> getKeyDescriptors() {
        final List<XMLObject> xmlObjects = getAdapted().getUnknownXMLObjects(KeyDescriptor.DEFAULT_ELEMENT_NAME);
        if (xmlObjects.isEmpty()) {
            return null;
        }
        return xmlObjects.stream()
                .filter(KeyDescriptor.class::isInstance)
                .map(KeyDescriptor.class::cast)
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    public Organization getOrganization() {
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
    @Override
    public void setOrganization(Organization organization) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public List<ContactPerson> getContactPersons() {
        final List<XMLObject> xmlObjects = getAdapted().getUnknownXMLObjects(ContactPerson.DEFAULT_ELEMENT_NAME);
        if (xmlObjects.isEmpty()) {
            return null;
        }
        return xmlObjects.stream()
                .filter(ContactPerson.class::isInstance)
                .map(ContactPerson.class::cast)
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    public List<Endpoint> getEndpoints() {
        final List<XMLObject> xmlObjects = getAdapted().getUnknownXMLObjects(Endpoint.DEFAULT_ELEMENT_NAME);
        if (xmlObjects.isEmpty()) {
            return null;
        }
        return xmlObjects.stream()
                .filter(Endpoint.class::isInstance)
                .map(Endpoint.class::cast)
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    public List<Endpoint> getEndpoints(QName type) {
        final List<XMLObject> xmlObjects = getAdapted().getUnknownXMLObjects(type);
        if (xmlObjects.isEmpty()) {
            return null;
        }
        return xmlObjects.stream()
                .filter(Endpoint.class::isInstance)
                .map(Endpoint.class::cast)
                .toList();
    }

}
