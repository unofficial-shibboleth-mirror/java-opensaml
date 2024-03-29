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

package org.opensaml.xmlsec.encryption.support;

import java.security.Key;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;

import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.DecodingException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

import org.apache.xml.security.Init;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.XMLSignatureBuilder;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.slf4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

/**
 * Supports encryption of XMLObjects, their content and keys, according to the XML Encryption specification, version
 * 20021210.
 * 
 * <p>
 * Various overloaded method variants are supplied for encrypting XMLObjects and their contents (with or without
 * encryption of the associated data encryption key), as well as for encrypting keys separately.
 * </p>
 * 
 * <p>
 * The parameters for data encryption are specified with an instance of {@link DataEncryptionParameters}. The parameters
 * for key encryption are specified with one or more instances of {@link KeyEncryptionParameters}.
 * </p>
 * 
 * <p>
 * The data encryption credential supplied by {@link DataEncryptionParameters#getEncryptionCredential()} is mandatory
 * unless key encryption is also being performed and all associated key encryption parameters contain a valid key
 * encryption credential containing a valid key encryption key. In this case the data encryption key will be randomly
 * generated based on the algorithm URI supplied by {@link DataEncryptionParameters#getAlgorithm()}.
 * </p>
 * 
 * <p>
 * If encryption of the data encryption key is being performed using the overloaded methods for elements or content, the
 * resulting EncryptedKey(s) will be placed inline within the KeyInfo of the resulting EncryptedData. If this is not the
 * desired behavior, the XMLObject and the data encryption key should be encrypted separately, and the placement of
 * EncryptedKey(s) handled by the caller. Specialized subclasses of this class maybe also handle key placement in an
 * application-specific manner.
 * </p>
 * 
 */
public class Encrypter {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(Encrypter.class);

    /** Unmarshaller used to create EncryptedData objects from DOM element. */
    @Nonnull private final Unmarshaller encryptedDataUnmarshaller;

    /** Unmarshaller used to create EncryptedData objects from DOM element. */
    @Nonnull private final Unmarshaller encryptedKeyUnmarshaller;

    /** Builder instance for building KeyInfo objects. */
    @Nonnull private final XMLSignatureBuilder<KeyInfo> keyInfoBuilder;

    /** The name of the JCA security provider to use. */
    @Nullable private String jcaProviderName;

    /**
     * Constructor.
     * 
     */
    public Encrypter() {
        final UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
        encryptedDataUnmarshaller = Constraint.isNotNull(
                unmarshallerFactory.getUnmarshaller(EncryptedData.DEFAULT_ELEMENT_NAME),
                "EncryptedData unmarshaller not configured");
        
        encryptedKeyUnmarshaller = Constraint.isNotNull(
                unmarshallerFactory.getUnmarshaller(EncryptedKey.DEFAULT_ELEMENT_NAME),
                "EncryptedKey unmarshaller not configured");

        final XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
        keyInfoBuilder = (XMLSignatureBuilder<KeyInfo>) builderFactory.<KeyInfo>ensureBuilder(
                KeyInfo.DEFAULT_ELEMENT_NAME);
    }

    /**
     * Get the Java Cryptography Architecture (JCA) security provider name that should be used to provide the encryption
     * support.
     * 
     * Defaults to <code>null</code>, which means that the first registered provider which supports the requested
     * encryption algorithm URI will be used.
     * 
     * @return the JCA provider name to use
     */
    @Nullable public String getJCAProviderName() {
        return jcaProviderName;
    }

    /**
     * Set the Java Cryptography Architecture (JCA) security provider name that should be used to provide the encryption
     * support.
     * 
     * Defaults to <code>null</code>, which means that the first registered provider which supports the requested
     * encryption algorithm URI will be used.
     * 
     * @param providerName the JCA provider name to use
     */
    public void setJCAProviderName(@Nullable final String providerName) {
        jcaProviderName = providerName;
    }

    /**
     * Encrypts the DOM representation of the XMLObject.
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encParams parameters for encrypting the data
     * 
     * @return the resulting EncryptedData element
     * @throws EncryptionException exception thrown on encryption errors
     */
    @Nonnull public EncryptedData encryptElement(@Nonnull final XMLObject xmlObject,
            @Nonnull final DataEncryptionParameters encParams) throws EncryptionException {
        final List<KeyEncryptionParameters> emptyKEKParamsList = new ArrayList<>();
        return encryptElement(xmlObject, encParams, emptyKEKParamsList, false);
    }

