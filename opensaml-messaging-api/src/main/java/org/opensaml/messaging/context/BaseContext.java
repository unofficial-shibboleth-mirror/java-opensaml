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

package org.opensaml.messaging.context;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.ClassIndexedSet;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.DeprecationSupport;
import net.shibboleth.shared.primitive.DeprecationSupport.ObjectType;
import net.shibboleth.shared.primitive.LoggerFactory;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.messaging.MessageRuntimeException;
import org.slf4j.Logger;


/**
 * Base implementation of a component which represents the context used to store state 
 * used for purposes related to messaging.
 * 
 * <p>
 * Specific implementations of contexts would normally add additional properties to the
 * context to represent the state that is to be stored by that particular context implementation.
 * </p>
 * 
 * <p>
 * A context may also function as a container of subcontexts.
 * Access to subcontexts is class-based.  The parent context may hold only
 * one instance of a given class at a given time.  This class-based indexing approach
 * is used to enforce type-safety over the subcontext instances returned from the parent context,
 * and avoids the need for casting.
 * </p>
 * 
 * <p>
 * When a subcontext is requested and it does not exist in the parent context, it may optionally be
 * auto-created.  In order to be auto-created in this manner, the subcontext type
 * <strong>MUST</strong> have a no-arg constructor. If the requested subcontext does not conform 
 * to this convention, auto-creation will fail.
 * </p>
 */
@NotThreadSafe
public abstract class BaseContext implements Iterable<BaseContext> {

    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(BaseContext.class);
    
    /** The owning parent context. */
    @Nullable private BaseContext parent;

    /** The subcontexts being managed. */
    @Nonnull private ClassIndexedSet<BaseContext> subcontexts;
    
    /** Constructor. Generates a random context id. */
    public BaseContext() {
        subcontexts = new ClassIndexedSet<>();
    }
    
    /**
     * Get the parent context, if there is one.
     * 
     * @return the parent context or null 
     */
    @Nullable public BaseContext getParent() {
        return parent;
    }
    
    /**
     * Set the context parent. 
     * 
     * @param newParent the new context parent
     */
    protected void setParent(@Nullable final BaseContext newParent) {
        parent = newParent;
    }
    
    /**
     * Get a subcontext of the current context.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the class type to obtain
     * 
     * @return the held instance of the class, or null
     */
    @Nullable public <T extends BaseContext> T getSubcontext(@Nonnull final Class<T> clazz) {
        return getSubcontext(clazz, false);
    }
    
    /**
     * Get a subcontext of the current context, creating it if it does not exist.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the class type to obtain
     * 
     * @return the held instance of the class, or null
     */ 
    @Nonnull public <T extends BaseContext> T ensureSubcontext(@Nonnull final Class<T> clazz) {
        final T newContext = getSubcontext(clazz, true);
        if (newContext == null) {
            throw new IllegalStateException("Context of type '" + clazz.getName() +
                    "' did not exist or was not created");
        }
        
        return newContext;
    }
    
    /**
     * Get a subcontext of the current context.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the class type to obtain
     * @param autocreate flag indicating whether the subcontext instance should be auto-created
     * 
     * @return the held instance of the class, or null
     * @deprecated use {@link #ensureSubcontext(Class)} or {#link {@link #getSubcontext(Class)}.
     */ 
    @Deprecated(since = "5.0.0", forRemoval = false)
    @Nullable public <T extends BaseContext> T getSubcontext(@Nonnull final Class<T> clazz, final boolean autocreate) {
        Constraint.isNotNull(clazz, "Class type cannot be null");
        
        log.trace("Request for subcontext of type: {}", clazz.getName());
        T subcontext = subcontexts.get(clazz);
        if (subcontext != null) {
            log.trace("Subcontext found of type: {}", clazz.getName());
            return subcontext;
        }
        
        if (autocreate) {
            log.trace("Subcontext not found of type, autocreating: {}", clazz.getName());
            subcontext = createSubcontext(clazz);
            addSubcontext(subcontext);
            return subcontext;
        }
        
        log.trace("Subcontext not found of type: {}", clazz.getName());
        return null;
    }

    /**
     * Get a subcontext of the current context.
     * 
     * @param className the name of the class type to obtain
     * 
     * @return the held instance of the class, or null
     */ 
    @Nullable public BaseContext getSubcontext(@Nonnull @NotEmpty final String className) {
        return getSubcontext(className, false);
    }
    

