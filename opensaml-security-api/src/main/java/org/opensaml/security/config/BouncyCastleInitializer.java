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

package org.opensaml.security.config;

import javax.annotation.Nonnull;

import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Initializer which initializes the Bouncy Castle library. 
 */
public class BouncyCastleInitializer implements Initializer {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(BouncyCastleInitializer.class);

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        final String laxPEMParsingPropName = "org.bouncycastle.pemreader.lax";
        // Don't override if it was set explicitly
        if (System.getProperty(laxPEMParsingPropName) == null) {
            log.debug("Enabling lax PEM reader parsing in Bouncy Castle");
            System.setProperty(laxPEMParsingPropName, "true");
        }
    }

}