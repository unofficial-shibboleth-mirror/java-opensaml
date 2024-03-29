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

package org.opensaml.saml.saml2.assertion.impl;

import java.security.KeyException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.namespace.QName;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.collection.LazyList;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.primitive.LoggerFactory;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.KeyInfoConfirmationDataType;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.DEREncodedKeyValue;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.X509Data;
import org.slf4j.Logger;

/**
 * Validates a Holder of Key subject confirmation.
 * 
 * <p>
 * A subject confirmation is considered confirmed if one of the
 * following checks has passed:
 * </p>
 * <ul>
 * <li>
 * the presenter's public key (either given explicitly or extracted from the given certificate) matches a
 * {@link KeyValue} or {@link DEREncodedKeyValue} within one of the {@link KeyInfo} entries in the confirmation data
 * </li>
 * <li>
 * the presenter's public cert matches an {@link org.opensaml.xmlsec.signature.X509Certificate} within one of the
 * {@link KeyInfo} entries in the confirmation data
 * </li>
 * </ul>
 * <p>
 * In both cases a "match" is determined via Java <code>equals()</code> comparison.
 * </p>
 * 
 * 
 * <p>
 * In addition to parameters defined in {@link AbstractSubjectConfirmationValidator}:
 * </p>
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * </p>
 * <ul>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_HOK_PRESENTER_CERT}:
 * Optional if key is supplied, otherwise required.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_HOK_PRESENTER_KEY}:
 * Optional if certificate is supplied, otherwise required.
 * </li>
 * </ul>
 * <p>
 * If both key and certificate are supplied, the public key of the supplied certificate must match the
 * supplied public key, otherwise a evaluation results in {@link ValidationResult#INDETERMINATE}. 
 * </p>
 * 
 * <p>
 * Supports the following {@link ValidationContext} dynamic parameters:
 * </p>
 * <ul>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_HOK_CONFIRMED_KEYINFO}:
 * Optional.
 * Will be present after validation iff Holder of Key subject confirmation was successfully performed.
 * </li>
 * </ul>
 */
@ThreadSafe
public class HolderOfKeySubjectConfirmationValidator extends AbstractSubjectConfirmationValidator {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(HolderOfKeySubjectConfirmationValidator.class);

    /** {@inheritDoc} */
    @Nonnull public String getServicedMethod() {
        return SubjectConfirmation.METHOD_HOLDER_OF_KEY;
    }

// Checkstyle: CyclomaticComplexity|ReturnCount OFF
    /** {@inheritDoc} */
    @Nonnull protected ValidationResult doValidate(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context) 
                    throws AssertionValidationException {
        
        if (!Objects.equals(confirmation.getMethod(), SubjectConfirmation.METHOD_HOLDER_OF_KEY)) {
            return ValidationResult.INDETERMINATE;
        }
        
        final SubjectConfirmationData confirmationData = confirmation.getSubjectConfirmationData();
        if (confirmationData == null) {
            return ValidationResult.INDETERMINATE;
        }
        
        log.debug("Attempting holder-of-key subject confirmation");
        if (!isValidConfirmationDataType(confirmationData)) {
            context.getValidationFailureMessages().add(
                    String.format("Subject confirmation data is not of type '%s'",
                            KeyInfoConfirmationDataType.TYPE_NAME));
            return ValidationResult.INVALID;
        }

        final List<KeyInfo> possibleKeys = getSubjectConfirmationKeyInformation(confirmationData, assertion, context);
        if (possibleKeys.isEmpty()) {
            context.getValidationFailureMessages().add(
                    String.format("No key information for holder of key subject confirmation in assertion '%s'",
                            assertion.getID()));
            return ValidationResult.INVALID;
        }

        Pair<PublicKey, X509Certificate> keyCertPair = null;
        try {
            keyCertPair = getKeyAndCertificate(context);
        } catch (final IllegalArgumentException e) {
            context.getValidationFailureMessages().add(String.format(
                    "Unable to obtain presenter key/cert params from validation context: %s", e.getMessage()));
            return ValidationResult.INDETERMINATE;
        }
        
        if (keyCertPair.getFirst() == null && keyCertPair.getSecond() == null) {
            context.getValidationFailureMessages().add(
                    "Neither the presenter's certificate nor its public key were provided");
            return ValidationResult.INDETERMINATE;
        }

        for (final KeyInfo keyInfo : possibleKeys) {
            assert keyInfo != null;
            if (matchesKeyValue(keyCertPair.getFirst(), keyInfo)) {
                log.debug("Successfully matched public key in subject confirmation data to supplied key param");
                context.getDynamicParameters().put(SAML2AssertionValidationParameters.SC_HOK_CONFIRMED_KEYINFO,
                        keyInfo);
                return ValidationResult.VALID;
            } else if (matchesX509Certificate(keyCertPair.getSecond(), keyInfo)) {
                log.debug("Successfully matched certificate in subject confirmation data to supplied cert param");
                context.getDynamicParameters().put(SAML2AssertionValidationParameters.SC_HOK_CONFIRMED_KEYINFO,
                        keyInfo);
                return ValidationResult.VALID;
            }
        }

        return ValidationResult.INVALID;
    }
// Checkstyle: CyclomaticComplexity|ReturnCount ON

