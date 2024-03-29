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

package org.opensaml.saml.security.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureImpl;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignaturePrevalidator;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A validator for instances of {@link Signature}, which validates that the signature meets security-related
 * requirements indicated by the SAML profile of XML Signature.
 */
public class SAMLSignatureProfileValidator implements SignaturePrevalidator {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAMLSignatureProfileValidator.class);

    /** {@inheritDoc} */
    @Override
    public void validate(@Nonnull final Signature signature) throws SignatureException {
        Constraint.isNotNull(signature, "Signature was null");

        if (!(signature instanceof SignatureImpl)) {
            log.info("Signature was not an instance of SignatureImpl, was {} validation not supported", signature
                    .getClass().getName());
            return;
        }

        validateSignatureImpl((SignatureImpl) signature);
    }

    /**
     * Validate an instance of {@link SignatureImpl}, which is in turn based on underlying Apache XML Security
     * <code>XMLSignature</code> instance.
     * 
     * @param sigImpl the signature implementation object to validate
     * @throws SignatureException thrown if the signature is not valid with respect to the profile
     */
    protected void validateSignatureImpl(@Nonnull final SignatureImpl sigImpl) throws SignatureException {

        final XMLSignature apacheSig = sigImpl.getXMLSignature();
        if (apacheSig == null) {
            log.error("SignatureImpl did not contain the an Apache XMLSignature child");
            throw new SignatureException("Apache XMLSignature does not exist on SignatureImpl");
        }

        if (!(sigImpl.getParent() instanceof SignableSAMLObject)) {
            log.error("Signature is not an immedidate child of a SignableSAMLObject");
            throw new SignatureException("Signature is not an immediate child of a SignableSAMLObject.");
        }
        final SignableSAMLObject signableObject = (SignableSAMLObject) sigImpl.getParent();

        final Reference ref = validateReference(apacheSig);

        assert signableObject != null;
        validateReferenceURI(ref.getURI(), signableObject);

        validateTransforms(ref);
        
        validateObjectChildren(apacheSig);
    }

    /**
     * Validate the Signature's SignedInfo Reference.
     * 
     * The SignedInfo must contain exactly 1 Reference.
     * 
     * @param apacheSig the Apache XML Signature instance
     * @return the valid Reference contained within the SignedInfo
     * @throws SignatureException thrown if the Signature does not contain exactly 1 Reference, or if there is an error
     *             obtaining the Reference instance
     */
    @Nonnull protected Reference validateReference(@Nonnull final XMLSignature apacheSig) throws SignatureException {
        final int numReferences = apacheSig.getSignedInfo().getLength();
        if (numReferences != 1) {
            log.error("Signature SignedInfo had invalid number of References: " + numReferences);
            throw new SignatureException("Signature SignedInfo must have exactly 1 Reference element");
        }

        Reference ref = null;
        try {
            ref = apacheSig.getSignedInfo().item(0);
        } catch (final XMLSecurityException e) {
            log.error("Apache XML Security exception obtaining Reference: {}", e.getMessage());
            throw new SignatureException("Could not obtain Reference from Signature/SignedInfo", e);
        }
        if (ref == null) {
            log.error("Signature Reference was null");
            throw new SignatureException("Signature Reference was null");
        }
        return ref;
    }

    /**
     * Validate the Signature's Reference URI.
     * 
     * First validate the Reference URI against the parent's ID itself.  Then validate that the 
     * URI (if non-empty) resolves to the same Element node as is cached by the SignableSAMLObject.
     * 
     * 
     * @param uri the Signature Reference URI attribute value
     * @param signableObject the SignableSAMLObject whose signature is being validated
     * @throws SignatureException  if the URI is invalid or doesn't resolve to the expected DOM node
     */
    protected void validateReferenceURI(final String uri, @Nonnull final SignableSAMLObject signableObject)
            throws SignatureException {
        final String id = signableObject.getSignatureReferenceID();
        validateReferenceURI(uri, id);
        
        if (Strings.isNullOrEmpty(uri)) {
            return;
        }
        
        final String uriID = uri.substring(1);
        
        final Element expected = signableObject.getDOM();
        if (expected == null) {
            log.error("SignableSAMLObject does not have a cached DOM Element.");
            throw new SignatureException("SignableSAMLObject does not have a cached DOM Element.");
        }
        final Document doc = expected.getOwnerDocument();
        
        final Element resolved = doc.getElementById(uriID);
        if (resolved == null) {
            log.error("DOM Document getElementById could not resolve the Element for id reference: {}", uriID);
            throw new SignatureException("DOM Document getElementById could not resolve the Element for id reference: "
                    + uriID);
        }
        
        if (!expected.isSameNode(resolved)) {
            log.error("Signature Reference URI '{}' did not resolve to the expected parent Element", uri);
            throw new SignatureException("Signature Reference URI did not resolve to the expected parent Element");
        }
    }
    
    /**
     * Validate the Reference URI and parent ID attribute values.
     * 
     * The URI must either be null or empty (indicating that the entire enclosing document was signed), or else it must
     * be a local document fragment reference and point to the SAMLObject parent via the latter's ID attribute value.
     * 
     * @param uri the Signature Reference URI attribute value
     * @param id the Signature parents ID attribute value
     * @throws SignatureException thrown if the URI or ID attribute values are invalid
     */
    protected void validateReferenceURI(@Nullable final String uri, @Nullable final String id)
            throws SignatureException {
        if (uri != null && !uri.isEmpty()) {
            if (!uri.startsWith("#")) {
                log.error("Signature Reference URI was not a document fragment reference: " + uri);
                throw new SignatureException("Signature Reference URI was not a document fragment reference");
            } else if (id == null || id.isEmpty()) {
                log.error("SignableSAMLObject did not contain an ID attribute");
                throw new SignatureException("SignableSAMLObject did not contain an ID attribute");
            } else if (uri.length() < 2 || !id.equals(uri.substring(1))) {
                log.error("Reference URI '{}' did not point to SignableSAMLObject with ID '{}'", uri, id);
                throw new SignatureException("Reference URI did not point to parent ID");
            }
        }
    }

    /**
     * Validate the transforms included in the Signature Reference.
     * 
     * The Reference may contain at most 2 transforms. One of them must be the Enveloped signature transform. An
     * Exclusive Canonicalization transform (with or without comments) may also be present. No other transforms are
     * allowed.
     * 
     * @param reference the Signature reference containing the transforms to evaluate
     * @throws SignatureException thrown if the set of transforms is invalid
     */
    protected void validateTransforms(@Nonnull final Reference reference) throws SignatureException {
        Transforms transforms = null;
        try {
            transforms = reference.getTransforms();
        } catch (final XMLSecurityException e) {
            log.error("Apache XML Security error obtaining Transforms instance: {}", e.getMessage());
            throw new SignatureException("Apache XML Security error obtaining Transforms instance", e);
        }

        if (transforms == null) {
            log.error("Error obtaining Transforms instance, null was returned");
            throw new SignatureException("Transforms instance was null");
        }

        final int numTransforms = transforms.getLength();
        if (numTransforms > 2) {
            log.error("Invalid number of Transforms was present: " + numTransforms);
            throw new SignatureException("Invalid number of transforms");
        }

        boolean sawEnveloped = false;
        for (int i = 0; i < numTransforms; i++) {
            Transform transform = null;
            try {
                transform = transforms.item(i);
            } catch (final TransformationException e) {
                log.error("Error obtaining transform instance: {}", e.getMessage());
                throw new SignatureException("Error obtaining transform instance", e);
            }
            final String uri = transform.getURI();
            if (Transforms.TRANSFORM_ENVELOPED_SIGNATURE.equals(uri)) {
                log.debug("Saw Enveloped signature transform");
                sawEnveloped = true;
            } else if (Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS.equals(uri)
                    || Transforms.TRANSFORM_C14N_EXCL_WITH_COMMENTS.equals(uri)) {
                log.debug("Saw Exclusive C14N signature transform");
            } else {
                log.error("Saw invalid signature transform: " + uri);
                throw new SignatureException("Signature contained an invalid transform");
            }
        }

        if (!sawEnveloped) {
            log.error("Signature was missing the required Enveloped signature transform");
            throw new SignatureException("Transforms did not contain the required enveloped transform");
        }
    }

    /**
     * Validate that the Signature instance does not contain any ds:Object children.
     *
     * @param apacheSig the Apache XML Signature instance
     * @throws SignatureException if the signature contains ds:Object children
     */
    protected void validateObjectChildren(@Nonnull final XMLSignature apacheSig) throws SignatureException {
        if (apacheSig.getObjectLength() > 0) {
            log.error("Signature contained {} ds:Object child element(s)", apacheSig.getObjectLength());
            throw new SignatureException("Signature contained illegal ds:Object children");
        }
    }

}