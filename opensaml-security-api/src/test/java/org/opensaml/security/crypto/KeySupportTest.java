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

import java.io.InputStream;
import java.security.InvalidParameterException;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.SecretKey;

import org.opensaml.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link KeySupport}.
 */
public class KeySupportTest {
    
    private final Logger log = LoggerFactory.getLogger(KeySupportTest.class);

    /** Location of non-encrypted, PEM formatted, RSA private key. */
    private String rsaPrivKeyPEMNoEncrypt = "/data/rsa-privkey-nopass.pem";

    /** Location of non-encrypted, DER formatted, RSA private key. */
    private String rsaPrivKeyDERNoEncrypt = "/data/rsa-privkey-nopass.der";
    
    /** Location of non-encrypted, PEM formatted, dSA private key. */
    private String dsaPrivKeyPEMNoEncrypt = "/data/dsa-privkey-nopass.pem";

    /** Location of non-encrypted, DER formatted, dSA private key. */
    private String dsaPrivKeyDERNoEncrypt = "/data/dsa-privkey-nopass.der";

    /** Password for private key. */
    private char[] privKeyPassword = { 'c', 'h', 'a', 'n', 'g', 'e', 'i', 't' };

    /** Location of encrypted, PEM formatted, RSA private key. */
    private String rsaPrivKeyPEMEncrypt = "/data/rsa-privkey-changeit-pass.pem";

    /** Location of encrypted, PEM formatted, DSA private key. */
    private String dsaPrivKeyPEMEncrypt = "/data/dsa-privkey-changeit-pass.pem";

    /** Location of non-encrypted, PEM formatted, EC private key. */
    private String ecPrivKeyPEMNoEncrypt = "/data/ec-privkey-nopass.pem";
    
    /** An invalid base64 string.*/
    private static final String INVALID_BASE64_KEY="AB==";

    /**
     * Test decoding an RSA private key, in PEM format, without encryption.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testDecodeRSAPrivateKeyPEMNoEncrypt() throws Exception {
        testPrivKey(rsaPrivKeyPEMNoEncrypt, null, "RSA");
    }

    /**
     * Test decoding an RSA private key, in PEM format, with encryption.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testDecodeRSAPrivateKeyPEMEncrypt() throws Exception {
        testPrivKey(rsaPrivKeyPEMEncrypt, privKeyPassword, "RSA");
    }

    /**
     * Test decoding an RSA private key, in DER format, without encryption.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testDecodeRSAPrivateKeyDERNoEncrypt() throws Exception {
        testPrivKey(rsaPrivKeyDERNoEncrypt, null, "RSA");
    }
    
    /**
     * Test decoding an DSA private key, in PEM format, without encryption.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testDecodeDSAPrivateKeyPEMNoEncrypt() throws Exception {
        testPrivKey(dsaPrivKeyPEMNoEncrypt, null, "DSA");
    }

    /**
     * Test decoding an DSA private key, in PEM format, with encryption.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testDecodeDSAPrivateKeyPEMEncrypt() throws Exception {
        testPrivKey(dsaPrivKeyPEMEncrypt, privKeyPassword, "DSA");
    }

    /**
     * Test decoding an DSA private key, in DER format, without encryption.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testDecodeDSAPrivateKeyDERNoEncrypt() throws Exception {
        testPrivKey(dsaPrivKeyDERNoEncrypt, null, "DSA");
    }    
    
    /**
     * Test decoding an EC private key, in PEM format, without encryption.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testDecodeECPrivateKeyPEMNoEncrypt() throws Exception {
        testPrivKey(ecPrivKeyPEMNoEncrypt, null, "EC");
    }
    
    
    /**
     * Test deriving a public key from an RSA, DSA and EC private key.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testDerivePublicKey() throws Exception{
        PrivateKey privKey = testPrivKey(rsaPrivKeyPEMNoEncrypt, null, "RSA");
        PublicKey pubKey = KeySupport.derivePublicKey(privKey);
        Assert.assertNotNull(pubKey);
        Assert.assertEquals(pubKey.getAlgorithm(), "RSA");
        Assert.assertTrue(KeySupport.matchKeyPair(pubKey, privKey));
        
        pubKey = null;
        privKey = testPrivKey(dsaPrivKeyPEMNoEncrypt, null, "DSA");
        pubKey = KeySupport.derivePublicKey(privKey);
        Assert.assertNotNull(pubKey);
        Assert.assertEquals(pubKey.getAlgorithm(), "DSA");
        Assert.assertTrue(KeySupport.matchKeyPair(pubKey, privKey));
        
        pubKey = null;
        privKey = testPrivKey(ecPrivKeyPEMNoEncrypt, null, "EC");
        pubKey = KeySupport.derivePublicKey(privKey);
        Assert.assertNotNull(pubKey);
        Assert.assertEquals(pubKey.getAlgorithm(), "EC");
        Assert.assertTrue(KeySupport.matchKeyPair(pubKey, privKey));
    }

    
    /**
     * Test the evaluation that 2 keys are members of the same key pair. 
     * 
     * @throws NoSuchProviderException ...
     * @throws NoSuchAlgorithmException ...
     * @throws SecurityException ...
     */
    @Test
    public void testKeyPairMatching() throws NoSuchAlgorithmException, NoSuchProviderException, SecurityException {
        final KeyPair kp1rsa = KeySupport.generateKeyPair("RSA", 1024, null);
        final KeyPair kp2rsa = KeySupport.generateKeyPair("RSA", 1024, null);
        final KeyPair kp1dsa = KeySupport.generateKeyPair("DSA", 1024, null);
        final KeyPair kp2dsa = KeySupport.generateKeyPair("DSA", 1024, null);
        
        Assert.assertTrue(KeySupport.matchKeyPair(kp1rsa.getPublic(), kp1rsa.getPrivate()));
        Assert.assertTrue(KeySupport.matchKeyPair(kp2rsa.getPublic(), kp2rsa.getPrivate()));
        Assert.assertFalse(KeySupport.matchKeyPair(kp1rsa.getPublic(), kp2rsa.getPrivate()));
        Assert.assertFalse(KeySupport.matchKeyPair(kp2rsa.getPublic(), kp1rsa.getPrivate()));
        
        Assert.assertTrue(KeySupport.matchKeyPair(kp1dsa.getPublic(), kp1dsa.getPrivate()));
        Assert.assertTrue(KeySupport.matchKeyPair(kp2dsa.getPublic(), kp2dsa.getPrivate()));
        Assert.assertFalse(KeySupport.matchKeyPair(kp1dsa.getPublic(), kp2dsa.getPrivate()));
        Assert.assertFalse(KeySupport.matchKeyPair(kp2dsa.getPublic(), kp1dsa.getPrivate()));
        
        try {
            // key algorithm type mismatch, should be an error
            Assert.assertFalse(KeySupport.matchKeyPair(kp1rsa.getPublic(), kp2dsa.getPrivate()));
            Assert.fail("Key algorithm mismatch should have caused evaluation failure");
        } catch (final SecurityException e) {
           // expected 
        }
    }
    
