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

/**
 * SAML-related configuration information.
 * 
 * <p>
 * The configuration instance to use would typically be retrieved from the
 * {@link org.opensaml.core.config.ConfigurationService}.
 * </p>
 * 
 */
public class SOAPConfiguration {

    /** Flag indicating whether to enforce SOAP message size limits in the decoders. */
    private boolean enforceDecoderSizeLimit;
    
    /** SOAP message size limit. */
    @Nullable private Integer decoderSizeLimit;

    /**
     * Get the flag indicating whether to enforce SOAP message size limits in the decoders.
     * 
     * @return true if enforcement enabled, false if not
     */
    public boolean isEnforceDecoderSizeLimit() {
        return enforceDecoderSizeLimit;
    }

    /**
     * Set the flag indicating whether to enforce SOAP message size limits in the decoders.
     * 
     * @param flag true if enforcement enabled, false if not
     */
    public void setEnforceDecoderSizeLimit(final boolean flag) {
        enforceDecoderSizeLimit = flag;
    }

    /**
     * Get the SOAP message size limit.
     * 
     * @return the limit in bytes
     */
    @Nullable public Integer getDecoderSizeLimit() {
        return decoderSizeLimit;
    }

    /**
     * Set the SOAP message size limit.
     * 
     * @param limit the limit in bytes
     */
    public void setDecoderSizeLimit(@Nullable final Integer limit) {
        decoderSizeLimit = limit;
    }

}