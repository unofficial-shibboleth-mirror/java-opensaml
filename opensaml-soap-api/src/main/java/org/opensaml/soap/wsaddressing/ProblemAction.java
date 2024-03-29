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

package org.opensaml.soap.wsaddressing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface for element &lt;wsa:ProblemAction&gt;.
 * 
 * @see "WS-Addressing 1.0 - SOAP Binding"
 * 
 */
public interface ProblemAction extends AttributeExtensibleXMLObject, WSAddressingObject {
    
    /** Element local name. */
    @Nonnull @NotEmpty public static final String ELEMENT_LOCAL_NAME = "ProblemAction";

    /** Default element name. */
    @Nonnull public static final QName ELEMENT_NAME =
        new QName(WSAddressingConstants.WSA_NS, ELEMENT_LOCAL_NAME, WSAddressingConstants.WSA_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "ProblemActionType"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME = 
        new QName(WSAddressingConstants.WSA_NS, TYPE_LOCAL_NAME, WSAddressingConstants.WSA_PREFIX);
    
    /**
     * Get the Action child element.
     * 
     * @return the Action child element
     */
    @Nullable public Action getAction();
    
    /**
     * Set the Action child element.
     * 
     * @param newAction the new Action child element
     */
    public void setAction(@Nullable final Action newAction);

    /**
     * Get the SoapAction child element.
     * 
     * @return the SoapAction child element
     */
    @Nullable public SoapAction getSoapAction();
    
    /**
     * Set the SoapAction child element.
     * 
     * @param newSoapAction the new SoapAction child element
     */
    public void setSoapAction(@Nullable final SoapAction newSoapAction);
}
