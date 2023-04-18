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

package org.opensaml.xmlsec.config;

import java.util.Iterator;
import java.util.ServiceLoader;

import javax.annotation.Nonnull;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.opensaml.xmlsec.agreement.KeyAgreementProcessor;
import org.opensaml.xmlsec.agreement.KeyAgreementProcessorRegistry;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * OpenSAML {@link Initializer} implementation for key agreement processors.
 */
public class GlobalKeyAgreementProcessorRegistryInitializer implements Initializer {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(GlobalKeyAgreementProcessorRegistryInitializer.class);

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        final KeyAgreementProcessorRegistry registry = new KeyAgreementProcessorRegistry();
        
        final ServiceLoader<KeyAgreementProcessor> descriptorsLoader = ServiceLoader.load(KeyAgreementProcessor.class);
        final Iterator<KeyAgreementProcessor> iter = descriptorsLoader.iterator();
        while (iter.hasNext()) {
            final KeyAgreementProcessor processor = iter.next();
            log.debug("Registering KeyAgreementProcessor for algorithm '{}': {}", 
                    processor.getAlgorithm(), processor.getClass().getName());
            registry.register(processor);
        }
        
        ConfigurationService.register(KeyAgreementProcessorRegistry.class, registry);
    }

}