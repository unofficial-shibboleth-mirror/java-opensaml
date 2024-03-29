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
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.ResponseType;
import org.opensaml.xacml.profile.saml.XACMLAuthzDecisionStatementType;

/**
 * A thread-safe Unmarshaller for {@link org.opensaml.xacml.profile.saml.XACMLAuthzDecisionStatementType}.
 */
public class XACMLAuthzDecisionStatementTypeUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(final XMLObject parentObject, final XMLObject childObject)
            throws UnmarshallingException {
        final XACMLAuthzDecisionStatementType xacmlauthzdecisionstatement =
                (XACMLAuthzDecisionStatementType) parentObject;

        if (childObject instanceof RequestType) {
            xacmlauthzdecisionstatement.setRequest((RequestType) childObject);
        } else if (childObject instanceof ResponseType) {
            xacmlauthzdecisionstatement.setResponse((ResponseType) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

}
