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

package org.opensaml.security.crypto.ec;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.KeyAgreement;

import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.crypto.ec.curves.BasicNamedCurve;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.logic.Constraint;

/**
 * Cryptography support related to Elliptic Curve.
 */
public final class ECSupport {
    
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(ECSupport.class);
    
    /** Constructor. */
    private ECSupport() { }
    
    /**
     * Perform ECDH key agreement between the given public and private keys.
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
    @Nonnull public static byte[] performKeyAgreement(@Nonnull final ECPublicKey publicKey,
            @Nonnull final ECPrivateKey privateKey, @Nullable final String provider)
                    throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        Constraint.isNotNull(publicKey, "ECPublicKey was null");
        Constraint.isNotNull(privateKey, "ECPrivateKey was null");
        
        KeyAgreement keyAgreement = null;
        if (provider != null) {
            keyAgreement = KeyAgreement.getInstance(JCAConstants.KEY_AGREEMENT_ECDH, provider);
        } else {
            keyAgreement = KeyAgreement.getInstance(JCAConstants.KEY_AGREEMENT_ECDH);
        }
        
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);
        return keyAgreement.generateSecret();
    }

    /**
     * Generate a key pair whose parameters are compatible with those of the specified EC public key.
     * 
     * @param publicKey the public key
     * @param provider the optional security provider to use
     * 
     * @return the generated key pair
     * 
     * @throws NoSuchAlgorithmException if algorithm is unknown
     * @throws NoSuchProviderException if provider is unknown
     * @throws InvalidAlgorithmParameterException if the public key's {@link ECParameterSpec} is not supported
     */
    @Nonnull public static KeyPair generateCompatibleKeyPair(@Nonnull final ECPublicKey publicKey,
            @Nullable final String provider)
                    throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        Constraint.isNotNull(publicKey, "ECPublicKey was null");
        
