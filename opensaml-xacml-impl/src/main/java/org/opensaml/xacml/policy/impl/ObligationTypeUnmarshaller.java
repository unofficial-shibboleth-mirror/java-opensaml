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
import org.opensaml.xacml.policy.AttributeAssignmentType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.ObligationType;
import org.w3c.dom.Attr;

/** UnMarshaller for {@link org.opensaml.xacml.policy.ObligationType}. */
public class ObligationTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(final XMLObject parentObject, final XMLObject childObject)
            throws UnmarshallingException {
        final ObligationType obligation = (ObligationType) parentObject;

        if (childObject instanceof AttributeAssignmentType) {
            obligation.getAttributeAssignments().add((AttributeAssignmentType) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(final XMLObject xmlObject, final Attr attribute) throws UnmarshallingException {

        final ObligationType obligation = (ObligationType) xmlObject;

        if (attribute.getLocalName().equals(ObligationType.OBLIGATION_ID_ATTRIB_NAME)) {
            obligation.setObligationId(attribute.getValue());
        } else  if (attribute.getLocalName().equals(ObligationType.FULFILL_ON_ATTRIB_NAME)) {
            if (attribute.getValue().equals(EffectType.Permit.toString())) {
                obligation.setFulfillOn(EffectType.Permit);
            } else {
                obligation.setFulfillOn(EffectType.Deny);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
        
    }
}