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
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.xacml.impl.AbstractXACMLObjectUnmarshaller;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.w3c.dom.Attr;

import net.shibboleth.shared.primitive.StringSupport;

/**
 * Unmarshaller for {@link AttributeDesignatorType}.
 */
public class AttributeDesignatorTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {
    
    /** {@inheritDoc} */
    @Override
    protected void processAttribute(final XMLObject xmlObject, final Attr attribute) throws UnmarshallingException {
        
        final AttributeDesignatorType attributeDesignatorType = (AttributeDesignatorType) xmlObject;
        
        if (attribute.getLocalName().equals(AttributeDesignatorType.ATTRIBUTE_ID_ATTRIB_NAME)){
            attributeDesignatorType.setAttributeId(StringSupport.trimOrNull(attribute.getValue()));
        } else  if (attribute.getLocalName().equals(AttributeDesignatorType.DATA_TYPE_ATTRIB_NAME)){
            attributeDesignatorType.setDataType(StringSupport.trimOrNull(attribute.getValue()));
        } else  if (attribute.getLocalName().equals(AttributeDesignatorType.ISSUER_ATTRIB_NAME)){
            attributeDesignatorType.setIssuer(StringSupport.trimOrNull(attribute.getValue()));
        } else  if (attribute.getLocalName().equals(AttributeDesignatorType.MUST_BE_PRESENT_ATTRIB_NAME)){
            if ("True".equals(attribute.getValue()) || "true".equals(attribute.getValue())) {
                attributeDesignatorType.setMustBePresentXSBoolean(XSBooleanValue.valueOf("1"));
            } else {
                attributeDesignatorType.setMustBePresentXSBoolean(XSBooleanValue.valueOf("0"));
            }          
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

}