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

package org.opensaml.xmlsec.signature.support;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.xmlsec.signature.Signature;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A service class which is responsible for cryptographically computing and storing the 
 * actual digital signature content held within a {@link Signature} instance.
 * 
 * This must be done as a separate step in order to support the following cases:
 * <ul>
 * <li>Multiple signable objects appear in the DOM tree, in which case the order that the objects should be signed in
 * is not known (e.g. object 1 could appear first in the tree, but contain a reference to signable object 2)</li>
 * <li>The DOM tree resulting from marshalling of the XMLObject tree is grafted onto another DOM tree which may cause
 * element ID conflicts that would invalidate the signature</li>
 * </ul>
 */
public final class Signer {
    
    /** Logger. */
    @Nonnull private static final Logger LOG = LoggerFactory.getLogger(Signer.class);
    
    /** The cached signer provider instance to use. */
    @Nullable private static SignerProvider signerInstance;

    /** Constructor. */
    protected Signer() { }

    /**
     * Signs the given XMLObject in the order provided.
     * 
     * @param signatures an ordered list of XMLObject to be signed
     * @throws SignatureException  thrown if there is an error computing the signature
     */
    public static void signObjects(@Nonnull final List<Signature> signatures)
            throws SignatureException {
        final SignerProvider signer = getSignerProvider();
        LOG.debug("Using a signer of implementation: {}", signer.getClass().getName());
        for (final Signature signature : signatures) {
            assert signature != null;
            signer.signObject(signature);
        }
    }

    /**
     * Signs a single XMLObject.
     * 
     * @param signature the signature to compute the signature on
     * @throws SignatureException thrown if there is an error computing the signature
     */
    public static void signObject(@Nonnull final Signature signature) throws SignatureException {
        final SignerProvider signer = getSignerProvider();
        LOG.debug("Using a signer of implemenation: {}", signer.getClass().getName());
        signer.signObject(signature);
    }
    
    /**
     * Obtain the {@link SignerProvider} instance to be used.
     * 
     * @return the SignerProvider
     * @throws SignatureException if a SignerProvider could not be loaded
     */
    @Nonnull private static synchronized SignerProvider getSignerProvider() throws SignatureException {
        if (signerInstance == null) {
            final ServiceLoader<SignerProvider> loader = ServiceLoader.load(SignerProvider.class);
            final Iterator<SignerProvider> iterator = loader.iterator();
            if (iterator.hasNext()) {
                signerInstance = iterator.next();
            } else {
                throw new SignatureException("Could not load a signer implementation via service API");
            }
        }
        assert signerInstance != null;
        return signerInstance;
    }

}