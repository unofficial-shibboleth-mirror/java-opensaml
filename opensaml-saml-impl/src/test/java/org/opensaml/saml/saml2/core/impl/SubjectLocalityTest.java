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
import org.opensaml.saml.saml2.core.SubjectLocality;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.SubjectLocalityImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class SubjectLocalityTest extends XMLObjectProviderBaseTestCase {

    /** Expected Address value */
    private String expectedAddress;

    /** Expected DNSName value */
    private String expectedDNSName;

    /** Constructor */
    public SubjectLocalityTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/SubjectLocality.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/SubjectLocalityOptionalAttributes.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAddress = "ip address";
        expectedDNSName = "dns name";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final SubjectLocality subjectLocality = (SubjectLocality) unmarshallElement(singleElementFile);
        assert subjectLocality !=null;
        final String address = subjectLocality.getAddress();

        Assert.assertEquals(address, expectedAddress, "Address was " + address + ", expected " + expectedAddress);

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final SubjectLocality subjectLocality = (SubjectLocality) unmarshallElement(singleElementOptionalAttributesFile);
        assert subjectLocality !=null;
        final String address = subjectLocality.getAddress();
        Assert.assertEquals(address, expectedAddress, "Address was " + address + ", expected " + expectedAddress);

        final String dnsName = subjectLocality.getDNSName();
        Assert.assertEquals(dnsName, expectedDNSName, "DNSName was " + dnsName + ", expected " + expectedDNSName);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, SubjectLocality.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        SubjectLocality subjectLocality = (SubjectLocality) buildXMLObject(qname);

        subjectLocality.setAddress(expectedAddress);
        assertXMLEquals(expectedDOM, subjectLocality);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20_NS, SubjectLocality.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final SubjectLocality subjectLocality = (SubjectLocality) buildXMLObject(qname);

        subjectLocality.setAddress(expectedAddress);
        subjectLocality.setDNSName(expectedDNSName);
        assertXMLEquals(expectedOptionalAttributesDOM, subjectLocality);
    }
}