    /**
     * Get a subcontext of the current context, creating it if necessary.
     * 
     * @param className the name of the class type to obtain
     * 
     * @return the held instance of the class, or null
     */ 
    @Nullable public BaseContext ensureSubcontext(@Nonnull @NotEmpty final String className) {
        final BaseContext newContext = getSubcontext(className, true);
        if (newContext == null) {
            throw new IllegalStateException("Context of type '" + className + "' did not exist or was not created");
        }
        
        return newContext;
    }
    
    /**
     * Get a subcontext of the current context.
     * 
     * <p>If autocreate is false, this method will respond to a {@link ClassNotFoundException}
     * by attempting to locate a matching subcontext based on the simple class name of the children and
     * return the first match. If no match is found or if auto-creation is set, it will return a null.</p>
     * 
     * @param className the name of the class type to obtain
     * @param autocreate flag indicating whether the subcontext instance should be auto-created
     * 
     * @return the held instance of the class, or null
     * @deprecated use {@link #ensureSubcontext(String)} or {#link {@link #getSubcontext(String)}.
     */ 
    @Deprecated(since = "5.0.0", forRemoval = false)
    @Nullable public BaseContext getSubcontext(@Nonnull @NotEmpty final String className, final boolean autocreate) {
        try {
            return getSubcontext(Class.forName(className).asSubclass(BaseContext.class), autocreate);
        } catch (final ClassNotFoundException e) {
            
            // Check for a deprecated class name.
            final DeprecatedContextClassNameLookAside lookaside =
                    ConfigurationService.get(DeprecatedContextClassNameLookAside.class);
            if (lookaside != null) {
                final Class<? extends BaseContext> claz = lookaside.get(className);
                if (claz != null) {
                    return getSubcontext(claz, autocreate);
                }
            }
            
            if (!autocreate) {
                for (final BaseContext child : this) {
                    if (child.getClass().getSimpleName().equals(className)) {
                        return child;
                    }
                }
            }
            
            log.warn("Trapped ClassNotFoundException on input: " + className);
            return null;
        }
    }
    
    /**
     * Add a subcontext to the current context.
     * 
     * @param subContext the subcontext to add
     * 
     * @return the context added
     */
    @Nonnull public BaseContext addSubcontext(@Nonnull final BaseContext subContext) {
        return addSubcontext(subContext, false);
    }
    
    /**
     * Add a subcontext to the current context.
     * 
     * @param subcontext the subcontext to add
     * @param replace flag indicating whether to replace the existing instance of the subcontext if present
     * 
     * @return the context added
     */
    @Nonnull public BaseContext addSubcontext(@Nonnull final BaseContext subcontext, final boolean replace) {
        Constraint.isNotNull(subcontext, "Subcontext cannot be null");
        
        final BaseContext existing = subcontexts.get(subcontext.getClass());
        if (existing == subcontext) {
            log.trace("Subcontext to add is already a child of the current context, skipping");
            return subcontext;
        }
        
        // Note: This will throw if replace == false and existing != null.
        // In that case, no link management happens, which is what we want, to leave things in a consistent state.
        log.trace("Attempting to store a subcontext with type '{}' with replace option '{}'", 
                new Object[]{subcontext.getClass().getName(), Boolean.valueOf(replace).toString()});
        subcontexts.add(subcontext, replace);
        
        // Manage parent/child links
        
        // If subcontext was formerly a child of another parent, remove that link
        final BaseContext oldParent = subcontext.getParent();
        if (oldParent != null && oldParent != this) {
            log.trace("New subcontext with type '{}' is currently a subcontext of "
                    + "parent with type '{}', removing it",
                    new Object[]{subcontext.getClass().getName(), oldParent.getClass().getName(),});
            oldParent.removeSubcontext(subcontext);
        }
        
        // Set parent pointer of new subcontext to this instance
        log.trace("New subcontext with type '{}' set to have parent with type '{}'",
                new Object[]{subcontext.getClass().getName(), getClass().getName(),});
        subcontext.setParent(this);
        
        // If we're replacing an existing subcontext (if class was a duplicate, will only get here if replace == true),
        // then clear out its parent pointer.
        if (existing != null) {
            log.trace("Old subcontext with type '{}' will have parent cleared", existing.getClass().getName());
            existing.setParent(null);
        }
        
        return subcontext;
    }
    