    @Test
    public void testKeyLength() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair kp = null;
        
        // Asymmetric: RSA
        kp = KeySupport.generateKeyPair("RSA", 1024, null);
        Assert.assertEquals(KeySupport.getKeyLength(kp.getPublic()), Integer.valueOf(1024));
        Assert.assertEquals(KeySupport.getKeyLength(kp.getPrivate()), Integer.valueOf(1024));
        
        kp = KeySupport.generateKeyPair("RSA", 2048, null);
        Assert.assertEquals(KeySupport.getKeyLength(kp.getPublic()), Integer.valueOf(2048));
        Assert.assertEquals(KeySupport.getKeyLength(kp.getPrivate()), Integer.valueOf(2048));
        
        kp = KeySupport.generateKeyPair("RSA", 4096, null);
        Assert.assertEquals(KeySupport.getKeyLength(kp.getPublic()), Integer.valueOf(4096));
        Assert.assertEquals(KeySupport.getKeyLength(kp.getPrivate()), Integer.valueOf(4096));
        
        // Asymmetric: DSA
        kp = KeySupport.generateKeyPair("DSA", 512, null);
        Assert.assertEquals(KeySupport.getKeyLength(kp.getPublic()), Integer.valueOf(512));
        Assert.assertEquals(KeySupport.getKeyLength(kp.getPrivate()), Integer.valueOf(512));
        
        kp = KeySupport.generateKeyPair("DSA", 1024, null);
        Assert.assertEquals(KeySupport.getKeyLength(kp.getPublic()), Integer.valueOf(1024));
        Assert.assertEquals(KeySupport.getKeyLength(kp.getPrivate()), Integer.valueOf(1024));
        
