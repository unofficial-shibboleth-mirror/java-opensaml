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

import org.opensaml.core.xml.schema.XSBooleanValue;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface for element having a <code>@wsp:Optional</code> attribute.
 */
public interface OptionalBearing {
    
    /** The wsp:@Optional attribute local name. */
    @Nonnull @NotEmpty public static final String WSP_OPTIONAL_ATTR_LOCAL_NAME = "Optional";

    /** The wsp:@Optional qualified attribute name. */
    @Nonnull public static final QName WSP_OPTIONAL_ATTR_NAME =
        new QName(WSPolicyConstants.WSP_NS, WSP_OPTIONAL_ATTR_LOCAL_NAME, WSPolicyConstants.WSP_PREFIX);
    
    /**
     * Get the attribute value.
     * 
     * @return return the attribute value
     */
    @Nullable public Boolean isWSP12Optional();
    
    /**
     * Get the attribute value.
     * 
     * @return return the attribute value
     */
    @Nullable public XSBooleanValue isWSP12OptionalXSBoolean();
    
    /**
     * Set the attribute value.
     * 
     * @param newOptional the new attribute value
     */
    public void setWSP12Optional(@Nullable final Boolean newOptional);
    
    /**
     * Set the attribute value.
     * 
     * @param newOptional the new attribute value
     */
    public void setWSP12Optional(@Nullable final XSBooleanValue newOptional);

}
