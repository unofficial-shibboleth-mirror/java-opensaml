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
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.saml2.core.IDPEntry;
import org.w3c.dom.Element;

/**
 * A thread safe Marshaller for {@link IDPEntry} objects.
 */
public class IDPEntryMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final IDPEntry entry = (IDPEntry) xmlObject;

        if (entry.getProviderID() != null) {
            domElement.setAttributeNS(null, IDPEntry.PROVIDER_ID_ATTRIB_NAME, entry.getProviderID());
        }
        if (entry.getName() != null) {
            domElement.setAttributeNS(null, IDPEntry.NAME_ATTRIB_NAME, entry.getName());
        }
        if (entry.getLoc() != null) {
            domElement.setAttributeNS(null, IDPEntry.LOC_ATTRIB_NAME, entry.getLoc());
        }
    }

}