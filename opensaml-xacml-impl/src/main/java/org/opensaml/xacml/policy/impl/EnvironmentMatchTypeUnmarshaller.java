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
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.AttributeSelectorType;
import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xacml.policy.EnvironmentMatchType;
import org.w3c.dom.Attr;

import net.shibboleth.shared.primitive.StringSupport;

/** Unmarshaller of {@link EnvironmentMatchType} objects. */
public class EnvironmentMatchTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(final XMLObject xmlObject, final Attr attribute) throws UnmarshallingException {
        if (attribute.getLocalName().equals(EnvironmentMatchType.MATCH_ID_ATTRIB_NAME)) {
            final EnvironmentMatchType matchType = (EnvironmentMatchType) xmlObject;
            matchType.setMatchId(StringSupport.trimOrNull(attribute.getValue()));
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(final XMLObject parentXMLObject, final XMLObject childXMLObject)
            throws UnmarshallingException {
        final EnvironmentMatchType matchType = (EnvironmentMatchType) parentXMLObject;
        if (childXMLObject instanceof AttributeValueType) {
            matchType.setAttributeValue((AttributeValueType) childXMLObject);
        } else if (childXMLObject instanceof AttributeDesignatorType) {
            matchType.setEnvironmentAttributeDesignator((AttributeDesignatorType) childXMLObject);
        } else if (childXMLObject instanceof AttributeSelectorType) {
            matchType.setAttributeSelector((AttributeSelectorType) childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}