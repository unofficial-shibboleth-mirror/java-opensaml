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

package org.opensaml.xmlsec.signature;

import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;

/**
 * An XMLObject whose DOM representation can be digitally signed.
 */
public interface SignableXMLObject extends XMLObject {
        
    /**
     * Checks to see if the element has been signed.
     * 
     * @return true iff this element is signed
     */
    boolean isSigned();
    
    /**
     * Gets the Signature XMLObject.
     * 
     * @return the Signature XMLObject
     */
    @Nullable Signature getSignature();
    
    /**
     * Sets the Signature XMLObject.
     * 
     * @param newSignature the Signature XMLObject
     */
    void setSignature(@Nullable final Signature newSignature);
}