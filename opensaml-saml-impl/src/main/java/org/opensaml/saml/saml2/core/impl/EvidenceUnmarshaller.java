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
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AssertionIDRef;
import org.opensaml.saml.saml2.core.AssertionURIRef;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Evidence;

/**
 * A thread-safe Unmarshaller for {@link Evidence}.
 */
public class EvidenceUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final Evidence evidence = (Evidence) parentObject;

        if (childObject instanceof AssertionIDRef) {
            evidence.getAssertionIDReferences().add((AssertionIDRef) childObject);
        } else if (childObject instanceof AssertionURIRef) {
            evidence.getAssertionURIReferences().add((AssertionURIRef) childObject);
        } else if (childObject instanceof Assertion) {
            evidence.getAssertions().add((Assertion) childObject);
        } else if (childObject instanceof EncryptedAssertion) {
            evidence.getEncryptedAssertions().add((EncryptedAssertion) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }
    
}