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

package org.opensaml.xmlsec.keyinfo.impl;

import javax.annotation.Nonnull;

import org.opensaml.security.credential.CredentialContext;
import org.opensaml.xmlsec.signature.KeyInfo;

import net.shibboleth.shared.annotation.ParameterName;

/**
 * Context for credentials resolved from a {@link KeyInfo} element.
 */
public class KeyInfoCredentialContext implements CredentialContext {
    
    /** The KeyInfo context. */
    @Nonnull private final KeyInfo keyInfo;
    
    /**
     * Constructor.
     *
     * @param ki the KeyInfo context 
     */
    public KeyInfoCredentialContext(@Nonnull @ParameterName(name="ki") final KeyInfo ki) {
       keyInfo = ki; 
    }
    
    /**
     * Get the KeyInfo context from which the credential was derived.
     * 
     * @return the KeyInfo context
     */
    @Nonnull public KeyInfo getKeyInfo() {
        return keyInfo;
    }

}