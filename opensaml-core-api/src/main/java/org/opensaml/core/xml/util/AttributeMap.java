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

package org.opensaml.core.xml.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.namespace.QName;

import net.shibboleth.shared.collection.LazyMap;
import net.shibboleth.shared.collection.LazySet;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.xml.QNameSupport;

import org.opensaml.core.xml.NamespaceManager;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.slf4j.Logger;

import com.google.common.base.Strings;

/**
 * A map of attribute names and attribute values that invalidates the DOM of the attribute owning XMLObject when the
 * attributes change.
 */
@NotThreadSafe
public class AttributeMap implements Map<QName, String> {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AttributeMap.class);

    /** XMLObject owning the attributes. */
    @Nonnull private final XMLObject attributeOwner;

    /** Map of attributes. */
    @Nonnull private Map<QName, String> attributes;
    
    /** Set of attribute QNames which have been locally registered as having an ID type within this 
     * AttributeMap instance. */
    @Nonnull private Set<QName> idAttribNames;
    
    /** Set of attribute QNames which have been locally registered as having an QName value type within this 
     * AttributeMap instance. */
    @Nonnull private Set<QName> qnameAttribNames;
    
    /** Flag indicating whether an attempt should be made to infer QName values, 
     * if attribute is not registered as a QName type. */
    private boolean inferQNameValues;

    /**
     * Constructor.
     *
     * @param newOwner the XMLObject that owns these attributes
     */
    public AttributeMap(@Nonnull final XMLObject newOwner) {
        Constraint.isNotNull(newOwner, "Attribute owner XMLObject cannot be null");

        attributeOwner = newOwner;
        attributes = new LazyMap<>();
        idAttribNames = new LazySet<>();
        qnameAttribNames = new LazySet<>();
    }

    /** {@inheritDoc} */
    public String put(final QName attributeName, final String value) {
        Constraint.isNotNull(attributeName, "Attribute name cannot be null");
        assert attributeName != null;
        final String oldValue = get(attributeName);
        if (!Objects.equals(value, oldValue)) {
            releaseDOM();
            attributes.put(attributeName, value);
            if (isIDAttribute(attributeName) || XMLObjectProviderRegistrySupport.isIDAttribute(attributeName)) {
                attributeOwner.getIDIndex().deregisterIDMapping(oldValue);
                attributeOwner.getIDIndex().registerIDMapping(value, attributeOwner);
            }
            if (!Strings.isNullOrEmpty(attributeName.getNamespaceURI())) {
                if (value == null) {
                    attributeOwner.getNamespaceManager().deregisterAttributeName(attributeName);
                } else {
                    attributeOwner.getNamespaceManager().registerAttributeName(attributeName);
                }
            }
            checkAndDeregisterQNameValue(attributeName, oldValue);
            checkAndRegisterQNameValue(attributeName, value);
        }
        
        return oldValue;
    }
    
    /**
     * Set an attribute value as a QName.  This method takes care of properly registering and 
     * deregistering the namespace information associated with the new QName being added, and
     * with the old QName being possibly removed.
     * 
     * @param attributeName the attribute name
     * @param value the QName attribute value
     * @return the old attribute value, possibly null
     */
    public QName put(final QName attributeName, final QName value) {
        Constraint.isNotNull(attributeName, "Attribute name cannot be null");
        assert attributeName != null;
        final String oldValueString = get(attributeName);
        
        QName oldValue = null;
        if (!Strings.isNullOrEmpty(oldValueString)) {
            oldValue = resolveQName(oldValueString, true);
        }
        
        if (!Objects.equals(oldValue, value)) {
            releaseDOM();
            if (value != null) {
                // new value is not null, old value was either null or non-equal
                final String newStringValue = constructAttributeValue(value);
                attributes.put(attributeName, newStringValue);
                registerQNameValue(attributeName, value);
                attributeOwner.getNamespaceManager().registerAttributeName(attributeName);
            } else {
                // new value is null, old value was not null
                deregisterQNameValue(attributeName);
                attributeOwner.getNamespaceManager().deregisterAttributeName(attributeName);
            }
        }
        
        return oldValue;
    }

    /** {@inheritDoc} */
    public void clear() {
        final LazySet<QName> keys = new LazySet<>();
        keys.addAll(attributes.keySet());
        for (final QName attributeName : keys) {
            remove(attributeName);
        }
    }

    /**
     * Returns the set of keys.
     * 
     * @return unmodifiable set of keys
     */
    public Set<QName> keySet() {
        return Collections.unmodifiableSet(attributes.keySet());
    }

    /** {@inheritDoc} */
    public int size() {
        return attributes.size();
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    /** {@inheritDoc} */
    public boolean containsKey(final Object key) {
        return attributes.containsKey(key);
    }

    /** {@inheritDoc} */
    public boolean containsValue(final Object value) {
        return attributes.containsValue(value);
    }

    /** {@inheritDoc} */
    public String get(final Object key) {
        return attributes.get(key);
    }

    /** {@inheritDoc} */
    public String remove(final Object key) {
        final String removedValue = attributes.remove(key);
        if (removedValue != null) {
            releaseDOM();
            final QName attributeName = (QName) key;
            assert attributeName != null;
            if (isIDAttribute(attributeName) || XMLObjectProviderRegistrySupport.isIDAttribute(attributeName)) {
                attributeOwner.getIDIndex().deregisterIDMapping(removedValue);
            }
            attributeOwner.getNamespaceManager().deregisterAttributeName(attributeName);
            checkAndDeregisterQNameValue(attributeName, removedValue);
        }

        return removedValue;
    }

    /** {@inheritDoc} */
    public void putAll(final Map<? extends QName, ? extends String> t) {
        if (t != null && t.size() > 0) {
            for (final Entry<? extends QName, ? extends String> entry : t.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Returns the values in this map.
     * 
     * @return an unmodifiable collection of values
     */
    public Collection<String> values() {
        return Collections.unmodifiableCollection(attributes.values());
    }

    /**
     * Returns the set of entries.
     * 
     * @return unmodifiable set of entries
     */
    public Set<Entry<QName, String>> entrySet() {
        return Collections.unmodifiableSet(attributes.entrySet());
    }
    
    /**
     * Register an attribute as having a type of ID.
     * 
     * @param attributeName the QName of the ID attribute to be registered
     */
    public void registerID(@Nonnull final QName attributeName) {
        if (! idAttribNames.contains(attributeName)) {
            idAttribNames.add(attributeName);
        }
        
        // In case attribute already has a value,
        // register the current value mapping with the XMLObject owner.
        if (containsKey(attributeName)) {
            attributeOwner.getIDIndex().registerIDMapping(get(attributeName), attributeOwner);
        }
    }
    
    /**
     * Deregister an attribute as having a type of ID.
     * 
     * @param attributeName the QName of the ID attribute to be de-registered
     */
    public void deregisterID(@Nonnull final QName attributeName) {
        if (idAttribNames.contains(attributeName)) {
            idAttribNames.remove(attributeName);
        }
        
        // In case attribute already has a value,
        // deregister the current value mapping with the XMLObject owner.
        if (containsKey(attributeName)) {
            attributeOwner.getIDIndex().deregisterIDMapping(get(attributeName));
        }
    }
    
    /**
     * Check whether a given attribute is locally registered as having an ID type within
     * this AttributeMap instance.
     * 
     * @param attributeName the QName of the attribute to be checked for ID type.
     * @return true if attribute is registered as having an ID type.
     */
    public boolean isIDAttribute(@Nonnull final QName attributeName) {
        return idAttribNames.contains(attributeName);
    }
    
    /**
     * Register an attribute as having a type of QName.
     * 
     * @param attributeName the name of the QName-valued attribute to be registered
     */
    public void registerQNameAttribute(@Nonnull final QName attributeName) {
        qnameAttribNames.add(attributeName);
    }
    
    /**
     * Deregister an attribute as having a type of QName.
     * 
     * @param attributeName the name of the QName-valued attribute to be registered
     */
    public void deregisterQNameAttribute(@Nonnull final QName attributeName) {
        qnameAttribNames.remove(attributeName);
    }
    
    /**
     * Check whether a given attribute is known to have a QName type.
     * 
     * @param attributeName the QName of the attribute to be checked for QName type.
     * @return true if attribute is registered as having an QName type.
     */
    public boolean isQNameAttribute(@Nonnull final QName attributeName) {
        return qnameAttribNames.contains(attributeName);
    }
    
    /**
     * Get the flag indicating whether an attempt should be made to infer QName values, 
     * if attribute is not registered via a configuration as a QName type. Default is false.
     * 
     * @return true if QName types should be inferred, false if not
     * 
     */
    public boolean isInferQNameValues() {
        return inferQNameValues;
    }
    
    /**
     * Set the flag indicating whether an attempt should be made to infer QName values, 
     * if attribute is not registered via a configuration as a QName type. Default is false.
     * 
     * @param flag true if QName types should be inferred, false if not
     * 
     */
    public void setInferQNameValues(final boolean flag) {
        inferQNameValues = flag;
    }
    
    /**
     * Releases the DOM caching associated XMLObject and its ancestors.
     */
    private void releaseDOM() {
        attributeOwner.releaseDOM();
        attributeOwner.releaseParentDOM(true);
    }
    
    /**
     * Check whether the attribute value is a QName type, and if it is,
     * register it with the owner's namespace manger.
     * 
     * @param attributeName the attribute name
     * @param attributeValue the attribute value
     */
    private void checkAndRegisterQNameValue(@Nonnull final QName attributeName, @Nullable final String attributeValue) {
        if (attributeValue == null) {
            return;
        }
        
        final QName qnameValue = checkQName(attributeName, attributeValue);
        if (qnameValue != null) {
            log.trace("Attribute '{}' with value '{}' was evaluated to be QName type", 
                    attributeName, attributeValue);
            registerQNameValue(attributeName, qnameValue);
        } else {
            log.trace("Attribute '{}' with value '{}' was not evaluated to be QName type", 
                    attributeName, attributeValue);
        }
        
    }
    
    /**
     * Register a QName attribute value with the owner's namespace manger.
     * 
     * @param attributeName the attribute name
     * @param attributeValue the attribute value
     */
    private void registerQNameValue(@Nonnull final QName attributeName, @Nonnull final QName attributeValue) {
        
        final String attributeID = NamespaceManager.generateAttributeID(attributeName);
        log.trace("Registering QName attribute value '{}' under attibute ID '{}'",
                attributeValue, attributeID);
        attributeOwner.getNamespaceManager().registerAttributeValue(attributeID, attributeValue);
    }
    
    /**
     * Check whether the attribute value is a QName type, and if it is,
     * deregister it with the owner's namespace manger.
     * 
     * @param attributeName the attribute name
     * @param attributeValue the attribute value
     */
    private void checkAndDeregisterQNameValue(@Nonnull final QName attributeName,
            @Nullable final String attributeValue) {
        if (attributeValue == null) {
            return;
        }
        
        final QName qnameValue = checkQName(attributeName, attributeValue);
        if (qnameValue != null) {
            log.trace("Attribute '{}' with value '{}' was evaluated to be QName type", 
                    attributeName, attributeValue);
            deregisterQNameValue(attributeName);
        } else {
            log.trace("Attribute '{}' with value '{}' was not evaluated to be QName type", 
                    attributeName, attributeValue);
        }
    }
    
    /**
     * Deregister a QName attribute value with the owner's namespace manger.
     * 
     * @param attributeName the attribute name whose QName attribute value should be deregistered
     */
    private void deregisterQNameValue(@Nonnull final QName attributeName) {
        final String attributeID = NamespaceManager.generateAttributeID(attributeName);
        log.trace("Deregistering QName attribute with attibute ID '{}'", attributeID);
        attributeOwner.getNamespaceManager().deregisterAttributeValue(attributeID);
    }
    
    /**
     * Check where the attribute value is a QName type, and if so, return the QName.
     * 
     * @param attributeName the attribute name
     * @param attributeValue the attribute value
     * @return the QName if the attribute value is a QName type, otherwise null
     */
    private QName checkQName(@Nonnull final QName attributeName, @Nullable final String attributeValue) {
        log.trace("Checking whether attribute '{}' with value {} is a QName type", attributeName, attributeValue);
        
        if (attributeValue == null) {
            log.trace("Attribute value was null, returning null");
            return null;
        }
        
        if (isQNameAttribute(attributeName)) {
            log.trace("Configuration indicates attribute with name '{}' is a QName type, resolving value QName", 
                    attributeName);
            // Do support the default namespace in this scenario, since we know it should be a QName
            final QName valueName = resolveQName(attributeValue, true);
            if (valueName != null) {
                log.trace("Successfully resolved attribute value to QName: {}", valueName);
            } else {
                log.trace("Could not resolve attribute value to QName, returning null");
            }
            return valueName;
        } else if (isInferQNameValues()) {
            log.trace("Attempting to infer whether attribute value is a QName");
            // Do not support the default namespace in this scenario, since we're trying to infer.
            // Better to fail to resolve than to infer a bogus QName value.
            final QName valueName = resolveQName(attributeValue, false);
            if (valueName != null) {
                log.trace("Resolved attribute as a QName: '{}'", valueName);
            } else {
                log.trace("Attribute value was not resolveable to a QName, returning null");
            }
            return valueName;
        } else {
            log.trace("Attribute was not registered in configuration as a QName type and QName inference is disabled");
            return null;
        }

    }
    
    /**
     * Attempt to resolve the specified attribute value into a QName.
     * 
     * @param attributeValue the value to evaluate
     * @param isDefaultNSOK flag indicating whether resolution should be attempted if the prefix is null, 
     *           that is, the value is considered to be be potentially in the default XML namespace
     * 
     * @return the QName, or null if unable to resolve into a QName
     */
    private QName resolveQName(@Nullable final String attributeValue, final boolean isDefaultNSOK) {
        if (attributeValue == null) {
            return null;
        }
        log.trace("Attemtping to resolve QName from attribute value '{}'", attributeValue);
        
        // Attempt to resolve value as a QName by splitting on colon and then attempting to resolve
        // this candidate prefix into a namespace URI. 
        String candidatePrefix = null;
        String localPart = null;
        final int ci = attributeValue.indexOf(':');
        if (ci > -1) {
            candidatePrefix = attributeValue.substring(0, ci);
            log.trace("Evaluating candiate namespace prefix '{}'", candidatePrefix);
            localPart = attributeValue.substring(ci+1);
        } else {
            // No prefix - possibly evaluate as if in the default namespace
            if (isDefaultNSOK) {
                candidatePrefix = null;
                log.trace("Value did not contain a colon, evaluating as default namespace");
                localPart = attributeValue;
            } else {
                log.trace("Value did not contain a colon, default namespace is disallowed, returning null");
                return null;
            }
        }
        
        log.trace("Evaluated QName local part as '{}'", localPart);
        
        final String nsURI = XMLObjectSupport.lookupNamespaceURI(attributeOwner, candidatePrefix);
        log.trace("Resolved namespace URI '{}'", nsURI);
        if (nsURI != null) {
            final QName name = QNameSupport.constructQName(nsURI, localPart, candidatePrefix);
            log.trace("Resolved QName '{}'", name);
            return name;
        }
        log.trace("Namespace URI for candidate prefix '{}' could not be resolved", candidatePrefix);
        
        log.trace("Value was either not a QName, or namespace URI could not be resolved");
        
        return null;
    }
    
    /**
     * Construct the string representation of a QName attribute value.
     * 
     * @param attributeValue the QName to process
     * @return the attribute value string representation of the QName
     */
    @Nullable private String constructAttributeValue(@Nonnull final QName attributeValue) {
        final String trimmedLocalName = StringSupport.trimOrNull(attributeValue.getLocalPart());

        if (trimmedLocalName == null) {
            throw new IllegalArgumentException("Local name may not be null or empty");
        }

        final String qualifiedName;
        final String trimmedPrefix = StringSupport.trimOrNull(attributeValue.getPrefix());
        if (trimmedPrefix != null) {
            qualifiedName = trimmedPrefix + ":" + StringSupport.trimOrNull(trimmedLocalName);
        } else {
            qualifiedName = StringSupport.trimOrNull(trimmedLocalName);
        }
        return qualifiedName;
    }

}