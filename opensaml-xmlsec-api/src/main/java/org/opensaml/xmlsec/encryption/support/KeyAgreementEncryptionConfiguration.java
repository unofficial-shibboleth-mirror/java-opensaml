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

package org.opensaml.xmlsec.encryption.support;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.opensaml.xmlsec.agreement.KeyAgreementParameter;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * A component representing the specific configuration for a key agreement encryption operation.
 */
public class KeyAgreementEncryptionConfiguration {
    
    /** The key agreement algorithm URI. */
    private String algorithm;
    
    /** The collection of {@link KeyAgreementParameter}. */
    @Nullable private Collection<KeyAgreementParameter> parameters;

    /**
     * Get the algorithm URI.
     * 
     * @return the algorithm URI
     */
    @Nullable public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Set the algorithm URI.
     * 
     * @param uri the algorithm URI
     */
    public void setAlgorithm(@Nullable final String uri) {
        algorithm = StringSupport.trimOrNull(uri);
    }

    /**
     * Get the collection of {@link KeyAgreementParameter}.
     * 
     * @return the collection of parameters
     */
    @Nullable @NonnullElements @NotLive @Unmodifiable
    public Collection<KeyAgreementParameter> getParameters() {
        return parameters;
    }

    /**
     * Set the collection of {@link KeyAgreementParameter}.
     * 
     * @param params the collection of parameters
     */
    public void setParameters(@Nullable final Collection<KeyAgreementParameter> params) {
        if (params == null) {
            parameters = null;
        } else {
            parameters = params.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
        }
    }

}
