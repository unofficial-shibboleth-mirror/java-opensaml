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

package org.opensaml.xacml.ctx.impl;

import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xacml.ctx.StatusMessageType;
import org.opensaml.xacml.impl.AbstractXACMLObject;

/**
 * Implementation of {@link org.opensaml.xacml.ctx.StatusMessageType}.
 */
public class StatusMessageTypeImpl extends AbstractXACMLObject implements StatusMessageType {

    /**Message.*/
    private String message;
    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected StatusMessageTypeImpl(final String namespaceURI, final String elementLocalName,
            final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
    
    /** {@inheritDoc} */
    public String getValue() {        
        return message;
    }

    /** {@inheritDoc} */
    public void setValue(final String newMessage) {
        message = prepareForAssignment(message,newMessage); 

    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        return null;
    }

}
