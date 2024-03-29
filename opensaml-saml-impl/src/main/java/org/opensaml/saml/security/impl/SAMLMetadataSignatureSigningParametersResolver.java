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

package org.opensaml.saml.security.impl;

import java.security.Key;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.impl.BasicSignatureSigningParametersResolver;
import org.slf4j.Logger;

/**
 * A specialization of {@link BasicSignatureSigningParametersResolver} which also supports input of SAML metadata, 
 * specifically the {@link SigningMethod} and {@link DigestMethod} extension elements.
 * 
 * <p>
 * In addition to the {@link net.shibboleth.shared.resolver.Criterion} inputs documented in 
 * {@link BasicSignatureSigningParametersResolver}, the following inputs are also supported:
 * </p>
 * <ul>
 * <li>{@link RoleDescriptorCriterion} - optional</li> 
 * </ul>
 */
public class SAMLMetadataSignatureSigningParametersResolver extends BasicSignatureSigningParametersResolver {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(SAMLMetadataSignatureSigningParametersResolver.class);

// Checkstyle: CyclomaticComplexity|ReturnCount OFF
    /** {@inheritDoc} */
    @Override
    protected void resolveAndPopulateCredentialAndSignatureAlgorithm(@Nonnull final SignatureSigningParameters params, 
            @Nonnull final CriteriaSet criteria, @Nonnull final Predicate<String> includeExcludePredicate) {
        
        final RoleDescriptorCriterion roleCrit = criteria.get(RoleDescriptorCriterion.class);
        if (roleCrit == null) {
            super.resolveAndPopulateCredentialAndSignatureAlgorithm(params, criteria, includeExcludePredicate);
            return;
        }
        
        final List<XMLObject> signingMethods = getExtensions(roleCrit.getRole(), SigningMethod.DEFAULT_ELEMENT_NAME);
        
        if (signingMethods == null || signingMethods.isEmpty()) {
            super.resolveAndPopulateCredentialAndSignatureAlgorithm(params, criteria, includeExcludePredicate);
            return;
        }
        
        final List<Credential> credentials = getEffectiveSigningCredentials(criteria);
        
        for (final XMLObject xmlObject : signingMethods) {
            final SigningMethod signingMethod = (SigningMethod) xmlObject;
            
            log.trace("Evaluating SAML metadata SigningMethod with algorithm: {}, minKeySize: {}, maxKeySize: {}", 
                    signingMethod.getAlgorithm(), signingMethod.getMinKeySize(), signingMethod.getMaxKeySize());
            
            if (signingMethod.getAlgorithm() == null 
                    || !getAlgorithmRuntimeSupportedPredicate().test(signingMethod.getAlgorithm())
                    || !includeExcludePredicate.test(signingMethod.getAlgorithm())) {
                continue;
            }
            
            for (final Credential credential : credentials) {
                assert credential != null;
                
                if (log.isTraceEnabled()) {
                    final Key key = CredentialSupport.extractSigningKey(credential);
                    log.trace("Evaluating credential of type: {}, with length: {}", 
                            key != null ? key.getAlgorithm() : "n/a",
                            key != null ? KeySupport.getKeyLength(key) : "n/a");
                }
                
                if (credentialSupportsSigningMethod(credential, signingMethod)) {
                    log.trace("Credential passed eval against SigningMethod");
                    log.debug("Resolved signature algorithm URI from SAML metadata SigningMethod: {}",
                            signingMethod.getAlgorithm());
                    params.setSigningCredential(credential);
                    params.setSignatureAlgorithm(signingMethod.getAlgorithm());
                    return;
                }
                log.trace("Credential failed eval against SigningMethod");
            }
        }
        
        log.debug("Could not resolve signing credential and algorithm based on SAML metadata, " 
                + "falling back to locally configured algorithms");
        
        super.resolveAndPopulateCredentialAndSignatureAlgorithm(params, criteria, includeExcludePredicate);
    }

