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
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.ArtifactResolutionService;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.ManageNameIDService;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 */
@SuppressWarnings({"null", "javadoc"})
public class SPSSODescriptorTest extends XMLObjectProviderBaseTestCase {

    /** expected value for AuthnRequestSigned attribute */
    protected XSBooleanValue expectedAuthnRequestSigned;

    /** expected value for WantAssertionsSigned attribute */
    protected XSBooleanValue expectedWantAssertionsSigned;

    /** List of expected supported protocols */
    protected ArrayList<String> expectedSupportedProtocol;

    /** Expected cacheDuration value in miliseconds */
    protected Duration expectedCacheDuration;

    /** Expected validUntil value */
    protected Instant expectedValidUntil;
    
    /** Expected Id */
    protected String expectedId;

    /**
     * Constructor
     */
    public SPSSODescriptorTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/SPSSODescriptor.xml";
        singleElementOptionalAttributesFile =
                "/org/opensaml/saml/saml2/metadata/impl/SPSSODescriptorOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/metadata/impl/SPSSODescriptorChildElements.xml";
    }

    @BeforeMethod protected void setUp() throws Exception {
        expectedAuthnRequestSigned = new XSBooleanValue(Boolean.TRUE, false);
        expectedWantAssertionsSigned = new XSBooleanValue(Boolean.TRUE, false);

        expectedSupportedProtocol = new ArrayList<>();
        expectedSupportedProtocol.add("urn:foo:bar");
        expectedSupportedProtocol.add("urn:fooz:baz");

        expectedCacheDuration = Duration.ofSeconds(90);
        expectedValidUntil = Instant.parse("2005-12-07T10:21:00Z");
        expectedId = "id";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        final SPSSODescriptor descriptor = (SPSSODescriptor) unmarshallElement(singleElementFile);
        assert descriptor!=null;
        Assert.assertEquals(descriptor.getSupportedProtocols(), expectedSupportedProtocol,
                "Supported protocols not equal to expected value");
        descriptor.removeAllSupportedProtocols();
        Assert.assertEquals(descriptor.getSupportedProtocols().size(), 0);
        Assert.assertTrue(descriptor.isValid());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesUnmarshall() {
        final SPSSODescriptor descriptor = (SPSSODescriptor) unmarshallElement(singleElementOptionalAttributesFile);
        assert descriptor!=null;
        Assert.assertEquals(descriptor.getSupportedProtocols(), expectedSupportedProtocol,
                "Supported protocols not equal to expected value");
        Assert.assertEquals(descriptor.isAuthnRequestsSignedXSBoolean(), expectedAuthnRequestSigned,
                "AuthnRequestsSigned attribute was not expected value");
        Assert.assertEquals(descriptor.getWantAssertionsSignedXSBoolean(), expectedWantAssertionsSigned,
                "WantAssertionsSigned attribute was not expected value");

        Assert.assertEquals(descriptor.getValidUntil(), expectedValidUntil,
                "ValudUntil attribute was not expected value");
        Assert.assertFalse(descriptor.isValid());
        
        descriptor.removeSupportedProtocol("urn:foo:bar");
        Assert.assertEquals(descriptor.getSupportedProtocols().size(), expectedSupportedProtocol.size() - 1);
        
        descriptor.removeSupportedProtocols(expectedSupportedProtocol);
        Assert.assertEquals(descriptor.getSupportedProtocols().size(), 0);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        final SPSSODescriptor descriptor = (SPSSODescriptor) unmarshallElement(childElementsFile);
        assert descriptor!=null;
        Assert.assertEquals(descriptor.getID(), expectedId);
        Assert.assertEquals(descriptor.getSignatureReferenceID(), expectedId);
        Assert.assertNotNull(descriptor.getSignature());
        
        Assert.assertNotNull(descriptor.getExtensions(), "Extensions");
        Assert.assertEquals(descriptor.getKeyDescriptors().size(), 0, "KeyDescriptor");
        Assert.assertNotNull(descriptor.getOrganization(), "Organization child");
        Assert.assertEquals(descriptor.getContactPersons().size(), 2, "ContactPerson count");

        Assert.assertEquals(descriptor.getArtifactResolutionServices().size(), 1, "ArtifactResolutionService count");
        Assert.assertEquals(descriptor.getEndpoints(ArtifactResolutionService.DEFAULT_ELEMENT_NAME).size(), 1, "ArtifactResolutionServices");
        
        Assert.assertEquals(descriptor.getSingleLogoutServices().size(), 2, "SingleLogoutService count");
        Assert.assertEquals(descriptor.getEndpoints(SingleLogoutService.DEFAULT_ELEMENT_NAME).size(), 2, "SingleLogoutService count");

        Assert.assertEquals(descriptor.getManageNameIDServices().size(), 4, "ManageNameIDService count");
        Assert.assertEquals(descriptor.getEndpoints(ManageNameIDService.DEFAULT_ELEMENT_NAME).size(), 4, "ManageNameIDService count");

        Assert.assertEquals(descriptor.getNameIDFormats().size(), 1, "NameIDFormat count");

        Assert.assertEquals(descriptor.getAssertionConsumerServices().size(), 2, "AssertionConsumerService count");
        Assert.assertEquals(descriptor.getEndpoints(AssertionConsumerService.DEFAULT_ELEMENT_NAME).size(), 2, "AssertionConsumerService count");
        Assert.assertEquals(descriptor.getAttributeConsumingServices().size(), 1, "AttributeConsumingService");

        Assert.assertEquals(descriptor.getEndpoints().size(), 9);
        
        Assert.assertNotNull(descriptor.getDefaultArtifactResolutionService());
        Assert.assertNotNull(descriptor.getDefaultAttributeConsumingService());
        Assert.assertNotNull(descriptor.getDefaultAssertionConsumerService());
    }

    @Test public void testSingleElementMarshall() {
        QName qname =
                new QName(SAMLConstants.SAML20MD_NS, SPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        SPSSODescriptor descriptor = (SPSSODescriptor) buildXMLObject(qname);

        for (String protocol : expectedSupportedProtocol) {
            descriptor.addSupportedProtocol(protocol);
        }

        assertXMLEquals(expectedDOM, descriptor);
    }

    @Test public void testSingleElementOptionalAttributesMarshall() {
        final SPSSODescriptor descriptor = (new SPSSODescriptorBuilder()).buildObject();

        descriptor.setAuthnRequestsSigned(expectedAuthnRequestSigned);
        descriptor.setWantAssertionsSigned(expectedWantAssertionsSigned);

        for (String protocol : expectedSupportedProtocol) {
            descriptor.addSupportedProtocol(protocol);
        }

        descriptor.setCacheDuration(expectedCacheDuration);
        descriptor.setValidUntil(expectedValidUntil);

        assertXMLEquals(expectedOptionalAttributesDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        final  QName qname =
                new QName(SAMLConstants.SAML20MD_NS, SPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        final SPSSODescriptor descriptor = (SPSSODescriptor) buildXMLObject(qname);

        descriptor.setID(expectedId);
        descriptor.setSignature( buildSignatureSkeleton() );

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

        final  QName artResQName =
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

        final QName assertConsumeQName =
                new QName(SAMLConstants.SAML20MD_NS, AssertionConsumerService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 2; i++) {
            descriptor.getAssertionConsumerServices()
                    .add((AssertionConsumerService) buildXMLObject(assertConsumeQName));
        }

        final QName attribConsumeQName =
                new QName(SAMLConstants.SAML20MD_NS, AttributeConsumingService.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        descriptor.getAttributeConsumingServices().add((AttributeConsumingService) buildXMLObject(attribConsumeQName));

        assertXMLEquals(expectedChildElementsDOM, descriptor);
    }

    /**
     * Test the proper behavior of the XSBooleanValue attributes.
     */
    @Test public void testXSBooleanAttributes() {
        final SPSSODescriptor descriptor = (SPSSODescriptor) buildXMLObject(SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        // AuthnRequestsSigned
        descriptor.setAuthnRequestsSigned(Boolean.TRUE);
        Assert.assertEquals(descriptor.isAuthnRequestsSigned(), Boolean.TRUE,
                "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.isAuthnRequestsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.isAuthnRequestsSignedXSBoolean(), new XSBooleanValue(Boolean.TRUE, false),
                "XSBooleanValue was unexpected value");
        XSBooleanValue bool = descriptor.isAuthnRequestsSignedXSBoolean();
        assert bool != null;
        Assert.assertEquals(bool.toString(), "true", "XSBooleanValue string was unexpected value");

        descriptor.setAuthnRequestsSigned(Boolean.FALSE);
        Assert.assertEquals(descriptor.isAuthnRequestsSigned(), Boolean.FALSE,
                "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.isAuthnRequestsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.isAuthnRequestsSignedXSBoolean(), new XSBooleanValue(Boolean.FALSE, false),
                "XSBooleanValue was unexpected value");
        bool = descriptor.isAuthnRequestsSignedXSBoolean();
        assert bool != null;
        Assert.assertEquals(bool.toString(), "false",
                "XSBooleanValue string was unexpected value");

        descriptor.setAuthnRequestsSigned((Boolean) null);
        Assert.assertEquals(descriptor.isAuthnRequestsSigned(), Boolean.FALSE,
                "Unexpected default value for boolean attribute found");
        Assert.assertNull(descriptor.isAuthnRequestsSignedXSBoolean(), "XSBooleanValue was not null");

        // WantAssertionsSigned
        descriptor.setWantAssertionsSigned(Boolean.TRUE);
        Assert.assertEquals(descriptor.getWantAssertionsSigned(), Boolean.TRUE,
                "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.getWantAssertionsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.getWantAssertionsSignedXSBoolean(), new XSBooleanValue(Boolean.TRUE, false),
                "XSBooleanValue was unexpected value");
        bool = descriptor.getWantAssertionsSignedXSBoolean();
        assert bool != null;
        Assert.assertEquals(bool.toString(), "true", "XSBooleanValue string was unexpected value");

        descriptor.setWantAssertionsSigned(Boolean.FALSE);
        Assert.assertEquals(descriptor.getWantAssertionsSigned(), Boolean.FALSE,
                "Unexpected value for boolean attribute found");
        Assert.assertNotNull(descriptor.getWantAssertionsSignedXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(descriptor.getWantAssertionsSignedXSBoolean(), new XSBooleanValue(Boolean.FALSE, false),
                "XSBooleanValue was unexpected value");
        bool = descriptor.getWantAssertionsSignedXSBoolean();
        assert bool != null;
        Assert.assertEquals(bool.toString(), "false", "XSBooleanValue string was unexpected value");

        descriptor.setWantAssertionsSigned((Boolean) null);
        Assert.assertEquals(descriptor.getWantAssertionsSigned(), Boolean.FALSE,
                "Unexpected default value for boolean attribute found");
        Assert.assertNull(descriptor.getWantAssertionsSignedXSBoolean(), "XSBooleanValue was not null");
    }

    /**
     * Build a Signature skeleton to use in marshalling unit tests.
     * 
     * @return minimally populated Signature element
     */
    private Signature buildSignatureSkeleton() {
        Signature signature = (Signature) buildXMLObject(Signature.DEFAULT_ELEMENT_NAME);
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        return signature;
    }

}