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

package org.opensaml.xmlsec.encryption.support;

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Constants defined in or related to the XML Encryption 1.0 and 1.1 specifications.
 */
public final class EncryptionConstants {

    /** XML Encryption namespace. */
    @Nonnull @NotEmpty public static final String XMLENC_NS = "http://www.w3.org/2001/04/xmlenc#";

    /** XML Encryption QName prefix. */
    @Nonnull @NotEmpty public static final String XMLENC_PREFIX = "xenc";

    // *********************************************************
    // URI values which represent type attribute values
    // *********************************************************
    /** URI for Content. */
    @Nonnull @NotEmpty public static final String TYPE_CONTENT = XMLENC_NS + "Content";

    /** URI for Element. */
    @Nonnull @NotEmpty public static final String TYPE_ELEMENT = XMLENC_NS + "Element";

    /** URI for EncryptionProperties. */
    @Nonnull @NotEmpty public static final String TYPE_ENCRYPTION_PROPERTIES = XMLENC_NS + "EncryptionProperties";

    /** URI for EncryptedKey. */
    @Nonnull @NotEmpty public static final String TYPE_ENCRYPTED_KEY = XMLENC_NS + "EncryptedKey";

    /** URI for DHKeyValue. */
    @Nonnull @NotEmpty public static final String TYPE_KEYINFO_DH_KEYVALUE = XMLENC_NS + "DHKeyValue";

    // *************************************************
    // Block encryption algorithms
    // *************************************************
    /** Block Encryption - REQUIRED TRIPLEDES. */
    @Nonnull @NotEmpty public static final String ALGO_ID_BLOCKCIPHER_TRIPLEDES = XMLENC_NS + "tripledes-cbc";

    /** Block Encryption - REQUIRED AES-128. */
    @Nonnull @NotEmpty public static final String ALGO_ID_BLOCKCIPHER_AES128 = XMLENC_NS + "aes128-cbc";

    /** Block Encryption - REQUIRED AES-256. */
    @Nonnull @NotEmpty public static final String ALGO_ID_BLOCKCIPHER_AES256 = XMLENC_NS + "aes256-cbc";

    /** Block Encryption - OPTIONAL AES-192. */
    @Nonnull @NotEmpty public static final String ALGO_ID_BLOCKCIPHER_AES192 = XMLENC_NS + "aes192-cbc";

    // *************************************************
    // Key Transport
    // *************************************************
    /** Key Transport - OPTIONAL RSA-v1.5. */
    @Nonnull @NotEmpty public static final String ALGO_ID_KEYTRANSPORT_RSA15 = XMLENC_NS + "rsa-1_5";

    /** Key Transport - REQUIRED RSA-OAEP (including MGF1 with SHA1). */
    @Nonnull @NotEmpty public static final String ALGO_ID_KEYTRANSPORT_RSAOAEP = XMLENC_NS + "rsa-oaep-mgf1p";

    // *************************************************
    // Key Agreement
    // *************************************************
    /** Key Agreement - OPTIONAL Diffie-Hellman with Legacy Key Derivation Function. */
    @Nonnull @NotEmpty public static final String ALGO_ID_KEYAGREEMENT_DH = XMLENC_NS + "dh";
    
    /** URI for DHKeyValue. */
    @Nonnull @NotEmpty public static final String TYPE_DH_KEY_VALUE = XMLENC_NS + "DHKeyValue";

    // *************************************************
    // Symmetric Key Wrap
    // *************************************************
    /** Symmetric Key Wrap - REQUIRED TRIPLEDES KeyWrap. */
    @Nonnull @NotEmpty public static final String ALGO_ID_KEYWRAP_TRIPLEDES = XMLENC_NS + "kw-tripledes";

    /** Symmetric Key Wrap - REQUIRED AES-128 KeyWrap. */
    @Nonnull @NotEmpty public static final String ALGO_ID_KEYWRAP_AES128 = XMLENC_NS + "kw-aes128";

    /** Symmetric Key Wrap - REQUIRED AES-256 KeyWrap. */
    @Nonnull @NotEmpty public static final String ALGO_ID_KEYWRAP_AES256 = XMLENC_NS + "kw-aes256";

    /** Symmetric Key Wrap - OPTIONAL AES-192 KeyWrap. */
    @Nonnull @NotEmpty public static final String ALGO_ID_KEYWRAP_AES192 = XMLENC_NS + "kw-aes192";

