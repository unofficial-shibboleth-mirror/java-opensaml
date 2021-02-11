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

package org.opensaml.security.config;

import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.opensaml.security.crypto.ec.ECSupport;
import org.opensaml.security.crypto.ec.NamedCurve;
import org.opensaml.security.crypto.ec.NamedCurveRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.InitializableComponent;

/**
 * OpenSAML {@link Initializer} implementation for {@link NamedCurveRegistry}.
 */
public class GlobalNamedCurveRegistryInitializer implements Initializer {
    
    /** Configuration property name for registering curves from Bouncy Castle. */
    public static final String CONFIG_PROPERTY_REGISTER_BOUNCY_CASTLE_CURVES =
            "opensaml.config.ec.registerBouncyCastleCurves";
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(GlobalNamedCurveRegistryInitializer.class);

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        final NamedCurveRegistry registry = new NamedCurveRegistry();
        
        final ServiceLoader<NamedCurve> curvesLoader = ServiceLoader.load(NamedCurve.class);
        final Iterator<NamedCurve> iter = curvesLoader.iterator();
        while (iter.hasNext()) {
            final NamedCurve curve = iter.next();
            try {
                if (InitializableComponent.class.isInstance(curve)) {
                    InitializableComponent.class.cast(curve).initialize();
                }
            } catch (final ComponentInitializationException e) {
                log.warn("Error initing NamedCurve, name '{}', OID '{}', URI '{}': {}",
                        curve.getName(), curve.getObjectIdentifier(), curve.getURI(), curve.getClass().getName());
                continue;
            }
            log.debug("Registering NamedCurve, name '{}', OID '{}', URI '{}': {}'",
                    curve.getName(), curve.getObjectIdentifier(), curve.getURI(), curve.getClass().getName());
            registry.register(curve);
        }
        
        final Properties props = ConfigurationService.getConfigurationProperties(); 
        final boolean registerBCCurves =
                (props != null) ? Boolean.parseBoolean(props.getProperty(CONFIG_PROPERTY_REGISTER_BOUNCY_CASTLE_CURVES))
                        : false;
        
        if (registerBCCurves) {
            // Don't register if already done above. Use the OID as the canonical unique identifier.  Names may differ.
            final Set<String> oids = registry.getRegisteredCurves().stream()
                    .map(NamedCurve::getObjectIdentifier)
                    .collect(Collectors.toSet());
            
            final Set<NamedCurve> curves = ECSupport.getCurvesFromBouncyCastle();
            for (final NamedCurve curve : curves) {
                if (!oids.contains(curve.getObjectIdentifier())) {
                    log.debug("Registering BC NamedCurve, name '{}', OID '{}', URI '{}': {}'",
                            curve.getName(), curve.getObjectIdentifier(), curve.getURI(), curve.getClass().getName());
                    registry.register(curve);
                } else {
                    log.debug("Skipping BC NamedCurve because already registered, name '{}', OID '{}', URI '{}': {}'",
                            curve.getName(), curve.getObjectIdentifier(), curve.getURI(), curve.getClass().getName());
                }
            }
        }
        
        ConfigurationService.register(NamedCurveRegistry.class, registry);
    }

}
