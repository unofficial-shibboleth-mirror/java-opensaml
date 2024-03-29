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

package org.opensaml.xmlsec.signature.support;

import javax.annotation.Nonnull;

import org.opensaml.security.credential.CredentialContext;
import org.opensaml.xmlsec.signature.Signature;

/**
 * A credential context for credentials resolved from a {@link org.opensaml.xmlsec.signature.KeyInfo} that was found in 
 * in XML Signature {@link Signature} element.
 */
public class XMLSignatureCredentialContext implements CredentialContext {

    /** The Signature element context. */ 
    @Nonnull private final Signature sig;
    
    /**
     * Constructor.
     *
     * @param signature the signature resolution context
     */
    public XMLSignatureCredentialContext(@Nonnull final Signature signature) {
        sig = signature;
    }

    /**
     * Gets the Signature element context.
     * 
     * @return signature context
     */
    @Nonnull public Signature getSignature() {
        return sig;
    }
    
}