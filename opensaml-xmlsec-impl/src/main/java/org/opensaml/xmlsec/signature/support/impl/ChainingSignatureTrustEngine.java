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

package org.opensaml.xmlsec.signature.support.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;

import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.slf4j.Logger;

/**
 * Evaluate a signature in sequence using a chain of subordinate trust engines. If the signature may be established as
 * trusted by any of the subordinate engines, the token is considered trusted. Otherwise it is considered untrusted.
 */
public class ChainingSignatureTrustEngine implements SignatureTrustEngine {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ChainingSignatureTrustEngine.class);

    /** The chain of subordinate trust engines. */
    @Nonnull private List<SignatureTrustEngine> engines;

    /**
     *  Constructor. 
     *  
     *  @param chain the list of trust engines in the chain
     */
    public ChainingSignatureTrustEngine(@Nonnull @ParameterName(name="chain") final List<SignatureTrustEngine> chain) {
        engines = CollectionSupport.copyToList(Constraint.isNotNull(chain, "SignatureTrustEngine list cannot be null"));
    }

    /**
     * Get the list of configured trust engines which constitute the trust evaluation chain.
     * 
     * @return the modifiable list of trust engines in the chain
     */
    @Nonnull @Unmodifiable @NotLive public List<SignatureTrustEngine> getChain() {
        return engines;
    }

    /** {@inheritDoc} */
    @Nullable public KeyInfoCredentialResolver getKeyInfoResolver() {
        // Chaining signature trust engine does not support an attached KeyInfoResolver
        return null;
    }

    /** {@inheritDoc} */
    public boolean validate(@Nonnull final Signature token, @Nullable final CriteriaSet trustBasisCriteria)
            throws SecurityException {
        
        for (final SignatureTrustEngine engine : engines) {
            if (engine.validate(token, trustBasisCriteria)) {
                log.debug("Signature was trusted by chain member: {}", engine.getClass().getName());
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    public boolean validate(@Nonnull final byte[] signature, @Nonnull final byte[] content,
            @Nonnull final String algorithmURI, @Nullable final CriteriaSet trustBasisCriteria,
            @Nullable final Credential candidateCredential) throws SecurityException {
        
        for (final SignatureTrustEngine engine : engines) {
            if (engine.validate(signature, content, algorithmURI, trustBasisCriteria, candidateCredential)) {
                log.debug("Signature was trusted by chain member: {}", engine.getClass().getName());
                return true;
            }
        }
        return false;
    }

}