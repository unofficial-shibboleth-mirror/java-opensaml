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
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AssertionURIRef;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.AssertionURIRefImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class AssertionURIRefTest extends XMLObjectProviderBaseTestCase {

    /** Expected Assertion URI value */
    protected String expectedAssertionURI;

    /** Constructor */
    public AssertionURIRefTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/AssertionURIRef.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAssertionURI = "assertion URI";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AssertionURIRef assertionURIRef = (AssertionURIRef) unmarshallElement(singleElementFile);
        assert assertionURIRef!=null;
        String assertionURI = assertionURIRef.getURI();
        Assert.assertEquals(assertionURI, expectedAssertionURI,
                "AssertionURI was " + assertionURI + ", expected " + expectedAssertionURI);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, AssertionURIRef.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        AssertionURIRef assertionURIRef = (AssertionURIRef) buildXMLObject(qname);

        assertionURIRef.setURI(expectedAssertionURI);
        assertXMLEquals(expectedDOM, assertionURIRef);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        // do nothing
    }
}