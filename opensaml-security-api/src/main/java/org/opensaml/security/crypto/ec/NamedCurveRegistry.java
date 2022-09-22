/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.security.crypto.ec;

import java.security.spec.ECParameterSpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * A registry of {@link NamedCurve} descriptors.
 */
public class NamedCurveRegistry {
    
    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(NamedCurveRegistry.class);
    
    /** Index by OID. */
    private Map<String, NamedCurve> byOID;
    
    /** Index by URI. */
    private Map<String, NamedCurve> byURI;
    
    /** Index by name. */
    private Map<String, NamedCurve> byName;
    
    /** Index by {@link EnhancedECParameterSpec}. */
    private Map<EnhancedECParameterSpec, NamedCurve> byParamSpec;
    
    
    /**
     * Constructor.
     */
    public NamedCurveRegistry() {
        byOID = new HashMap<>();
        byURI = new HashMap<>();
        byName = new HashMap<>();
        byParamSpec = new HashMap<>();
    }
    
    /**
     * Register a curve.
     * 
     * @param curve the curve to register
     */
    public void register(@Nonnull final NamedCurve curve) {
        Constraint.isNotNull(curve, "NamedCurve was null in registration");
        
        byOID.put(curve.getObjectIdentifier(), curve);
        byURI.put(curve.getURI(), curve);
        byName.put(curve.getName(), curve);
        byParamSpec.put(new EnhancedECParameterSpec(curve.getParameterSpec()), curve);
        
        log.debug("Registered NamedCurve: {}", curve);
    }

    /**
     * Deregister a curve.
     * 
     * @param curve the curve to deregister
     */
    public void deregister(@Nonnull final NamedCurve curve) {
        Constraint.isNotNull(curve, "NamedCurve was null in deregistration");
        
        byOID.remove(curve.getObjectIdentifier());
        byURI.remove(curve.getURI());
        byName.remove(curve.getName());
        byParamSpec.remove(new EnhancedECParameterSpec(curve.getParameterSpec()));
        
        log.debug("Deregistered NamedCurve: {}", curve);
    }

    /**
     * Deregister a curve.
     * 
     * @param oid the object identifier (OID) of the curve to deregister
     */
    public void deregisterByOID(@Nonnull final String oid) {
        Constraint.isNotNull(oid, "OID was null in NamedCurve deregistration");
        final NamedCurve curve = getByOID(oid);
        if (curve != null) {
            deregister(curve);
        }
    }
    
    /**
     * Deregister a curve.
     * 
     * @param uri the URI
     */
    public void deregisterByURI(@Nonnull final String uri) {
        Constraint.isNotNull(uri, "URI was null in NamedCurve deregistration");
        final NamedCurve curve = getByURI(uri);
        if (curve != null) {
            deregister(curve);
        }
    }
    
    /**
     * Deregister a curve.
     * 
     * @param name the curve name
     */
    public void deregisterByName(@Nonnull final String name) {
        Constraint.isNotNull(name, "Name was null in NamedCurve deregistration");
        final NamedCurve curve = getByName(name);
        if (curve != null) {
            deregister(curve);
        }
    }
    
    /**
     * Deregister a curve.
     * 
     * @param spec the parameter spec instance
     */
    public void deregisterByParameterSpec(@Nonnull final ECParameterSpec spec) {
        Constraint.isNotNull(spec, "ECParameterSpec was null in NamedCurve deregistration");
        final NamedCurve curve = getByParameterSpec(spec);
        if (curve != null) {
            deregister(curve);
        }
    }
    
    /**
     * Clear all registered curves.
     */
    public void clear() {
        byOID.clear();
        byURI.clear();
        byName.clear();
        byParamSpec.clear();
        
        log.debug("Cleared all registered NamedCurves");
    }
    
    /**
     * Get a set of all the registered curves.
     * 
     * @return the set of registered curves
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive
    public Set<NamedCurve> getRegisteredCurves() {
        return Set.copyOf(byOID.values());
    }
    
    /**
     * Lookup a curve by object identifier (OID).
     * 
     * @param oid the object identifier
     * 
     * @return the {@link NamedCurve} instance, or null if no registered curve matched
     */
    @Nullable public NamedCurve getByOID(@Nonnull final String oid) {
        Constraint.isNotNull(oid, "OID was null in NamedCurve lookup");
        return byOID.get(StringSupport.trimOrNull(oid));
    }

    /**
     * Lookup a curve by URI.
     * 
     * @param uri the URI
     * 
     * @return the {@link NamedCurve} instance, or null if no registered curve matched
     */
    @Nullable public NamedCurve getByURI(@Nonnull final String uri) {
        Constraint.isNotNull(uri, "URI was null in NamedCurve lookup");
        return byURI.get(StringSupport.trimOrNull(uri));
    }

    /**
     * Lookup a curve by the canonical name by which it is known to the Java Cryptography Architecture (JCA).
     * 
     * @param name the name
     * 
     * @return the {@link NamedCurve} instance, or null if no registered curve matched
     */
    @Nullable public NamedCurve getByName(@Nonnull final String name) {
        Constraint.isNotNull(name, "Name was null in NamedCurve lookup");
        return byName.get(StringSupport.trimOrNull(name));
    }

    /**
     * Lookup a curve by {@link ECParameterSpec}.
     * 
     * @param spec the parameter spec instance
     * 
     * @return the {@link NamedCurve} instance, or null if no registered curve matched
     */
    @Nullable public NamedCurve getByParameterSpec(@Nonnull final ECParameterSpec spec) {
        Constraint.isNotNull(spec, "ECParameterSpec was null in NamedCurve lookup");
        return byParamSpec.get(new EnhancedECParameterSpec(spec));
    }

}
