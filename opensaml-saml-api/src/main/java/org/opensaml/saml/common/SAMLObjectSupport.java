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

package org.opensaml.saml.common;

import java.util.List;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.Namespace;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.ContentReference;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A helper class for working with SAMLObjects.
 */
public final class SAMLObjectSupport {
    
    /** Logger. */
    @Nonnull private static final Logger LOG = LoggerFactory.getLogger(SAMLObjectSupport.class);
    
    /** Constructor. */
    private SAMLObjectSupport() { }
    
    /**
     * Examines the {@link SignableSAMLObject} for the need to declare non-visible namespaces 
     * before marshalling and signing, and if required, performs the declarations.
     * 
     * <p>
     * If the object does not already have a cached DOM, does have a signature attached,
     * and the signature contains a {@link SAMLObjectContentReference} with a transform of either 
     * {@link SignatureConstants#TRANSFORM_C14N_EXCL_OMIT_COMMENTS}
     * or {@link SignatureConstants#TRANSFORM_C14N_EXCL_WITH_COMMENTS}, 
     * it declares on the object all non-visible namespaces
     * as determined by {@link org.opensaml.core.xml.NamespaceManager#getNonVisibleNamespaces()}.
     * </p>
     * 
     * @param signableObject the signable SAML object to evaluate
     */
    public static void declareNonVisibleNamespaces(@Nonnull final SignableSAMLObject signableObject) {
        if (signableObject.getDOM() == null) {
            final Signature sig = signableObject.getSignature();
            if (sig != null) {
                LOG.debug("Examining signed object for content references with exclusive canonicalization transform");
                boolean sawExclusive = false;
                for (final ContentReference cr : sig.getContentReferences()) {
                    if (cr instanceof SAMLObjectContentReference) {
                        final List<String> transforms = ((SAMLObjectContentReference)cr).getTransforms();
                        if (transforms.contains(SignatureConstants.TRANSFORM_C14N_EXCL_WITH_COMMENTS) 
                                || transforms.contains(SignatureConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS)) {
                            sawExclusive = true;
                            break;
                        }
                    }
                }
                
                if (sawExclusive) {
                    LOG.debug("Saw exclusive transform, declaring non-visible namespaces on signed object");
                    for (final Namespace ns : signableObject.getNamespaceManager().getNonVisibleNamespaces()) {
                        assert ns != null;
                        signableObject.getNamespaceManager().registerNamespaceDeclaration(ns);
                    }
                }
            }
        }
    }

}