    // *************************************************
    // Message Digest
    // *************************************************
    /** Message Digest - REQUIRED SHA256. */
    @Nonnull @NotEmpty public static final String ALGO_ID_DIGEST_SHA256 = XMLENC_NS + "sha256";

    /** Message Digest - OPTIONAL SHA512. */
    @Nonnull @NotEmpty public static final String ALGO_ID_DIGEST_SHA512 = XMLENC_NS + "sha512";

    /** Message Digest - OPTIONAL RIPEMD-160. */
    @Nonnull @NotEmpty public static final String ALGO_ID_DIGEST_RIPEMD160 = XMLENC_NS + "ripemd160";

    // *********************************************************
    // Some additional algorithm URIs from XML Encryption 1.1
    // *********************************************************
    /** XML Encryption 1.1 namespace. */
    @Nonnull @NotEmpty public static final String XMLENC11_NS = "http://www.w3.org/2009/xmlenc11#";

    /** XML Encryption 1.1 QName prefix. */
    @Nonnull @NotEmpty public static final String XMLENC11_PREFIX = "xenc11";
    
    /** Key Transport - OPTIONAL RSA-OAEP.  */
    @Nonnull @NotEmpty public static final String ALGO_ID_KEYTRANSPORT_RSAOAEP11 = XMLENC11_NS + "rsa-oaep";

    /** Block Encryption - REQUIRED AES128-GCM. */
    @Nonnull @NotEmpty public static final String ALGO_ID_BLOCKCIPHER_AES128_GCM = XMLENC11_NS + "aes128-gcm";
        
    /** Block Encryption - OPTIONAL AES192-GCM. */
    @Nonnull @NotEmpty public static final String ALGO_ID_BLOCKCIPHER_AES192_GCM = XMLENC11_NS + "aes192-gcm";

    /** Block Encryption - OPTIONAL AES256-GCM. */
    @Nonnull @NotEmpty public static final String ALGO_ID_BLOCKCIPHER_AES256_GCM = XMLENC11_NS + "aes256-gcm";
    
    /** Mask Generation Function - MGF1 with SHA-1. */
    @Nonnull @NotEmpty public static final String ALGO_ID_MGF1_SHA1 = XMLENC11_NS + "mgf1sha1";

    /** Mask Generation Function - MGF1 with SHA-224. */
    @Nonnull @NotEmpty public static final String ALGO_ID_MGF1_SHA224 = XMLENC11_NS + "mgf1sha224";

    /** Mask Generation Function - MGF1 with SHA-256. */
    @Nonnull @NotEmpty public static final String ALGO_ID_MGF1_SHA256 = XMLENC11_NS + "mgf1sha256";

    /** Mask Generation Function - MGF1 with SHA-384. */
    @Nonnull @NotEmpty public static final String ALGO_ID_MGF1_SHA384 = XMLENC11_NS + "mgf1sha384";

    /** Mask Generation Function - MGF1 with SHA-512. */
    @Nonnull @NotEmpty public static final String ALGO_ID_MGF1_SHA512 = XMLENC11_NS + "mgf1sha512";

    /** URI for DerivedKey. */
    @Nonnull @NotEmpty public static final String TYPE_DERIVED_KEY = XMLENC11_NS + "DerivedKey";
        
    /** URI for ECKeyValue. */
    @Nonnull @NotEmpty public static final String TYPE_EC_KEY_VALUE = XMLENC11_NS + "ECKeyValue";
        
    /** Key Agreement - Diffie-Hellman with Explicit Key Derivation Function. */
    @Nonnull @NotEmpty public static final String ALGO_ID_KEYAGREEMENT_DH_EXPLICIT_KDF = XMLENC11_NS + "dh-es";
        
    /** Key Agreement - Elliptic Curve Diffie-Hellman (ECDH) Ephemeral-Static Mode. */
    @Nonnull @NotEmpty public static final String ALGO_ID_KEYAGREEMENT_ECDH_ES = XMLENC11_NS + "ECDH-ES";
        
    /** Key Derivation Method - ConcatKDF. */
    @Nonnull @NotEmpty public static final String ALGO_ID_KEYDERIVATION_CONCATKDF = XMLENC11_NS + "ConcatKDF";
        
    /** Key Derivation Method - PBKDF2. */
    @Nonnull @NotEmpty public static final String ALGO_ID_KEYDERIVATION_PBKDF2 = XMLENC11_NS + "pbkdf2";
        
    /** Constructor. */
    private EncryptionConstants() {

    }
}
