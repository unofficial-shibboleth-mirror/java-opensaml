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

package org.opensaml.saml.saml2.core.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.AudienceRestrictionImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class AudienceRestrictionTest extends XMLObjectProviderBaseTestCase {

    /** Count of Audience subelements */
    protected int expectedAudienceCount = 2;

    /** Constructor */
    public AudienceRestrictionTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/AudienceRestriction.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/AudienceRestrictionChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final AudienceRestriction audienceRestriction = (AudienceRestriction) unmarshallElement(singleElementFile);

        Assert.assertNotNull(audienceRestriction);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20_NS, AudienceRestriction.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final AudienceRestriction audienceRestriction = (AudienceRestriction) buildXMLObject(qname);

        assertXMLEquals(expectedDOM, audienceRestriction);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final AudienceRestriction audienceRestriction = (AudienceRestriction) unmarshallElement(childElementsFile);
        assert audienceRestriction!=null;
        Assert.assertEquals(audienceRestriction.getAudiences().size(), expectedAudienceCount, "Audience Count");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20_NS, AudienceRestriction.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final AudienceRestriction audienceRestriction = (AudienceRestriction) buildXMLObject(qname);

        final QName audienceQName = new QName(SAMLConstants.SAML20_NS, Audience.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        for (int i = 0; i < expectedAudienceCount; i++) {
            audienceRestriction.getAudiences().add((Audience) buildXMLObject(audienceQName));
        }

        assertXMLEquals(expectedChildElementsDOM, audienceRestriction);
    }
}