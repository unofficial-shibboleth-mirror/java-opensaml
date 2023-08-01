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

package org.opensaml.security.crypto.dh;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.DomainParameters;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.slf4j.Logger;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Cryptography support related to Elliptic Curve.
 */
public final class DHSupport {
    
    /** Logger. */
    @Nonnull private static final Logger LOG = LoggerFactory.getLogger(DHSupport.class);
    
    /** Constructor. */
    private DHSupport() { }
    
    /**
     * Perform DH key agreement between the given public and private keys.
     * 
     * @param publicKey the public key
     * @param privateKey the private key
     * @param provider the optional security provider to use
     * 
     * @return the secret produced by key agreement
     * 
     * @throws NoSuchAlgorithmException if algorithm is unknown
     * @throws NoSuchProviderException if provider is unknown
     * @throws InvalidKeyException if supplied key is invalid
     */
    @Nonnull public static byte[] performKeyAgreement(@Nonnull final DHPublicKey publicKey,
            @Nonnull final DHPrivateKey privateKey, @Nullable final String provider)
                    throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        Constraint.isNotNull(publicKey, "DHPublicKey was null");
        Constraint.isNotNull(privateKey, "DHPrivateKey was null");
        
        KeyAgreement keyAgreement = null;
        if (provider != null) {
            keyAgreement = KeyAgreement.getInstance(JCAConstants.KEY_AGREEMENT_DH, provider);
        } else {
            keyAgreement = KeyAgreement.getInstance(JCAConstants.KEY_AGREEMENT_DH);
        }
        
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);
        return keyAgreement.generateSecret();
    }

    /**
     * Generate a key pair whose parameters are compatible with those of the specified DH public key.
     * 
     * @param publicKey the public key
     * @param provider the optional security provider to use
     * 
     * @return the generated key pair
     * 
     * @throws NoSuchAlgorithmException if algorithm is unknown
     * @throws NoSuchProviderException if provider is unknown
     * @throws InvalidAlgorithmParameterException if the public key's {@link DHParameterSpec} is not supported
     */
    @Nonnull public static KeyPair generateCompatibleKeyPair(@Nonnull final DHPublicKey publicKey,
            @Nullable final String provider)
                    throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        Constraint.isNotNull(publicKey, "DHPublicKey was null");
        
        return KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_DIFFIE_HELLMAN, publicKey.getParams(), provider);
    }
    
    /**
     * Obtain the prime Q domain parameter from the specified DH public key.
     * 
     * <p>
     * Java's interface for DH domain parameters {@link DHParameterSpec} doesn't expose
     * the prime Q parameter, but in some contexts it is required, e.g XML Encryption <code>DHKeyValue</code>
     * element. The approach here is to parse the ASN.1 encoding of the key directly.
     * </p>
     * 
     * @param publicKey the public key
     * 
     * @return the prime Q domain parameter, or null if could not be processed
     */
    @Nullable public static BigInteger getPrimeQDomainParameter(@Nonnull final DHPublicKey publicKey) {
        Constraint.isNotNull(publicKey, "DHPublicKey was null");
        try (ASN1InputStream input = new ASN1InputStream(publicKey.getEncoded())) {
            final SubjectPublicKeyInfo spki = SubjectPublicKeyInfo.getInstance(input.readObject());
            if (spki.getAlgorithm().getParameters() != null) {
                final DomainParameters dp = DomainParameters.getInstance(spki.getAlgorithm().getParameters());
                return dp.getQ();
            }
            return null;
        } catch (final Exception e) {
            LOG.warn("Error processing DHPublicKey for prime Q parameter", e);
            return null;
        }
    }

} 