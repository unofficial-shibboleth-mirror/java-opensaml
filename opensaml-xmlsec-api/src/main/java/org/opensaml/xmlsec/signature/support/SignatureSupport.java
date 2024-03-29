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

package org.opensaml.xmlsec.signature.support;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.SignableXMLObject;
import org.opensaml.xmlsec.signature.Signature;
import org.slf4j.Logger;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Helper methods for working with XML Signature.
 */
public final class SignatureSupport {
    
    /** Logger. */
    @Nonnull private static final Logger LOG = LoggerFactory.getLogger(SignatureSupport.class);
    
    /** Set of known canonicalization algorithm URIs. */
    @Nonnull private static final Set<String> C14N_ALGORITHMS = CollectionSupport.setOf(
            SignatureConstants.ALGO_ID_C14N11_OMIT_COMMENTS,
            SignatureConstants.ALGO_ID_C14N11_WITH_COMMENTS,
            SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS,
            SignatureConstants.ALGO_ID_C14N_EXCL_WITH_COMMENTS,
            SignatureConstants.ALGO_ID_C14N_OMIT_COMMENTS,
            SignatureConstants.ALGO_ID_C14N_WITH_COMMENTS
            );
    
    /** Constructor. */
    private SignatureSupport() {
        
    }
    
    /**
     * Prepare a {@link Signature} with necessary additional information prior to signing.
     * 
     * <p>
     * <strong>NOTE:</strong>Since this operation modifies the specified Signature object, it should be called
     * <strong>prior</strong> to marshalling the Signature object.
     * </p>
     * 
     * <p>
     * The following Signature values will be added:
     * </p>
     * <ul>
     * <li>signing credential</li>
     * <li>signature algorithm URI</li>
     * <li>canonicalization algorithm URI</li>
     * <li>reference digest method</li>
     * <li>HMAC output length (if applicable and a value is configured)</li>
     * <li>a {@link KeyInfo} element representing the signing credential</li>
     * </ul>
     * 
     * <p>
     * Existing (non-null) values of these parameters on the specified signature will <strong>NOT</strong> be
     * overwritten, however.
     * </p>
     * 
     * <p>
     * All values are determined by the specified {@link SignatureSigningParameters}. If no value for 
     * a required parameter is specified or included on the passed signature, a {@link SecurityException}
     * will be thrown.
     * </p>
     * 
     * @param signature the Signature to be updated
     * @param parameters the signing parameters to use
     * 
     * @throws SecurityException thrown if a required parameter is not supplied in the parameters instance
     *          or available on the Signature instance
     */
    public static void prepareSignatureParams(@Nonnull final Signature signature,
            @Nonnull final SignatureSigningParameters parameters) throws SecurityException {
        Constraint.isNotNull(signature, "Signature cannot be null");
        Constraint.isNotNull(parameters, "Signature signing parameters cannot be null");

        // Signing credential
        if (signature.getSigningCredential() == null) {
            signature.setSigningCredential(parameters.getSigningCredential());
        }
        if (signature.getSigningCredential() == null) {
            throw new SecurityException("No signing credential was available on the signing parameters or Signature");
        }
    
        // Signing algorithm
        if (signature.getSignatureAlgorithm() == null) {
            signature.setSignatureAlgorithm(parameters.getSignatureAlgorithm());
        }
        final String alg = signature.getSignatureAlgorithm();
        if (alg == null) {
            throw new SecurityException("No signature algorithm was available on the signing parameters or Signature");
        }
    
        // HMAC output length, if applicable
        if (signature.getHMACOutputLength() == null &&  AlgorithmSupport.isHMAC(alg)) {
            signature.setHMACOutputLength(parameters.getSignatureHMACOutputLength());
        }
    
        // SignedInfo C14N
        if (signature.getCanonicalizationAlgorithm() == null) {
            signature.setCanonicalizationAlgorithm(parameters.getSignatureCanonicalizationAlgorithm());
        }
        if (signature.getCanonicalizationAlgorithm() == null) {
            throw new SecurityException("No C14N algorithm was available on the signing parameters or Signature");
        }
    
        // Content reference(s): digest method and c14 transform
        processContentReferences(signature, parameters);
    
        // KeyInfo
        processKeyInfo(signature, parameters);
    }

    /**
     * Prepare the content references.
     * 
     * @param signature the Signature to be updated
     * @param parameters the signing parameters to use
     * 
     * @throws SecurityException thrown if a required parameter is not supplied in the parameters instance
     *          or available on the Signature instance
     */
    private static void processKeyInfo(@Nonnull final Signature signature, 
            final SignatureSigningParameters parameters) throws SecurityException {
        
        if (signature.getKeyInfo() == null) {
            final KeyInfoGenerator kiGenerator = parameters.getKeyInfoGenerator();
            if (kiGenerator != null) {
                try {
                    final KeyInfo keyInfo = kiGenerator.generate(signature.getSigningCredential());
                    signature.setKeyInfo(keyInfo);
                } catch (final SecurityException e) {
                    LOG.error("Error generating KeyInfo from credential: {}", e.getMessage());
                    throw e;
                }
            } else {
                final Credential cred = signature.getSigningCredential();
                LOG.info("No KeyInfoGenerator was supplied in parameters or resolveable " 
                        + "for credential type {}, No KeyInfo will be generated for Signature", 
                        cred != null ? cred.getCredentialType().getName() : "(null)");
            }
        }
    }

