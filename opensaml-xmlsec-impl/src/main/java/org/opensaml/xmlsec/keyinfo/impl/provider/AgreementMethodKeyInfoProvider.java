/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xmlsec.keyinfo.impl.provider;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialContext;
import org.opensaml.xmlsec.agreement.KeyAgreementCredential;
import org.opensaml.xmlsec.agreement.KeyAgreementException;
import org.opensaml.xmlsec.agreement.KeyAgreementParameters;
import org.opensaml.xmlsec.agreement.KeyAgreementProcessor;
import org.opensaml.xmlsec.agreement.KeyAgreementProcessorRegistry;
import org.opensaml.xmlsec.agreement.KeyAgreementSupport;
import org.opensaml.xmlsec.agreement.impl.KeyAgreementParametersParser;
import org.opensaml.xmlsec.agreement.impl.PrivateCredential;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.EncryptedType;
import org.opensaml.xmlsec.encryption.OriginatorKeyInfo;
import org.opensaml.xmlsec.encryption.RecipientKeyInfo;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolutionMode;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolutionMode.Mode;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCriterion;
import org.opensaml.xmlsec.keyinfo.impl.KeyInfoResolutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.shared.collection.LazySet;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

/**
 * Implementation of {@link org.opensaml.xmlsec.keyinfo.impl.KeyInfoProvider} which supports {@link AgreementMethod}.
 */
public class AgreementMethodKeyInfoProvider extends AbstractKeyInfoProvider {
    
    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(AgreementMethodKeyInfoProvider.class);
    
    /** Parser for AgreementMethod parameters. */
    private final KeyAgreementParametersParser parametersParser = new KeyAgreementParametersParser();

    /** {@inheritDoc} */
    public boolean handles(@Nonnull final XMLObject keyInfoChild) {
        if (!AgreementMethod.class.isInstance(keyInfoChild)) {
            log.debug("XMLObject is not an AgreementMethod");
            return false;
        }
        final AgreementMethod agreementMethod = AgreementMethod.class.cast(keyInfoChild);
        
        final KeyAgreementProcessorRegistry registry = KeyAgreementSupport.getGlobalProcessorRegistry();
        if (registry == null) {
            log.debug("Global KeyAgreementProcessorRegistry is not configured");
            return false;
        }
        
        if (!registry.getRegisteredAlgorithms().contains(agreementMethod.getAlgorithm())) {
            log.debug("No KeyAgreementProcessor registered for algorithm: {}", agreementMethod.getAlgorithm());
            return false;
        }
        
        if (agreementMethod.getParent() == null || agreementMethod.getParent().getParent() == null
                || !EncryptedType.class.isInstance(agreementMethod.getParent().getParent())) {
            log.debug("AgreementMethod is not the grandchild of an EncryptedType element");
            return false;
        }
        
        return true;
    }

    /** {@inheritDoc} */
    @Nullable public Collection<Credential> process(@Nonnull final KeyInfoCredentialResolver resolver,
            @Nonnull final XMLObject keyInfoChild, @Nullable final CriteriaSet criteriaSet,
            @Nonnull final KeyInfoResolutionContext kiContext) throws SecurityException {
        
        // Sanity check
        if (!handles(keyInfoChild)) {
            return null;
        }
        
        final AgreementMethod agreementMethod = AgreementMethod.class.cast(keyInfoChild);
        final KeyAgreementProcessor processor =
                KeyAgreementSupport.getGlobalProcessorRegistry().getProcessor(agreementMethod.getAlgorithm());
        
        log.debug("Attempting to process key agreemenent for algorithm: {}", processor.getAlgorithm());
        
        KeyAgreementCredential cred = null;
        try {
            final Credential originatorCredential = resolveOriginatorCredential(agreementMethod, resolver);
            final Credential recipientCredential = resolveRecipientCredential(agreementMethod, resolver);
            
            final KeyAgreementParameters parameters = parametersParser.parse(agreementMethod);
            parameters.add(new PrivateCredential(recipientCredential));
            
            final String keyAlgorithm = resolveKeyAlgorithm(agreementMethod);
            
            cred = processor.execute(originatorCredential, keyAlgorithm, parameters);
            
        } catch (final KeyAgreementException e) {
            log.error("Error processing AgreementMethod with algorithm: {}", processor.getAlgorithm(), e);
            throw new SecurityException("Error processing AgreementMethod", e);
        }
        
        cred.getKeyNames().addAll(kiContext.getKeyNames());

        final CredentialContext credContext = buildCredentialContext(kiContext);
        if (credContext != null) {
            cred.getCredentialContextSet().add(credContext);
        }

        log.debug("Credential successfully produced by AgreementMethod with algorithm: {}", cred.getAlgorithm());
        final LazySet<Credential> credentialSet = new LazySet<>();
        credentialSet.add(cred);
        return credentialSet;
    }

