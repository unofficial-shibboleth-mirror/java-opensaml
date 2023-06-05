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

package org.opensaml.messaging.encoder.servlet;

import javax.annotation.Nullable;

import org.opensaml.messaging.encoder.MessageEncoder;

import jakarta.servlet.http.HttpServletResponse;
import net.shibboleth.shared.primitive.NonnullSupplier;

/**
 * A specialization of {@link MessageEncoder} that operates on a sink message data type of {@link HttpServletResponse}.
 */
public interface HttpServletResponseMessageEncoder extends MessageEncoder {
    
    /**
     * Get the current Http Servlet response if available.
     *
     * @return current Http Servlet response or null
     */
    @Nullable HttpServletResponse getHttpServletResponse();
    
    /**
     * Set the supplier for the HTTP servlet response on which to operate.
     *
     * @param responseSupplier the supplier for the HTTP servlet response
     */
    void setHttpServletResponseSupplier(@Nullable final NonnullSupplier<HttpServletResponse> responseSupplier);

}