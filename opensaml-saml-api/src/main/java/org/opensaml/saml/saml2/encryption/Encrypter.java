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

package org.opensaml.saml.saml2.encryption;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.security.IdentifierGenerationStrategy;
import net.shibboleth.shared.security.IdentifierGenerationStrategy.ProviderType;
import net.shibboleth.shared.xml.SerializeSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.BaseID;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.EncryptedAttribute;
import org.opensaml.saml.saml2.core.EncryptedElementType;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NewEncryptedID;
import org.opensaml.saml.saml2.core.NewID;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.xmlsec.encryption.CarriedKeyName;
import org.opensaml.xmlsec.encryption.DataReference;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.ReferenceList;
import org.opensaml.xmlsec.encryption.XMLEncryptionBuilder;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.encryption.support.KeyEncryptionParameters;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyName;
import org.opensaml.xmlsec.signature.RetrievalMethod;
import org.opensaml.xmlsec.signature.XMLSignatureBuilder;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

/**
 * Encrypter for SAML 2 SAMLObjects which has specific options for generating instances of subtypes of
 * {@link EncryptedElementType}.
 * 
 * <p>
 * Overloaded methods are provided for encrypting various SAML 2 elements to their corresponding encrypted element
 * variant of {@link EncryptedElementType}.
 * </p>
 * 
 * <p>
 * Support is also provided for differing placement options for any associated EncryptedKeys that may be generated. The
 * options are:
 * </p>
 *
 * <ul>
 * <li><code>INLINE</code>: EncryptedKeys will placed inside the KeyInfo element of the EncryptedData element</li>
 * <li><code>PEER</code>: EncryptedKeys will be placed as peer elements of the EncryptedData inside the
 * EncryptedElementType element</li>
 * </ul>
 *
 * <p>
 * The default placement is <code>PEER</code>.
 * </p>
 * 
 * <p>
 * The EncryptedKey forward and back referencing behavior associated with these key placement options is intended to be
 * consistent with the guidelines detailed in SAML 2 Errata E43. See that document for further information.
 * </p>
 * 
 * <p>
 * For information on other parameters and options, and general XML Encryption issues, see
 * {@link org.opensaml.xmlsec.encryption.support.Encrypter}.
 * </p>
 * 
 */
public class Encrypter extends org.opensaml.xmlsec.encryption.support.Encrypter {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(Encrypter.class);
    
    /**
     * Options for where to place the resulting EncryptedKey elements with respect to the associated EncryptedData
     * element.
     */
    public enum KeyPlacement {
        /** Place the EncryptedKey element(s) as a peer to the EncryptedData inside the EncryptedElementType. */
        PEER,

        /** Place the EncryptedKey element(s) within the KeyInfo of the EncryptedData. */
        INLINE
    }

    /** Factory for building XMLObject instances. */
    private XMLObjectBuilderFactory builderFactory;

    /** Builder for KeyInfo objects. */
    private XMLSignatureBuilder<KeyInfo> keyInfoBuilder;

    /** Builder for DataReference objects. */
    private XMLEncryptionBuilder<DataReference> dataReferenceBuilder;

    /** Builder for ReferenceList objects. */
    private XMLEncryptionBuilder<ReferenceList> referenceListBuilder;

    /** Builder for RetrievalMethod objects. */
    private XMLSignatureBuilder<RetrievalMethod> retrievalMethodBuilder;

    /** Builder for KeyName objects. */
    private XMLSignatureBuilder<KeyName> keyNameBuilder;

    /** Builder for CarriedKeyName objects. */
    private XMLEncryptionBuilder<CarriedKeyName> carriedKeyNameBuilder;

    /** Generator for XML ID attribute values. */
    private IdentifierGenerationStrategy idGenerator;

    /** The parameters to use for encrypting the data. */
    private DataEncryptionParameters encParams;

    /** The parameters to use for encrypting (wrapping) the data encryption key. */
    private List<KeyEncryptionParameters> kekParamsList;

    /** The option for where to place the generated EncryptedKey elements. */
    @Nonnull private KeyPlacement keyPlacement = KeyPlacement.PEER;

