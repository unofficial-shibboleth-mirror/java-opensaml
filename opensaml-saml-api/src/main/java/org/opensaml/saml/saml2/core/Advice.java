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

package org.opensaml.saml.saml2.core;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Core Advice.
 */
public interface Advice extends SAMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "Advice";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "AdviceType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /**
     * Gets the list of all child elements attached to this advice.
     * 
     * @return the list of all child elements attached to this advice
     */
    @Nonnull @Live List<XMLObject> getChildren();

    /**
     * Gets the list of child elements attached to this advice that match a particular QName.
     * 
     * @param typeOrName the QName of the child elements to return
     * @return the list of matching child elements attached to this advice
     */
    @Nonnull @Live List<XMLObject> getChildren(@Nonnull final QName typeOrName);

    /**
     * Gets the list of AssertionID references used as advice.
     * 
     * @return the list of AssertionID references used as advice
     */
    @Nonnull @Live List<AssertionIDRef> getAssertionIDReferences();

    /**
     * Gets the list of AssertionURI references used as advice.
     * 
     * @return the list of AssertionURI references used as advice
     */
    @Nonnull @Live List<AssertionURIRef> getAssertionURIReferences();

    /**
     * Gets the list of Assertions used as advice.
     * 
     * @return the list of Assertions used as advice
     */
    @Nonnull @Live List<Assertion> getAssertions();

    /**
     * Gets the list of EncryptedAssertions used as advice.
     * 
     * @return the list of EncryptedAssertions used as advice
     */
    @Nonnull @Live List<EncryptedAssertion> getEncryptedAssertions();
}
