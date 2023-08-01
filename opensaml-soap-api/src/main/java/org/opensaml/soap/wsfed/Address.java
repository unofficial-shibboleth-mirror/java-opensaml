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
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

/**
 * This interface defines how the object representing a WS Address <code> Address </code> element behaves.
 */
public interface Address extends WSFedObject {

    /** Element name, no namespace. */
    @Nonnull public static final String DEFAULT_ELEMENT_LOCAL_NAME = "Address";

    /** Default element name. */
    @Nonnull public static final QName DEFAULT_ELEMENT_NAME =
            new QName(WSFedConstants.WSADDRESS_NS, DEFAULT_ELEMENT_LOCAL_NAME, WSFedConstants.WSADDRESS_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull public static final String TYPE_LOCAL_NAME = "AddressType";

    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = new QName(WSFedConstants.WSADDRESS_NS, TYPE_LOCAL_NAME,
            WSFedConstants.WSADDRESS_PREFIX);

    /**
     * Gets the end point reference address.
     * 
     * @return the end point address
     */
    @Nullable public String getValue();

    /**
     * Sets the end point reference address.
     * 
     * @param value the end point address
     */
    public void setValue(@Nullable final String value);
}