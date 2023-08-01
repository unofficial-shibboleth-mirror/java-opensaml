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

package org.opensaml.saml.saml1.core.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Assertion;

/** 
 * Builder of {@link Assertion} objects. 
 */
public class AssertionBuilder extends AbstractSAMLObjectBuilder<Assertion> {

    /** {@inheritDoc} */
    @Override
    @Nonnull public AssertionImpl buildObject() {
        return new AssertionImpl(SAMLConstants.SAML1_NS, Assertion.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML1_PREFIX);
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public AssertionImpl buildObject(@Nullable final String namespaceURI, @Nonnull final String localName,
            @Nullable final String namespacePrefix) {
        return new AssertionImpl(namespaceURI, localName, namespacePrefix);
    }

}