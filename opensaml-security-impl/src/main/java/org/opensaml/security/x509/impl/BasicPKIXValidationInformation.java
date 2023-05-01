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

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.annotation.Nullable;

import org.opensaml.security.x509.PKIXValidationInformation;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Basic implementation of {@link PKIXValidationInformation}.
 */
public class BasicPKIXValidationInformation implements PKIXValidationInformation {

    /** Certs used as the trust anchors. */
    @Nullable @NonnullElements private final Collection<X509Certificate> trustAnchors;

    /** CRLs used during validation. */
    @Nullable @NonnullElements private final Collection<X509CRL> trustedCRLs;

    /** Max verification depth during PKIX validation. */
    @Nullable private final Integer verificationDepth;

    /**
     * Constructor.
     * 
     * @param anchors certs used as trust anchors during validation
     * @param crls CRLs used during validation
     * @param depth max verification path depth
     */
    public BasicPKIXValidationInformation(
            @Nullable @NonnullElements @ParameterName(name="anchors") final Collection<X509Certificate> anchors,
            @Nullable @NonnullElements @ParameterName(name="crls") final Collection<X509CRL> crls,
            @Nullable @NonnullElements @ParameterName(name="depth") final Integer depth) {

        verificationDepth = depth;
        trustAnchors = anchors != null ? CollectionSupport.copyToList(anchors) : null;
        trustedCRLs = crls != null ? CollectionSupport.copyToList(crls) : null;
    }

    /** {@inheritDoc} */
    @Nullable @NonnullElements @Unmodifiable @NotLive public Collection<X509CRL> getCRLs() {
        return trustedCRLs;
    }

    /** {@inheritDoc} */
    @Nullable @NonnullElements @Unmodifiable @NotLive public Collection<X509Certificate> getCertificates() {
        return trustAnchors;
    }

    /** {@inheritDoc} */
    @Nullable public Integer getVerificationDepth() {
        return verificationDepth;
    }

}