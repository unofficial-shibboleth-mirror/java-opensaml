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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xacml.impl.AbstractXACMLObjectUnmarshaller;
import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xacml.policy.CombinerParameterType;
import org.w3c.dom.Attr;

import net.shibboleth.shared.primitive.StringSupport;

/**
 * Unmarshaller for {@link CombinerParameterType}.
 */
public class CombinerParameterTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {
    
    /** {@inheritDoc} */
    @Override
    protected void processAttribute(final XMLObject xmlObject, final Attr attribute) throws UnmarshallingException {
        final CombinerParameterType combinerParameterType = (CombinerParameterType) xmlObject;
      
        if(attribute.getLocalName().equals(CombinerParameterType.PARAMETER_NAMEATTRIB_NAME)){
            combinerParameterType.setParameterName(StringSupport.trimOrNull(attribute.getValue()));
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(final XMLObject parentXMLObject, final XMLObject childXMLObject)
            throws UnmarshallingException {
        final CombinerParameterType combinerParameterType = (CombinerParameterType) parentXMLObject;
        
        if(childXMLObject instanceof AttributeValueType){
            combinerParameterType.setAttributeValue((AttributeValueType)childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}