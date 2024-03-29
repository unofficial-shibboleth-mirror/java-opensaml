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

package org.opensaml.security.credential.criteria.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.security.SecurityException;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.Criterion;

/**
 * A registry which manages mappings from types of {@link Criterion} to the class type which can evaluate that
 * criteria's data against a Credential target. That latter class will be a subtype of
 * {@link EvaluableCredentialCriterion}. Each EvaluableCredentialCriterion implementation that is registered
 * <strong>MUST</strong> implement a single-arg constructor which takes an instance of the Criterion to be evaluated.
 * The evaluable instance is instantiated reflectively based on this requirement.
 */
public final class EvaluableCredentialCriteriaRegistry {
    
    /**
     * Properties file storing default mappings from criteria to evaluable credential criteria. Will be loaded as a
     * resource stream relative to this class.
     */
    @Nonnull @NotEmpty public static final String DEFAULT_MAPPINGS_FILE = "/credential-criteria-registry.properties";

    /** Storage for the registry mappings. */
    private static Map<Class<? extends Criterion>, Class<? extends EvaluableCredentialCriterion>> registry;

    /** Flag to track whether registry is initialized. */
    private static boolean initialized;
    
    /** Logger. */
    @Nonnull private static final Logger LOG = LoggerFactory.getLogger(EvaluableCredentialCriteriaRegistry.class);

    /** Constructor. */
    private EvaluableCredentialCriteriaRegistry() {
    }

    /**
     * Get an instance of {@link EvaluableCredentialCriterion} which can evaluate the supplied criteria's
     * requirements against a Credential target.
     * 
     * @param criteria the criteria to be evaluated against a credential
     * @return an instance of of EvaluableCredentialCriterion representing the specified criteria's requirements
     * @throws SecurityException thrown if there is an error reflectively instantiating a new instance of
     *             EvaluableCredentialCriterion based on class information stored in the registry
     */
    @Nullable public static EvaluableCredentialCriterion getEvaluator(@Nonnull final Criterion criteria)
            throws SecurityException {
        Constraint.isNotNull(criteria, "Criteria to map cannot be null");
        
        final Class<? extends EvaluableCredentialCriterion> clazz = lookup(criteria.getClass());

        if (clazz != null) {
            LOG.debug("Registry located evaluable criteria class {} for criteria class {}", clazz.getName(), criteria
                    .getClass().getName());

            try {
                final Constructor<? extends EvaluableCredentialCriterion> constructor = 
                        clazz.getConstructor(new Class[] { criteria.getClass() });

                return constructor.newInstance(new Object[] { criteria });

            } catch (final java.lang.SecurityException | InstantiationException | IllegalAccessException 
                    | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
                LOG.error("Error instantiating new EvaluableCredentialCriterion instance: {}", e.getMessage());
                throw new SecurityException("Could not create new EvaluableCredentialCriterion", e);
            }
        }
        LOG.debug("Registry could not locate evaluable criteria for criteria class {}", criteria.getClass().getName());
        return null;
    }

    /**
     * Lookup the class subtype of EvaluableCredentialCriterion which is registered for the specified Criterion class.
     * 
     * @param clazz the Criterion class subtype to lookup
     * @return the registered EvaluableCredentialCriterion class subtype
     */
    @Nullable public static synchronized Class<? extends EvaluableCredentialCriterion> lookup(
            @Nonnull final Class<? extends Criterion> clazz) {
        Constraint.isNotNull(clazz, "Criterion class to lookup cannot be null");
        return registry.get(clazz);
    }

    /**
     * Register a credential evaluator class for a criteria class.
     * 
     * @param criteriaClass class subtype of {@link Criterion}
     * @param evaluableClass class subtype of {@link EvaluableCredentialCriterion}
     */
    public static synchronized void register(@Nonnull final Class<? extends Criterion> criteriaClass,
            @Nonnull final Class<? extends EvaluableCredentialCriterion> evaluableClass) {
        Constraint.isNotNull(criteriaClass, "Criterion class to register cannot be null");
        Constraint.isNotNull(evaluableClass, "Evaluable class to register cannot be null");
        
        LOG.debug("Registering class {} as evaluator for class {}", evaluableClass.getName(), criteriaClass.getName());

        registry.put(criteriaClass, evaluableClass);
    }

    /**
     * Deregister a criteria-evaluator mapping.
     * 
     * @param criteriaClass class subtype of {@link Criterion}
     */
    public static synchronized void deregister(@Nonnull final Class<? extends Criterion> criteriaClass) {
        Constraint.isNotNull(criteriaClass, "Criterion class to unregister cannot be null");
        
        LOG.debug("Deregistering evaluator for class {}", criteriaClass.getName());
        registry.remove(criteriaClass);
    }

    /**
     * Clear all mappings from the registry.
     */
    public static synchronized void clearRegistry() {
        LOG.debug("Clearing evaluable criteria registry");

        registry.clear();
    }

    /**
     * Check whether the registry has been initialized.
     * 
     * @return true if registry is already initialized, false otherwise
     */
    public static synchronized boolean isInitialized() {
        return initialized;
    }

    /**
     * Initialize the registry.
     */
    public static synchronized void init() {
        if (isInitialized()) {
            return;
        }

        registry = new HashMap<>();

        loadDefaultMappings();

        initialized = true;
    }

    /**
     * Load the default set of criteria-evaluator mappings from the default mappings properties file.
     */
    public static synchronized void loadDefaultMappings() {
        LOG.debug("Loading default evaluable credential criteria mappings");
        try (final InputStream inStream =
                EvaluableCredentialCriteriaRegistry.class.getResourceAsStream(DEFAULT_MAPPINGS_FILE) ) {

            if (inStream == null) {
                LOG.error("Could not open resource stream from default mappings file '{}'", DEFAULT_MAPPINGS_FILE);
                return;
            }

            final Properties defaultMappings = new Properties();
            defaultMappings.load(inStream);

            loadMappings(defaultMappings);

        } catch (final IOException e) {
            LOG.error("Error loading properties file from resource stream", e);
            return;
        }

    }

    /**
     * Load a set of criteria-evaluator mappings from the supplied properties set.
     * 
     * @param mappings properties set where the key is the criteria class name, the value is the evaluator class name
     */
    public static synchronized void loadMappings(@Nonnull final Properties mappings) {
        Constraint.isNotNull(mappings, "Mappings to load cannot be null");
        
        for (final Object key : mappings.keySet()) {
            if (!(key instanceof String)) {
                LOG.error("Properties key was not an instance of String, was '{}', skipping...", 
                        key.getClass().getName());
                continue;
            }
            final String criteriaName = (String) key;
            final String evaluatorName = mappings.getProperty(criteriaName);

            final ClassLoader classLoader = XMLObjectProviderRegistrySupport.class.getClassLoader();
            Class<? extends Criterion> criteriaClass = null;
            try {
                criteriaClass = (Class<? extends Criterion>) classLoader.loadClass(criteriaName);
            } catch (final ClassNotFoundException e) {
                LOG.error("Could not find criteria class '{}', skipping registration", criteriaName);
                continue;
            }

            Class<? extends EvaluableCredentialCriterion> evaluableClass = null;
            try {
                evaluableClass = (Class<? extends EvaluableCredentialCriterion>) classLoader.loadClass(evaluatorName);
            } catch (final ClassNotFoundException e) {
                LOG.error("Could not find evaluator class '{}', skipping registration", criteriaName);
                continue;
            }

            register(criteriaClass, evaluableClass);
        }

    }

    static {
        init();
    }
}