    /**
     * Encrypts the DOM representation of the XMLObject, encrypts the encryption key using the specified key encryption
     * parameters and places the resulting EncryptedKey within the EncryptedData's KeyInfo.
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encParams parameters for encrypting the data
     * @param kekParams parameters for encrypting the encryption key
     * 
     * @return the resulting EncryptedData element
     * @throws EncryptionException exception thrown on encryption errors
     */
    @Nonnull public EncryptedData encryptElement(@Nonnull final XMLObject xmlObject,
            @Nonnull final DataEncryptionParameters encParams, @Nonnull final KeyEncryptionParameters kekParams)
                    throws EncryptionException {
        final List<KeyEncryptionParameters> kekParamsList = new ArrayList<>();
        kekParamsList.add(kekParams);
        return encryptElement(xmlObject, encParams, kekParamsList, false);
    }

    /**
     * Encrypts the DOM representation of the XMLObject, encrypts the encryption key using the specified key encryption
     * parameters and places the resulting EncryptedKey(s) within the EncryptedData's KeyInfo.
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encParams parameters for encrypting the data
     * @param kekParamsList parameters for encrypting the encryption key
     * 
     * @return the resulting EncryptedData element
     * @throws EncryptionException exception thrown on encryption errors
     */
    @Nonnull public EncryptedData encryptElement(@Nonnull final XMLObject xmlObject,
            @Nonnull final DataEncryptionParameters encParams,
            @Nonnull final List<KeyEncryptionParameters> kekParamsList) throws EncryptionException {
        return encryptElement(xmlObject, encParams, kekParamsList, false);
    }

    /**
     * Encrypts the DOM representation of the content of an XMLObject.
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encParams parameters for encrypting the data
     * 
     * @return the resulting EncryptedData element
     * @throws EncryptionException exception thrown on encryption errors
     */
    @Nonnull public EncryptedData encryptElementContent(@Nonnull final XMLObject xmlObject,
            @Nonnull final DataEncryptionParameters encParams) throws EncryptionException {
        final List<KeyEncryptionParameters> emptyKEKParamsList = new ArrayList<>();
        return encryptElement(xmlObject, encParams, emptyKEKParamsList, true);
    }

    /**
     * Encrypts the DOM representation of the content of an XMLObject, encrypts the encryption key using the specified
     * key encryption parameters and places the resulting EncryptedKey within the EncryptedData's KeyInfo..
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encParams parameters for encrypting the data
     * @param kekParams parameters for encrypting the encryption key
     * 
     * @return the resulting EncryptedData element
     * @throws EncryptionException exception thrown on encryption errors
     */
    @Nonnull public EncryptedData encryptElementContent(@Nonnull final XMLObject xmlObject,
            @Nonnull final DataEncryptionParameters encParams, @Nonnull final KeyEncryptionParameters kekParams)
                    throws EncryptionException {
        final List<KeyEncryptionParameters> kekParamsList = new ArrayList<>();
        kekParamsList.add(kekParams);
        return encryptElement(xmlObject, encParams, kekParamsList, true);
    }

    /**
     * Encrypts the DOM representation of the content of an XMLObject, encrypts the encryption key using the specified
     * key encryption parameters and places the resulting EncryptedKey(s) within the EncryptedData's KeyInfo..
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encParams parameters for encrypting the data
     * @param kekParamsList parameters for encrypting the encryption key
     * 
     * @return the resulting EncryptedData element
     * @throws EncryptionException exception thrown on encryption errors
     */
    @Nonnull public EncryptedData encryptElementContent(@Nonnull final XMLObject xmlObject,
            @Nonnull final DataEncryptionParameters encParams,
            @Nonnull final List<KeyEncryptionParameters> kekParamsList) throws EncryptionException {
        return encryptElement(xmlObject, encParams, kekParamsList, true);
    }

    /**
     * Encrypts a key once for each key encryption parameters set that is supplied.
     * 
     * @param key the key to encrypt
     * @param kekParamsList a list parameters for encrypting the key
     * @param containingDocument the document that will own the DOM element underlying the resulting EncryptedKey
     *            objects
     * 
     * @return the resulting list of EncryptedKey objects
     * 
     * @throws EncryptionException exception thrown on encryption errors
     */
    @Nonnull public List<EncryptedKey> encryptKey(@Nonnull final Key key,
            @Nonnull final List<KeyEncryptionParameters> kekParamsList, @Nonnull final Document containingDocument)
                    throws EncryptionException {

        checkParams(kekParamsList, false);

        final List<EncryptedKey> encKeys = new ArrayList<>();

        for (final KeyEncryptionParameters kekParam : kekParamsList) {
            assert kekParam != null;
            encKeys.add(encryptKey(key, kekParam, containingDocument));
        }
        return encKeys;
    }

