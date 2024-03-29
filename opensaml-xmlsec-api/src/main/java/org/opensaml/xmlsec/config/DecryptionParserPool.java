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

package org.opensaml.xmlsec.config;

import javax.annotation.Nonnull;

import org.opensaml.core.config.ConfigurationService;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.xml.ParserPool;

/**
 * A wrapper bean containing an instance of {@link ParserPool} used with XML decryption
 * that can be registered with the global {@link ConfigurationService}.
 */
public class DecryptionParserPool {
    
    /** The wrapped parser pool instance. */
    @Nonnull private ParserPool parserPool;
    
    /**
     * Constructor.
     *
     * @param pool the parser pool instance
     */
    public DecryptionParserPool(@Nonnull final ParserPool pool) {
        parserPool = Constraint.isNotNull(pool, "Decryption ParserPool may not be null");
    }
    
    /**
     * Obtain the wrapped parser pool instance.
     * 
     * @return the wrapped parser pool instance 
     */
    @Nonnull public ParserPool getParserPool() {
        return parserPool;
    }

}
