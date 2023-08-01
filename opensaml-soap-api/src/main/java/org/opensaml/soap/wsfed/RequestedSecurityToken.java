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

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;

/**
 * This interface defines how the object representing a Requested Security Token <code> RequestedSecurityToken </code>
 * element behaves.
 */
public interface RequestedSecurityToken extends WSFedObject {

    /** Element name, no namespace. */
    @Nonnull public static final String DEFAULT_ELEMENT_LOCAL_NAME = "RequestedSecurityToken";

    /** Default element name. */
    @Nonnull public static final QName DEFAULT_ELEMENT_NAME =
            new QName(WSFedConstants.WSFED11P_NS, DEFAULT_ELEMENT_LOCAL_NAME, WSFedConstants.WSFED1P_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull public static final String TYPE_LOCAL_NAME = "RequestedSecurityTokenType";

    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = new QName(WSFedConstants.WSFED11P_NS, TYPE_LOCAL_NAME,
            WSFedConstants.WSFED1P_PREFIX);

    /**
     * Return the list security tokens.
     * 
     * @return the list security tokens
     */
    @Nonnull public List<XMLObject> getSecurityTokens();
}