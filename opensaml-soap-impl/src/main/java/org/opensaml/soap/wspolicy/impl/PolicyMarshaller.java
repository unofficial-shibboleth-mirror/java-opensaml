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

package org.opensaml.soap.wspolicy.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.soap.wspolicy.Policy;
import org.opensaml.soap.wssecurity.IdBearing;
import org.w3c.dom.Element;


/**
 * Marshaller for the wsp:Policy element.
 * 
 */
public class PolicyMarshaller extends OperatorContentTypeMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final Policy policy = (Policy) xmlObject;
        
        if (policy.getName() != null) {
            domElement.setAttributeNS(null, Policy.NAME_ATTRIB_NAME, policy.getName());
        }
        
        if (policy.getWSUId() != null) {
            XMLObjectSupport.marshallAttribute(IdBearing.WSU_ID_ATTR_NAME, policy.getWSUId(), domElement, true);
        }
        
        XMLObjectSupport.marshallAttributeMap(policy.getUnknownAttributes(), domElement);
    }
    
    /** {@inheritDoc} */
    protected void marshallAttributeIDness(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        XMLObjectSupport.marshallAttributeIDness(IdBearing.WSU_ID_ATTR_NAME, domElement, true);
        
        super.marshallAttributeIDness(xmlObject, domElement);
    }

}
