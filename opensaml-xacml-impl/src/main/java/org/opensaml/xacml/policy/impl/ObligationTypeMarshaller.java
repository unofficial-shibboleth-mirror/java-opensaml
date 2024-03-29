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
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.ObligationType;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

/** Marshaller for {@link ObligationType}. */
public class ObligationTypeMarshaller extends AbstractXACMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(final XMLObject samlElement, final Element domElement)
            throws MarshallingException {
        final ObligationType obligation = (ObligationType) samlElement;

        if (!Strings.isNullOrEmpty(obligation.getObligationId())) {
            domElement.setAttributeNS(null, ObligationType.OBLIGATION_ID_ATTRIB_NAME, obligation.getObligationId());
        }
        if (obligation.getFulfillOn() != null) {
            if (obligation.getFulfillOn().equals(EffectType.Deny)) {
                domElement.setAttributeNS(null, ObligationType.FULFILL_ON_ATTRIB_NAME, EffectType.Deny.toString());
            } else {
                domElement.setAttributeNS(null, ObligationType.FULFILL_ON_ATTRIB_NAME, EffectType.Permit.toString());
            }                     
        }
    }
}