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
import org.opensaml.soap.wstrust.Renewing;
import org.w3c.dom.Element;

/**
 * Marshaller for the Renewing element.
 * 
 */
public class RenewingMarshaller extends AbstractWSTrustObjectMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final Renewing renewing = (Renewing) xmlObject;
        
        final XSBooleanValue allow = renewing.isAllowXSBoolean();
        if (allow != null && allow.getValue() != null) {
            domElement.setAttributeNS(null, Renewing.ALLOW_ATTRIB_NAME, allow.toString());
        }
        
        final XSBooleanValue ok = renewing.isOKXSBoolean();
        if (ok != null && ok.getValue() != null) {
            domElement.setAttributeNS(null, Renewing.OK_ATTRIB_NAME, ok.toString());
        }
    }
    
}