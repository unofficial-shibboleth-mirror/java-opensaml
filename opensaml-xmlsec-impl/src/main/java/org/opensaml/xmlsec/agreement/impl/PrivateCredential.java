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

import java.security.PrivateKey;

import javax.annotation.Nonnull;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.agreement.KeyAgreementParameter;

import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * Key agreement parameter used to pass a Credential holding a required private key.
 * 
 * <p>
 * This is typically used in the decryption case to pass in the recipient's private credential.
 * </p>
 */
public class PrivateCredential implements KeyAgreementParameter {
    
    /** The wrapped Credential. */
    private Credential credential;

    /**
     * Constructor.
     *
     * @param newCredential the private credential, containing a {@link PrivateKey}
     */
    public PrivateCredential(@Nonnull final Credential newCredential) {
        credential = Constraint.isNotNull(newCredential, "Private Credential was null");
        Constraint.isNotNull(credential.getPrivateKey(), "Credential did not contain required PrivateKey");
    }
    
    /**
     * Get the wrapped credential.
     * 
     * @return the credential
     */
    @Nonnull public Credential getCredential() {
        return credential;
    }

}
