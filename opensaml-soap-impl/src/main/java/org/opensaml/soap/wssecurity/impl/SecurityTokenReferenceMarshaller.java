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

package org.opensaml.soap.wssecurity.impl;

import java.util.List;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.soap.wssecurity.SecurityTokenReference;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

/**
 * SecurityTokenReferenceMarshaller.
 * 
 */
public class SecurityTokenReferenceMarshaller extends AbstractWSSecurityObjectMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final SecurityTokenReference str = (SecurityTokenReference) xmlObject;
        
        if (!Strings.isNullOrEmpty(str.getWSUId())) {
            XMLObjectSupport.marshallAttribute(SecurityTokenReference.WSU_ID_ATTR_NAME, str.getWSUId(), domElement,
                    true);
        }
        
        final List<String> usages = str.getWSSEUsages();
        if (usages != null && ! usages.isEmpty()) {
            XMLObjectSupport.marshallAttribute(SecurityTokenReference.WSSE_USAGE_ATTR_NAME, usages, domElement, false);
        }
        
        XMLObjectSupport.marshallAttributeMap(str.getUnknownAttributes(), domElement);
    }
    
    /** {@inheritDoc} */
    protected void marshallAttributeIDness(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        XMLObjectSupport.marshallAttributeIDness(SecurityTokenReference.WSU_ID_ATTR_NAME, domElement, true);
        
        super.marshallAttributeIDness(xmlObject, domElement);
    }

}
