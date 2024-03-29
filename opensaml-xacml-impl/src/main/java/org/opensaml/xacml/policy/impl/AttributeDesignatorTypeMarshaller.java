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
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.xacml.impl.AbstractXACMLObjectMarshaller;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

/**
 * Marshaller for {@link AttributeDesignatorType}.
 */
public class AttributeDesignatorTypeMarshaller extends AbstractXACMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(final XMLObject xmlObject, final Element domElement) throws MarshallingException {
        final AttributeDesignatorType attributeDesignatorType = (AttributeDesignatorType) xmlObject;

        if (!Strings.isNullOrEmpty(attributeDesignatorType.getAttributeId())) {
            domElement.setAttributeNS(null, AttributeDesignatorType.ATTRIBUTE_ID_ATTRIB_NAME,
                    attributeDesignatorType.getAttributeId());
        }
        if (!Strings.isNullOrEmpty(attributeDesignatorType.getDataType())) {
            domElement.setAttributeNS(null, AttributeDesignatorType.DATA_TYPE_ATTRIB_NAME,
                    attributeDesignatorType.getDataType());
        }
        if (!Strings.isNullOrEmpty(attributeDesignatorType.getIssuer())) {
            domElement.setAttributeNS(null, AttributeDesignatorType.ISSUER_ATTRIB_NAME,
                    attributeDesignatorType.getIssuer());
        }
        if (attributeDesignatorType.getMustBePresentXSBoolean() != null) {
            domElement.setAttributeNS(null, AttributeDesignatorType.MUST_BE_PRESENT_ATTRIB_NAME,
                    Boolean.toString(attributeDesignatorType.getMustBePresentXSBoolean().getValue()));
        }

    }

}