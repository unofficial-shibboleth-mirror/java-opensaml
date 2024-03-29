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

package org.opensaml.saml.saml2.core.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.BaseID;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDMappingRequest;
import org.opensaml.saml.saml2.core.NameIDPolicy;

/**
 * A thread-safe Unmarshaller for {@link NameIDMappingRequest} objects.
 */
public class NameIDMappingRequestUnmarshaller extends RequestAbstractTypeUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final NameIDMappingRequest req = (NameIDMappingRequest) parentObject;

        if (childObject instanceof BaseID) {
            req.setBaseID((BaseID) childObject);
        } else if (childObject instanceof NameID) {
            req.setNameID((NameID) childObject);
        } else if (childObject instanceof EncryptedID) {
            req.setEncryptedID((EncryptedID) childObject);
        } else if (childObject instanceof NameIDPolicy) {
            req.setNameIDPolicy((NameIDPolicy) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }
    
}