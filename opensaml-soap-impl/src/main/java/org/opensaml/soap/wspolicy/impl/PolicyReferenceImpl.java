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

package org.opensaml.soap.wspolicy.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.soap.wspolicy.PolicyReference;

/**
 * PolicyReferenceImpl.
 * 
 */
public class PolicyReferenceImpl extends AbstractWSPolicyObject implements PolicyReference {

    /** URI attribute value. */
    @Nullable private String uri;

    /** Digest attribute value. */
    @Nullable private String digest;

    /** DigestAlgorithm attribute value. */
    @Nullable private String digestAlgorithm;

    /** xs:anyAttribute attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public PolicyReferenceImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getDigest() {
        return digest;
    }

    /** {@inheritDoc} */
    @Nullable public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    /** {@inheritDoc} */
    @Nullable public String getURI() {
        return uri;
    }

    /** {@inheritDoc} */
    public void setDigest(@Nullable final String newDigest) {
        digest = prepareForAssignment(digest, newDigest);
    }

    /** {@inheritDoc} */
    public void setDigestAlgorithm(@Nullable final String newDigestAlgorithm) {
        digestAlgorithm = prepareForAssignment(digestAlgorithm, newDigestAlgorithm);
    }

    /** {@inheritDoc} */
    public void setURI(@Nullable final String newURI) {
        uri = prepareForAssignment(uri, newURI);
    }

    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        return null;
    }

}
