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
import org.opensaml.saml.saml2.core.Action;
import org.opensaml.saml.saml2.core.AuthzDecisionQuery;
import org.opensaml.saml.saml2.core.Evidence;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link AuthzDecisionQuery} objects.
 */
public class AuthzDecisionQueryUnmarshaller extends SubjectQueryUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final AuthzDecisionQuery query = (AuthzDecisionQuery) parentObject;
    
        if (childObject instanceof Action) {
            query.getActions().add((Action) childObject);
        } else if (childObject instanceof Evidence) {
            query.setEvidence((Evidence) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final AuthzDecisionQuery query = (AuthzDecisionQuery) xmlObject;

        if (attribute.getLocalName().equals(AuthzDecisionQuery.RESOURCE_ATTRIB_NAME)
                && attribute.getNamespaceURI() == null) {
            query.setResource(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}