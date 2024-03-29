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

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.soap.wspolicy.PolicyReference;
import org.w3c.dom.Attr;

import net.shibboleth.shared.xml.QNameSupport;

/**
 * Unmarshaller for the wsp:PolicyReference element.
 * 
 */
public class PolicyReferenceUnmarshaller extends AbstractWSPolicyObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final PolicyReference pr = (PolicyReference) xmlObject;

        final QName uriName = new QName(PolicyReference.URI_ATTRIB_NAME);
        final QName digestName = new QName(PolicyReference.DIGEST_ATTRIB_NAME);
        final QName digestAlgorithmName = new QName(PolicyReference.DIGEST_ALGORITHM_ATTRIB_NAME);

        final QName attribQName = 
            QNameSupport.constructQName(attribute.getNamespaceURI(), attribute.getLocalName(), attribute .getPrefix());

        if (uriName.equals(attribQName)) {
            pr.setURI(attribute.getValue());
        } else if (digestName.equals(attribQName)) {
            pr.setDigest(attribute.getValue());
        } else if (digestAlgorithmName.equals(attribQName)) {
            pr.setDigestAlgorithm(attribute.getValue());
        } else {
            XMLObjectSupport.unmarshallToAttributeMap(pr.getUnknownAttributes(), attribute);
        }
    }

}
