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

package org.opensaml.saml.common.messaging.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.MessageContext;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.logic.Constraint;

/**
 * A context intended to be used as a subcontext of a {@link MessageContext} that carries
 * some basic information about the SAML message.
 *
 * <p>
 * This context is generally used to hold information about the endpoint at which a SAML message
 * was received.  In particular it is useful when the SAML message is being processed during
 * a different HTTP request than the one in which it was actually received.
 *
 * </p>
 */
public class SAMLMessageReceivedEndpointContext extends BaseContext {

    /** The request URL. */
    private String requestURL;

    /** Constructor. */
    public SAMLMessageReceivedEndpointContext() {
        super();
    }

    /**
     * Constructor.
     *
     * <p>
     * This is a convenient copy constructor for info from {@link HttpServletRequest}.
     * The constructor does NOT store a reference to the request, it merely copies
     * relevant information to the properties of this class.
     * </p>
     *
     * @param request the HTTP request
     */
    public SAMLMessageReceivedEndpointContext(@Nonnull final HttpServletRequest request) {
        super();
        Constraint.isNotNull(request, "HttpServletRequest was null");
        setRequestURL(request.getRequestURL().toString());
        //TODO add and populate other fields?
    }

    /**
     * Get the request URL.
     *
     * @return the request URL
     */
    @Nullable public String getRequestURL() {
        return requestURL;
    }

    /**
     * Set the request URL.
     *
     * @param url the request URL
     */
    void setRequestURL(@Nullable final String url) {
        requestURL = url;
    }

}