    /**
     * Checks to see whether the schema type of the subject confirmation data, if present, is the required
     * {@link KeyInfoConfirmationDataType#TYPE_NAME}.
     * 
     * @param confirmationData subject confirmation data to be checked
     * 
     * @return true if the confirmation data's schema type is correct, false otherwise
     * 
     * @throws AssertionValidationException thrown if there is a problem validating the confirmation data type
     */
    protected boolean isValidConfirmationDataType(@Nonnull final SubjectConfirmationData confirmationData) 
            throws AssertionValidationException {
        final QName confirmationDataSchemaType = confirmationData.getSchemaType();
        if (confirmationDataSchemaType != null
                && !confirmationDataSchemaType.equals(KeyInfoConfirmationDataType.TYPE_NAME)) {
            log.debug("SubjectConfirmationData xsi:type was non-null and did not match {}",
                    KeyInfoConfirmationDataType.TYPE_NAME);
            return false;
        }
        
        log.debug("SubjectConfirmationData xsi:type was either null or matched {}",
                KeyInfoConfirmationDataType.TYPE_NAME);
        
        return true;
    }

    /**
     * Extracts the presenter's key and/or certificate from the validation context.
     * 
     * @param context current validation context
     * 
     * @return the presenter's key/cert pair, information not available in the context is null
     * 
     * @throws AssertionValidationException thrown if there is a problem obtaining the data
     */
    @Nonnull protected Pair<PublicKey, X509Certificate> getKeyAndCertificate(@Nonnull final ValidationContext context) 
            throws AssertionValidationException {
        PublicKey presenterKey = null;
        try {
            presenterKey = (PublicKey) context.getStaticParameters().get(
                    SAML2AssertionValidationParameters.SC_HOK_PRESENTER_KEY);
        } catch (final ClassCastException e) {
            throw new IllegalArgumentException(String.format(
                    "The value of the static validation parameter '%s' was not of the required type '%s'",
                    SAML2AssertionValidationParameters.SC_HOK_PRESENTER_KEY, PublicKey.class.getName()));
        }

        X509Certificate presenterCert = null;
        try {
            presenterCert = (X509Certificate) context.getStaticParameters().get(
                    SAML2AssertionValidationParameters.SC_HOK_PRESENTER_CERT);
            if (presenterCert != null) {
                if (presenterKey != null) {
                    if (!presenterKey.equals(presenterCert.getPublicKey())) {
                        throw new IllegalArgumentException(
                                "Presenter's certificate contains a different public key " 
                                + "than the one explicitly given");
                    }
                } else {
                    presenterKey = presenterCert.getPublicKey();
                }
            }
        } catch (final ClassCastException e) {
            throw new IllegalArgumentException(String.format(
                    "The value of the static validation parameter '%s' was not of the required type '%s'",
                    SAML2AssertionValidationParameters.SC_HOK_PRESENTER_CERT, X509Certificate.class.getName()));
        }

        return new Pair<>(presenterKey, presenterCert);
    }

