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

package org.opensaml.xacml.profile.saml.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.impl.RequestAbstractTypeMarshaller;
import org.opensaml.xacml.profile.saml.XACMLAuthzDecisionQueryType;
import org.w3c.dom.Element;

/**
 * A thread-safe Marshaller for {@link org.opensaml.xacml.profile.saml.XACMLAuthzDecisionQueryType} objects.
 */
public class XACMLAuthzDecisionQueryTypeMarshaller extends RequestAbstractTypeMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(final XMLObject samlObject, final Element domElement)
            throws MarshallingException {
        final XACMLAuthzDecisionQueryType query = (XACMLAuthzDecisionQueryType) samlObject;

        if (query.getInputContextOnlyXSBooleanValue() != null) {
            domElement.setAttributeNS(null, XACMLAuthzDecisionQueryType.INPUTCONTEXTONLY_ATTRIB_NAME, query
                    .getInputContextOnlyXSBooleanValue().toString());
        }

        if (query.getReturnContextXSBooleanValue() != null) {
            domElement.setAttributeNS(null, XACMLAuthzDecisionQueryType.RETURNCONTEXT_ATTRIB_NAME, query
                    .getReturnContextXSBooleanValue().toString());
        }

        if (query.getCombinePoliciesXSBooleanValue() != null) {
            domElement.setAttributeNS(null, XACMLAuthzDecisionQueryType.COMBINEPOLICIES_ATTRIB_NAME, query
                    .getCombinePoliciesXSBooleanValue().toString());
        }

        super.marshallAttributes(samlObject, domElement);
    }
}
