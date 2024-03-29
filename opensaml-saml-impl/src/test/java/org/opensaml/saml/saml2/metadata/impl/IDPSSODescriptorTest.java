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

package org.opensaml.saml.saml2.metadata.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.ArtifactResolutionService;
import org.opensaml.saml.saml2.metadata.AssertionIDRequestService;
import org.opensaml.saml.saml2.metadata.AttributeProfile;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.ManageNameIDService;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.NameIDMappingService;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 */
@SuppressWarnings({"null", "javadoc"})
public class IDPSSODescriptorTest extends XMLObjectProviderBaseTestCase {

    /** List of expected supported protocols */
    protected ArrayList<String> expectedSupportedProtocol;

    /** Expected cacheDuration value in miliseconds */
    protected Duration expectedCacheDuration;

    /** Expected validUntil value */
    protected Instant expectedValidUntil;

    /** Expected error url */
    protected String expectedErrorURL;

    /** expected value for WantAuthnRequestSigned attribute */
    protected XSBooleanValue expectedWantAuthnReqSigned;

    /** Unknown Attributes */
    protected QName[] unknownAttributeNames = {new QName("urn:foo:bar", "bar", "foo")};

    /** Unknown Attribute Values */
    protected String[] unknownAttributeValues = {"fred"};

    /**
     * Constructor
     */
    public IDPSSODescriptorTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/IDPSSODescriptor.xml";
        singleElementOptionalAttributesFile =
                "/org/opensaml/saml/saml2/metadata/impl/IDPSSODescriptorOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/metadata/impl/IDPSSODescriptorChildElements.xml";
        singleElementUnknownAttributesFile =
                "/org/opensaml/saml/saml2/metadata/impl/IDPSSODescriptorUnknownAttributes.xml";
    }

    @BeforeMethod protected void setUp() throws Exception {
        expectedSupportedProtocol = new ArrayList<>();
        expectedSupportedProtocol.add("urn:foo:bar");
        expectedSupportedProtocol.add("urn:fooz:baz");

        expectedCacheDuration = Duration.ofSeconds(90);
        expectedValidUntil = Instant.parse("2005-12-07T10:21:00Z");

        expectedErrorURL = "http://example.org";

        expectedWantAuthnReqSigned = new XSBooleanValue(Boolean.TRUE, false);
    }

    @Test public void testSingleElementUnmarshall() {
        final IDPSSODescriptor descriptor = (IDPSSODescriptor) unmarshallElement(singleElementFile);
        assert descriptor!=null;
        Assert.assertEquals(descriptor.getSupportedProtocols(), expectedSupportedProtocol,
                "Supported protocols not equal to expected value");
    }

    @Test public void testSingleElementOptionalAttributesUnmarshall() {
        final IDPSSODescriptor descriptor = (IDPSSODescriptor) unmarshallElement(singleElementOptionalAttributesFile);
        assert descriptor!=null;
        Assert.assertEquals(descriptor.getCacheDuration(), expectedCacheDuration,
                "Cache duration was not expected value");
        Assert.assertEquals(descriptor.getValidUntil(), expectedValidUntil, "ValidUntil was not expected value");
        Assert.assertEquals(descriptor.getWantAuthnRequestsSignedXSBoolean(), expectedWantAuthnReqSigned,
                "WantAuthnRequestsSigned attribute was not expected value");
    }

    @Test public void testSingleElementUnknownAttributesMarshall() {
        final IDPSSODescriptor descriptor = (new IDPSSODescriptorBuilder()).buildObject();

        for (int i = 0; i < unknownAttributeNames.length; i++) {
            descriptor.getUnknownAttributes().put(unknownAttributeNames[i], unknownAttributeValues[i]);
        }
        assertXMLEquals(expectedUnknownAttributesDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnknownAttributesUnmarshall() {
        final  IDPSSODescriptor descriptor = (IDPSSODescriptor) unmarshallElement(singleElementUnknownAttributesFile);
        assert descriptor!=null;
        final AttributeMap attributes = descriptor.getUnknownAttributes();
        assert descriptor!=null;
        Assert.assertEquals(attributes.entrySet().size(), unknownAttributeNames.length);
        for (int i = 0; i < unknownAttributeNames.length; i++) {
            Assert.assertEquals(attributes.get(unknownAttributeNames[i]), unknownAttributeValues[i]);
        }
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        final IDPSSODescriptor descriptor = (IDPSSODescriptor) unmarshallElement(childElementsFile);
        assert descriptor!=null;
        Assert.assertNotNull(descriptor.getExtensions(), "Extensions");
        Assert.assertNotNull(descriptor.getOrganization(), "Organization child");
        Assert.assertEquals(descriptor.getContactPersons().size(), 2, "ContactPerson count");

        Assert.assertEquals(descriptor.getArtifactResolutionServices().size(), 1, "ArtifactResolutionService count");
        Assert.assertEquals(descriptor.getSingleLogoutServices().size(), 2, "SingleLogoutService count");
        Assert.assertEquals(descriptor.getManageNameIDServices().size(), 4, "ManageNameIDService count");
        Assert.assertEquals(descriptor.getNameIDFormats().size(), 1, "NameIDFormat count");
        
        Assert.assertEquals(descriptor.getEndpoints().size(), 15, "All Endpoints");
        

        Assert.assertEquals(descriptor.getSingleSignOnServices().size(), 3, "SingleSignOnService count");
        Assert.assertEquals(descriptor.getEndpoints(SingleSignOnService.DEFAULT_ELEMENT_NAME).size(), 3, "SingleSignOnService count");

        Assert.assertEquals(descriptor.getNameIDMappingServices().size(), 2, "NameIDMappingService count");
        Assert.assertEquals(descriptor.getEndpoints(NameIDMappingService.DEFAULT_ELEMENT_NAME).size(), 2, "NameIDMappingService count");

        Assert.assertEquals(descriptor.getAssertionIDRequestServices().size(), 3, "AssertionIDRequestService count");
        Assert.assertEquals(descriptor.getEndpoints(AssertionIDRequestService.DEFAULT_ELEMENT_NAME).size(), 3, "AssertionIDRequestService count");

        Assert.assertEquals(descriptor.getAttributeProfiles().size(), 3, "AttributeProfile count");
        
        Assert.assertEquals(descriptor.getAttributes().size(), 1);
    }

    @Test public void testSingleElementMarshall() {
        final QName qname =
                new QName(SAMLConstants.SAML20MD_NS, IDPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        final IDPSSODescriptor descriptor = (IDPSSODescriptor) buildXMLObject(qname);

        for (String protocol : expectedSupportedProtocol) {
            descriptor.addSupportedProtocol(protocol);
        }
        descriptor.setWantAuthnRequestsSigned(expectedWantAuthnReqSigned);

        assertXMLEquals(expectedDOM, descriptor);
    }

    @Test public void testSingleElementOptionalAttributesMarshall() {
        final QName qname =
                new QName(SAMLConstants.SAML20MD_NS, IDPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        final IDPSSODescriptor descriptor = (IDPSSODescriptor) buildXMLObject(qname);

        for (String protocol : expectedSupportedProtocol) {
            descriptor.addSupportedProtocol(protocol);
        }

        descriptor.setCacheDuration(expectedCacheDuration);
        descriptor.setValidUntil(expectedValidUntil);
        descriptor.setErrorURL(expectedErrorURL);
        descriptor.setWantAuthnRequestsSigned(expectedWantAuthnReqSigned);

        assertXMLEquals(expectedOptionalAttributesDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        final QName qname =
                new QName(SAMLConstants.SAML20MD_NS, IDPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        final IDPSSODescriptor descriptor = (IDPSSODescriptor) buildXMLObject(qname);

        final QName extensionsQName =
                new QName(SAMLConstants.SAML20MD_NS, Extensions.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        descriptor.setExtensions((Extensions) buildXMLObject(extensionsQName));

        final QName orgQName =
                new QName(SAMLConstants.SAML20MD_NS, Organization.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        descriptor.setOrganization((Organization) buildXMLObject(orgQName));

        final QName contactQName =
                new QName(SAMLConstants.SAML20MD_NS, ContactPerson.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 2; i++) {
            descriptor.getContactPersons().add((ContactPerson) buildXMLObject(contactQName));
        }

        final QName artResQName =
                new QName(SAMLConstants.SAML20MD_NS, ArtifactResolutionService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        descriptor.getArtifactResolutionServices().add((ArtifactResolutionService) buildXMLObject(artResQName));

        final QName sloQName =
                new QName(SAMLConstants.SAML20MD_NS, SingleLogoutService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 2; i++) {
            descriptor.getSingleLogoutServices().add((SingleLogoutService) buildXMLObject(sloQName));
        }

        final QName mngNameIDQName =
                new QName(SAMLConstants.SAML20MD_NS, ManageNameIDService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 4; i++) {
            descriptor.getManageNameIDServices().add((ManageNameIDService) buildXMLObject(mngNameIDQName));
        }

        final QName nameIDFormatQName =
                new QName(SAMLConstants.SAML20MD_NS, NameIDFormat.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        descriptor.getNameIDFormats().add((NameIDFormat) buildXMLObject(nameIDFormatQName));

        final QName ssoQName =
                new QName(SAMLConstants.SAML20MD_NS, SingleSignOnService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 3; i++) {
            descriptor.getSingleSignOnServices().add((SingleSignOnService) buildXMLObject(ssoQName));
        }

        final QName nameIDMapQName =
                new QName(SAMLConstants.SAML20MD_NS, NameIDMappingService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 2; i++) {
            descriptor.getNameIDMappingServices().add((NameIDMappingService) buildXMLObject(nameIDMapQName));
        }

        final  QName assertIDReqQName =
                new QName(SAMLConstants.SAML20MD_NS, AssertionIDRequestService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 3; i++) {
            descriptor.getAssertionIDRequestServices()
                    .add((AssertionIDRequestService) buildXMLObject(assertIDReqQName));
        }

        final QName attributeProlfileQName =
                new QName(SAMLConstants.SAML20MD_NS, AttributeProfile.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 3; i++) {
            descriptor.getAttributeProfiles().add((AttributeProfile) buildXMLObject(attributeProlfileQName));
        }

        final SAMLObjectBuilder<Attribute> builder = (SAMLObjectBuilder<Attribute>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Attribute>ensureBuilder(
                        Attribute.DEFAULT_ELEMENT_NAME);
        descriptor.getAttributes().add(builder.buildObject());
        assertXMLEquals(expectedChildElementsDOM, descriptor);
    }

    /**
     * Test the proper behavior of the XSBooleanValue attributes.
     */
    @Test public void testXSBooleanAttributes() {
        final IDPSSODescriptor descriptor = (IDPSSODescriptor) buildXMLObject(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);

        descriptor.setWantAuthnRequestsSigned(Boolean.TRUE);
        Assert.assertEquals(descriptor.getWantAuthnRequestsSigned(), Boolean.TRUE,
                "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.getWantAuthnRequestsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.getWantAuthnRequestsSignedXSBoolean(), new XSBooleanValue(Boolean.TRUE, false),
                "XSBooleanValue was unexpected value");
        XSBooleanValue bool = descriptor.getWantAuthnRequestsSignedXSBoolean();
        assert bool != null;
        Assert.assertEquals(bool.toString(), "true",
                "XSBooleanValue string was unexpected value");

        descriptor.setWantAuthnRequestsSigned(Boolean.FALSE);
        Assert.assertEquals(descriptor.getWantAuthnRequestsSigned(), Boolean.FALSE,
                "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.getWantAuthnRequestsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.getWantAuthnRequestsSignedXSBoolean(), new XSBooleanValue(Boolean.FALSE, false),
                "XSBooleanValue was unexpected value");
        bool = descriptor.getWantAuthnRequestsSignedXSBoolean();
        assert bool != null;
        Assert.assertEquals(bool.toString(), "false",
                "XSBooleanValue string was unexpected value");

        descriptor.setWantAuthnRequestsSigned((Boolean) null);
        Assert.assertEquals(descriptor.getWantAuthnRequestsSigned(), Boolean.FALSE,
                "Unexpected default value for boolean attribute found");
        Assert.assertNull(descriptor.getWantAuthnRequestsSignedXSBoolean(), "XSBooleanValue was not null");
    }

}