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

package org.opensaml.xmlsec.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.xmlsec.SignatureValidationConfiguration;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;

/**
 * Basic implementation of {@link SignatureValidationConfiguration}.
 */
public class BasicSignatureValidationConfiguration extends BasicAlgorithmPolicyConfiguration
        implements SignatureValidationConfiguration {
    
    /** The signature trust engine to use. */
    @Nullable private SignatureTrustEngine signatureTrustEngine;
    
    //TODO chaining to parent config instance on getters? or use a wrapping proxy, etc?
    
    /**
     * Get the signature trust engine to use.
     * 
     * @return the signature trust engine
     */
    @Nullable public SignatureTrustEngine getSignatureTrustEngine() {
        return signatureTrustEngine;
    }

    /**
     * Set the signature trust engine to use.
     * 
     * @param engine the signature trust engine
     * 
     * @return this object
     */
    @Nonnull public BasicSignatureValidationConfiguration setSignatureTrustEngine(
            @Nullable final SignatureTrustEngine engine) {
        signatureTrustEngine = engine;
        
        return this;
    }

}