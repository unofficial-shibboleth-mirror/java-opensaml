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

package org.opensaml.core.config;

import java.util.Iterator;
import java.util.ServiceLoader;

import javax.annotation.Nonnull;

import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Service which initializes OpenSAML library modules using the Java Services API.
 * 
 * <p>
 * See also {@link Initializer}.
 * </p>
 */
public class InitializationService {
    
    /** Logger. */
    @Nonnull private static final Logger LOG = LoggerFactory.getLogger(InitializationService.class);
    
    /** Constructor.*/
    protected InitializationService() { }
    
    /**
     *  Initialize all the registered library modules.
     *  
     * @throws InitializationException  if initialization did not complete successfully
     */
    public static synchronized void initialize() throws InitializationException {
        LOG.info("Initializing OpenSAML using the Java Services API");
        
        final ServiceLoader<Initializer> serviceLoader = getServiceLoader();
        final Iterator<Initializer> iter = serviceLoader.iterator();
        while (iter.hasNext()) {
            final Initializer initializer  = iter.next();
            LOG.debug("Initializing module initializer implementation: {}", initializer.getClass().getName());
            try {
                initializer.init();
            } catch (final InitializationException e) {
                LOG.error("Error initializing module: {}", e.getMessage());
                throw e;
            }
        }
    }

    /**
     * Obtain the service loader instance used in the initialization process.
     * 
     * @return the service loader instance to use
     */
    @SuppressWarnings("null")
    @Nonnull private static ServiceLoader<Initializer> getServiceLoader() {
        // TODO ideally would store off loader and reuse on subsequent calls,
        // so inited state in providers would be persisted across calls,
        // avoiding re-initing problems
        // This would take advantage of the caching that the ServiceLoader does
        return ServiceLoader.load(Initializer.class);
    }
    
}