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

package org.opensaml.core.xml;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.collection.LazyMap;
import net.shibboleth.shared.collection.LazySet;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.xml.XMLConstants;

import com.google.common.base.Strings;

/**
 * A class which is responsible for managing XML namespace-related data for an {@link XMLObject}.
 * 
 * <p>
 * Code which mutates the state of an XMLObject such that XML namespace-related data is also logically changed,
 * should call the appropriate method, based on the type of change being made.
 * </p>
 */
public class NamespaceManager {
    
    /** The token used to represent the default namespace in {@link #getNonVisibleNamespacePrefixes()}. */
    @Nonnull @NotEmpty public static final String DEFAULT_NS_TOKEN = "#default";
    
    /** The 'xml' namespace. */
    @Nonnull private static final Namespace XML_NAMESPACE = 
        new Namespace(XMLConstants.XML_NS, XMLConstants.XML_PREFIX);
    
    /** The 'xsi' namespace. */
    @Nonnull private static final Namespace XSI_NAMESPACE = 
        new Namespace(XMLConstants.XSI_NS, XMLConstants.XSI_PREFIX);
    
    /** The owning XMLObject. */
    @Nonnull private final XMLObject owner;
    
    /** XMLObject name namespace. */
    @Nullable private Namespace elementName;
    
    /** XMLObject type namespace. */
    @Nullable private Namespace elementType;
    
    /** Explicitly declared namespaces. */
    @Nonnull private final Set<Namespace> decls;
    
    /** Registered namespaces of attribute names. */
    @Nonnull private final Set<Namespace> attrNames;
    
    /** Registered namespaces of attribute values. */
    @Nonnull private final Map<String, Namespace> attrValues;
    
    /** Registered namespaces of content values. */
    @Nullable private Namespace contentValue;
    
    /**
     * Constructor.
     *
     * @param owningObject the XMLObject whose namespace info is to be managed
     */
    public NamespaceManager(@Nonnull final XMLObject owningObject) {
        owner = Constraint.isNotNull(owningObject, "Owner XMLObject cannot be null");
        
        decls = new LazySet<>();
        attrNames = new LazySet<>();
        attrValues = new LazyMap<>();
    }
    
    /**
     * From a QName representing a qualified attribute name, generate an attribute ID
     * suitable for use in {@link #registerAttributeValue(String, QName)} 
     * and {@link #deregisterAttributeValue(String)}.
     * 
     * @param name attribute name as a QName
     * @return a string attribute ID
     */
    @SuppressWarnings("null")
    @Nonnull @NotEmpty public static String generateAttributeID(@Nonnull final QName name) {
       return name.toString(); 
    }
    
    /**
     * Get the owning XMLObject instance.
     * 
     * @return the owning XMLObject
     */
    @Nonnull public XMLObject getOwner() {
        return owner;
    }
    
    /**
     * Get the set of namespaces currently in use on the owning XMLObject.
     * 
     * @return the unmodifiable set of namespaces
     */
    @Nonnull @Unmodifiable @NotLive public Set<Namespace> getNamespaces() {
        final Set<Namespace> namespaces = mergeNamespaceCollections(decls, attrNames, attrValues.values());
        addNamespace(namespaces, getElementNameNamespace());
        addNamespace(namespaces, getElementTypeNamespace());
        addNamespace(namespaces, contentValue);
        return CollectionSupport.copyToSet(namespaces);
    }
    
    /**
     * Register a namespace declaration.
     * 
     * @param namespace the namespace to register
     */
    public void registerNamespaceDeclaration(@Nonnull final Namespace namespace) {
        addNamespace(decls, namespace);
    }
    
    /**
     * Deregister a namespace declaration.
     * 
     * @param namespace the namespace to deregister
     */
    public void deregisterNamespaceDeclaration(@Nonnull final Namespace namespace) {
        removeNamespace(decls, namespace);
    }
    
    /**
     * Get the set of namespace declarations registered on the owning XMLObject.
     * 
     * @return the set of namespace declarations
     */
    @Nonnull @Unmodifiable @NotLive public Set<Namespace> getNamespaceDeclarations() {
        return CollectionSupport.copyToSet(decls);
    }
    
    /**
     * Register a namespace-qualified attribute name.
     * 
     * @param attributeName the attribute name to register
     */
    public void registerAttributeName(@Nonnull final QName attributeName) {
        if (checkQName(attributeName)) {
            addNamespace(attrNames, buildNamespace(attributeName));
        }
    }
    
    /**
     * Deregister a namespace-qualified attribute name.
     * 
     * @param attributeName the attribute name to deregister
     */
    public void deregisterAttributeName(@Nonnull final QName attributeName) {
        if (checkQName(attributeName)) {
            removeNamespace(attrNames, buildNamespace(attributeName));
        }
    }
    
    /**
     * Register a QName attribute value.
     * 
     * @param attributeID unique identifier for the attribute within the XMLObject's content model
     * @param attributeValue the QName value to register
     */
    public void registerAttributeValue(@Nonnull final String attributeID, @Nonnull final QName attributeValue) {
        if (checkQName(attributeValue)) {
            attrValues.put(attributeID, buildNamespace(attributeValue));
        }
    }
    
