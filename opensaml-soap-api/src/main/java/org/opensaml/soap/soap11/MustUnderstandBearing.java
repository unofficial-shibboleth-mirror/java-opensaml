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
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.soap.util.SOAPConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface for element having a <code>@soap11:mustUnderstand</code> attribute.
 */
public interface MustUnderstandBearing {
    
    /** The soap11:@mustUnderstand attribute local name. */
    @Nonnull @NotEmpty public static final String SOAP11_MUST_UNDERSTAND_ATTR_LOCAL_NAME = "mustUnderstand";

    /** The soap11:@mustUnderstand qualified attribute name. */
    @Nonnull public static final QName SOAP11_MUST_UNDERSTAND_ATTR_NAME =
        new QName(SOAPConstants.SOAP11_NS, SOAP11_MUST_UNDERSTAND_ATTR_LOCAL_NAME, SOAPConstants.SOAP11_PREFIX);
    
    /**
     * Get the attribute value.
     * 
     * @return return the attribute vlue
     */
    @Nullable public Boolean isSOAP11MustUnderstand();
    
    /**
     * Get the attribute value.
     * 
     * @return return the attribute vlue
     */
    @Nullable public XSBooleanValue isSOAP11MustUnderstandXSBoolean();
    
    /**
     * Set the attribute value.
     * 
     * @param newMustUnderstand the new attribute value
     */
    public void setSOAP11MustUnderstand(@Nullable final Boolean newMustUnderstand);
    
    /**
     * Set the attribute value.
     * 
     * @param newMustUnderstand the new attribute value
     */
    public void setSOAP11MustUnderstand(@Nullable final XSBooleanValue newMustUnderstand);

}
