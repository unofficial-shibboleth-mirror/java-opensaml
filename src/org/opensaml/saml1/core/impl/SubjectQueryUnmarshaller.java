/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
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

/**
 * 
 */
package org.opensaml.saml1.core.impl;

import org.opensaml.common.impl.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml1.core.Subject;
import org.opensaml.saml1.core.SubjectQuery;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;

/**
 * A thread safe Unmarshaller for {@link org.opensaml.saml1.core.SubjectQuery} objects.
 */
public abstract class SubjectQueryUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /**
     * Constructor
     *
     * @param targetNamespaceURI
     * @param targetLocalName
     * @throws IllegalArgumentException
     */
    protected SubjectQueryUnmarshaller(String targetNamespaceURI, String targetLocalName) throws IllegalArgumentException {
        super(targetNamespaceURI, targetLocalName);
    }

    /*
     * @see org.opensaml.common.impl.AbstractSAMLObjectUnmarshaller#processChildElement(org.opensaml.xml.XMLObject, org.opensaml.xml.XMLObject)
     */
    protected void processChildElement(XMLObject parentSAMLObject, XMLObject childSAMLObject) throws UnmarshallingException {
        SubjectQuery query = (SubjectQuery) parentSAMLObject;
        
        if (childSAMLObject instanceof Subject) {
            query.setSubject((Subject) childSAMLObject);
        } else {
            super.processChildElement(parentSAMLObject, childSAMLObject);
        }
    }

}
