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

package org.opensaml.xacml.policy.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.xacml.impl.AbstractXACMLObject;
import org.opensaml.xacml.policy.EnvironmentMatchType;
import org.opensaml.xacml.policy.EnvironmentType;

/**
 * Implementation for {@link EnvironmentType}.
 */
public class EnvironmentTypeImpl extends AbstractXACMLObject implements EnvironmentType {
    
    /**List of environment matches.*/
    private XMLObjectChildrenList<EnvironmentMatchType> environmentMatches;
    
    
    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected EnvironmentTypeImpl(final String namespaceURI, final String elementLocalName,
            final String namespacePrefix) {
        super(namespaceURI,elementLocalName,namespacePrefix);
        environmentMatches = new XMLObjectChildrenList<>(this);
    }
    /** {@inheritDoc} */
    public List<EnvironmentMatchType> getEnvrionmentMatches() {
        return environmentMatches;
    }   
    
    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        
        final ArrayList<XMLObject> children = new ArrayList<>();        
        
        children.addAll(environmentMatches);      
                
        return Collections.unmodifiableList(children);
    }

}
