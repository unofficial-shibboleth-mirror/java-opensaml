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

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.xml.QNameSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xacml.impl.AbstractXACMLObjectUnmarshaller;
import org.opensaml.xacml.policy.AttributeValueType;
import org.w3c.dom.Attr;

/** Unmarshaller for {@link org.opensaml.xacml.policy.AttributeValueType}. */
public class AttributeValueTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final AttributeValueType attributeValue = (AttributeValueType) xmlObject;

        final QName attribQName = QNameSupport.getNodeQName(attribute);
        if (attribute.isId()) {
            attributeValue.getUnknownAttributes().registerID(attribQName);
        }
        attributeValue.getUnknownAttributes().put(attribQName, attribute.getValue());
        
        if(attribute.getLocalName().equals(AttributeValueType.DATA_TYPE_ATTRIB_NAME)){
            attributeValue.setDataType(StringSupport.trimOrNull(attribute.getValue()));
        } else {
            processUnknownAttribute(attributeValue, attribute);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final AttributeValueType attributeValue = (AttributeValueType) parentXMLObject;
        attributeValue.getUnknownXMLObjects().add(childXMLObject);
    }

    /** {@inheritDoc} */
    @Override
    protected void processElementContent(@Nonnull final XMLObject xmlObject, @Nonnull final String elementContent) {
        final AttributeValueType attributeValue = (AttributeValueType) xmlObject;
        attributeValue.setValue(elementContent);
    }
}