    /**
     * Constructor.
     * 
     * @param dataEncParams the data encryption parameters
     * @param keyEncParams the key encryption parameters
     */
    public Encrypter(final DataEncryptionParameters dataEncParams, final List<KeyEncryptionParameters> keyEncParams) {
        encParams = dataEncParams;
        kekParamsList = keyEncParams;

        init();
    }

    /**
     * Constructor.
     * 
     * @param dataEncParams the data encryption parameters
     * @param keyEncParam the key encryption parameter
     */
    public Encrypter(final DataEncryptionParameters dataEncParams, final KeyEncryptionParameters keyEncParam) {
        final List<KeyEncryptionParameters> keks = new ArrayList<>();
        keks.add(keyEncParam);

        encParams = dataEncParams;
        kekParamsList = keks;

        init();
    }

    /**
     * Constructor.
     * 
     * @param dataEncParams the data encryption parameters
     */
    public Encrypter(final DataEncryptionParameters dataEncParams) {
        final List<KeyEncryptionParameters> keks = new ArrayList<>();

        encParams = dataEncParams;
        kekParamsList = keks;

        init();
    }

    /**
     * Helper method for constructors.
     */
    private void init() {
        builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
        keyInfoBuilder =
                (XMLSignatureBuilder<KeyInfo>) builderFactory.<KeyInfo>ensureBuilder(KeyInfo.DEFAULT_ELEMENT_NAME);
        dataReferenceBuilder =
                (XMLEncryptionBuilder<DataReference>) builderFactory.<DataReference>ensureBuilder(
                        DataReference.DEFAULT_ELEMENT_NAME);
        referenceListBuilder =
                (XMLEncryptionBuilder<ReferenceList>) builderFactory.<ReferenceList>ensureBuilder(
                        ReferenceList.DEFAULT_ELEMENT_NAME);
        retrievalMethodBuilder =
                (XMLSignatureBuilder<RetrievalMethod>) builderFactory.<RetrievalMethod>ensureBuilder(
                        RetrievalMethod.DEFAULT_ELEMENT_NAME);
        keyNameBuilder =
                (XMLSignatureBuilder<KeyName>) builderFactory.<KeyName>ensureBuilder(KeyName.DEFAULT_ELEMENT_NAME);
        carriedKeyNameBuilder =
                (XMLEncryptionBuilder<CarriedKeyName>) builderFactory.<CarriedKeyName>ensureBuilder(
                        CarriedKeyName.DEFAULT_ELEMENT_NAME);

        idGenerator = IdentifierGenerationStrategy.getInstance(ProviderType.RANDOM);
    }

    /**
     * Set the generator to use when creating XML ID attribute values.
     * 
     * @param newIDGenerator the new IdentifierGenerator to use
     */
    public void setIDGenerator(@Nonnull final IdentifierGenerationStrategy newIDGenerator) {
        idGenerator = Constraint.isNotNull(newIDGenerator, "IdentifierGenerationStrategy cannot be null");
    }

    /**
     * Get the current key placement option.
     * 
     * @return returns the key placement option.
     */
    @Nonnull public KeyPlacement getKeyPlacement() {
        return keyPlacement;
    }

    /**
     * Set the key placement option.
     * 
     * @param newKeyPlacement The new key placement option to set
     */
    public void setKeyPlacement(@Nonnull final KeyPlacement newKeyPlacement) {
        keyPlacement = Constraint.isNotNull(newKeyPlacement, "KeyPlacement cannot be null");
    }

    /**
     * Encrypt the specified Assertion.
     * 
     * @param assertion the Assertion to encrypt
     * @return an EncryptedAssertion
     * @throws EncryptionException thrown when encryption generates an error
     */
    @Nonnull public EncryptedAssertion encrypt(@Nonnull final Assertion assertion) throws EncryptionException {
        logPreEncryption(assertion, "Assertion");
        return (EncryptedAssertion) encrypt(assertion, EncryptedAssertion.DEFAULT_ELEMENT_NAME);
    }

    /**
     * Encrypt the specified Assertion, treating as an identifier and returning an EncryptedID.
     * 
     * @param assertion the Assertion to encrypt
     * @return an EncryptedID
     * @throws EncryptionException thrown when encryption generates an error
     */
    @Nonnull public EncryptedID encryptAsID(@Nonnull final Assertion assertion) throws EncryptionException {
        logPreEncryption(assertion, "Assertion (as EncryptedID)");
        return (EncryptedID) encrypt(assertion, EncryptedID.DEFAULT_ELEMENT_NAME);
    }

