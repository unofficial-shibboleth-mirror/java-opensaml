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

package org.opensaml.soap.wssecurity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface for element having a <code>@wsu:Id</code> attribute.
 * 
 */
public interface IdBearing {

    /** the <code>Id</code> attribute local name. */
    @Nonnull @NotEmpty public static final String WSU_ID_ATTR_LOCAL_NAME = "Id";

    /** the <code>wsu:Id</code> qualified attribute name. */
    @Nonnull public static final QName WSU_ID_ATTR_NAME =
        new QName(WSSecurityConstants.WSU_NS, WSU_ID_ATTR_LOCAL_NAME, WSSecurityConstants.WSU_PREFIX);

    /**
     * Returns the <code>@wsu:Id</code> attribute value.
     * 
     * @return The <code>@wsu:Id</code> attribute value or <code>null</code>.
     */
    @Nullable public String getWSUId();

    /**
     * Sets the <code>@wsu:Id</code> attribute value.
     * 
     * @param newId The <code>@wsu:Id</code> attribute value
     */
    public void setWSUId(@Nullable final String newId);

}
