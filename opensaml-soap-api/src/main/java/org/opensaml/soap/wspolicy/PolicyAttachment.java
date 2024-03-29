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

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.ElementExtensibleXMLObject;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * The wsp:PolicyAttachment element.
 * 
 * @see "WS-Policy (http://schemas.xmlsoap.org/ws/2004/09/policy)"
 */
public interface PolicyAttachment extends WSPolicyObject, ElementExtensibleXMLObject, AttributeExtensibleXMLObject {
    
    /** Element local name. */
    @Nonnull @NotEmpty public static final String ELEMENT_LOCAL_NAME = "PolicyAttachment";

    /** Default element name. */
    @Nonnull public static final QName ELEMENT_NAME =
        new QName(WSPolicyConstants.WSP_NS, ELEMENT_LOCAL_NAME, WSPolicyConstants.WSP_PREFIX);
    
    /**
     * Get the AppliesTo child element.
     * 
     * @return the child element
     */
    @Nullable public AppliesTo getAppliesTo();
    
    /**
     * Set the AppliesTo child element.
     * 
     * @param newAppliesTo the new child element
     */
    public void setAppliesTo(@Nullable final AppliesTo newAppliesTo);
    
    /**
     * Get the list of Policy child elements.
     * 
     * @return the list of child elements
     */
    @Nonnull public List<Policy> getPolicies();
    
    /**
     * Get the list of PolicyReference child elements.
     * 
     * @return the list of child elements
     */
    @Nonnull public List<PolicyReference> getPolicyReferences();

}
