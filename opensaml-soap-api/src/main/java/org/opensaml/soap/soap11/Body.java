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

package org.opensaml.soap.soap11;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.ElementExtensibleXMLObject;
import org.opensaml.soap.common.SOAPObject;
import org.opensaml.soap.util.SOAPConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SOAP 1.1 Body.
 */
public interface Body extends SOAPObject, ElementExtensibleXMLObject, AttributeExtensibleXMLObject {
    
    /** Element local name. */
    @Nonnull @NotEmpty public static final String DEFAULT_ELEMENT_LOCAL_NAME = "Body";
    
    /** Default element name. */
    @Nonnull public static final QName DEFAULT_ELEMENT_NAME = 
        new QName(SOAPConstants.SOAP11_NS, DEFAULT_ELEMENT_LOCAL_NAME, SOAPConstants.SOAP11_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "Body"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = 
        new QName(SOAPConstants.SOAP11_NS, TYPE_LOCAL_NAME, SOAPConstants.SOAP11_PREFIX);
}
