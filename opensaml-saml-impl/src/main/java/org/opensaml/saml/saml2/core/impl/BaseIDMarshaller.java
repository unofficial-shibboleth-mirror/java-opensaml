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
import org.opensaml.saml.saml2.core.BaseID;
import org.w3c.dom.Element;

/**
 * A thread-safe Marshaller for {@link BaseID} objects.
 */
public abstract class BaseIDMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final BaseID baseID = (BaseID) xmlObject;

        if (baseID.getNameQualifier() != null) {
            domElement.setAttributeNS(null, BaseID.NAME_QUALIFIER_ATTRIB_NAME, baseID.getNameQualifier());
        }

        if (baseID.getSPNameQualifier() != null) {
            domElement.setAttributeNS(null, BaseID.SP_NAME_QUALIFIER_ATTRIB_NAME, baseID.getSPNameQualifier());
        }
    }

}