    /**
     * Encrypts a key.
     * 
     * @param key the key to encrypt
     * @param kekParams parameters for encrypting the key
     * @param containingDocument the document that will own the DOM element underlying the resulting EncryptedKey object
     * 
     * @return the resulting EncryptedKey object
     * 
     * @throws EncryptionException exception thrown on encryption errors
     */
    @Nonnull public EncryptedKey encryptKey(@Nonnull final Key key, @Nonnull final KeyEncryptionParameters kekParams,
            @Nonnull final Document containingDocument) throws EncryptionException {

        checkParams(kekParams, false);

        final Credential encryptionCred = kekParams.getEncryptionCredential();
        final Key encryptionKey = encryptionCred != null
                ? CredentialSupport.extractEncryptionKey(encryptionCred) : null;
        if (encryptionKey == null) {
            throw new EncryptionException("Unable to obtain encryption key from parameters");
        }

        final String alg = kekParams.getAlgorithm();
        assert alg != null;
        final EncryptedKey encryptedKey =
                encryptKey(key, encryptionKey, alg, kekParams.getRSAOAEPParameters(), containingDocument);

        final KeyInfoGenerator keyInfoGenerator = kekParams.getKeyInfoGenerator();
        if (keyInfoGenerator != null) {
            log.debug("Dynamically generating KeyInfo from Credential for EncryptedKey using generator: {}",
                    keyInfoGenerator.getClass().getName());
            try {
                encryptedKey.setKeyInfo(keyInfoGenerator.generate(kekParams.getEncryptionCredential()));
            } catch (final SecurityException e) {
                log.error("Error during EncryptedKey KeyInfo generation: {}", e.getMessage());
                throw new EncryptionException("Error during EncryptedKey KeyInfo generation", e);
            }
        }

        if (kekParams.getRecipient() != null) {
            encryptedKey.setRecipient(kekParams.getRecipient());
        }

        return encryptedKey;
    }

    /**
     * Encrypts a key using the specified encryption key and algorithm URI.
     * 
     * @param targetKey the key to encrypt
     * @param encryptionKey the key with which to encrypt the target key
     * @param encryptionAlgorithmURI the XML Encryption algorithm URI corresponding to the encryption key
     * @param rsaOAEPParams the RSA-OAEP params instance (may be null)
     * @param containingDocument the document that will own the resulting element
     * @return the new EncryptedKey object
     * @throws EncryptionException exception thrown on encryption errors
     */
    @Nonnull protected EncryptedKey encryptKey(@Nonnull final Key targetKey, @Nonnull final Key encryptionKey,
            @Nonnull final String encryptionAlgorithmURI, @Nullable final RSAOAEPParameters rsaOAEPParams,
            @Nonnull final Document containingDocument) throws EncryptionException {
        
        Constraint.isNotNull(targetKey, "Target key cannot be null");
        Constraint.isNotNull(encryptionKey, "Encryption key cannot be null");
        Constraint.isNotNull(encryptionAlgorithmURI, "Encryption algorithm URI cannot be null");
        Constraint.isNotNull(containingDocument, "Containing document cannot be null");

        log.debug("Encrypting encryption key with algorithm: {}", encryptionAlgorithmURI);
        
        final XMLCipher xmlCipher;
        try {
            xmlCipher = buildXMLCipher(encryptionKey, encryptionAlgorithmURI, rsaOAEPParams);
        } catch (final XMLEncryptionException e) {
            log.error("Error initializing cipher instance on key encryption: {}", e.getMessage());
            throw new EncryptionException("Error initializing cipher instance on key encryption", e);
        }

        final org.apache.xml.security.encryption.EncryptedKey apacheEncryptedKey;
        try {
            if (AlgorithmSupport.isRSAOAEP(encryptionAlgorithmURI) && rsaOAEPParams != null) {
                apacheEncryptedKey = xmlCipher.encryptKey(containingDocument, targetKey, 
                        getEffectiveMGF(encryptionAlgorithmURI, rsaOAEPParams), 
                        decodeOAEPParams(rsaOAEPParams.getOAEPParams()));
            } else {
                apacheEncryptedKey = xmlCipher.encryptKey(containingDocument, targetKey);
            }
            assert apacheEncryptedKey != null;
            postProcessApacheEncryptedKey(apacheEncryptedKey, targetKey, encryptionKey, encryptionAlgorithmURI,
                    containingDocument);
        } catch (final XMLEncryptionException e) {
            log.error("Error encrypting element on key encryption: {}", e.getMessage());
            throw new EncryptionException("Error encrypting element on key encryption", e);
        }

        try {
            final Element encKeyElement = xmlCipher.martial(containingDocument, apacheEncryptedKey);
            assert encKeyElement != null;
            return (EncryptedKey) encryptedKeyUnmarshaller.unmarshall(encKeyElement);
        } catch (final UnmarshallingException e) {
            log.error("Error unmarshalling EncryptedKey element: {}", e.getMessage());
            throw new EncryptionException("Error unmarshalling EncryptedKey element");
        }
    }

