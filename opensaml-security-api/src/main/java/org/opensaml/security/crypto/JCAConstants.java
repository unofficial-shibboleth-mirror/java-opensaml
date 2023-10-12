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

package org.opensaml.security.crypto;

import java.security.Key;

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Various useful constants defined in and/or used with the Java Cryptography Architecture (JCA) specification.
 */
public final class JCAConstants {
    
    // Key types
    
    /** Key algorithm: "RSA". */
    @Nonnull @NotEmpty public static final String KEY_ALGO_RSA = "RSA";
    
    /** Key algorithm: "DSA". */
    @Nonnull @NotEmpty public static final String KEY_ALGO_DSA = "DSA";
    
    /** Key algorithm: "EC". */
    @Nonnull @NotEmpty public static final String KEY_ALGO_EC = "EC";
    
    /** Key algorithm: "AES". */
    @Nonnull @NotEmpty public static final String KEY_ALGO_AES = "AES";
    
    /** Key algorithm: "DES". */
    @Nonnull @NotEmpty public static final String KEY_ALGO_DES = "DES";
    
    /** Key algorithm: "DESede". */
    @Nonnull @NotEmpty public static final String KEY_ALGO_DESEDE = "DESede";
    
    /** Key algorithm: "DH" (returned by {@link Key#getAlgorithm()}). */
    @Nonnull @NotEmpty public static final String KEY_ALGO_DH = "DH";
    
    /** Key algorithm: "DiffieHellman" (used with key and key pair factories, generators, etc). */
    @Nonnull @NotEmpty public static final String KEY_ALGO_DIFFIE_HELLMAN = "DiffieHellman";
    
    
    
    // Key formats
    
    /** Key format: "RAW". */
    @Nonnull @NotEmpty public static final String KEY_FORMAT_RAW = "RAW";
    
    
    
    // Cipher modes
    
    /** Cipher mode: "ECB". */
    @Nonnull @NotEmpty public static final String CIPHER_MODE_ECB = "ECB";
    
    /** Cipher mode: "CBC". */
    @Nonnull @NotEmpty public static final String CIPHER_MODE_CBC = "CBC";
    
    /** Cipher mode: "GCM". */
    @Nonnull @NotEmpty public static final String CIPHER_MODE_GCM = "GCM";
    
    
    
    // Cipher padding
    
    /** Cipher padding: "NoPadding". */
    @Nonnull @NotEmpty public static final String CIPHER_PADDING_NONE = "NoPadding";
    
    /** Cipher padding: "ISO10126Padding". */
    @Nonnull @NotEmpty public static final String CIPHER_PADDING_ISO10126 = "ISO10126Padding";
    
    /** Cipher padding: "PKCS1Padding". */
    @Nonnull @NotEmpty public static final String CIPHER_PADDING_PKCS1 = "PKCS1Padding";
    
    /** Cipher padding: "OAEPPadding". */
    @Nonnull @NotEmpty public static final String CIPHER_PADDING_OAEP = "OAEPPadding";
    
    
    
    // Symmetric key wrap algorithms
    
    /** Symmetric key wrap algorithm: "DESedeWrap". */
    @Nonnull @NotEmpty public static final String KEYWRAP_ALGO_DESEDE = "DESedeWrap";
    
    /** Symmetric key wrap algorithm: "AESWrap". */
    @Nonnull @NotEmpty public static final String KEYWRAP_ALGO_AES = "AESWrap";
    
    
    
    // Digest types
    
    /** Digest algorithm: "MD5". */
    @Nonnull @NotEmpty public static final String DIGEST_MD5 = "MD5";
    
    /** Digest algorithm: "RIPEMD160". */
    @Nonnull @NotEmpty public static final String DIGEST_RIPEMD160 = "RIPEMD160";
    
    /** Digest algorithm: "SHA-1". */
    @Nonnull @NotEmpty public static final String DIGEST_SHA1 = "SHA-1";
    
    /** Digest algorithm: "SHA-224". */
    @Nonnull @NotEmpty public static final String DIGEST_SHA224 = "SHA-224";
    
    /** Digest algorithm: "SHA-256". */
    @Nonnull @NotEmpty public static final String DIGEST_SHA256 = "SHA-256";
    
    /** Digest algorithm: "SHA-384". */
    @Nonnull @NotEmpty public static final String DIGEST_SHA384 = "SHA-384";
    
    /** Digest algorithm: "SHA-512". */
    @Nonnull @NotEmpty public static final String DIGEST_SHA512 = "SHA-512";
    
    /** Digest algorithm: "SHA-512". */
    @Nonnull @NotEmpty public static final String DIGEST_SHA3_224 = "SHA3-224";
    
    /** Digest algorithm: "SHA-512". */
    @Nonnull @NotEmpty public static final String DIGEST_SHA3_256 = "SHA3-256";
    
    /** Digest algorithm: "SHA-512". */
    @Nonnull @NotEmpty public static final String DIGEST_SHA3_384 = "SHA3-384";
    
    /** Digest algorithm: "SHA-512". */
    @Nonnull @NotEmpty public static final String DIGEST_SHA3_512 = "SHA3-512";
    
    
    // Signature types
    
    /** Signature algorithm: "SHA1withDSA". */
    @Nonnull @NotEmpty public static final String SIGNATURE_DSA_SHA1 = "SHA1withDSA";
    
