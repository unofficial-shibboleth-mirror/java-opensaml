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

package org.opensaml.xacml.policy.impl;

import org.opensaml.xacml.impl.AbstractXACMLObjectBuilder;
import org.opensaml.xacml.policy.SubjectAttributeDesignatorType;

/**
 * Builder for {@link SubjectAttributeDesignatorType}.
 */
public class SubjectAttributeDesignatorTypeImplBuilder
        extends AbstractXACMLObjectBuilder<SubjectAttributeDesignatorType> {

    /** {@inheritDoc} */
    public SubjectAttributeDesignatorType buildObject(final String namespaceURI, final String localName,
            final String namespacePrefix) {
        return new SubjectAttributeDesignatorTypeImpl(namespaceURI,localName,namespacePrefix);
    }

    /** {@inheritDoc} */
    public SubjectAttributeDesignatorType buildObject() {
       return buildObject(SubjectAttributeDesignatorType.DEFAULT_ELEMENT_QNAME);
    }

}