    /**
     * Extracts the {@link KeyInfo}s from the given subject confirmation data.
     * 
     * @param confirmationData subject confirmation data
     * @param assertion assertion bearing the subject to be confirmed
     * @param context current message processing context
     * 
     * @return list of key informations available in the subject confirmation data, never null
     * 
     * @throws AssertionValidationException if there is a problem processing the SubjectConfirmation
     *
     */
    @Nonnull @Live protected List<KeyInfo> getSubjectConfirmationKeyInformation(
            @Nonnull final SubjectConfirmationData confirmationData, @Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        final List<KeyInfo> keyInfos = new LazyList<>();
        for (final XMLObject object : confirmationData.getUnknownXMLObjects(KeyInfo.DEFAULT_ELEMENT_NAME)) {
            keyInfos.add((KeyInfo) object);
        }

        log.debug("Found '{}' KeyInfo children of SubjectConfirmationData", keyInfos.size());
        return keyInfos;
    }

    /**
     * Checks whether the supplied public key matches one of the keys in the given KeyInfo.
     * 
     * <p>
     * Evaluates both {@link KeyValue} and {@link DEREncodedKeyValue} children of the KeyInfo.
     * </p>
     * 
     * 
     * <p>
     * Matches are performed using Java <code>equals()</code> against {@link PublicKey}s decoded
     * from the KeyInfo data.
     * </p>
     * 
     * @param key public key presenter of the assertion
     * @param keyInfo key info from subject confirmation of the assertion
     * 
     * @return true if the public key in the certificate matches one of the key values in the key info, false otherwise
     * 
     * @throws AssertionValidationException thrown if there is a problem matching the key value
     */
    protected boolean matchesKeyValue(@Nullable final PublicKey key, @Nonnull final KeyInfo keyInfo) 
            throws AssertionValidationException {
        
        if (key == null) {
            log.debug("Presenter PublicKey was null, skipping KeyValue match");
            return false;
        }
        
        if (matchesKeyValue(key, keyInfo.getKeyValues())) {
            return true;
        }
        
        if (matchesDEREncodedKeyValue(key, keyInfo.getDEREncodedKeyValues())) {
            return true;
        }

        log.debug("Failed to match either a KeyInfo KeyValue or DEREncodedKeyValue against supplied PublicKey param");
        return false;
    }
    
