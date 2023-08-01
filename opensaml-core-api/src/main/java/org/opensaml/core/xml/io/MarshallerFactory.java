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

package org.opensaml.core.xml.io;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLRuntimeException;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * This thread-safe factory creates {@link org.opensaml.core.xml.io.Marshaller}s that can be used to convert
 * {@link org.opensaml.core.xml.XMLObject}s into W3C DOM elements. Marshallers are stored and retrieved by a
 * {@link javax.xml.namespace.QName} key. This key is either the XML Schema Type or element QName of the XML element the
 * XMLObject is marshalled into.
 */
public class MarshallerFactory {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(MarshallerFactory.class);

    /** Map of marshallers to the elements they are for. */
    @Nonnull private final Map<QName, Marshaller> marshallers;

    /**
     * Constructor.
     */
    public MarshallerFactory() {
        marshallers = new ConcurrentHashMap<>();
    }

    /**
     * Gets the Marshaller for a particular element or null if no marshaller is registered for an element.
     * 
     * @param key the key the marshaller was registered under
     * 
     * @return the Marshaller or null
     */
    @Nullable public Marshaller getMarshaller(@Nonnull final QName key) {
        return marshallers.get(key);
    }

    /**
     * Retrieves the marshaller for the given XMLObject. The schema type, if present, is tried first as the key with the
     * element QName used if no schema type is present or does not have a marshaller registered under it.
     * 
     * @param xmlObject the XMLObject to retrieve the marshaller for
     * 
     * @return the marshaller that can be used for the given XMLObject
     */
    @Nullable public Marshaller getMarshaller(@Nonnull final XMLObject xmlObject) {
        Marshaller marshaller = null;

        final QName xsitype = xmlObject.getSchemaType();
        if (xsitype != null) {
            marshaller = getMarshaller(xsitype);
        }

        if (marshaller == null) {
            marshaller = getMarshaller(xmlObject.getElementQName());
        }

        return marshaller;
    }

    /**
     * Call {@link #getMarshaller(QName)} and raise an exception if no marshaller is registered.
     * 
     * @param key type of marshaller to fetch
     * 
     * @return the registered marshaller
     * 
     * @throws XMLRuntimeException if no marshaller is registered
     * 
     * @since 5.0.0
     */
    @Nonnull public Marshaller ensureMarshaller(@Nonnull final QName key) {
        final Marshaller m = getMarshaller(key);
        if (m != null) {
            return m;
        }
        throw new XMLRuntimeException("Unable to obtain marshaller for " + key.toString());
    }

    /**
     * Call {@link #getMarshaller(XMLObject)} and raise an exception if no marshaller is registered.
     * 
     * @param xmlObject type of marshaller to fetch
     * 
     * @return the registered marshaller
     * 
     * @throws XMLRuntimeException if no marshaller is registered
     * 
     * @since 5.0.0
     */
    @Nonnull public Marshaller ensureMarshaller(@Nonnull final XMLObject xmlObject) {
        final Marshaller m = getMarshaller(xmlObject);
        if (m != null) {
            return m;
        }
        throw new XMLRuntimeException("Unable to obtain marshaller for " + xmlObject.getClass().getName());
    }

    /**
     * Gets an immutable listing of all the Marshallers currently registered.
     * 
     * @return a listing of all the Marshallers currently registered
     */
    @Nonnull @Unmodifiable @NotLive public Map<QName, Marshaller> getMarshallers() {
        return CollectionSupport.copyToMap(marshallers);
    }

    /**
     * Registers a Marshaller with this factory. If a Marshaller exist for the element name given it is replaced with
     * the given marshaller.
     * 
     * @param key the key the marshaller was registered under
     * @param marshaller the Marshaller
     */
    public void registerMarshaller(@Nonnull final QName key, @Nonnull final Marshaller marshaller) {
        Constraint.isNotNull(key, "Marshaller key cannot be null");
        Constraint.isNotNull(marshaller, "Marshaller cannot be null");
        log.debug("Registering marshaller, {}, for object type {}", marshaller.getClass().getName(), key);

        marshallers.put(key, marshaller);
    }

    /**
     * Deregisters the marshaller for the given element.
     * 
     * @param key the key the marshaller was registered under
     * 
     * @return the Marshaller previously registered or null
     */
    @Nullable public Marshaller deregisterMarshaller(@Nonnull final QName key) {
        log.debug("Deregistering marshaller for object type {}", key);
        return marshallers.remove(key);
    }
    
}