    /**
     * Evaluate whether the specified credential is supported for use with the specified {@link SigningMethod}.
     * 
     * @param credential the credential to evaluate
     * @param signingMethod the signing method to evaluate
     * @return true if credential may be used with the supplied algorithm URI, false otherwise
     */
    protected boolean credentialSupportsSigningMethod(@Nonnull final Credential credential, 
            @Nonnull @NotEmpty final SigningMethod signingMethod) {
        
        final String signAlg = signingMethod.getAlgorithm();
        if (signAlg == null || !credentialSupportsAlgorithm(credential, signAlg)) {
            return false;
        }
        
        final Integer minSize = signingMethod.getMinKeySize();
        final Integer maxSize = signingMethod.getMaxKeySize();
        
        if (minSize != null  || maxSize != null) {
            final Key signingKey = CredentialSupport.extractSigningKey(credential);
            if (signingKey == null) {
                log.warn("Could not extract signing key from credential. Failing evaluation");
                return false;
            }
            
            final Integer keyLength = KeySupport.getKeyLength(signingKey);
            if (keyLength == null) {
                log.warn("Could not determine key length of candidate signing credential. Failing evaluation");
                return false;
            }
            
            if (minSize != null && keyLength < minSize) {
                log.trace("Candidate signing credential does not meet minKeySize requirement");
                return false;
            }
            
            if (maxSize != null && keyLength > maxSize) {
                log.trace("Candidate signing credential does not meet maxKeySize requirement");
                return false;
            }
        }
        
        return true;
    }
// Checkstyle: ReturnCount|CyclomaticComplexity ON

    /** {@inheritDoc} */
    @Override
    @Nullable protected String resolveReferenceDigestMethod(@Nonnull final CriteriaSet criteria, 
            @Nonnull final Predicate<String> includeExcludePredicate) {
        
        final RoleDescriptorCriterion roleCrit = criteria.get(RoleDescriptorCriterion.class);
        
        if (roleCrit == null) {
            return super.resolveReferenceDigestMethod(criteria, includeExcludePredicate);
        }
        
        final List<XMLObject> digestMethods = getExtensions(roleCrit.getRole(), DigestMethod.DEFAULT_ELEMENT_NAME);
        
        if (digestMethods == null || digestMethods.isEmpty()) {
            return super.resolveReferenceDigestMethod(criteria, includeExcludePredicate);
        }
        
        for (final XMLObject xmlObject : digestMethods) {
            final DigestMethod digestMethod = (DigestMethod) xmlObject;
            
            log.trace("Evaluating SAML metadata DigestMethod with algorithm: {}", digestMethod.getAlgorithm());
            
            if (digestMethod.getAlgorithm() != null 
                    && getAlgorithmRuntimeSupportedPredicate().test(digestMethod.getAlgorithm())
                    && includeExcludePredicate.test(digestMethod.getAlgorithm())) {
                log.debug("Resolved reference digest method algorithm URI from SAML metadata DigestMethod: {}",
                        digestMethod.getAlgorithm());
                return digestMethod.getAlgorithm();
            }
        }
        
        log.debug("Could not resolve signature reference digest method algorithm based on SAML metadata, " 
                + "falling back to locally configured algorithms");
        
        return super.resolveReferenceDigestMethod(criteria, includeExcludePredicate);
    }
    
    /**
     * Get the extensions indicated by the passed QName.  The passed RoleDescriptor's Extensions element
     * is examined first. If at least 1 such extension is found there, that list is returned.
     * If no such extensions are found on the RoleDescriptor, then the RoleDescriptor's parent EntityDescriptor 
     * will be examined, if it exists.
     * 
     * @param roleDescriptor the role descriptor instance to examine
     * @param extensionName the extension name for which to search
     * @return the list of extension XMLObjects found, or null
     */
    @Nullable @Unmodifiable @NotLive protected List<XMLObject> getExtensions(
            @Nonnull final RoleDescriptor roleDescriptor, @Nonnull final QName extensionName) {
        List<XMLObject> result;
        Extensions extensions = roleDescriptor.getExtensions();
        if (extensions != null) {
            result = extensions.getUnknownXMLObjects(extensionName);
            if (!result.isEmpty()) {
                log.trace("Resolved extensions from RoleDescriptor: {}", extensionName);
                return result;
            }
        }
        
        if (roleDescriptor.getParent() instanceof EntityDescriptor entity) {
            extensions = entity.getExtensions();
            if (extensions != null) {
                result = extensions.getUnknownXMLObjects(extensionName);
                if (!result.isEmpty()) {
                    log.trace("Resolved extensions from parent EntityDescriptor: {}", extensionName);
                    return result;
                }
            }
        }
        return null;
    }

}