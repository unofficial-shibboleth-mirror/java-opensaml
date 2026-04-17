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

package org.opensaml.soap.config;

import javax.annotation.Nullable;

import org.opensaml.core.config.ConfigurationService;

/**
 * Helper class for working with the registered instance of {@link SOAPConfiguration}, as obtained from
 * the {@link ConfigurationService}.
 * 
 */
public final class SOAPConfigurationSupport {
    
    /** Constructor. */
    private SOAPConfigurationSupport() {}
    
    /**
     * Get the flag indicating whether to enforce SOAP message size limits in the decoders.
     * 
     * @return true if enforcement enabled, false if not
     */
    public static boolean isEnforceDecoderSizeLimit() {
        return ConfigurationService.ensure(SOAPConfiguration.class).isEnforceDecoderSizeLimit();
    }

    /**
     * Set the flag indicating whether to enforce SOAP message size limits in the decoders.
     * 
     * @param flag true if enforcement enabled, false if not
     */
    public static void setEnforceDecoderSizeLimit(final boolean flag) {
        ConfigurationService.ensure(SOAPConfiguration.class).setEnforceDecoderSizeLimit(flag);
    }

    /**
     * Get the SOAP message size limit.
     * 
     * @return the limit in bytes
     */
    @Nullable public static Integer getDecoderSizeLimit() {
        return ConfigurationService.ensure(SOAPConfiguration.class).getDecoderSizeLimit();
    }

    /**
     * Set the SOAP message size limit.
     * 
     * @param limit the limit in bytes
     */
    public static void setDecoderSizeLimit(@Nullable final Integer limit) {
        ConfigurationService.ensure(SOAPConfiguration.class).setDecoderSizeLimit(limit);
    }

}