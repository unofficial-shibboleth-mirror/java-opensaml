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

package org.opensaml.xmlsec.signature.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.xml.ElementSupport;

import org.apache.xml.security.Init;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
 * An unmarshaller for {@link Signature} objects.
 */
public class SignatureUnmarshaller implements Unmarshaller {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SignatureUnmarshaller.class);

    /** Constructor. */
    public SignatureUnmarshaller() {
        if (!Init.isInitialized()) {
            log.debug("Initializing XML security library");
            Init.init();
        }
    }

    /** {@inheritDoc} */
    @Nonnull public Signature unmarshall(@Nonnull final Element signatureElement) throws UnmarshallingException {
        log.debug("Starting to unmarshall Apache XML-Security-based SignatureImpl element");

        final SignatureImpl signature =
                new SignatureImpl(signatureElement.getNamespaceURI(), signatureElement.getLocalName(),
                        signatureElement.getPrefix());

        try {
            log.debug("Constructing Apache XMLSignature object");

            final XMLSignature xmlSignature = new XMLSignature(signatureElement, "");

            final SignedInfo signedInfo = xmlSignature.getSignedInfo();

            log.debug("Adding canonicalization and signing algorithms, and HMAC output length to Signature");
            signature.setCanonicalizationAlgorithm(signedInfo.getCanonicalizationMethodURI());
            signature.setSignatureAlgorithm(signedInfo.getSignatureMethodURI());
            signature.setHMACOutputLength(getHMACOutputLengthValue(signedInfo.getSignatureMethodElement()));

            final org.apache.xml.security.keys.KeyInfo xmlSecKeyInfo = xmlSignature.getKeyInfo();
            if (xmlSecKeyInfo != null) {
                log.debug("Adding KeyInfo to Signature");
                final Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory()
                        .ensureUnmarshaller(xmlSecKeyInfo.getElement());
                final KeyInfo keyInfo = (KeyInfo) unmarshaller.unmarshall(xmlSecKeyInfo.getElement());
                signature.setKeyInfo(keyInfo);
            }
            signature.setXMLSignature(xmlSignature);
            signature.setDOM(signatureElement);
            return signature;
        } catch (final XMLSecurityException e) {
            log.error("Error constructing Apache XMLSignature instance from Signature element: {}", e.getMessage());
            throw new UnmarshallingException("Unable to unmarshall Signature with Apache XMLSignature", e);
        }
    }

    /**
     * Find and return the integer value contained within the HMACOutputLength element, if present.
     * 
     * @param signatureMethodElement the ds:SignatureMethod element
     * @return the HMAC output length value, or null if not present
     */
    @Nullable private Integer getHMACOutputLengthValue(@Nullable final Element signatureMethodElement) {
        if (signatureMethodElement == null) {
            return null;
        }
        // Should be at most one element
        final List<Element> children =
                ElementSupport.getChildElementsByTagNameNS(signatureMethodElement, SignatureConstants.XMLSIG_NS,
                        "HMACOutputLength");
        if (!children.isEmpty()) {
            final Element hmacElement = children.get(0);
            final String value = StringSupport.trimOrNull(hmacElement.getTextContent());
            if (value != null) {
                return Integer.valueOf(value);
            }
        }
        return null;
    }
    
}