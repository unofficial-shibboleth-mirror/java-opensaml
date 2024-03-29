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

package org.opensaml.saml.common;

import javax.annotation.Nullable;

/**
 * Base exception for SAML related exception.
 */
public class SAMLException extends Exception {

    /** Serial version UID. */
    private static final long serialVersionUID = 6308450535247361691L;

    /**
     * Constructor.
     */
    public SAMLException() {
        super();
    }
    
    /**
     * Constructor.
     * 
     * @param message exception message
     */
    public SAMLException(@Nullable final String message) {
        super(message);
    }
    
    /**
     * Constructor.
     * 
     * @param wrappedException exception to be wrapped by this one
     */
    public SAMLException(@Nullable final Exception wrappedException) {
        super(wrappedException);
    }
    
    /**
     * Constructor.
     * 
     * @param message exception message
     * @param wrappedException exception to be wrapped by this one
     */
    public SAMLException(@Nullable final String message, @Nullable final Exception wrappedException) {
        super(message, wrappedException);
    }
}