    /**
     * Resolve the encryption algorithm URI to be used with the derived key.
     * 
     * <p>
     * This comes from the AgreementMethod's grandparent's EncryptionMethod child element.
     * </p>
     * 
     * @param agreementMethod the AgreementMethod to process
     * 
     * @return the encryption algorithm URI
     * 
     * @throws SecurityException if the algorithm URI can not be resolved
     */
    @Nonnull private String resolveKeyAlgorithm(@Nonnull final AgreementMethod agreementMethod)
            throws SecurityException {
        
        // This was already validated in handles(...)
        final EncryptedType encrytpedType = EncryptedType.class.cast(agreementMethod.getParent().getParent());
        
        if (encrytpedType.getEncryptionMethod() == null || encrytpedType.getEncryptionMethod().getAlgorithm() == null) {
            throw new SecurityException("EncryptedType contains no EncryptionMethod algorithm");
        }
        
        return encrytpedType.getEncryptionMethod().getAlgorithm();
    }

    /**
     * Resolve the originator {@link Credential} from the {@link OriginatorKeyInfo} element.
     * 
     * <p>
     * This will be the public key credential from the encrypting party.
     * </p>
     * 
     * @param agreementMethod the AgreementMethod to process
     * @param resolver the KeyInfoCredentialResolver to use
     * 
     * @return the originator credential
     * 
     * @throws SecurityException if the originator credential can not be resolved
     */
    @Nonnull private Credential resolveOriginatorCredential(@Nonnull final AgreementMethod agreementMethod,
            @Nonnull final KeyInfoCredentialResolver resolver) throws SecurityException {
        
        if (agreementMethod.getOriginatorKeyInfo() == null) {
            throw new SecurityException("AgreementMethod OriginatorKeyInfo was null");
        }
        
        final CriteriaSet criteria = new CriteriaSet(
                new KeyInfoCriterion(agreementMethod.getOriginatorKeyInfo()),
                new KeyInfoCredentialResolutionMode(Mode.PUBLIC));
        try {
            final Credential cred = resolver.resolveSingle(criteria);
            if (cred == null) {
                throw new SecurityException("Failed to resolve Credential from OriginatorKeyInfo ");
            }
            return cred;
        } catch (final ResolverException e) {
            throw new SecurityException("Error resolving Credential from OriginatorKeyInfo", e);
        }
    }

    /**
     * Resolve the recipient {@link Credential} from the {@link RecipientKeyInfo} element.
     * 
     * <p>
     * This will be the private key credential from the decrypting party (this party).
     * </p>
     * 
     * @param agreementMethod the AgreementMethod to process
     * @param resolver the KeyInfoCredentialResolver to use
     * 
     * @return the recipient credential
     * 
     * @throws SecurityException if the recipient credential can not be resolved or does not contain
     *                           a private key
     */
    @Nonnull private Credential resolveRecipientCredential(@Nonnull final AgreementMethod agreementMethod,
            @Nonnull final KeyInfoCredentialResolver resolver) throws SecurityException {
        
        if (agreementMethod.getRecipientKeyInfo() == null) {
            throw new SecurityException("AgreementMethod RecipientKeyInfo was null");
        }
        
        final CriteriaSet criteria = new CriteriaSet(new KeyInfoCriterion(agreementMethod.getRecipientKeyInfo()));
        try {
            final Credential cred = resolver.resolveSingle(criteria);
            if (cred == null) {
                throw new SecurityException("Failed to resolve Credential from RecipientKeyInfo ");
            }
            if (cred.getPrivateKey() == null) {
                throw new SecurityException("Credential resolved from RecipientKeyInfo did not contain PrivateKey");
            }
            return cred;
        } catch (final ResolverException e) {
            throw new SecurityException("Error resolving Credential from RecipientKeyInfo", e);
        }
    }

}
