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

package org.opensaml.core.config;

import javax.annotation.Nullable;

/**
 * Exception indicating a problem during the library initialization process.
 */
public class InitializationException extends Exception {

    /** Serial version UID.*/
    private static final long serialVersionUID = 4498093523448059017L;

    /** Constructor. */
    public InitializationException() {
    }

    /**
     * Constructor.
     *
     * @param msg the exception message
     */
    public InitializationException(@Nullable final String msg) {
        super(msg);
    }

    /**
     * Constructor.
     *
     * @param cause the exception cause
     */
    public InitializationException(@Nullable final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     *
     * @param message the exception message
     * @param cause the exception cause
     */
    public InitializationException(@Nullable final String message, @Nullable final Throwable cause) {
        super(message, cause);
    }

}