        // Asymmetric: EC
        try {
            kp = KeySupport.generateKeyPair("EC", 256, null);
            Assert.assertEquals(KeySupport.getKeyLength(kp.getPublic()), Integer.valueOf(256));
            Assert.assertEquals(KeySupport.getKeyLength(kp.getPrivate()), Integer.valueOf(256));
        
        } catch (final NoSuchAlgorithmException| InvalidParameterException e) {
            // EC support isn't universal, e.g. OpenJDK 7 doesn't ship with an EC provider out-of-the-box.
            // Just ignore unsupported algorithm abd InvalidParameter failures here for now.
            log.warn("EC-256 failed", e);
        }
        try {
            kp = KeySupport.generateKeyPair("EC", 384, null);
            Assert.assertEquals(KeySupport.getKeyLength(kp.getPublic()), Integer.valueOf(384));
            Assert.assertEquals(KeySupport.getKeyLength(kp.getPrivate()), Integer.valueOf(384));
        
        } catch (final NoSuchAlgorithmException| InvalidParameterException e) {
            // EC support isn't universal, e.g. OpenJDK 7 doesn't ship with an EC provider out-of-the-box.
            // Just ignore unsupported algorithm abd InvalidParameter failures here for now.
            log.warn("EC-384 failed", e);
        }
        try {
            kp = KeySupport.generateKeyPair("EC", 521, null);
            Assert.assertEquals(KeySupport.getKeyLength(kp.getPublic()), Integer.valueOf(521));
            Assert.assertEquals(KeySupport.getKeyLength(kp.getPrivate()), Integer.valueOf(521));
        } catch (final NoSuchAlgorithmException| InvalidParameterException e) {
            // EC support isn't universal, e.g. OpenJDK 7 doesn't ship with an EC provider out-of-the-box.
            // Just ignore unsupported algorithm abd InvalidParameter failures here for now.
            log.warn("EC-521 failed", e);
        }
        
        // Symmetric: AES
        Assert.assertEquals(KeySupport.getKeyLength(KeySupport.generateKey("AES", 128, null)), Integer.valueOf(128));
        Assert.assertEquals(KeySupport.getKeyLength(KeySupport.generateKey("AES", 192, null)), Integer.valueOf(192));
        Assert.assertEquals(KeySupport.getKeyLength(KeySupport.generateKey("AES", 256, null)), Integer.valueOf(256));
        
        // Symmetric: DES and DESede
        //
        // These numbers are correct, albeit unintuitive. 
        // DES keys are always 64 bits.
        // DESede keys are always 64*3 = 192 bits (i.e. triple of DES).
        Assert.assertEquals(KeySupport.getKeyLength(KeySupport.generateKey("DES", 56, null)), Integer.valueOf(64));
        Assert.assertEquals(KeySupport.getKeyLength(KeySupport.generateKey("DESede", 112, null)), Integer.valueOf(192));
        Assert.assertEquals(KeySupport.getKeyLength(KeySupport.generateKey("DESede", 168, null)), Integer.valueOf(192));
    }
    
    @DataProvider
    public Object[][] decodeSecretKeyData() {
       return new Object[][] {
              new Object[] {128, "AES"}, 
              new Object[] {192, "AES"}, 
              new Object[] {256, "AES"}, 
              new Object[] {64, "DES"}, 
              new Object[] {168, "DESede"}, 
              new Object[] {192, "DESede"}, 
       };
    }
    
    @Test(dataProvider="decodeSecretKeyData")
    public void testDecodeSecretKey(final Integer keyLengthBits, final String algorithm) throws NoSuchAlgorithmException, KeyException {
        // This is just for testing, not real.
        final byte[] key = new byte[keyLengthBits/8];
        SecureRandom.getInstance("SHA1PRNG").nextBytes(key);
        final SecretKey secretKey = KeySupport.decodeSecretKey(key, algorithm);
        Assert.assertNotNull(secretKey);
        Assert.assertEquals(secretKey.getAlgorithm(), algorithm);
        Assert.assertEquals(secretKey.getEncoded(), key);
    }
    
    @Test(expectedExceptions = KeyException.class) public void testBuildJavaDSAPublicKeyWithInvalidBase64() throws KeyException {
        KeySupport.buildJavaDSAPublicKey(INVALID_BASE64_KEY);        
    }
    
    @Test(expectedExceptions = KeyException.class) public void testBuildJavaRSAPublicKeyWithInvalidBase64() throws KeyException {
        KeySupport.buildJavaRSAPublicKey(INVALID_BASE64_KEY);        
    }
    
    @Test(expectedExceptions = KeyException.class) public void testBuildJavaECPublicKeyWithInvalidBase64() throws KeyException {
        KeySupport.buildJavaECPublicKey(INVALID_BASE64_KEY);        
    }

    /**
     * Generic key testing.
     * 
     * @param keyFile ...
     * @param password ...
     * @param algo ...
     * 
     * @return the private key
     * 
     * @throws Exception if something goes wrong
     */
    protected PrivateKey testPrivKey(final String keyFile, final char[] password, final String algo) throws Exception {
        final InputStream keyInS = KeySupportTest.class.getResourceAsStream(keyFile);

        final byte[] keyBytes = new byte[keyInS.available()];
        keyInS.read(keyBytes);

        PrivateKey key = KeySupport.decodePrivateKey(keyBytes, password);
        Assert.assertNotNull(key);
        Assert.assertEquals(key.getAlgorithm(), algo);
        
        key = KeySupport.decodePrivateKey(KeySupportTest.class.getResourceAsStream(keyFile), password);
        Assert.assertNotNull(key);
        Assert.assertEquals(key.getAlgorithm(), algo);
        
        return key;
    }
}