    /**
     * Prepare the content references.
     * 
     * @param signature the Signature to be updated
     * @param parameters the signing parameters to use
     * 
     * @throws SecurityException thrown if a required parameter is not supplied in the parameters instance
     *          or available on the Signature instance
     */
    private static void processContentReferences(@Nonnull final Signature signature, 
            @Nonnull final SignatureSigningParameters parameters) throws SecurityException {
        
        final String paramsDigestAlgo = parameters.getSignatureReferenceDigestMethod();
        final String paramsC14NTransform = parameters.getSignatureReferenceCanonicalizationAlgorithm();
        
        for (final ContentReference cr : signature.getContentReferences()) {
            if (cr instanceof ConfigurableContentReference) {
                final ConfigurableContentReference configurableReference = (ConfigurableContentReference) cr;
                if (paramsDigestAlgo != null) {
                    configurableReference.setDigestAlgorithm(paramsDigestAlgo);
                }
                if (configurableReference.getDigestAlgorithm() == null) {
                    throw new SecurityException("No reference digest algorithm was available " 
                            + "on the signing parameters or Signature ContentReference");
                }
            }
            
            if (paramsC14NTransform != null) {
                addOrReplaceReferenceCanonicalizationTransform(cr, paramsC14NTransform);
            }
        }
    }

    /**
     * Process the indicated content reference and either add or replace its canonicalization Transform algorithm
     * with the indicated algorithm.
     * 
     * @param cr the content reference to process
     * @param uri the canonicalization algorithm to either add or replace
     */
    private static void addOrReplaceReferenceCanonicalizationTransform(@Nullable final ContentReference cr, 
            @Nullable final String uri) {
        
        if (cr == null || uri == null) {
            return;
        }
        
        LOG.trace("Adding or replacing content reference transform: {}", uri);
        
        if (cr instanceof TransformsConfigurableContentReference) {
            final List<String> transforms = ((TransformsConfigurableContentReference)cr).getTransforms();
            if (transforms == null) {
                return;
            }
            
            for (int i=0; i<transforms.size(); i++) {
                if (isCanonicalizationAlgorithm(transforms.get(i))) {
                    transforms.set(i, uri);
                    return;
                }
            }
            // Didn't see an existing one, so add it
            transforms.add(uri);
        } else {
            LOG.warn("A non-null signature reference c14n transform was specified, " 
                    + "but ContentReference was not configurable for transforms: {}",
                    cr.getClass().getName());
        }
    }
    
    /**
     * Evaluate whether the indicated algorithm URI is a canonicalization algorithm URI.
     * 
     * @param uri the algorithm URI to evaluate
     * @return true if is a canonicalization algorithm, false otherwise
     */
    private static boolean isCanonicalizationAlgorithm(@Nullable final String uri) {
        final String trimmed = StringSupport.trimOrNull(uri);
        if (trimmed == null) {
            return false;
        }
        return C14N_ALGORITHMS.contains(trimmed);
    }
    
    /**
     * Signs a {@link SignableXMLObject}.
     * 
     * @param signable the signable XMLObject to sign
     * @param parameters the signing parameters to use
     * 
     * @throws SecurityException if there is a problem preparing the signature
     * @throws MarshallingException if there is a problem marshalling the XMLObject
     * @throws SignatureException if there is a problem with the signature operation
     */
    public static void signObject(@Nonnull final SignableXMLObject signable,
            @Nonnull final SignatureSigningParameters parameters) throws SecurityException, MarshallingException,
            SignatureException {
        Constraint.isNotNull(signable, "Signable XMLObject cannot be null");
        Constraint.isNotNull(parameters, "Signature signing parameters cannot be null");

        final XMLObjectBuilder<Signature> signatureBuilder =
                XMLObjectProviderRegistrySupport.getBuilderFactory().ensureBuilder(
                        Signature.DEFAULT_ELEMENT_NAME);
        final Signature signature = signatureBuilder.buildObject(Signature.DEFAULT_ELEMENT_NAME);

        signable.setSignature(signature);

        SignatureSupport.prepareSignatureParams(signature, parameters);

        final Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(signable);
        if (marshaller == null) {
            throw new MarshallingException("Unable to locate marshaller for " + signable.getClass());
        }
        marshaller.marshall(signable);

        Signer.signObject(signature);
    }

}