    /** Signature algorithm: "SHA224withDSA". */
    @Nonnull @NotEmpty public static final String SIGNATURE_DSA_SHA224 = "SHA224withDSA";
    
    /** Signature algorithm: "SHA256withDSA". */
    @Nonnull @NotEmpty public static final String SIGNATURE_DSA_SHA256 = "SHA256withDSA";
    
    /** Signature algorithm: "MD5withRSA". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_MD5 = "MD5withRSA";
    
    /** Signature algorithm: "RIPEMD160withRSA". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_RIPEMD160 = "RIPEMD160withRSA";
    
    /** Signature algorithm: "SHA1withRSA". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_SHA1 = "SHA1withRSA";
    
    /** Signature algorithm: "SHA224withRSA". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_SHA224 = "SHA224withRSA";
    
    /** Signature algorithm: "SHA256withRSA". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_SHA256 = "SHA256withRSA";
    
    /** Signature algorithm: "SHA384withRSA". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_SHA384 = "SHA384withRSA";
    
    /** Signature algorithm: "SHA512withRSA". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_SHA512 = "SHA512withRSA";
    
    /** Signature algorithm: "SHA1withECDSA". */
    @Nonnull @NotEmpty public static final String SIGNATURE_ECDSA_SHA1 = "SHA1withECDSA";
    
    /** Signature algorithm: "SHA224withECDSA". */
    @Nonnull @NotEmpty public static final String SIGNATURE_ECDSA_SHA224 = "SHA224withECDSA";
    
    /** Signature algorithm: "SHA256withECDSA". */
    @Nonnull @NotEmpty public static final String SIGNATURE_ECDSA_SHA256 = "SHA256withECDSA";
    
    /** Signature algorithm: "SHA384withECDSA". */
    @Nonnull @NotEmpty public static final String SIGNATURE_ECDSA_SHA384 = "SHA384withECDSA";
    
    /** Signature algorithm: "SHA512withECDSA". */
    @Nonnull @NotEmpty public static final String SIGNATURE_ECDSA_SHA512 = "SHA512withECDSA";
    
    /** Signature algorithm: "SHA1withRSAandMGF1". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_SHA1_MGF1= "SHA1withRSAandMGF1";
    
    /** Signature algorithm: "SHA224withRSAandMGF1". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_SHA224_MGF1= "SHA224withRSAandMGF1";
    
    /** Signature algorithm: "SHA256withRSAandMGF1". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_SHA256_MGF1 = "SHA256withRSAandMGF1";
    
    /** Signature algorithm: "SHA384withRSAandMGF1". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_SHA384_MGF1 = "SHA384withRSAandMGF1";
    
    /** Signature algorithm: "SHA512withRSAandMGF1". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_SHA512_MGF1= "SHA512withRSAandMGF1";
    
    /** Signature algorithm: "SHA3-224withRSAandMGF1". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_SHA3_224_MGF1 = "SHA3-224withRSAandMGF1";
    
    /** Signature algorithm: "SHA3-256withRSAandMGF1". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_SHA3_256_MGF1 = "SHA3-256withRSAandMGF1";
    
    /** Signature algorithm: "SHA3-384withRSAandMGF1". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_SHA3_384_MGF1 = "SHA3-384withRSAandMGF1";
    
    /** Signature algorithm: "SHA3-512withRSAandMGF1". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_SHA3_512_MGF1 = "SHA3-512withRSAandMGF1";
    
    /** Signature algorithm: "RSASSA-PSS". */
    @Nonnull @NotEmpty public static final String SIGNATURE_RSA_SSA_PSS= "RSASSA-PSS";
    
    
    // MAC types
    
    /** MAC algorithm: "HmacMD5". */
    @Nonnull @NotEmpty public static final String HMAC_MD5 = "HmacMD5";
    
    /** MAC algorithm: "HMACRIPEMD160". */
    @Nonnull @NotEmpty public static final String HMAC_RIPEMD160 = "HMACRIPEMD160";
    
    /** MAC algorithm: "HmacSHA1". */
    @Nonnull @NotEmpty public static final String HMAC_SHA1 = "HmacSHA1";
    
    /** MAC algorithm: "HmacSHA224". */
    @Nonnull @NotEmpty public static final String HMAC_SHA224 = "HmacSHA224";
    
    /** MAC algorithm: "HmacSHA256". */
    @Nonnull @NotEmpty public static final String HMAC_SHA256 = "HmacSHA256";
    
    /** MAC algorithm: "HmacSHA384". */
    @Nonnull @NotEmpty public static final String HMAC_SHA384 = "HmacSHA384";
    
    /** MAC algorithm: "HmacSHA512". */
    @Nonnull @NotEmpty public static final String HMAC_SHA512 = "HmacSHA512";
    
    
    
    // Key Agreement types
    
    /** Key Agreement algorithm: Diffie-Hellman. */
    @Nonnull @NotEmpty public static final String KEY_AGREEMENT_DH = "DiffieHellman";
    
    /** Key Agreement algorithm: Elliptic Curve Diffie-Hellman. */
    @Nonnull @NotEmpty public static final String KEY_AGREEMENT_ECDH = "ECDH";


    /** Constructor. Private to disable instantiation. */
    private JCAConstants() { }
    
    
}
