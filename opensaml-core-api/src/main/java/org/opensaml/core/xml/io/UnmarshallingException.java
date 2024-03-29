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

package org.opensaml.core.xml.io;

import javax.annotation.Nullable;

/**
 * Exception thrown when error occurs unmarshalling a DOM Element to a XMLObject.
 */
public class UnmarshallingException extends Exception {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 7512624219806550152L;

    /**
     * Constructor.
     */
    public UnmarshallingException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message exception message
     */
    public UnmarshallingException(@Nullable final String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param wrappedException exception to be wrapped by this one
     */
    public UnmarshallingException(@Nullable final Exception wrappedException) {
        super(wrappedException);
    }

    /**
     * Constructor.
     * 
     * @param message exception message
     * @param wrappedException exception to be wrapped by this one
     */
    public UnmarshallingException(@Nullable final String message, @Nullable final Exception wrappedException) {
        super(message, wrappedException);
    }
}