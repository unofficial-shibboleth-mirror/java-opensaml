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

import javax.annotation.Nonnull;

import org.apache.xml.security.Init;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureImpl;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignerProvider;
import org.slf4j.Logger;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Implementation of {@link SignerProvider} which is based on the Apache Santuario library
 * and is used with {@link Signature} instances which are instances of {@link SignatureImpl}. 
 */
public class ApacheSantuarioSignerProviderImpl implements SignerProvider {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(ApacheSantuarioSignerProviderImpl.class);

    /** {@inheritDoc} */
    public void signObject(@Nonnull final Signature signature) throws SignatureException {
        Constraint.isNotNull(signature, "Signature cannot be null");
        Constraint.isTrue(Init.isInitialized(), "Apache XML security library is not initialized");
        
        try {
            final XMLSignature xmlSignature = ((SignatureImpl) signature).getXMLSignature();

            if (xmlSignature == null) {
                log.error("Unable to compute signature, Signature XMLObject does not have the XMLSignature "
                        + "created during marshalling");
                throw new SignatureException(
                        "XMLObject does not have XMLSignature instance, unable to compute signature");
            }
            
            final Credential signingCred = signature.getSigningCredential();
            if (signingCred == null) {
                log.error("Unable to compute signature, Signature XMLObject does not contain a signing key");
                throw new SignatureException("XMLObject does not have signing key, unable to compute signature");
            }
            
            log.debug("Computing signature over XMLSignature object");
            xmlSignature.sign(CredentialSupport.extractSigningKey(signingCred));
        } catch (final XMLSecurityException e) {
            log.error("An error occured computing the digital signature: {}", e.getMessage());
            throw new SignatureException("Signature computation error", e);
        }
    }

}