    /**
     * Encrypt the specified Attribute.
     * 
     * @param attribute the Attribute to encrypt
     * @return an EncryptedAttribute
     * @throws EncryptionException thrown when encryption generates an error
     */
    @Nonnull public EncryptedAttribute encrypt(@Nonnull final Attribute attribute) throws EncryptionException {
        logPreEncryption(attribute, "Attribute");
        return (EncryptedAttribute) encrypt(attribute, EncryptedAttribute.DEFAULT_ELEMENT_NAME);
    }

    /**
     * Encrypt the specified NameID.
     * 
     * @param nameID the NameID to encrypt
     * @return an EncryptedID
     * @throws EncryptionException thrown when encryption generates an error
     */
    @Nonnull public EncryptedID encrypt(@Nonnull final NameID nameID) throws EncryptionException {
        logPreEncryption(nameID, "NameID");
        return (EncryptedID) encrypt(nameID, EncryptedID.DEFAULT_ELEMENT_NAME);
    }

    /**
     * Encrypt the specified BaseID.
     * 
     * @param baseID the BaseID to encrypt
     * @return an EncryptedID
     * @throws EncryptionException thrown when encryption generates an error
     */
    @Nonnull public EncryptedID encrypt(@Nonnull final BaseID baseID) throws EncryptionException {
        logPreEncryption(baseID, "BaseID");
        return (EncryptedID) encrypt(baseID, EncryptedID.DEFAULT_ELEMENT_NAME);
    }

    /**
     * Encrypt the specified NewID.
     * 
     * @param newID the NewID to encrypt
     * @return a NewEncryptedID
     * @throws EncryptionException thrown when encryption generates an error
     */
    @Nonnull public NewEncryptedID encrypt(@Nonnull final NewID newID) throws EncryptionException {
        logPreEncryption(newID, "NewID");
        return (NewEncryptedID) encrypt(newID, NewEncryptedID.DEFAULT_ELEMENT_NAME);
    }
    
    /**
     * Log the target object prior to encryption.
     * 
     * @param xmlObject the XMLObject to encrypt
     * @param objectType String description of the type of object to encrypt
     */
    private void logPreEncryption(@Nonnull final XMLObject xmlObject, @Nonnull final String objectType) {
        if (log.isDebugEnabled()) {
            try {
                final Element dom = XMLObjectSupport.marshall(xmlObject);
                log.debug("{} before encryption:\n{}", objectType, SerializeSupport.prettyPrintXML(dom));
            } catch (final MarshallingException e) {
                log.error("Unable to marshall {} for logging purposes", objectType, e);
            }
        }
    }

    /**
     * Encrypt the specified XMLObject, and return it as an instance of the specified QName, which should be one of the
     * types derived from {@link org.opensaml.saml.saml2.core.EncryptedElementType}.
     * 
     * @param xmlObject the XMLObject to encrypt
     * @param encElementName the QName of the specialization of EncryptedElementType to return
     * @return a specialization of {@link org.opensaml.saml.saml2.core.EncryptedElementType}
     * @throws EncryptionException thrown when encryption generates an error
     */
    @Nonnull private EncryptedElementType encrypt(@Nonnull final XMLObject xmlObject,
            @Nonnull final QName encElementName) throws EncryptionException {

        checkParams(encParams, kekParamsList);

        final EncryptedElementType encElement =
                (EncryptedElementType) builderFactory.ensureBuilder(encElementName).buildObject(encElementName);

        // Marshall the containing element, we will need its Document context to pass
        // to the key encryption method
        checkAndMarshall(encElement);
        final Element domNode = encElement.getDOM();
        assert domNode != null;
        final Document ownerDocument = domNode.getOwnerDocument();

        final String encryptionAlgorithmURI = encParams.getAlgorithm();
        // Checked above.
        assert encryptionAlgorithmURI != null;
        
        final Credential encryptionCred = encParams.getEncryptionCredential();
        Key encryptionKey = encryptionCred != null ? CredentialSupport.extractEncryptionKey(encryptionCred) : null;
        if (encryptionKey == null) {
            encryptionKey = generateEncryptionKey(encryptionAlgorithmURI);
        }

        final EncryptedData encryptedData = encryptElement(xmlObject, encryptionKey, encryptionAlgorithmURI, false);
        final KeyInfoGenerator generator = encParams.getKeyInfoGenerator();
        if (generator != null) {
            log.debug("Dynamically generating KeyInfo from Credential for EncryptedData using generator: {}", generator
                    .getClass().getName());
            try {
                encryptedData.setKeyInfo(generator.generate(encParams.getEncryptionCredential()));
            } catch (final SecurityException e) {
                throw new EncryptionException("Error generating EncryptedData KeyInfo", e);
            }
        }

        final List<EncryptedKey> encryptedKeys = new ArrayList<>();
        if (kekParamsList != null && !kekParamsList.isEmpty()) {
            encryptedKeys.addAll(encryptKey(encryptionKey, kekParamsList, ownerDocument));
        }

        return processElements(encElement, encryptedData, encryptedKeys);
    }

