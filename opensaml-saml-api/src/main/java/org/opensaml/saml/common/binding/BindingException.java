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

import javax.annotation.Nullable;

import org.opensaml.saml.common.SAMLException;

/**
 * Base exception for errors that occur when messages are encoded/decoded for a specific binding.
 */
public class BindingException extends SAMLException{

    /** Serial version UID. */
    private static final long serialVersionUID = 8759204244381246777L;

    /**
     * Constructor.
     */
    public BindingException() {
        super();
    }
    
    /**
     * Constructor.
     * 
     * @param message exception message
     */
    public BindingException(@Nullable final String message) {
        super(message);
    }
    
    /**
     * Constructor.
     * 
     * @param wrappedException exception to be wrapped by this one
     */
    public BindingException(@Nullable final Exception wrappedException) {
        super(wrappedException);
    }
    
    /**
     * Constructor.
     * 
     * @param message exception message
     * @param wrappedException exception to be wrapped by this one
     */
    public BindingException(@Nullable final String message, @Nullable final Exception wrappedException) {
        super(message, wrappedException);
    }
    
}