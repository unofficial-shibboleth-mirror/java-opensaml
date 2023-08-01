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

/**
 * 
 */

package org.opensaml.saml.saml2.core.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.core.Action;
import org.opensaml.saml.saml2.core.AuthzDecisionStatement;
import org.opensaml.saml.saml2.core.DecisionTypeEnumeration;
import org.opensaml.saml.saml2.core.Evidence;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link AuthzDecisionStatement}.
 */
public class AuthzDecisionStatementUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final AuthzDecisionStatement authzDS = (AuthzDecisionStatement) parentObject;

        if (childObject instanceof Action) {
            authzDS.getActions().add((Action) childObject);
        } else if (childObject instanceof Evidence) {
            authzDS.setEvidence((Evidence) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final AuthzDecisionStatement authzDS = (AuthzDecisionStatement) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (attribute.getLocalName().equals(AuthzDecisionStatement.RESOURCE_ATTRIB_NAME)) {
                authzDS.setResource(attribute.getValue());
            } else if (attribute.getLocalName().equals(AuthzDecisionStatement.DECISION_ATTRIB_NAME)) {
                try {
                    if (attribute.getValue() != null) {
                        authzDS.setDecision(DecisionTypeEnumeration.valueOf(attribute.getValue().toUpperCase()));
                    } else {
                        throw new UnmarshallingException("Saw an empty value for Decision attribute");
                    }
                } catch (final IllegalArgumentException e) {
                    throw new UnmarshallingException("Saw an invalid value for Decision attribute: "
                            + attribute.getValue());
                }
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}