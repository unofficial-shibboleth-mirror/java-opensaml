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
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * The OperatorContentType complex type.
 */
public interface OperatorContentType extends WSPolicyObject {
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "OperatorContentType"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = 
        new QName(WSPolicyConstants.WSP_NS, TYPE_LOCAL_NAME, WSPolicyConstants.WSP_PREFIX);
    
    /**
     * Get the list of {@link Policy} elements.
     * 
     * @return the list of {@link Policy} elements
     */
    @Nonnull public List<Policy> getPolicies();
    
    /**
     * Get the list of {@link All} elements.
     * 
     * @return the list of {@link All} elements
     */
    @Nonnull public List<All> getAlls();
    
    /**
     * Get the list of {@link ExactlyOne} elements.
     * 
     * @return the list of {@link ExactlyOne} elements
     */
    @Nonnull public List<ExactlyOne> getExactlyOnes();
    
    /**
     * Get the list of {@link PolicyReference} elements.
     * 
     * @return the list of {@link PolicyReference} elements
     */
    @Nonnull public List<PolicyReference> getPolicyReferences();
    
    /**
     * Get the complete modifiable list of XMLObject children.
     * 
     * @return the list of {@link XMLObject} instances
     */
    @Nonnull public List<XMLObject> getXMLObjects();
    
    /**
     * Get the modifiable sublist of XMLObject children which match the specified
     * type or element name.
     * 
     * @param typeOrName the element name or xsi:type
     * 
     * @return the list of {@link XMLObject} instances
     */
    @Nonnull public List<XMLObject> getXMLObjects(@Nonnull QName typeOrName);

}