    /**
     * Deregister a QName attribute value.
     * 
     * @param attributeID unique identifier for the attribute within the XMLObject's content model
     */
    public void deregisterAttributeValue(@Nonnull final String attributeID) {
        attrValues.remove(attributeID);
    }
    
    /**
     * Register a QName element content value.
     * 
     * @param content the QName value to register
     */
    public void registerContentValue(@Nonnull final QName content) {
        if (checkQName(content)) {
            contentValue = buildNamespace(content);
        }
    }
    
    /**
     * Deregister a QName content value.
     * 
     */
    public void deregisterContentValue() {
        contentValue = null;
    }
    
    /**
     * Obtain the set of namespace prefixes used in a non-visible manner on owning XMLObject
     * and its children.
     * 
     * <p>
     * The primary use case for this information is to support the inclusive prefixes
     * information that may optionally be supplied as a part of XML exclusive canonicalization.
     * </p>
     * 
     * @return the set of non-visibly used namespace prefixes
     */
    @Nonnull @Unmodifiable @NotLive public Set<String> getNonVisibleNamespacePrefixes() {
        final LazySet<String> prefixes = new LazySet<>();
        addPrefixes(prefixes, getNonVisibleNamespaces());
        return prefixes;
    }
    
    /**
     * Obtain the set of namespaces used in a non-visible manner on owning XMLObject
     * and its children.
     * 
     * <p>
     * The primary use case for this information is to support the inclusive prefixes
     * information that may optionally be supplied as a part of XML exclusive canonicalization.
     * </p>
     * 
     * @return the set of non-visibly used namespaces 
     */
    @Nonnull @Unmodifiable @NotLive public Set<Namespace> getNonVisibleNamespaces() {
        final LazySet<Namespace> nonVisibleCandidates = new LazySet<>();

        // Collect each child's non-visible namespaces
        final List<XMLObject> children = getOwner().getOrderedChildren();
        if (children != null) {
            for(final XMLObject child : children) {
                if (child != null) {
                    final Set<Namespace> childNonVisibleNamespaces =
                            child.getNamespaceManager().getNonVisibleNamespaces();
                    if (!childNonVisibleNamespaces.isEmpty()) {
                        nonVisibleCandidates.addAll(childNonVisibleNamespaces);
                    }
                }
            }
        }

        // Collect this node's non-visible candidate namespaces
        nonVisibleCandidates.addAll(getNonVisibleNamespaceCandidates());

        // Now subtract this object's visible namespaces
        nonVisibleCandidates.removeAll(getVisibleNamespaces());
        
        // As a special case, never return the 'xml' prefix.
        nonVisibleCandidates.remove(XML_NAMESPACE);

        // What remains is the effective set of non-visible namespaces
        // for the subtree rooted at this node.
        return nonVisibleCandidates;

    }
    
    /**
     * Get the set of all namespaces which are in scope within the subtree rooted
     * at the owning XMLObject.
     * 
     * @return set of all namespaces in scope for the owning object
     */
    @Nonnull @Unmodifiable @NotLive public Set<Namespace> getAllNamespacesInSubtreeScope() {
        final LazySet<Namespace> namespaces = new LazySet<>();

        // Collect namespaces for the subtree rooted at each child
        final List<XMLObject> children = getOwner().getOrderedChildren();
        if (children != null) {
            for (final XMLObject child : children) {
                // TODO: This check isn't necessary by spec, but we have XACML code still including null elements.
                if (child != null) {
                    final Set<Namespace> childNamespaces = child.getNamespaceManager().getAllNamespacesInSubtreeScope();
                    if (!childNamespaces.isEmpty()) {
                        namespaces.addAll(childNamespaces);
                    }
                }
            }
        }

        // Collect this node's namespaces.
        for (final Namespace myNS : getNamespaces()) {
            namespaces.add(myNS);
        }

        return namespaces;
    }
    
    /**
     * Register the owning XMLObject's element name.
     * 
     * @param name the element name to register
     */
    public void registerElementName(@Nonnull final QName name) {
        if (checkQName(name)) {
            elementName = buildNamespace(name);
        }
    }

    /**
     * Register the owning XMLObject's element type, if explicitly declared via an xsi:type.
     * 
     * @param type the element type to register
     */
    public void registerElementType(@Nullable final QName type) {
        if (type != null) {
            if (checkQName(type)) {
                elementType = buildNamespace(type);
            }
        } else {
            elementType = null;
        }
    }
    
    /**
     * Return a Namespace instance representing the namespace of the element name.
     * 
     * @return the element name's namespace
     */
    @Nullable private Namespace getElementNameNamespace() {
        if (elementName == null && checkQName(owner.getElementQName())) {
            elementName = buildNamespace(owner.getElementQName());
        }
        return elementName;
    }

    /**
     * Return a Namespace instance representing the namespace of the element type, if known.
     * 
     * @return the element type's namespace
     */
    @Nullable private Namespace getElementTypeNamespace() {
        if (elementType == null) {
            final QName type = owner.getSchemaType();
            if (type != null && checkQName(type)) {
                elementType = buildNamespace(type);
            }
        }
        return elementType;
    }
    
