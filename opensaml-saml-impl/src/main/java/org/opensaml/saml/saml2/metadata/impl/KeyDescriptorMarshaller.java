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

package org.opensaml.saml.saml2.metadata.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.security.credential.UsageType;
import org.w3c.dom.Element;

/**
 * A thread-safe marshaller for {@link KeyDescriptor}s.
 */
public class KeyDescriptorMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final KeyDescriptor keyDescriptor = (KeyDescriptor) xmlObject;

        final UsageType use = keyDescriptor.getUse();
        if (use != null) {
            // UsageType enum contains more values than are allowed by SAML 2 schema
            if (use.equals(UsageType.SIGNING) || use.equals(UsageType.ENCRYPTION)) {
                domElement.setAttributeNS(null, KeyDescriptor.USE_ATTRIB_NAME, use.toString().toLowerCase());
            } else if (use.equals(UsageType.UNSPECIFIED)) {
                // emit nothing for unspecified - this is semantically equivalent to non-existent attribute
            } else {
                // Just in case values are unknowingly added to UsageType in the future...
                throw new MarshallingException("KeyDescriptor had illegal value for use attribute: " + use.toString());
            }
        }
    }

}