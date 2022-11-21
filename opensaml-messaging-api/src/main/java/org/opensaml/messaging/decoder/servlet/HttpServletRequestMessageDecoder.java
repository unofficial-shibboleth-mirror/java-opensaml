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

package org.opensaml.messaging.decoder.servlet;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.opensaml.messaging.decoder.MessageDecoder;

import net.shibboleth.utilities.java.support.primitive.NonnullSupplier;


/**
 * A specialization of {@link MessageDecoder} that operates on a source message data type of {@link HttpServletRequest}.
 */
public interface HttpServletRequestMessageDecoder extends MessageDecoder {
    
    /**
     * Get the HTTP servlet request on which to operate.
     *
     * @return the HTTP servlet request
     */
    @Nullable HttpServletRequest getHttpServletRequest();
    
    /**
     * Set the HTTP servlet request on which to operate.
     *
     * @param request the HTTP servlet request
     */
    @Deprecated(forRemoval = true, since="4.3")
    void setHttpServletRequest(@Nullable final HttpServletRequest request);

    /**
     * Set the supplier for the HTTP servlet request on which to operate.
     *
     * @param requestSupplier the HTTP servlet request
     */
    default void setHttpServletRequestSupplier(@Nullable final NonnullSupplier<HttpServletRequest> requestSupplier) {
        setHttpServletRequest(requestSupplier.get());
    }
}