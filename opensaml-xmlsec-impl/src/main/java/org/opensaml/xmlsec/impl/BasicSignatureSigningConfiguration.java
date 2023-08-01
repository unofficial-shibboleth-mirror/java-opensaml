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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.StringSupport;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;

/**
 * Basic implementation of {@link SignatureSigningConfiguration}.
 */
public class BasicSignatureSigningConfiguration extends BasicAlgorithmPolicyConfiguration 
        implements SignatureSigningConfiguration {
    
    /** Signing credentials. */
    @Nonnull private List<Credential> signingCredentials;
    
    /** Signature method algorithm URIs. */
    @Nonnull private List<String> signatureAlgorithms;
    
    /** Digest method algorithm URIs. */
    @Nonnull private List<String> signatureReferenceDigestMethods;
    
    /** The signature reference canonicalization transform algorithm. */
    @Nullable private String signatureReferenceCanonicalizationAlgorithm;
    
    /** Signature canonicalization algorithm URI. */
    @Nullable private String signatureCanonicalization;
    
    /** Signature HMAC output length. */
    @Nullable private Integer signatureHMACOutputLength;
    
    /** Manager for named KeyInfoGenerator instances. */
    @Nullable private NamedKeyInfoGeneratorManager keyInfoGeneratorManager;
    
    //TODO chaining to parent config instance on getters? or use a wrapping proxy, etc?
    
    //TODO update for modern coding conventions, Guava, etc
    
    /** Constructor. */
    public BasicSignatureSigningConfiguration() {
        signingCredentials = CollectionSupport.emptyList();
        signatureAlgorithms = CollectionSupport.emptyList();
        signatureReferenceDigestMethods = CollectionSupport.emptyList();
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @Unmodifiable @NotLive public List<Credential> getSigningCredentials() {
        return signingCredentials;
    }
    
    /**
     * Set the signing credentials to use when signing.
     * 
     * @param credentials the list of signing credentials
     * 
     * @return this object
     */
    @Nonnull public BasicSignatureSigningConfiguration setSigningCredentials(
            @Nullable final List<Credential> credentials) {
        if (credentials == null) {
            signingCredentials = CollectionSupport.emptyList();
        } else {
            signingCredentials = CollectionSupport.copyToList(credentials);
        }
        
        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @Unmodifiable @NotLive public List<String> getSignatureAlgorithms() {
        return signatureAlgorithms;
    }
    
    /**
     * Set the signature algorithms to use when signing.
     * 
     * @param algorithms the list of signature algorithms
     * 
     * @return this object
     */
    @Nonnull public BasicSignatureSigningConfiguration setSignatureAlgorithms(@Nullable final List<String> algorithms) {
        if (algorithms == null) {
            signatureAlgorithms = CollectionSupport.emptyList();
        } else {
            signatureAlgorithms = CollectionSupport.copyToList(StringSupport.normalizeStringCollection(algorithms));
        }
        
        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @Unmodifiable @NotLive public List<String> getSignatureReferenceDigestMethods() {
        return signatureReferenceDigestMethods;
    }
    
    /**
     * Set a digest method algorithm URI suitable for use as a Signature Reference DigestMethod value.
     * 
     * @param algorithms a list of digest method algorithm URIs
     * 
     * @return this object
     */
    @Nonnull public BasicSignatureSigningConfiguration setSignatureReferenceDigestMethods(
            @Nullable final List<String> algorithms) {
        if (algorithms == null) {
            signatureReferenceDigestMethods = CollectionSupport.emptyList();
        } else {
            signatureReferenceDigestMethods =
                    CollectionSupport.copyToList(StringSupport.normalizeStringCollection(algorithms));
        }
        
        return this;
    }
    
    /**
     * Get a canonicalization algorithm URI suitable for use as a Signature Reference Transform value.
     * 
     * @return a digest method algorithm URI
     */
    @Override
    @Nullable public String getSignatureReferenceCanonicalizationAlgorithm() {
        return signatureReferenceCanonicalizationAlgorithm;
    }

    /**
     * Get a canonicalization algorithm URI suitable for use as a Signature Reference Transform value.
     * 
     * @param uri a canonicalization algorithm URI
     * 
     * @return this object
     */
    @Nonnull public BasicSignatureSigningConfiguration setSignatureReferenceCanonicalizationAlgorithm(
            @Nullable final String uri) {
        signatureReferenceCanonicalizationAlgorithm = StringSupport.trimOrNull(uri);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public String getSignatureCanonicalizationAlgorithm() {
        return signatureCanonicalization;
    }
    
    /**
     * Set a canonicalization algorithm URI suitable for use as a Signature CanonicalizationMethod value.
     * 
     * @param algorithmURI a canonicalization algorithm URI
     * 
     * @return this object
     */
    @Nonnull public BasicSignatureSigningConfiguration setSignatureCanonicalizationAlgorithm(
            @Nullable final String algorithmURI) {
        signatureCanonicalization = StringSupport.trimOrNull(algorithmURI);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public Integer getSignatureHMACOutputLength() {
        return signatureHMACOutputLength;
    }
    
    /**
     * Set the value to be used as the Signature SignatureMethod HMACOutputLength value, used
     * only when signing with an HMAC algorithm.  This value is optional when using HMAC.
     * 
     * @param length the HMAC output length value to use when performing HMAC signing (may be null)
     * 
     * @return this object
     */
    @Nonnull public BasicSignatureSigningConfiguration setSignatureHMACOutputLength(
            @Nullable final Integer length) {
        signatureHMACOutputLength = length;
        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable public NamedKeyInfoGeneratorManager getKeyInfoGeneratorManager() {
        return keyInfoGeneratorManager;
    }
    
    /**
     * Set the manager for named KeyInfoGenerator instances.
     * 
     * @param keyInfoManager the KeyInfoGenerator manager to use
     * 
     * @return this object
     */
    @Nonnull public BasicSignatureSigningConfiguration setKeyInfoGeneratorManager(
            @Nullable final NamedKeyInfoGeneratorManager keyInfoManager) {
        keyInfoGeneratorManager = keyInfoManager;
        return this;
    }

}