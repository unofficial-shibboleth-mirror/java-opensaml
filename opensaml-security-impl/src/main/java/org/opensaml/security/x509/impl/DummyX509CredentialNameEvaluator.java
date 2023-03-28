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

package org.opensaml.security.x509.impl;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.SecurityException;
import org.opensaml.security.x509.X509Credential;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A dummy implementation of {@link X509CredentialNameEvaluator} which always returns true.
 * 
 * <p>This is a convenience class to assist with cases where it may be necessary to always
 * have an instance of the interface but not always perform the checking.</p>
 * 
 * @since 5.0.0
 */
public class DummyX509CredentialNameEvaluator implements X509CredentialNameEvaluator {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(DummyX509CredentialNameEvaluator.class);

    /**
     * {@inheritDoc} 
     * 
     * <p>
     * If the set of trusted names is null or empty, or if no supported name types are configured to be
     * checked, then the evaluation is considered successful.
     * </p>
     * 
     */
    public boolean evaluate(@Nonnull final X509Credential credential, @Nullable final Set<String> trustedNames)
            throws SecurityException {

        log.debug("dummy name evaluator returning true");
        return true;
    }
    
}