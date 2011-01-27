/*
 * Copyright 2009 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.ws.wspolicy.impl;

import org.opensaml.ws.wspolicy.AppliesTo;
import org.opensaml.ws.wspolicy.Policy;
import org.opensaml.ws.wspolicy.PolicyAttachment;
import org.opensaml.ws.wspolicy.PolicyReference;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for PolicyAttachment.
 */
public class PolicyAttachmentUnmarshaller extends AbstractWSPolicyObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        PolicyAttachment pa = (PolicyAttachment) xmlObject;
        XMLHelper.unmarshallToAttributeMap(pa.getUnknownAttributes(), attribute);
    }

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject)
            throws UnmarshallingException {
        PolicyAttachment pa = (PolicyAttachment) parentXMLObject;
        
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