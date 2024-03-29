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

package org.opensaml.xmlsec.signature.support.impl.provider;

import java.security.Key;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureImpl;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureValidationProvider;
import org.slf4j.Logger;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Implementation of {@link SignatureValidationProvider} which is based on the Apache Santuario library
 * and is used with {@link Signature} instances which are instances of {@link SignatureImpl}. 
 */
public class ApacheSantuarioSignatureValidationProviderImpl implements SignatureValidationProvider {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ApacheSantuarioSignatureValidationProviderImpl.class);

    /** {@inheritDoc} */
    public void validate(@Nonnull final Signature signature, @Nonnull final Credential validationCredential) 
            throws SignatureException {
        log.debug("Attempting to validate signature using key from supplied credential");
        Constraint.isNotNull(validationCredential, "Validation credential cannot be null");

        final XMLSignature xmlSig = getXMLSignature(signature);
        if (xmlSig == null) {
            log.debug("No native XMLSignature object associated with Signature XMLObject");
            throw new SignatureException("Native XMLSignature object not available for validation");
        }

        final Key validationKey = CredentialSupport.extractVerificationKey(validationCredential);
        if (validationKey == null) {
            log.debug("Supplied credential contained no key suitable for signature validation");
            throw new SignatureException("No key available to validate signature");
        }
        
        log.debug("Validating signature with signature algorithm URI: {}", signature.getSignatureAlgorithm());
        log.debug("Validation credential key algorithm '{}', key instance class '{}'", 
                validationKey.getAlgorithm(), validationKey.getClass().getName());

        try {
            if (xmlSig.checkSignatureValue(validationKey)) {
                log.debug("Signature validated with key from supplied credential");
                return;
            }
        } catch (final XMLSignatureException e) {
            throw new SignatureException("Unable to evaluate key against signature", e);
        }

        log.debug("Signature cryptographic validation not successful");
        throw new SignatureException("Signature cryptographic validation not successful");
    }

    /**
     * Access the {@link XMLSignature} from the given signature object.
     * 
     * @param signature the signature
     * 
     * @return the related XMLSignature
     */
    @Nullable protected XMLSignature getXMLSignature(@Nonnull final Signature signature) {
        Constraint.isNotNull(signature, "Signature cannot be null");
        
        log.debug("Accessing XMLSignature object");
        return ((SignatureImpl) signature).getXMLSignature();
    }

}