    /**
     * Construct and return an instance of {@link XMLCipher} based on the given inputs.
     * 
     * @param encryptionKey the key transport encryption key with which to initialize the XMLCipher
     * @param encryptionAlgorithmURI the key transport encryption algorithm URI
     * @param rsaOAEPParams the optional RSA OAEP parameters instance
     * @return new XMLCipher instance
     * @throws XMLEncryptionException if there is a problem constructing the XMLCipher instance
     */
    @Nonnull protected XMLCipher buildXMLCipher(@Nonnull final Key encryptionKey, 
            @Nonnull final String encryptionAlgorithmURI, @Nullable final RSAOAEPParameters rsaOAEPParams) 
                    throws XMLEncryptionException { 
        
        final XMLCipher xmlCipher;
        
        if (getJCAProviderName() != null) {
            if (AlgorithmSupport.isRSAOAEP(encryptionAlgorithmURI) && rsaOAEPParams != null 
                    && rsaOAEPParams.getDigestMethod() != null) {
                xmlCipher = XMLCipher.getProviderInstance(encryptionAlgorithmURI, getJCAProviderName(), 
                        (String)null, rsaOAEPParams.getDigestMethod());
            } else {
                xmlCipher = XMLCipher.getProviderInstance(encryptionAlgorithmURI, getJCAProviderName());
            }
        } else {
            if (AlgorithmSupport.isRSAOAEP(encryptionAlgorithmURI) && rsaOAEPParams != null 
                    && rsaOAEPParams.getDigestMethod() != null) {
                xmlCipher = XMLCipher.getInstance(encryptionAlgorithmURI, null, rsaOAEPParams.getDigestMethod());
            } else {
                xmlCipher = XMLCipher.getInstance(encryptionAlgorithmURI);
            }
        }
        
        xmlCipher.init(XMLCipher.WRAP_MODE, encryptionKey);
        
        return xmlCipher;
    }
    
    /**
     * Get the effective RSA OAEP mask generation function (MGF) to use.
     * 
     * @param encryptionAlgorithmURI the key transport encryption algorithm URI
     * @param rsaOAEPParams the optional RSA OAEP params instance
     * @return the effective MGF algorithm URI to use, may be null
     */
    @Nullable protected String getEffectiveMGF(@Nonnull final String encryptionAlgorithmURI, 
            @Nullable final RSAOAEPParameters rsaOAEPParams) {
        
        if (EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11.equals(encryptionAlgorithmURI) 
                && rsaOAEPParams != null) {
            return rsaOAEPParams.getMaskGenerationFunction();
        }
        return null;
    }
    
    /**
     * Safely decode and normalize base64-encoded OAEPParams data.
     * 
     * @param base64Params the base64-encoded parameters
     * @return the decoded parameters or null
     * @throws EncryptionException if there is a problem base64-decoding the OAEPParams data
     */
    @Nullable protected byte[] decodeOAEPParams(@Nullable final String base64Params) throws EncryptionException {
        try {
            if (base64Params != null) {
                final byte[] oaepParams = Base64Support.decode(base64Params);
                if (oaepParams.length == 0) {
                    return null;
                }
                return oaepParams;
            }
            return null;
        } catch (final DecodingException e) {
            throw new EncryptionException(String.format("Error decoding OAEPParams data '%s'", base64Params), e);
        }
    }

