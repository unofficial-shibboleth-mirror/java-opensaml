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

package org.opensaml.saml.common.binding;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Enum for representing SAML message types based on HTTP request parameters: request, response, artifact.
 */
public enum MessageType {
    /** Request. */
    REQUEST("SAMLRequest"),

    /** Response. */
    RESPONSE("SAMLResponse"),
    
    /** Artifact. */
    ARTIFACT("SAMLart");
    
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(MessageType.class);

    /** Parameter name. */
    private final String paramName;

    /**
     * Constructor.
     *
     * @param name parameter name
     */
    private MessageType(@Nonnull final String name) {
        paramName = name;
    }
    
    /**
     * Get the parameter name.
     * 
     * @return the parameter name
     */
    public String getParameterName() {
        return paramName;
    }
    
    /**
     * Return the enum value whose parameter name matches the specified name.
     * 
     * @param paramName the parameter name
     * @return the matching MessageType enum value, or none
     */
    public static MessageType fromParameterName(@Nullable final String paramName) {
        if (paramName == null) {
            LOG.warn("Passed parameter name was null, returning null");
            return null;
        }

        for (final MessageType mt : values()) {
            if (mt.getParameterName().equals(paramName)) {
                return mt;
            }
        }
        LOG.warn("Found no MessageType matching parameter name '{}', returning null", paramName);
        return null;
    }
    
    /**
     * Get the set of all valid message parameter names.
     * 
     * @return the set of all message parameter names
     */
    @Nonnull @NotLive public static Set<String> getAllParameterNames() {
        return Stream.of(MessageType.values())
                .map(MessageType::getParameterName)
                .collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableSet())).get();
    }

}
