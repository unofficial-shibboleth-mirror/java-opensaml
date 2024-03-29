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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.soap.wspolicy.AppliesTo;
import org.opensaml.soap.wspolicy.Policy;
import org.opensaml.soap.wspolicy.PolicyAttachment;
import org.opensaml.soap.wspolicy.PolicyReference;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for PolicyAttachment.
 */
public class PolicyAttachmentUnmarshaller extends AbstractWSPolicyObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final PolicyAttachment pa = (PolicyAttachment) xmlObject;
        XMLObjectSupport.unmarshallToAttributeMap(pa.getUnknownAttributes(), attribute);
    }

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final PolicyAttachment pa = (PolicyAttachment) parentXMLObject;
        
        if (childXMLObject instanceof AppliesTo) {
            pa.setAppliesTo((AppliesTo) childXMLObject);
        } else if (childXMLObject instanceof Policy) {
            pa.getPolicies().add((Policy) childXMLObject);
        } else if (childXMLObject instanceof PolicyReference) {
            pa.getPolicyReferences().add((PolicyReference) childXMLObject);
        } else {
            pa.getUnknownXMLObjects().add(childXMLObject);
        }
    }

}
