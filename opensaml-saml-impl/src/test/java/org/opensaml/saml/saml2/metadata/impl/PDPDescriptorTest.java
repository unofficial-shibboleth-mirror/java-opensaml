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

/**
 * 
 */

package org.opensaml.saml.saml2.metadata.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.AssertionIDRequestService;
import org.opensaml.saml.saml2.metadata.AuthzService;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.PDPDescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.PDPDescriptorImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class PDPDescriptorTest extends XMLObjectProviderBaseTestCase {

    /** List of expected supported protocols */
    protected ArrayList<String> expectedSupportedProtocol;

    /** Expected cacheDuration value in miliseconds */
    protected Duration expectedCacheDuration;

    /** Expected validUntil value */
    protected Instant expectedValidUntil;

    /** Expected error url */
    protected String expectedErrorURL;

    /**
     * Constructor
     */
    public PDPDescriptorTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/PDPDescriptor.xml";
        singleElementOptionalAttributesFile =
                "/org/opensaml/saml/saml2/metadata/impl/PDPDescriptorOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/metadata/impl/PDPDescriptorChildElements.xml";
    }

    @BeforeMethod protected void setUp() throws Exception {
        expectedSupportedProtocol = new ArrayList<>();
        expectedSupportedProtocol.add("urn:foo:bar");
        expectedSupportedProtocol.add("urn:fooz:baz");

        expectedCacheDuration = Duration.ofSeconds(90);
        expectedValidUntil = Instant.parse("2005-12-07T10:21:00Z");

        expectedErrorURL = "http://example.org";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        final PDPDescriptor descriptor = (PDPDescriptor) unmarshallElement(singleElementFile);
        assert descriptor!=null;
        Assert.assertEquals(descriptor.getSupportedProtocols(), expectedSupportedProtocol,
                "Supported protocols not equal to expected value");
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesUnmarshall() {
        final PDPDescriptor descriptor = (PDPDescriptor) unmarshallElement(singleElementOptionalAttributesFile);
        assert descriptor!=null;
        Assert.assertEquals(descriptor.getCacheDuration(), expectedCacheDuration,
                "Cache duration was not expected value");
        Assert.assertEquals(descriptor.getValidUntil(), expectedValidUntil, "ValidUntil was not expected value");
        Assert.assertEquals(descriptor.getErrorURL(), expectedErrorURL, "ErrorURL was not expected value");
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        final PDPDescriptor descriptor = (PDPDescriptor) unmarshallElement(childElementsFile);
        assert descriptor!=null;
        Assert.assertNotNull(descriptor.getExtensions(), "<Extensions>");
        Assert.assertEquals(descriptor.getKeyDescriptors().size(), 0, "KeyDescriptor");

        Assert.assertEquals(descriptor.getAuthzServices().size(), 3, "AuthzService count");
        Assert.assertEquals(descriptor.getEndpoints(AuthzService.DEFAULT_ELEMENT_NAME).size(), 3, "AuthzService count");

        Assert.assertEquals(descriptor.getAssertionIDRequestServices().size(), 2, "AssertionIDRequestService count");
        Assert.assertEquals(descriptor.getEndpoints(AssertionIDRequestService.DEFAULT_ELEMENT_NAME).size(), 2,
                "AssertionIDRequestService count");
        
        Assert.assertTrue(descriptor.getEndpoints(PDPDescriptor.DEFAULT_ELEMENT_NAME).isEmpty());
        
        Assert.assertEquals(descriptor.getEndpoints().size(), 5, "EndPoints");
        Assert.assertEquals(descriptor.getNameIDFormats().size(), 1, "NameIDFormat count");
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        final PDPDescriptor descriptor = (new PDPDescriptorBuilder()).buildObject();

        for (String protocol : expectedSupportedProtocol) {
            descriptor.addSupportedProtocol(protocol);
        }

        assertXMLEquals(expectedDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesMarshall() {
        final QName qname =
                new QName(SAMLConstants.SAML20MD_NS, PDPDescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        final PDPDescriptor descriptor = (PDPDescriptor) buildXMLObject(qname);

        for (String protocol : expectedSupportedProtocol) {
            descriptor.addSupportedProtocol(protocol);
        }

        descriptor.setCacheDuration(expectedCacheDuration);
        descriptor.setValidUntil(expectedValidUntil);
        descriptor.setErrorURL(expectedErrorURL);

        assertXMLEquals(expectedOptionalAttributesDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        final QName qname =
                new QName(SAMLConstants.SAML20MD_NS, PDPDescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        final PDPDescriptor descriptor = (PDPDescriptor) buildXMLObject(qname);

        final QName extensionsQName =
                new QName(SAMLConstants.SAML20MD_NS, Extensions.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        descriptor.setExtensions((Extensions) buildXMLObject(extensionsQName));

        final QName authzQName =
                new QName(SAMLConstants.SAML20MD_NS, AuthzService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 3; i++) {
            descriptor.getAuthzServices().add((AuthzService) buildXMLObject(authzQName));
        }

        final QName assertIDReqQName =
                new QName(SAMLConstants.SAML20MD_NS, AssertionIDRequestService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 2; i++) {
            descriptor.getAssertionIDRequestServices()
                    .add((AssertionIDRequestService) buildXMLObject(assertIDReqQName));
        }

        final QName nameIDFormatQName =
                new QName(SAMLConstants.SAML20MD_NS, NameIDFormat.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        descriptor.getNameIDFormats().add((NameIDFormat) buildXMLObject(nameIDFormatQName));

        assertXMLEquals(expectedChildElementsDOM, descriptor);
    }
}