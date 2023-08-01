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

package org.opensaml.saml.saml1.core.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml1.core.Condition;
import org.opensaml.saml.saml1.core.Conditions;
import org.w3c.dom.Attr;

import com.google.common.base.Strings;

import net.shibboleth.shared.xml.DOMTypeSupport;

/**
 * A thread-safe Unmarshaller for {@link Conditions} objects.
 */
public class ConditionsUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final Conditions conditions = (Conditions) parentObject;

        if (childObject instanceof Condition) {
            conditions.getConditions().add((Condition) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject samlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {

        final Conditions conditions = (Conditions) samlObject;

        if (attribute.getNamespaceURI() == null) {
            if (Conditions.NOTBEFORE_ATTRIB_NAME.equals(attribute.getLocalName())
                    && !Strings.isNullOrEmpty(attribute.getValue())) {
                conditions.setNotBefore(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else if (Conditions.NOTONORAFTER_ATTRIB_NAME.equals(attribute.getLocalName())
                    && !Strings.isNullOrEmpty(attribute.getValue())) {
                conditions.setNotOnOrAfter(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else {
                super.processAttribute(samlObject, attribute);
            }
        } else {
            super.processAttribute(samlObject, attribute);
        }
    }

}