    /**
     * Handle post-processing of generated EncryptedData and EncryptedKey(s) and storage in the appropriate
     * EncryptedElementType instance.
     * 
     * @param encElement the EncryptedElementType instance which will hold the encrypted data and keys
     * @param encData the EncryptedData object
     * @param encKeys the list of EncryptedKey objects
     * @return the processed EncryptedElementType instance
     * 
     * @throws EncryptionException thrown when processing encounters an error
     */
    @Nonnull protected EncryptedElementType processElements(@Nonnull final EncryptedElementType encElement,
            @Nonnull final EncryptedData encData, @Nonnull final List<EncryptedKey> encKeys)
                    throws EncryptionException {
        // First ensure certain elements/attributes are non-null, common to all cases.
        if (encData.getID() == null) {
            encData.setID(idGenerator.generateIdentifier());
        }

        // If not doing key wrapping, just return the encrypted element
        if (encKeys.isEmpty()) {
            encElement.setEncryptedData(encData);
            return encElement;
        }

        if (encData.getKeyInfo() == null) {
            encData.setKeyInfo(keyInfoBuilder.buildObject());
        }

        for (final EncryptedKey encKey : encKeys) {
            if (encKey.getID() == null) {
                encKey.setID(idGenerator.generateIdentifier());
            }
        }

        switch (keyPlacement) {
            case INLINE:
                return placeKeysInline(encElement, encData, encKeys);
            case PEER:
                return placeKeysAsPeers(encElement, encData, encKeys);
            default:
                // Shouldn't be able to get here, but just in case...
                throw new EncryptionException("Unsupported key placement option was specified: " + keyPlacement);
        }
    }

    /**
     * Place the EncryptedKey elements inside the KeyInfo element within the EncryptedData element.
     * 
     * Although operationally trivial, this method is provided so that subclasses may override or augment as desired.
     * 
     * @param encElement the EncryptedElementType instance which will hold the encrypted data and keys
     * @param encData the EncryptedData object
     * @param encKeys the list of EncryptedKey objects
     * @return the processed EncryptedElementType instance
     */
    @Nonnull protected EncryptedElementType placeKeysInline(@Nonnull final EncryptedElementType encElement,
            @Nonnull final EncryptedData encData, @Nonnull final List<EncryptedKey> encKeys) {

        log.debug("Placing EncryptedKey elements inline inside EncryptedData");

        final KeyInfo keyInfo = encData.getKeyInfo();
        if (keyInfo != null) {
            keyInfo.getEncryptedKeys().addAll(encKeys);
        }
        encElement.setEncryptedData(encData);
        return encElement;
    }

    /**
     * Store the specified EncryptedData and EncryptedKey(s) in the specified instance of EncryptedElementType as peer
     * elements, following SAML 2 Errata E43 guidelines for forward and back referencing between the EncryptedData and
     * EncryptedKey(s).
     * 
     * @param encElement a specialization of EncryptedElementType to store the encrypted data and keys
     * @param encData the EncryptedData to store
     * @param encKeys the EncryptedKey(s) to store
     * @return the resulting specialization of EncryptedElementType
     */
    @Nonnull protected EncryptedElementType placeKeysAsPeers(@Nonnull final EncryptedElementType encElement,
            @Nonnull final EncryptedData encData, @Nonnull final List<EncryptedKey> encKeys) {

        log.debug("Placing EncryptedKey elements as peers of EncryptedData in EncryptedElementType");

        for (final EncryptedKey encKey : encKeys) {
            if (encKey.getReferenceList() == null) {
                encKey.setReferenceList(referenceListBuilder.buildObject());
            }
        }

        // If there is only 1 EncryptedKey we have a simple forward reference (RetrievalMethod)
        // and back reference (ReferenceList/DataReference) requirement.
        // Multiple "multicast" keys use back reference + CarriedKeyName
        if (encKeys.size() == 1) {
            linkSinglePeerKey(encData, encKeys.get(0));
        } else if (encKeys.size() > 1) {
            linkMultiplePeerKeys(encData, encKeys);
        }

        encElement.setEncryptedData(encData);
        encElement.getEncryptedKeys().addAll(encKeys);

        return encElement;
    }

