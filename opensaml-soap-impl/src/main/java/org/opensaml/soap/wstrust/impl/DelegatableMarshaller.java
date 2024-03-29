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

package org.opensaml.soap.wstrust.impl;


import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.soap.wstrust.Delegatable;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.ElementSupport;

/**
 * Marshaller for the Delegatable element.
 * 
 */
public class DelegatableMarshaller extends AbstractWSTrustObjectMarshaller {

    /** {@inheritDoc} */
    protected void marshallElementContent(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final Delegatable delegatable = (Delegatable) xmlObject;
        final XSBooleanValue value= delegatable.getValue();
        if (value != null) {
            ElementSupport.appendTextContent(domElement, value.toString());
        }
    }

}