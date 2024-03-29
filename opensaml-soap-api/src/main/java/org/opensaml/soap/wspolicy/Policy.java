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

package org.opensaml.soap.wspolicy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.soap.wssecurity.IdBearing;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * The wsp:Policy element.
 * 
 * @see "WS-Policy (http://schemas.xmlsoap.org/ws/2004/09/policy)"
 * 
 */
public interface Policy extends OperatorContentType, AttributeExtensibleXMLObject, IdBearing {

    /** Element local name. */
    @Nonnull @NotEmpty public static final String ELEMENT_LOCAL_NAME = "Policy";

    /** Default element name. */
    @Nonnull public static final QName ELEMENT_NAME =
        new QName(WSPolicyConstants.WSP_NS, ELEMENT_LOCAL_NAME, WSPolicyConstants.WSP_PREFIX);

    /** The wsp:Policy/@Name attribute local name. */
    @Nonnull @NotEmpty public static final String NAME_ATTRIB_NAME = "Name";

    /**
     * Returns the wsp:Policy/@Name attribute value.
     * 
     * @return the <code>Name</code> attribute value or <code>null</code>.
     */
    @Nullable public String getName();

    /**
     * Sets the wsp:Policy/@Name attribute value.
     * 
     * @param name the <code>Name</code> attribute value to set.
     */
    public void setName(@Nullable final String name);

}