    /**
     * 
     * Post-process the Apache EncryptedKey, prior to marshalling to DOM and unmarshalling into an XMLObject.
     * 
     * @param apacheEncryptedKey the Apache EncryptedKeyObject to post-process
     * @param targetKey the key to encrypt
     * @param encryptionKey the key with which to encrypt the target key
     * @param encryptionAlgorithmURI the XML Encryption algorithm URI corresponding to the encryption key
     * @param containingDocument the document that will own the resulting element
     * 
     * @throws EncryptionException exception thrown on encryption errors
     */
    protected void postProcessApacheEncryptedKey(
            @Nonnull final org.apache.xml.security.encryption.EncryptedKey apacheEncryptedKey,
            @Nonnull final Key targetKey, @Nonnull final Key encryptionKey,
            @Nonnull final String encryptionAlgorithmURI, @Nonnull final Document containingDocument)
            throws EncryptionException {

        // To maximize interop, explicitly express the defaults of SHA-1 digest method and MGF-1 w/ SHA-1 input
        // parameters to RSA-OAEP key transport algorithm. The latter only applies to the XML Encryption 1.1 variant.
        // Check and only add if the library hasn't already done so.
        if (AlgorithmSupport.isRSAOAEP(encryptionAlgorithmURI)) {
            final org.apache.xml.security.encryption.EncryptionMethod apacheEncryptionMethod =
                    apacheEncryptedKey.getEncryptionMethod();
            
            if (apacheEncryptionMethod.getDigestAlgorithm() == null) {
                apacheEncryptionMethod.setDigestAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA1);
            }
            
            if (!EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP.equals(encryptionAlgorithmURI)) {
                if (apacheEncryptionMethod.getMGFAlgorithm() == null) {
                    apacheEncryptionMethod.setMGFAlgorithm(EncryptionConstants.ALGO_ID_MGF1_SHA1);
                }
            }
        }
    }

    /**
     * Encrypts the given XMLObject using the specified encryption key, algorithm URI and content mode flag.
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encryptionKey the key with which to encrypt the XMLObject
     * @param encryptionAlgorithmURI the XML Encryption algorithm URI corresponding to the encryption key
     * @param encryptContentMode whether just the content of the XMLObject should be encrypted
     * @return the resulting EncryptedData object
     * @throws EncryptionException exception thrown on encryption errors
     */
    @Nonnull protected EncryptedData encryptElement(@Nonnull final XMLObject xmlObject,
            @Nonnull final Key encryptionKey, @Nonnull final String encryptionAlgorithmURI,
            final boolean encryptContentMode) throws EncryptionException {

        log.debug("Encrypting XMLObject using algorithm URI {} with content mode {}", encryptionAlgorithmURI,
                encryptContentMode);

        checkAndMarshall(xmlObject);

        final Element targetElement = xmlObject.getDOM();
        assert targetElement != null;
        final Document ownerDocument = targetElement.getOwnerDocument();

        final XMLCipher xmlCipher;
        try {
            if (getJCAProviderName() != null) {
                xmlCipher = XMLCipher.getProviderInstance(encryptionAlgorithmURI, getJCAProviderName());
            } else {
                xmlCipher = XMLCipher.getInstance(encryptionAlgorithmURI);
            }
            xmlCipher.init(XMLCipher.ENCRYPT_MODE, encryptionKey);
        } catch (final XMLEncryptionException e) {
            log.error("Error initializing cipher instance on XMLObject encryption: {}", e.getMessage());
            throw new EncryptionException("Error initializing cipher instance", e);
        }

        final org.apache.xml.security.encryption.EncryptedData apacheEncryptedData;
        try {
            apacheEncryptedData = xmlCipher.encryptData(ownerDocument, targetElement, encryptContentMode);
        } catch (final Exception e) {
            log.error("Error encrypting XMLObject: {}", e.getMessage());
            throw new EncryptionException("Error encrypting XMLObject", e);
        }

        try {
            final Element encDataElement = xmlCipher.martial(ownerDocument, apacheEncryptedData);
            assert encDataElement != null;
            return (EncryptedData) encryptedDataUnmarshaller.unmarshall(encDataElement);
        } catch (final UnmarshallingException e) {
            log.error("Error unmarshalling EncryptedData element: {}", e.getMessage());
            throw new EncryptionException("Error unmarshalling EncryptedData element", e);
        }
    }

    /**
     * Encrypts the given XMLObject using the specified encryption key, algorithm URI and content mode flag.
     * EncryptedKeys, if any, are placed inline within the KeyInfo of the resulting EncryptedData.
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encParams the encryption parameters to use
     * @param kekParamsList the key encryption parameters to use
     * @param encryptContentMode whether just the content of the XMLObject should be encrypted
     * 
     * @return the resulting EncryptedData object
     * @throws EncryptionException exception thrown on encryption errors
     */
    @Nonnull private EncryptedData encryptElement(@Nonnull final XMLObject xmlObject,
            @Nonnull final DataEncryptionParameters encParams,
            @Nonnull final List<KeyEncryptionParameters> kekParamsList, final boolean encryptContentMode)
                    throws EncryptionException {

        checkParams(encParams, kekParamsList);

        final String encryptionAlgorithmURI = encParams.getAlgorithm();
        // Checked above.
        assert encryptionAlgorithmURI != null;
        
        final Credential encryptionCred = encParams.getEncryptionCredential();
        Key encryptionKey = encryptionCred != null ? CredentialSupport.extractEncryptionKey(encryptionCred) : null;
        if (encryptionKey == null) {
            encryptionKey = generateEncryptionKey(encryptionAlgorithmURI);
        }

        final EncryptedData encryptedData =
                encryptElement(xmlObject, encryptionKey, encryptionAlgorithmURI, encryptContentMode);
        final Element domNode = encryptedData.getDOM();
        assert domNode != null;
        final Document ownerDocument = domNode.getOwnerDocument();
        assert ownerDocument != null;

        final KeyInfoGenerator keyInfoGenerator = encParams.getKeyInfoGenerator();
        if (keyInfoGenerator != null) {
            log.debug("Dynamically generating KeyInfo from Credential for EncryptedData using generator: {}",
                    keyInfoGenerator.getClass().getName());
            try {
                encryptedData.setKeyInfo(keyInfoGenerator.generate(encParams.getEncryptionCredential()));
            } catch (final SecurityException e) {
                log.error("Error during EncryptedData KeyInfo generation: {}", e.getMessage());
                throw new EncryptionException("Error during EncryptedData KeyInfo generation", e);
            }
        }

        for (final KeyEncryptionParameters kekParams : kekParamsList) {
            assert kekParams != null;
            final EncryptedKey encryptedKey = encryptKey(encryptionKey, kekParams, ownerDocument);
            KeyInfo keyInfo = encryptedData.getKeyInfo();
            if (keyInfo == null) {
                keyInfo = keyInfoBuilder.buildObject();
                encryptedData.setKeyInfo(keyInfo);
            }
            keyInfo.getEncryptedKeys().add(encryptedKey);
        }

        return encryptedData;
    }

    /**
     * Ensure that the XMLObject is marshalled.
     * 
     * @param xmlObject the object to check and marshall
     * @throws EncryptionException thrown if there is an error when marshalling the XMLObject
     */
    protected void checkAndMarshall(@Nonnull final XMLObject xmlObject) throws EncryptionException {
        Element targetElement = xmlObject.getDOM();
        if (targetElement == null) {
            try {
                final Marshaller marshaller =
                        XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(xmlObject);
                if (marshaller == null) {
                    throw new MarshallingException("No marshaller available for " + xmlObject.getElementQName());
                }
                targetElement = marshaller.marshall(xmlObject);
            } catch (final MarshallingException e) {
                log.error("Error marshalling target XMLObject: {}", e.getMessage());
                throw new EncryptionException("Error marshalling target XMLObject", e);
            }
        }
    }

    /**
     * Check data encryption parameters for consistency and required values.
     * 
     * @param encParams the data encryption parameters to check
     * 
     * @throws EncryptionException thrown if any parameters are missing or have invalid values
     */
    protected void checkParams(@Nonnull final DataEncryptionParameters encParams) throws EncryptionException {
        if (Strings.isNullOrEmpty(encParams.getAlgorithm())) {
            log.error("Data encryption algorithm URI is required");
            throw new EncryptionException("Data encryption algorithm URI is required");
        }
    }

    /**
     * Check key encryption parameters for consistency and required values.
     * 
     * @param kekParams the key encryption parameters to check
     * @param allowEmpty if false, a null parameter is treated as an error
     * 
     * @throws EncryptionException thrown if any parameters are missing or have invalid values
     */
    protected void checkParams(@Nullable final KeyEncryptionParameters kekParams, final boolean allowEmpty)
            throws EncryptionException {
        if (kekParams == null) {
            if (allowEmpty) {
                return;
            }
            log.error("Key encryption parameters are required");
            throw new EncryptionException("Key encryption parameters are required");
        }
        
        final Credential encryptionCred = kekParams.getEncryptionCredential();
        final Key key = encryptionCred != null ? CredentialSupport.extractEncryptionKey(encryptionCred) : null;
        if (key == null) {
            log.error("Key encryption credential and contained key are required");
            throw new EncryptionException("Key encryption credential and contained key are required");
        } else if (key instanceof DSAPublicKey) {
            log.error("Attempt made to use DSA key for encrypted key transport");
            throw new EncryptionException("DSA keys may not be used for encrypted key transport");
        } else if (key instanceof ECPublicKey) {
            log.error("Attempt made to use EC key for encrypted key transport");
            throw new EncryptionException("EC keys may not be used for encrypted key transport");
        } else if (Strings.isNullOrEmpty(kekParams.getAlgorithm())) {
            log.error("Key encryption algorithm URI is required");
            throw new EncryptionException("Key encryption algorithm URI is required");
        }
    }

    /**
     * Check a list of key encryption parameters for consistency and required values.
     * 
     * @param kekParamsList the key encryption parameters list to check
     * @param allowEmpty if false, a null or empty list is treated as an error
     * 
     * @throws EncryptionException thrown if any parameters are missing or have invalid values
     */
    protected void checkParams(@Nullable final List<KeyEncryptionParameters> kekParamsList, final boolean allowEmpty)
            throws EncryptionException {
        if (kekParamsList == null || kekParamsList.isEmpty()) {
            if (allowEmpty) {
                return;
            }
            log.error("Key encryption parameters list may not be empty");
            throw new EncryptionException("Key encryption parameters list may not be empty");
        }
        for (final KeyEncryptionParameters kekParams : kekParamsList) {
            checkParams(kekParams, false);
        }
    }

    /**
     * Check the encryption parameters and key encryption parameters for valid combinations of options.
     * 
     * @param encParams the encryption parameters to use
     * @param kekParamsList the key encryption parameters to use
     * @throws EncryptionException exception thrown on encryption errors
     */
    protected void checkParams(@Nonnull final DataEncryptionParameters encParams,
            @Nullable final List<KeyEncryptionParameters> kekParamsList) throws EncryptionException {

        checkParams(encParams);
        checkParams(kekParamsList, true);

        final Credential encryptionCred = encParams.getEncryptionCredential();
        
        if ((encryptionCred == null || CredentialSupport.extractEncryptionKey(encryptionCred) == null)
                && (kekParamsList == null || kekParamsList.isEmpty())) {
            log.error("Using a generated encryption key requires a KeyEncryptionParameters "
                    + "object and key encryption key");
            throw new EncryptionException("Using a generated encryption key requires a KeyEncryptionParameters "
                    + "object and key encryption key");
        }
    }

    /**
     * Generate a random symmetric encryption key.
     * 
     * @param encryptionAlgorithmURI the encryption algorithm URI
     * @return a randomly generated symmetric key
     * @throws EncryptionException thrown if the key cannot be generated based on the specified algorithm URI
     */
    @Nonnull protected SecretKey generateEncryptionKey(@Nonnull final String encryptionAlgorithmURI)
            throws EncryptionException {
        try {
            log.debug("Generating random symmetric data encryption key from algorithm URI: {}", encryptionAlgorithmURI);
            return AlgorithmSupport.generateSymmetricKey(encryptionAlgorithmURI);
        } catch (final NoSuchAlgorithmException e) {
            log.error("Could not generate encryption key, algorithm URI was invalid: " + encryptionAlgorithmURI);
            throw new EncryptionException("Could not generate encryption key, algorithm URI was invalid: "
                    + encryptionAlgorithmURI);
        } catch (final KeyException e) {
            log.error("Could not generate encryption key from algorithm URI: " + encryptionAlgorithmURI);
            throw new EncryptionException("Could not generate encryption key from algorithm URI: "
                    + encryptionAlgorithmURI);
        }
    }

    /*
     * Initialize the Apache XML security library if it hasn't been already
     */
    static {
        if (!Init.isInitialized()) {
            Init.init();
        }
    }

}