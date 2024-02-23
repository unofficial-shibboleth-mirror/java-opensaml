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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
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
 * 
 * <p>
 * If the 'known' child elements which are explicitly defined on {@link RoleDescriptor} are mutated via the
 * relevant setter or mutable collection, those changes will be synced back to the adapted {@link XSAny}
 * instance. Do not modify such children on the adapted instance directly (via {@link #getAdapted()} and
 * {@link XSAny#getUnknownXMLObjects()}). These changes can not and will not be synced back to this adapter.
 * Other child element types specific to the adapted role descriptor sub-type can and must be mutated via
 * calls against the adapted instance directly.
 * </p>
 */
public class RoleDescriptorXSAnyAdapter extends AbstractXSAnyAdapter implements RoleDescriptor {
    
    /** Set of QNames which are 'known' child element names and managed internally by this implementation. */
    private static final Set<QName> KNOWN_CHILD_ELEMENTS = CollectionSupport.setOf(
            Signature.DEFAULT_ELEMENT_NAME,
            Extensions.DEFAULT_ELEMENT_NAME,
            KeyDescriptor.DEFAULT_ELEMENT_NAME,
            Organization.DEFAULT_ELEMENT_NAME,
            ContactPerson.DEFAULT_ELEMENT_NAME
            );
    
    /** Signature child. */
    @Nullable private Signature signature;
    
    /** Extensions child. */
    @Nullable private Extensions extensions;

    /** Organization child. */
    @Nullable private Organization organization;
    
    /** KeyDescriptor children.  */
    @Nonnull private MutableChildrenList<KeyDescriptor> keyDescriptors = new MutableChildrenList<>(new ArrayList<>());

    /** ContactPerson children.  */
    @Nonnull private MutableChildrenList<ContactPerson> contactPersons = new MutableChildrenList<>(new ArrayList<>());

    /**
     * Constructor.
     *
     * @param xsAny the instance to adapt
     */
    public RoleDescriptorXSAnyAdapter(@Nonnull final XSAny xsAny) {
        super(xsAny);

        getAdapted().getUnknownAttributes().registerID(new QName(RoleDescriptor.ID_ATTRIB_NAME));
        
        // Initialize the known child element type data from the adapted instance
        signature = getAdapted().getUnknownXMLObjects().stream()
                .filter(Signature.class::isInstance)
                .map(Signature.class::cast)
                .findFirst().orElse(null);
        
        extensions = getAdapted().getUnknownXMLObjects().stream()
                .filter(Extensions.class::isInstance)
                .map(Extensions.class::cast)
                .findFirst().orElse(null);

        organization = getAdapted().getUnknownXMLObjects().stream()
                .filter(Organization.class::isInstance)
                .map(Organization.class::cast)
                .findFirst().orElse(null);
        
        keyDescriptors.addAllNoSync(getAdapted().getUnknownXMLObjects().stream()
                .filter(KeyDescriptor.class::isInstance)
                .map(KeyDescriptor.class::cast)
                .toList());

        contactPersons.addAllNoSync(getAdapted().getUnknownXMLObjects().stream()
                .filter(ContactPerson.class::isInstance)
                .map(ContactPerson.class::cast)
                .toList());
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
        return signature;
    }

    /** {@inheritDoc} */
    public void setSignature(@Nullable final Signature newSignature) {
        if (signature != newSignature) {
            signature = newSignature;
            syncChildren();
        }
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
        return extensions;
    }

    /** {@inheritDoc} */
    public void setExtensions(@Nullable final Extensions newExtensions) {
        if (extensions != newExtensions) {
            extensions = newExtensions;
            syncChildren();
        }
    }

    /** {@inheritDoc} */
    @Nullable public Organization getOrganization() {
        return organization;
    }

    /** {@inheritDoc} */
    public void setOrganization(@Nullable final Organization newOrganization) {
        if (organization != newOrganization) {
            organization = newOrganization;
            syncChildren();
        }
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<KeyDescriptor> getKeyDescriptors() {
        return keyDescriptors;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<ContactPerson> getContactPersons() {
        return contactPersons;
    }

    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<Endpoint> getEndpoints() {
        //Note that this can and will only return Endpoints which have existing XMLObject support
        return getAdapted().getUnknownXMLObjects().stream()
                .filter(Endpoint.class::isInstance)
                .map(Endpoint.class::cast)
                .collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableList())).get();
    }

    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<Endpoint> getEndpoints(@Nonnull final QName type) {
        //Note that this can and will only return Endpoints which have existing XMLObject support
        return getEndpoints().stream()
                .filter(t -> type.equals(t.getElementQName()) || type.equals(t.getSchemaType()))
                .collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableList())).get();
    }
    
    /**
     * Synchronize the instance's local child element storage back to the adapted instance of {@link XSAny}.
     */
    private void syncChildren() {
        List<XMLObject> children = new LinkedList<>();
        
        if (getSignature() != null) {
            children.add(getSignature());
        }
        if (getExtensions() != null) {
            children.add(getExtensions());
        }
        if (!getKeyDescriptors().isEmpty()) {
            children.addAll(getKeyDescriptors());
        }
        if (getOrganization() != null) {
            children.add(getOrganization());
        }
        if (!getContactPersons().isEmpty()) {
            children.addAll(getContactPersons());
        }

        // These are the children that are not 'known' by the base role descriptor and are therefore
        // presumably part of the sub-type data model. Since RoleDescriptor uses a <sequence>, these
        // will always come after the 'known' child types. We just leave these stored in the adapted
        // instance's child list and preserve here on a sync op.
        children.addAll(
                getAdapted().getUnknownXMLObjects().stream()
                .filter(Objects::nonNull)
                .filter(t -> ! KNOWN_CHILD_ELEMENTS.contains(t.getElementQName()))
                .toList());
        
        getAdapted().getUnknownXMLObjects().clear();
        getAdapted().getUnknownXMLObjects().addAll(children);
    }
    
    /**
     *
     * Array implementation which causes all XMLObject children of the owning instance to be synced back to the
     * underlying adapted {@link XSAny} on any list mutation operations.
     * 
     * @param <T> the type of the list
     */
    private class MutableChildrenList<T extends XMLObject> implements List<T> {
        
        /** Internal storage for the list. */
        @Nonnull private List<T> storage;

        /**
         * Constructor.
         *
         * @param list the backing storage for the list
         */
        public MutableChildrenList(@Nonnull final List<T> list) {
            storage = list;
        }

        /** {@inheritDoc} */
        @Override
        public T set(final int index, final T element) {
            T result = storage.set(index, element);
            syncChildren();
            return result;
        }

        /** {@inheritDoc} */
        @Override
        public boolean add(final T e) {
            boolean result = storage.add(e);
            syncChildren();
            return result;
        }

        /** {@inheritDoc} */
        @Override
        public void add(final int index, final T element) {
            storage.add(index, element);
            syncChildren();
        }

        /** {@inheritDoc} */
        @Override
        public T remove(final int index) {
            T result = storage.remove(index);
            syncChildren();
            return result;
        }

        /** {@inheritDoc} */
        @Override
        public boolean remove(final Object o) {
            boolean result = storage.remove(o);
            syncChildren();
            return result;
        }

        /** {@inheritDoc} */
        @Override
        public void clear() {
            storage.clear();
            syncChildren();
        }

        /** {@inheritDoc} */
        @Override
        public boolean addAll(final Collection<? extends T> c) {
            boolean result = storage.addAll(c);
            syncChildren();
            return result;
        }

        /** {@inheritDoc} */
        @Override
        public boolean addAll(final int index, final Collection<? extends T> c) {
            boolean result = storage.addAll(index, c);
            syncChildren();
            return result;
        }

        /** {@inheritDoc} */
        @Override
        public boolean removeAll(final Collection<?> c) {
            boolean result = storage.removeAll(c);
            syncChildren();
            return result;
        }

        /** {@inheritDoc} */
        @Override
        public boolean retainAll(final Collection<?> c) {
            boolean result = storage.retainAll(c);
            syncChildren();
            return result;
        }

        /** {@inheritDoc} */
        @Override
        public boolean removeIf(final Predicate<? super T> filter) {
            boolean result = storage.removeIf(filter);
            syncChildren();
            return result;
        }

        /** {@inheritDoc} */
        @Override
        public void replaceAll(final UnaryOperator<T> operator) {
            storage.replaceAll(operator);
            syncChildren();
        }
        
        /** {@inheritDoc} */
        @Override
        public int size() {
            return storage.size();
        }

        /** {@inheritDoc} */
        @Override
        public boolean isEmpty() {
            return storage.isEmpty();
        }

        /** {@inheritDoc} */
        @Override
        public boolean contains(final Object o) {
            return storage.contains(o);
        }

        /** {@inheritDoc} */
        @Override
        public Object[] toArray() {
            return storage.toArray();
        }

        /** {@inheritDoc} */
        @Override
        public <T> T[] toArray(final T[] a) {
            return storage.toArray(a);
        }

        /** {@inheritDoc} */
        @Override
        public boolean containsAll(final Collection<?> c) {
            return storage.containsAll(c);
        }

        /** {@inheritDoc} */
        @Override
        public T get(final int index) {
            return storage.get(index);
        }

        /** {@inheritDoc} */
        @Override
        public int indexOf(final Object o) {
            return storage.indexOf(o);
        }

        /** {@inheritDoc} */
        @Override
        public int lastIndexOf(final Object o) {
            return storage.lastIndexOf(o);
        }

        /** {@inheritDoc} */
        @Override
        public Iterator<T> iterator() {
            return new MutableChildrenIterator<>(storage.iterator());
        }

        /** {@inheritDoc} */
        @Override
        public ListIterator<T> listIterator() {
            return new MutableChildrenListIterator<>(storage.listIterator());
        }

        /** {@inheritDoc} */
        @Override
        public ListIterator<T> listIterator(final int index) {
            return new MutableChildrenListIterator<>(storage.listIterator(index));
        }

        /** {@inheritDoc} */
        @Override
        public List<T> subList(final int fromIndex, final int toIndex) {
            return new MutableChildrenList<>(storage.subList(fromIndex, toIndex));
        }

        /**
         * Same as {@link #addAll(Collection)}, except do not sync back to adapted instance.
         * 
         * @param c collection containing elements to be added to this list
         * 
         * @return true if this list changed as a result of the call
         */
        private boolean addAllNoSync(final Collection<? extends T> c) {
            return storage.addAll(c);
        }
        
        /**
         * Iterator for mutable children which disallows removal.
         * 
         * @param <E> the type of the iterator
         */
        private class MutableChildrenIterator<E> implements Iterator<E> {
            
            /** The wrapped iterator instance. */
            @Nonnull private Iterator<E> wrapped;

            /**
             * Constructor.
             *
             * @param iter the wrapped iterator
             */
            public MutableChildrenIterator(@Nonnull final Iterator<E> iter) {
                wrapped = iter;
            }

            /** {@inheritDoc} */
            @Override
            public boolean hasNext() {
                return wrapped.hasNext();
            }

            /** {@inheritDoc} */
            @Override
            public E next() {
                return wrapped.next();
            }

            /** {@inheritDoc} */
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

            /** {@inheritDoc} */
            @Override
            public void forEachRemaining(final Consumer<? super E> action) {
                throw new UnsupportedOperationException("forEachRemaining");
            }
            
        }
        
        /**
         * ListIterator for mutable children which disallows removal.
         * 
         * @param <E> the type of the iterator
         */
        private class MutableChildrenListIterator<E> implements ListIterator<E> {
            
            /** The wrapper iterator. */
            @Nonnull private ListIterator<E> wrapped;

            /**
             * Constructor.
             *
             * @param iter the wrapped iterator
             */
            public MutableChildrenListIterator(@Nonnull final ListIterator<E> iter) {
                wrapped = iter;
            }

            /** {@inheritDoc} */
            @Override
            public boolean hasNext() {
                return wrapped.hasNext();
            }

            /** {@inheritDoc} */
            @Override
            public E next() {
                return wrapped.next();
            }

            /** {@inheritDoc} */
            @Override
            public boolean hasPrevious() {
                return wrapped.hasPrevious();
            }

            /** {@inheritDoc} */
            @Override
            public E previous() {
                return wrapped.previous();
            }

            /** {@inheritDoc} */
            @Override
            public int nextIndex() {
                return wrapped.nextIndex();
            }

            /** {@inheritDoc} */
            @Override
            public int previousIndex() {
                return wrapped.previousIndex();
            }

            /** {@inheritDoc} */
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

            /** {@inheritDoc} */
            @Override
            public void set(final E e) {
                throw new UnsupportedOperationException("set");
            }

            /** {@inheritDoc} */
            @Override
            public void add(final E e) {
                throw new UnsupportedOperationException("add");
            }
            
        }

    }

}