    /**
     * Link a single EncryptedKey to the EncryptedData according to guidelines in SAML Errata E43.
     * 
     * @param encData the EncryptedData
     * @param encKey the EncryptedKey
     */
    protected void linkSinglePeerKey(@Nonnull final EncryptedData encData, @Nonnull final EncryptedKey encKey) {
        log.debug("Linking single peer EncryptedKey with RetrievalMethod and DataReference");
        
        final KeyInfo keyInfo = encData.getKeyInfo();
        if (keyInfo != null) {
            // Forward reference from EncryptedData to the EncryptedKey
            final RetrievalMethod rm = retrievalMethodBuilder.buildObject();
            rm.setURI("#" + encKey.getID());
            rm.setType(EncryptionConstants.TYPE_ENCRYPTED_KEY);
            keyInfo.getRetrievalMethods().add(rm);
        }

        // Back reference from the EncryptedKey to the EncryptedData
        final ReferenceList refList = encKey.getReferenceList();
        if (refList != null) {
            final DataReference dr = dataReferenceBuilder.buildObject();
            dr.setURI("#" + encData.getID());
            refList.getDataReferences().add(dr);
        }
    }

    /**
     * Link multiple "multicast" EncryptedKeys to the EncryptedData according to guidelines in SAML Errata E43.
     * 
     * @param encData the EncryptedData
     * @param encKeys the list of EncryptedKeys
     */
    protected void linkMultiplePeerKeys(@Nonnull final EncryptedData encData,
            @Nonnull final List<EncryptedKey> encKeys) {
        log.debug("Linking multiple peer EncryptedKeys with CarriedKeyName and DataReference");
        // Get the name of the data encryption key
        final List<KeyName> dataEncKeyNames;
        final KeyInfo keyInfo = encData.getKeyInfo();
        if (keyInfo != null) {
            dataEncKeyNames = keyInfo.getKeyNames();
        } else {
            dataEncKeyNames = CollectionSupport.emptyList();
        }
        
        final String carriedKeyNameValue;
        if (dataEncKeyNames.size() == 0 || Strings.isNullOrEmpty(dataEncKeyNames.get(0).getValue())) {
            // If there isn't one, autogenerate a random key name.
            final String keyNameValue = idGenerator.generateIdentifier();
            log.debug("EncryptedData encryption key had no KeyName, generated one for use in CarriedKeyName: {}",
                    keyNameValue);

            KeyName keyName = dataEncKeyNames.get(0);
            if (keyName == null) {
                keyName = keyNameBuilder.buildObject();
                dataEncKeyNames.add(keyName);
            }
            keyName.setValue(keyNameValue);
            carriedKeyNameValue = keyNameValue;
        } else {
            carriedKeyNameValue = dataEncKeyNames.get(0).getValue();
        }

        // Set carried key name of the multicast key in each EncryptedKey
        for (final EncryptedKey encKey : encKeys) {
            CarriedKeyName carriedName = encKey.getCarriedKeyName();
            if (carriedName == null) {
                carriedName = carriedKeyNameBuilder.buildObject();
                encKey.setCarriedKeyName(carriedName);
            }
            carriedName.setValue(carriedKeyNameValue);

            // Back reference from the EncryptedKeys to the EncryptedData
            final ReferenceList refList = encKey.getReferenceList();
            if (refList != null) {
                final DataReference dr = dataReferenceBuilder.buildObject();
                dr.setURI("#" + encData.getID());
                refList.getDataReferences().add(dr);
            }

        }
    }

}