        return KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, publicKey.getParams(), provider);
    }
    
    /**
     * Get the global {@link NamedCurveRegistry} instance.
     * 
     * @return the global named curve registry, or null if nothing registered
     */
    @Nullable public static NamedCurveRegistry getGlobalNamedCurveRegistry() {
        return ConfigurationService.get(NamedCurveRegistry.class);
    }

    /**
     * Get the {@link NamedCurve} for the specified {@link ECPublicKey}.
     * 
     * @param publicKey the {@link ECPublicKey}
     * 
     * @return the {@link NamedCurve} instance, or null if can not be determined,
     *         possibly because the key's domain parameters do not correspond to a named curve
     */
    @Nullable public static NamedCurve getNamedCurve(@Nonnull final ECPublicKey publicKey) {
        Constraint.isNotNull(publicKey, "ECPublicKey was null");
        
        final NamedCurveRegistry registry = getGlobalNamedCurveRegistry();
        if (registry == null) {
            LOG.warn("No NamedCurveRegistry is configured");
            return null;
        }
        return registry.getByParameterSpec(publicKey.getParams());
    }

    /**
     * Get the {@link NamedCurve} for the specified URI.
     * 
     * @param uri the URI
     * 
     * @return the {@link NamedCurve} instance, or null if can not be determined,
     */
    @Nullable public static NamedCurve getNamedCurve(@Nonnull final String uri) {
        Constraint.isNotNull(uri, "NamedCurve URI was null");
        
        final NamedCurveRegistry registry = getGlobalNamedCurveRegistry();
        if (registry == null) {
            LOG.warn("No NamedCurveRegistry is configured");
            return null;
        }
        return registry.getByURI(uri);
    }

    /**
     * Get the URI of the named curve for the specified {@link ECPublicKey}.
     * 
     * @param publicKey the {@link ECPublicKey}
     * 
     * @return the URI or null if can not be determined, possibly because is not a named curve
     */
    @Nullable public static String getNamedCurveURI(@Nonnull final ECPublicKey publicKey) {
        Constraint.isNotNull(publicKey, "ECPublicKey was null");
        
        final NamedCurve namedCurve = getNamedCurve(publicKey);
        if (namedCurve == null) {
            LOG.warn("Could not resolve NamedCurve for ECPublicKey");
            return null;
        }
        return namedCurve.getURI();
    }

    /**
     * Get an {@link ECParameterSpec} instance which corresponds to the specified named curve URI.
     * 
     * @param uri the URI of the named curve
     * 
     * @return the {@link ECParameterSpec} instance
     */
    @Nullable public static ECParameterSpec getParameterSpecForURI(@Nonnull final String uri) {
        Constraint.isNotNull(uri, "NamedCurve URI was null");
        
        final NamedCurve namedCurve = getNamedCurve(uri);
        if (namedCurve == null) {
            LOG.warn("Could not resolve NamedCurve for URI: {}", uri);
            return null;
        }
        return namedCurve.getParameterSpec();
    }
    
    /**
     * Decode the {@link ECPoint} from the byte representation.
     * 
     * <p>
     * Only uncompressed point types (0x04) are supported.
     * </p>
     * 
     * @param data the EC point byte representation
     * @param curve the {@link EllipticCurve}
     * 
     * @return the {@link ECPoint}
     * 
     * @throws KeyException if point is not in uncompressed format, or point does not match curve's field size
     */
    @Nonnull public static ECPoint decodeECPoint(@Nonnull final byte[] data, @Nonnull final EllipticCurve curve)
            throws KeyException {
        Constraint.isNotNull(data, "ECPoint byte array was null");
        Constraint.isNotNull(curve, "EllipticCurve was null");
        
        // This implementation borrowed from Santuario 2.2.1 (unfortunately private static methods)
        // See: org.apache.xml.security.keys.content.keyvalues.ECKeyValue#decodePoint(...)
        
        if (data.length == 0 || data[0] != 4) {
            throw new KeyException("Only uncompressed point format supported");
        }
        
        // Per ANSI X9.62, an encoded point is a 1 byte type followed by
        // ceiling(LOG base 2 field-size / 8) bytes of x and the same of y.
        final int n = (data.length - 1) / 2;
        if (n != (curve.getField().getFieldSize() + 7) >> 3) {
            throw new KeyException("Point does not match field size");
        }

        final byte[] xb = Arrays.copyOfRange(data, 1, 1 + n);
        final byte[] yb = Arrays.copyOfRange(data, n + 1, n + 1 + n);

        return new ECPoint(new BigInteger(1, xb), new BigInteger(1, yb));
    }
    
    /**
     * Encode the uncompressed byte representation of the specified {@link ECPoint}.
     * 
     * @param point the {@link ECPoint}
     * @param curve the {@link EllipticCurve}
     * 
     * @return the uncompressed byte representation
     */
    @Nonnull public static byte[] encodeECPointUncompressed(@Nonnull final ECPoint point,
            @Nonnull final EllipticCurve curve) {
        Constraint.isNotNull(point, "ECPoint was null");
        Constraint.isNotNull(curve, "EllipticCurve was null");
        
        // This implementation borrowed from Santuario 2.2.1 (unfortunately private static methods)
        // See: org.apache.xml.security.keys.content.keyvalues.ECKeyValue#encodePoint(...)
        
        // get field size in bytes (rounding up)
        final int n = (curve.getField().getFieldSize() + 7) >> 3;
        final byte[] xb = trimZeroes(point.getAffineX().toByteArray());
        final byte[] yb = trimZeroes(point.getAffineY().toByteArray());
        if (xb.length > n || yb.length > n) {
            throw new IllegalArgumentException("Point coordinates do not match field size");
        }
        final byte[] b = new byte[1 + (n << 1)];
        // 0x04 indicates the uncompressed type
        b[0] = 4;
        System.arraycopy(xb, 0, b, n - xb.length + 1, xb.length);
        System.arraycopy(yb, 0, b, b.length - yb.length, yb.length);
        return b;
    }

    /**
     * Trim leading zero bytes from the byte array.
     * 
     * @param b the byte array
     * @return the byte array without leading zero bytes
     */
    @Nonnull private static byte[] trimZeroes(@Nonnull final byte[] b) {
        Constraint.isNotNull(b, "byte[] data was null");
        
        int i = 0;
        while (i < b.length - 1 && b[i] == 0) {
            i++;
        }
        if (i == 0) {
            return b;
        }
        return Arrays.copyOfRange(b, i, b.length);
    }
    
    /**
     * Convert a Bouncy Castle {@link ECNamedCurveParameterSpec}, such as obtained from the {@link ECNamedCurveTable},
     * to a standard JCA {@link ECParameterSpec}.
     * 
     * @param bcSpec the Bouncy Castle parameter spec instance
     * 
     * @return the standard parameter spec instance
     */
    @Nullable public static ECParameterSpec convert(@Nullable final ECNamedCurveParameterSpec bcSpec) {
        if (bcSpec == null) {
            return null;
        }
        
        return new ECParameterSpec(
                EC5Util.convertCurve(bcSpec.getCurve(), bcSpec.getSeed()),
                EC5Util.convertPoint(bcSpec.getG()),
                bcSpec.getN(),
                bcSpec.getH().intValue());
    }
    
    /**
     * Return a set of all curves known to Bouncy Castle as instances of {@link NamedCurve}.
     * 
     * @return the set of curves known to Bouncy Castle
     */
    @Nonnull @NonnullElements @NotLive
    public static Set<NamedCurve> getCurvesFromBouncyCastle() {
        final HashSet<NamedCurve> curves = new HashSet<>();
        
        // There seems to be duplication between the main and custom curve tables, so use OID to only find unique ones.
        final HashSet<String> oids = new HashSet<>();
        
        final Enumeration<String> standardNames = org.bouncycastle.asn1.x9.ECNamedCurveTable.getNames();
        while (standardNames.hasMoreElements()) {
            final String name = standardNames.nextElement();
            final String oid = org.bouncycastle.asn1.x9.ECNamedCurveTable.getOID(name).getId();
            if (!oids.contains(oid)) {
                final ECParameterSpec paramSpec =
                        EC5Util.convertToSpec(org.bouncycastle.asn1.x9.ECNamedCurveTable.getByName(name));
                curves.add(new BasicNamedCurve(oid, name, paramSpec));
                oids.add(oid);
            }
        }
        
        final Enumeration<String> customNames = org.bouncycastle.crypto.ec.CustomNamedCurves.getNames();
        while (customNames.hasMoreElements()) {
            final String name = customNames.nextElement();
            final String oid = org.bouncycastle.crypto.ec.CustomNamedCurves.getOID(name).getId();
            if (!oids.contains(oid)) {
                final ECParameterSpec paramSpec =
                        EC5Util.convertToSpec(org.bouncycastle.crypto.ec.CustomNamedCurves.getByName(name));
                curves.add(new BasicNamedCurve(oid, name, paramSpec));
                oids.add(oid);
            }
        }
        
        return curves;
    }

} 