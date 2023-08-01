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

package org.opensaml.saml.metadata.resolver.filter.data.impl;

import javax.annotation.Nullable;

import org.opensaml.saml.metadata.resolver.filter.MetadataFilterContext;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilterContext.Data;

/**
 * Data object for {@link MetadataFilterContext} intended to hold information about the source of the
 * metadata currently being processed.
 * 
 * @since 4.0.0
 */
public class MetadataSource implements Data {

    /** An identifier for the source of the metadata, typically the resolver ID. */
    @Nullable private String sourceId;

    /** Flag indicating whether the metadata source is trusted. */
    private boolean trusted;
    
    /**
     * Get identifier of the metadata source.
     * 
     * @return source identifier
     */
    @Nullable public String getSourceId() {
        return sourceId;
    }
    
    /**
     * Set identifier of the metadata source. 
     * 
     * @param id source identifier
     */
    public void setSourceId(@Nullable final String id) {
        sourceId = id;
    }

    /**
     * Get whether the metadata source is trusted.
     *
     * @return true if trusted, false if not
     */
    public boolean isTrusted() {
        return trusted;
    }

    /**
     * Set whether the metadata source is trusted.
     *
     * @param flag true if trusted, false if not
     */
    public void setTrusted(final boolean flag) {
        this.trusted = flag;
    }

}