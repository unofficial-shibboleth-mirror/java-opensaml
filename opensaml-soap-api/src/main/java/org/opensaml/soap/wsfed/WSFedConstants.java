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

package org.opensaml.soap.wsfed;

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/** WS-Federation Constants. */
public final class WSFedConstants {

    /** WSFED 1.1 protocol XML namespace. */
    @Nonnull @NotEmpty public static final String WSFED11P_NS = "http://schemas.xmlsoap.org/ws/2005/02/trust";

    /** WSFED 1.X Protocol QName prefix. */
    @Nonnull @NotEmpty public static final String WSFED1P_PREFIX = "wst";

    /** WSFED 1.X Policy XML namespace. */
    @Nonnull @NotEmpty public static final String WSPOLICY_NS = "http://schemas.xmlsoap.org/ws/2004/09/policy";

    /** WSFED 1.X Policy QName prefix. */
    @Nonnull @NotEmpty public static final String WSPOLICY_PREFIX = "wsp";

    /** WSFED 1.X Address XML namespace. */
    @Nonnull @NotEmpty public static final String WSADDRESS_NS = "http://schemas.xmlsoap.org/ws/2004/08/addressing";

    /** WSFED 1.X Address QName prefix. */
    @Nonnull @NotEmpty public static final String WSADDRESS_PREFIX = "wsa";
    
    /** Constructor. */
    private WSFedConstants() {
        
    }
}