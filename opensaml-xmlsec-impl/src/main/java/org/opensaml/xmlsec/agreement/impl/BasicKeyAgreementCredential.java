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

package org.opensaml.xmlsec.agreement.impl;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;

import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.agreement.KeyAgreementCredential;
import org.opensaml.xmlsec.agreement.KeyAgreementParameters;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Basic implementation of {@link KeyAgreementCredential}.
 */
public class BasicKeyAgreementCredential extends BasicCredential implements KeyAgreementCredential {
    
    /** Algorithm URI. */
    @Nonnull private String algorithm;
    
    /** Originator credential. */
    @Nonnull private Credential originatorCredential;

    /** Recipient credential. */
    @Nonnull private Credential recipientCredential;
    
    /** Parameters. */
    @Nonnull private KeyAgreementParameters parameters;

    /**
     * Constructor.
     *
     * @param derivedKey the derived secret key
     * @param agreementAlgorithm the key agreement algorithm
     * @param originator the originator credential
     * @param recipient the recipient credential
     */
    public BasicKeyAgreementCredential(@Nonnull final SecretKey derivedKey, @Nonnull final String agreementAlgorithm,
            @Nonnull final Credential originator, @Nonnull final Credential recipient) {
        
        super(Constraint.isNotNull(derivedKey, "SecretKey was null"));
        algorithm = Constraint.isNotNull(StringSupport.trimOrNull(agreementAlgorithm), "Algorithm was null");
        originatorCredential = Constraint.isNotNull(originator, "Originator credential was null");
        recipientCredential = Constraint.isNotNull(recipient, "Recipient credential was null");
        parameters = new KeyAgreementParameters();
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull public Class<? extends Credential> getCredentialType() {
        return KeyAgreementCredential.class;
    }

    /** {@inheritDoc} */
    @Nonnull public String getAlgorithm() {
        return algorithm;
    }

    /** {@inheritDoc} */
    @Nonnull public Credential getOriginatorCredential() {
        return originatorCredential;
    }

    /** {@inheritDoc} */
    @Nonnull public Credential getRecipientCredential() {
        return recipientCredential;
    }

    /** {@inheritDoc} */
    @Nonnull public KeyAgreementParameters getParameters() {
        return parameters;
    }

}