    /**
     * Build a {@link Namespace} instance from a {@link QName}.
     * 
     * @param name the source QName 
     * @return a Namespace built using the information in the QName
     */
    @Nonnull private Namespace buildNamespace(@Nonnull final QName name) {
        Constraint.isNotNull(name, "QName cannot be null");
        final String uri = Constraint.isNotNull(StringSupport.trimOrNull(name.getNamespaceURI()),
                "Namespace URI of QName cannot be null");
        final String prefix = StringSupport.trimOrNull(name.getPrefix());
        return new Namespace(uri, prefix);
    }
    
    /**
     * Add a Namespace to a set of Namespaces.  Namespaces with identical URI and prefix will be treated as equivalent.
     * 
     * @param namespaces the set of namespaces
     * @param newNamespace the namespace to add to the set
     */
    private void addNamespace(@Nonnull @Live final Set<Namespace> namespaces, @Nullable final Namespace newNamespace) {
        if (newNamespace == null) {
            return;
        }
        
        namespaces.add(newNamespace);
    }
    
    /**
     * Remove a Namespace from a set of Namespaces.
     * 
     * @param namespaces the set of namespaces
     * @param oldNamespace the namespace to add to the set
     */
    private void removeNamespace(@Nonnull @Live final Set<Namespace> namespaces,
            @Nullable final Namespace oldNamespace) {
        if (oldNamespace == null) {
            return;
        }
        
        namespaces.remove(oldNamespace);
    }
    
    /**
     * Merge 2 or more Namespace collections into a single set.
     * 
     * @param namespaces list of Namespaces to merge
     * @return the a new set of merged Namespaces
     */
    @SafeVarargs
    @Nonnull @Unmodifiable @NotLive private Set<Namespace> mergeNamespaceCollections(
            @Nonnull final Collection<Namespace> ... namespaces) {
        final LazySet<Namespace> newNamespaces = new LazySet<>();
        
        for (final Collection<Namespace> nsCollection : namespaces) {
            for (final Namespace ns : nsCollection) {
                if (ns != null) {
                    addNamespace(newNamespaces, ns);
                }
            }
        }
        
        return newNamespaces;
    }
    
    /**
     * Get the set of namespaces which are currently visibly-used on the owning XMLObject (only the owner,
     * not its children).
     * 
     * @return the set of visibly-used namespaces
     */
    @Nonnull @Unmodifiable @NotLive private Set<Namespace> getVisibleNamespaces() {
        final LazySet<Namespace> namespaces = new LazySet<>();

        // Add namespace from element name.
        if (getElementNameNamespace() != null) {
            namespaces.add(getElementNameNamespace());
        }

        // Add xsi attribute prefix, if element carries an xsi:type.
        if (getElementTypeNamespace() != null) {
            namespaces.add(XSI_NAMESPACE);
        }
        
        // Add namespaces from attribute names
        for (final Namespace attribName : attrNames) {
            if (attribName != null) {
                namespaces.add(attribName);
            }
        }

        return namespaces;
    }

    /**
     * Get the set of non-visibly used namespaces used on the owning XMLObject (only the owner,
     * not the owner's children).
     * 
     * @return the set of non-visibly-used namespaces
     */
    @Nonnull @Unmodifiable @NotLive private Set<Namespace> getNonVisibleNamespaceCandidates() {
        final LazySet<Namespace> namespaces = new LazySet<>();

        // Add xsi:type value's prefix, if element carries an xsi:type
        if (getElementTypeNamespace() != null) {
            namespaces.add(getElementTypeNamespace());
        }
        
        // Add prefixes from attribute and content values
        for (final Namespace attribValue : attrValues.values()) {
            if (attribValue != null) {
                namespaces.add(attribValue);
            }
        }
        if (contentValue != null) {
            namespaces.add(contentValue);
        }

        return namespaces;
    }

    
    /**
     * Add the prefixes from a collection of namespaces to a set of prefixes. The 
     * value used to represent the default namespace will be normalized to {@link NamespaceManager#DEFAULT_NS_TOKEN}.
     * 
     * @param prefixes the set of prefixes to which to add
     * @param namespaces the source set of Namespaces
     */
    private void addPrefixes(@Nonnull @Live final Set<String> prefixes,
            @Nonnull final Collection<Namespace> namespaces) {
        for (final Namespace ns : namespaces) {
            String prefix = StringSupport.trimOrNull(ns.getNamespacePrefix());
            if (prefix == null) {
                prefix = DEFAULT_NS_TOKEN;
            }
            prefixes.add(prefix);
        }
    }
    
    /**
     * Check whether the supplied QName contains non-empty namespace info and should
     * be managed by the namespace manager.
     * 
     * @param name the QName to check
     * @return true if the QName contains non-empty namespace info and should be managed, false otherwise
     */
    private boolean checkQName(@Nullable final QName name) {
        if (name != null) {
            return !Strings.isNullOrEmpty(name.getNamespaceURI());
        } else {
            return false;
        }
    }
    
}
