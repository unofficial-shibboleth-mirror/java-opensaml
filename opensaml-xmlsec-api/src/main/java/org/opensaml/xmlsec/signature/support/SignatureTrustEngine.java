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
import javax.annotation.Nullable;

import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.Signature;

import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * Evaluates the trustworthiness and validity of XML or raw Signatures against implementation-specific requirements.
 */
public interface SignatureTrustEngine extends TrustEngine<Signature> {
    
    /**
     * Get the KeyInfoCredentialResolver instance used to resolve (advisory) signing credential information
     * from KeyInfo elements contained within a Signature element.
     * 
     * Note that credential(s) obtained via this resolver are not themselves trusted.  They must be evaluated
     * against the trusted credential information obtained from the trusted credential resolver.
     * 
     * @return a KeyInfoCredentialResolver instance
     */
    @Nullable KeyInfoCredentialResolver getKeyInfoResolver();

    /**
     * Determines whether a raw signature over specified content is valid and signed by a trusted credential.
     * 
     * <p>A candidate verification credential may optionally be supplied.  If one is supplied and is
     * determined to successfully verify the signature, an attempt will be made to establish
     * trust on this basis.</p>
     * 
     * <p>If a candidate credential is not supplied, or it does not successfully verify the signature,
     * some implementations may be able to resolve candidate verification credential(s) in an
     * implementation-specific manner based on the trusted criteria supplied, and then attempt 
     * to verify the signature and establish trust on this basis.</p>
     * 
     * @param signature the signature value
     * @param content the content that was signed
     * @param algorithmURI the signature algorithm URI which was used to sign the content
     * @param trustBasisCriteria criteria used to describe and/or resolve the information
     *          which serves as the basis for trust evaluation
     * @param candidateCredential the untrusted candidate credential containing the validation key
     *          for the signature (optional)
     * 
     * @return true if the signature was valid for the provided content and was signed by a key
     *          contained within a credential established as trusted based on the supplied criteria,
     *          otherwise false
     * 
     * @throws SecurityException thrown if there is a problem attempting to verify the signature such as the signature
     *             algorithm not being supported
     */
    boolean validate(@Nonnull final byte[] signature, @Nonnull final byte[] content,
            @Nonnull final String algorithmURI, @Nullable final CriteriaSet trustBasisCriteria,
            @Nullable final Credential candidateCredential) throws SecurityException;
}