    /**
     * Remove a subcontext from the current context.
     * 
     * @param subcontext the subcontext to remove
     */
    public void removeSubcontext(@Nonnull final BaseContext subcontext) {
        Constraint.isNotNull(subcontext, "Subcontext cannot be null");
        
        log.trace("Removing subcontext with type '{}' from parent with type '{}'",
                new Object[]{subcontext.getClass().getName(), getClass().getName()});
        subcontext.setParent(null);
        subcontexts.remove(subcontext);
    }

    /** Remove from our parent (if there is one).
     */
    public void removeFromParent() {
        final BaseContext parent = getParent();
        if (parent == null) {
            return;
        }
        parent.removeSubcontext(this);
    }

    /**
     * Remove the subcontext from the current context which corresponds to the supplied class.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the subcontext class to remove
     */
    public <T extends BaseContext>void removeSubcontext(@Nonnull final Class<T> clazz) {
        final BaseContext subcontext = getSubcontext(clazz);
        if (subcontext != null) {
            removeSubcontext(subcontext);
        }
    }
    
    /**
     * Return whether the current context currently contains an instance of
     * the specified subcontext class.
     * 
     * @param <T> the type of subcontext being operated on
     * @param clazz the class to check
     * @return true if the current context contains an instance of the class, false otherwise
     */
    public <T extends BaseContext> boolean containsSubcontext(@Nonnull final Class<T> clazz) {
        Constraint.isNotNull(clazz, "Class type cannot be null");
        
        return subcontexts.contains(clazz);
    }
    
    /**
     * Clear the subcontexts of the current context.
     */
    public void clearSubcontexts() {
        log.trace("Clearing all subcontexts from context with type '{}'", getClass().getName());
        for (final BaseContext subcontext : subcontexts) {
            subcontext.setParent(null);
        }
        subcontexts.clear();
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull public Iterator<BaseContext> iterator() {
        return new ContextSetNoRemoveIteratorDecorator(subcontexts.iterator());
    }
    
    /**
     * Create an instance of the specified subcontext class.
     * 
     * @param <T> the type of subcontext
     * @param clazz the class of the subcontext instance to create
     * @return the new subcontext instance
     */
    @Nonnull protected <T extends BaseContext> T createSubcontext(@Nonnull final Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (final SecurityException|NoSuchMethodException|IllegalArgumentException|InstantiationException|
                    IllegalAccessException|InvocationTargetException e) {
            log.error("Error creating subcontext: {}", e.getMessage());
            throw new MessageRuntimeException("Error creating subcontext", e);
        }
    }
    
    /**
     * Iterator decorator which disallows the remove() operation on the iterator.
     */
    protected class ContextSetNoRemoveIteratorDecorator implements Iterator<BaseContext> {
        
        /** The decorated iterator. */
        private Iterator<BaseContext> wrappedIterator;
        
        /**
         * Constructor.
         *
         * @param iterator the iterator instance to decorator
         */
        protected ContextSetNoRemoveIteratorDecorator(final Iterator<BaseContext> iterator) {
            wrappedIterator = iterator;
        }

        /** {@inheritDoc} */
        @Override
        public boolean hasNext() {
            return wrappedIterator.hasNext();
        }

        /** {@inheritDoc} */
        @Override
        public BaseContext next() {
            return wrappedIterator.next();
        }

        /** {@inheritDoc} */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Removal of subcontexts via the iterator is unsupported");
        }
        
    }

    /**
     * A facade for a map of class names to class types that allows string-based access to renamed classes.
     * 
     * @since 5.0.0
     */
    public static class DeprecatedContextClassNameLookAside {
    
        /** Map of renamed classes. */
        @Nonnull private final Map<String,Class<? extends BaseContext>> lookAsideMap;
        
        /**
         * Constructor.
         *
         * @param map look aside map of class name strings to classes
         */
        public DeprecatedContextClassNameLookAside(@Nullable final Map<String,Class<? extends BaseContext>> map) {
            if (map != null) {
                lookAsideMap = CollectionSupport.copyToMap(map);
            } else {
                lookAsideMap = CollectionSupport.emptyMap();
            }
        }
        
        /**
         * Get the relocated class object if it exists.
         * 
         * @param name class name
         * 
         * @return relocated class object
         */
        @Nullable public Class<? extends BaseContext> get(@Nonnull final String name) {
            final Class<? extends BaseContext> claz = lookAsideMap.get(name);
            if (claz != null) {
                DeprecationSupport.warn(ObjectType.CLASS, name, null, claz.getName());
            }
            return claz;
        }
    }
    
}