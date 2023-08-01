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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface for element having a <code>@wsp:PolicyURIs</code> attribute.
 */
public interface PolicyURIsBearing {
    
    /** The wsp:@PolicyURIs attribute local name. */
    @Nonnull @NotEmpty public static final String WSP_POLICY_URIS_ATTR_LOCAL_NAME = "PolicyURIs";

    /** The wsp:@PolicyURIs qualified attribute name. */
    @Nonnull public static final QName WSP_POLICY_URIS_ATTR_NAME =
        new QName(WSPolicyConstants.WSP_NS, WSP_POLICY_URIS_ATTR_LOCAL_NAME, WSPolicyConstants.WSP_PREFIX);
    
    /**
     * Get the attribute value.
     * 
     * @return return the list of attribute values
     */
    @Nullable public List<String> getWSP12PolicyURIs();
    
    /**
     * Set the attribute value.
     * 
     * @param newPolicyURIs the new list of attribute values
     */
    public void setWSP12PolicyURIs(@Nullable final List<String> newPolicyURIs);

}