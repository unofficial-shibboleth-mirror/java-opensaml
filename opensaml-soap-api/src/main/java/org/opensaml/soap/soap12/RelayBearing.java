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

package org.opensaml.soap.soap12;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.soap.util.SOAPConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface for element having a <code>@soap12:relay</code> attribute.
 */
public interface RelayBearing {
    
    /** The soap12:@relay attribute local name. */
    @Nonnull @NotEmpty public static final String SOAP12_RELAY_ATTR_LOCAL_NAME = "relay";

    /** The soap12:@relay qualified attribute name. */
    @Nonnull public static final QName SOAP12_RELAY_ATTR_NAME =
        new QName(SOAPConstants.SOAP12_NS, SOAP12_RELAY_ATTR_LOCAL_NAME, SOAPConstants.SOAP12_PREFIX);
    
    /**
     * Get the attribute value.
     * 
     * @return return the attribute vlue
     */
    @Nullable public Boolean isSOAP12Relay();
    
    /**
     * Get the attribute value.
     * 
     * @return return the attribute vlue
     */
    @Nullable public XSBooleanValue isSOAP12RelayXSBoolean();
    
    /**
     * Set the attribute value.
     * 
     * @param newRelay the new attribute value
     */
    public void setSOAP12Relay(@Nullable final Boolean newRelay);
    
    /**
     * Set the attribute value.
     * 
     * @param newRelay the new attribute value
     */
    public void setSOAP12Relay(@Nullable final XSBooleanValue newRelay);

}
