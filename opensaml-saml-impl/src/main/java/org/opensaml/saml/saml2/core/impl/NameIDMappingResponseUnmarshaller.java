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
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDMappingResponse;

/**
 * A thread safe Unmarshaller for {@link NameIDMappingResponse} objects.
 */
public class NameIDMappingResponseUnmarshaller extends StatusResponseTypeUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final NameIDMappingResponse resp = (NameIDMappingResponse) parentObject;

        if (childObject instanceof NameID) {
            resp.setNameID((NameID) childObject);
        } else if (childObject instanceof EncryptedID) {
            resp.setEncryptedID((EncryptedID) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }
}