    /**
     * Checks whether the supplied public key matches one of the supplied {@link KeyValue} elements.
     * 
     * <p>
     * Matches are performed using Java <code>equals()</code> against {@link PublicKey}s decoded
     * from the KeyInfo data.
     * </p>
     * 
     * @param key public key presenter of the assertion
     * @param keyValues candidate KeyValue elements
     * 
     * @return true if the public key in the certificate matches one of the key values, false otherwise
     */
    protected boolean matchesKeyValue(@Nonnull final PublicKey key, @Nonnull final List<KeyValue> keyValues)  {
        
        if (keyValues.isEmpty()) {
            log.debug("KeyInfo contained no KeyValue children");
            return false;
        }
        
        log.debug("Attempting to match KeyInfo KeyValue to supplied PublicKey param of type: {}", key.getAlgorithm());
        
        for (final KeyValue keyValue : keyValues) {
            try {
                assert keyValue != null;
                final PublicKey kiPublicKey = KeyInfoSupport.getKey(keyValue);
                if (Objects.equals(key, kiPublicKey)) {
                    log.debug("Matched KeyValue PublicKey");
                    return true;
                }
            } catch (final KeyException e) {
                log.warn("KeyInfo contained KeyValue that can not be parsed", e);
            }
        }
        
        log.debug("Failed to match any KeyValue");
        return false;
    }
    
    
    /**
     * Checks whether the supplied public key matches one of the supplied {@link DEREncodedKeyValue} elements.
     * 
     * <p>
     * Matches are performed using Java <code>equals()</code> against {@link PublicKey}s decoded
     * from the KeyInfo data.
     * </p>
     * 
     * @param key public key presenter of the assertion
     * @param derEncodedKeyValues candidate DEREncodedKeyValue elements
     * 
     * @return true if the public key in the certificate matches one of the DER-encoded key values, false otherwise
     */
    protected boolean matchesDEREncodedKeyValue(@Nonnull final PublicKey key, 
            @Nonnull final List<DEREncodedKeyValue> derEncodedKeyValues)  {
        
        if (derEncodedKeyValues.isEmpty()) {
            log.debug("KeyInfo contained no DEREncodedKeyValue children");
            return false;
        }
        
        log.debug("Attempting to match KeyInfo DEREncodedKeyValue to supplied PublicKey param of type: {}", 
                key.getAlgorithm());
        
        for (final DEREncodedKeyValue derEncodedKeyValue : derEncodedKeyValues) {
            try {
                assert derEncodedKeyValue != null;
                final PublicKey kiPublicKey = KeyInfoSupport.getKey(derEncodedKeyValue);
                if (Objects.equals(key, kiPublicKey)) {
                    log.debug("Matched DEREncodedKeyValue PublicKey");
                    return true;
                }
            } catch (final KeyException e) {
                log.warn("KeyInfo contained DEREncodedKeyValue that can not be parsed", e);
            }
        }
        
        log.debug("Failed to match any DEREncodedKeyValue");
        return false;
    }

    /**
     * Checks whether the presenter's certificate matches a certificate described by the X509Data within the KeyInfo.
     * 
     * 
     * 
     * <p>
     * Matches are performed using Java <code>equals()</code> against {@link X509Certificate}s decoded
     * from the KeyInfo data.
     * </p>
     * 
     * @param cert certificate of the presenter of the assertion
     * @param keyInfo key info from subject confirmation of the assertion
     * 
     * @return true if the presenter's certificate matches the key described by an X509Data within the KeyInfo, false
     *         otherwise.
     *         
     * @throws AssertionValidationException thrown if there is a problem matching the certificate
     */
    protected boolean matchesX509Certificate(@Nullable final X509Certificate cert, @Nonnull final KeyInfo keyInfo) 
            throws AssertionValidationException {
        if (cert == null) {
            log.debug("Presenter X509Certificate was null, skipping certificate match");
            return false;
        }

        final List<X509Data> x509Datas = keyInfo.getX509Datas();
        if (x509Datas == null || x509Datas.isEmpty()) {
            log.debug("KeyInfo contained no X509Data children, skipping certificate match");
            return false;
        }
        
        log.debug("Attempting to match KeyInfo X509Data to supplied X509Certificate param");

        List<org.opensaml.xmlsec.signature.X509Certificate> xmlCertificates;
        for (final X509Data data : x509Datas) {
            xmlCertificates = data.getX509Certificates();
            if (xmlCertificates == null || xmlCertificates.isEmpty()) {
                log.debug("X509Data contained no X509Certificate children, skipping certificate match");
                continue;
            }

            for (final org.opensaml.xmlsec.signature.X509Certificate xmlCertificate : xmlCertificates) {
                try {
                    final X509Certificate kiCert = KeyInfoSupport.getCertificate(xmlCertificate);
                    if (Objects.equals(cert, kiCert)) {
                        log.debug("Matched X509Certificate");
                        return true;
                    }
                } catch (final CertificateException e) {
                    log.warn("KeyInfo contained Certificate value that can not be parsed", e);
                }
            }
        }

        log.debug("Failed to match a KeyInfo X509Data against supplied X509Certificate param");
        return false;
    }
}