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
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.core.BaseID;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;

/**
 * A thread-safe Unmarshaller for {@link Subject} objects.
 */
public class SubjectUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final Subject subject = (Subject) parentObject;

        if (childObject instanceof BaseID) {
            subject.setBaseID((BaseID) childObject);
        } else if (BaseID.DEFAULT_ELEMENT_NAME.equals(childObject.getElementQName())
                && XSAny.class.isInstance(childObject)) {
            subject.setBaseID(new BaseIDXSAnyAdapter(XSAny.class.cast(childObject)));
        } else if (childObject instanceof NameID) {
            subject.setNameID((NameID) childObject);
        } else if (childObject instanceof EncryptedID) {
            subject.setEncryptedID((EncryptedID) childObject);
        } else if (childObject instanceof SubjectConfirmation) {
            subject.getSubjectConfirmations().add((SubjectConfirmation) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }
    
}