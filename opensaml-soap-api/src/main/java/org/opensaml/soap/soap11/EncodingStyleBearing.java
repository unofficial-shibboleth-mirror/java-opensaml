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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.soap.util.SOAPConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface for element having a <code>@soap11:encodingStyle</code> attribute.
 */
public interface EncodingStyleBearing {
    
    /** The soap11:@encodingStyle attribute local name. */
    @Nonnull @NotEmpty public static final String SOAP11_ENCODING_STYLE_ATTR_LOCAL_NAME = "encodingStyle";

    /** The soap11:@encodingStyle qualified attribute name. */
    @Nonnull public static final QName SOAP11_ENCODING_STYLE_ATTR_NAME =
        new QName(SOAPConstants.SOAP11_NS, SOAP11_ENCODING_STYLE_ATTR_LOCAL_NAME, SOAPConstants.SOAP11_PREFIX);
    
    /**
     * Get the attribute value.
     * 
     * @return return the list of attribute values
     */
    @Nullable public List<String> getSOAP11EncodingStyles();
    
    /**
     * Set the attribute value.
     * 
     * @param newEncodingStyles the new list of attribute values
     */
    public void setSOAP11EncodingStyles(@Nullable final List<String> newEncodingStyles);

}
