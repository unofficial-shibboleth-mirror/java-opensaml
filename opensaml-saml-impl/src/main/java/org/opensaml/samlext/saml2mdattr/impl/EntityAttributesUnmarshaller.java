/*
 * Licensed to the University Corporation for Advanced Internet Development, Inc.
 * under one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.samlext.saml2mdattr.impl;

import org.opensaml.common.impl.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.samlext.saml2mdattr.EntityAttributes;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;

/** A thread-safe Unmarshaller for {@link org.opensaml.samlext.saml2mdattr.EntityAttributes}. */
public class EntityAttributesUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentObject, XMLObject childObject) throws UnmarshallingException {
        EntityAttributes entityAttrs = (EntityAttributes) parentObject;

        if (childObject instanceof Attribute) {
            entityAttrs.getAttributes().add((Attribute) childObject);
        } else if (childObject instanceof Assertion) {
            entityAttrs.getAssertions().add((Assertion) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }
}