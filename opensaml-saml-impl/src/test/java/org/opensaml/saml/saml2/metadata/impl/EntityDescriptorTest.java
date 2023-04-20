/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
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

package org.opensaml.saml.saml2.metadata.impl;

import java.time.Duration;
import java.time.Instant;

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.AdditionalMetadataLocation;
import org.opensaml.saml.saml2.metadata.AffiliationDescriptor;
import org.opensaml.saml.saml2.metadata.AttributeAuthorityDescriptor;
import org.opensaml.saml.saml2.metadata.AuthnAuthorityDescriptor;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.PDPDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.EntityDescriptorImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class EntityDescriptorTest extends XMLObjectProviderBaseTestCase {

    /** Expected entityID value */
    protected String expectedEntityID;

    /** Expected ID value */
    protected String expectedID;

    /** Expected cacheDuration value in miliseconds */
    protected Duration expectedCacheDuration;

    /** Expected validUntil value */
    protected Instant expectedValidUntil;

    /** Unknown Attributes */
    protected QName[] unknownAttributeNames = {new QName("urn:foo:bar", "bar", "foo")};

    /** Unknown Attribute Values */
    protected String[] unknownAttributeValues = {"fred"};

    /**
     * Constructor
     */
    public EntityDescriptorTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/EntityDescriptor.xml";
        singleElementOptionalAttributesFile =
                "/org/opensaml/saml/saml2/metadata/impl/EntityDescriptorOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/metadata/impl/EntityDescriptorChildElements.xml";
        singleElementUnknownAttributesFile =
                "/org/opensaml/saml/saml2/metadata/impl/EntityDescriptorUnknownAttributes.xml";

    }

    @BeforeMethod protected void setUp() throws Exception {
        expectedID = "id";
        expectedEntityID = "99ff33";
        expectedCacheDuration = Duration.ofSeconds(90);
        expectedValidUntil = Instant.parse("2005-12-07T10:21:00Z");
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        final EntityDescriptor descriptor = (EntityDescriptor) unmarshallElement(singleElementFile);
        assert descriptor!=null;
        final String entityID = descriptor.getEntityID();
        Assert.assertEquals(entityID, expectedEntityID, "entityID attribute has a value of " + entityID
                + ", expected a value of " + expectedEntityID);

        final Duration duration = descriptor.getCacheDuration();
        Assert.assertNull(duration, "cacheDuration attribute has a value of " + duration + ", expected no value");

        final Instant validUntil = descriptor.getValidUntil();
        Assert.assertNull(validUntil, "validUntil attribute has a value of " + validUntil + ", expected no value");

        Assert.assertTrue(descriptor.isValid());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesUnmarshall() {
        final EntityDescriptor descriptor = (EntityDescriptor) unmarshallElement(singleElementOptionalAttributesFile);
        assert descriptor!=null;
        final String entityID = descriptor.getEntityID();
        Assert.assertEquals(entityID, expectedEntityID, "entityID attribute has a value of " + entityID
                + ", expected a value of " + expectedEntityID);

        final String id = descriptor.getID();
        Assert.assertEquals(id, expectedID, "ID attribute has a value of " + id + ", expected a value of " + expectedID);

        final Duration duration = descriptor.getCacheDuration();
        Assert.assertEquals(duration, expectedCacheDuration, "cacheDuration attribute has a value of " + duration
                + ", expected a value of " + expectedCacheDuration);

        final Instant validUntil = descriptor.getValidUntil();
        Assert.assertEquals(expectedValidUntil.compareTo(validUntil), 0,
                "validUntil attribute value did not match expected value");

        Assert.assertFalse(descriptor.isValid());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnknownAttributesUnmarshall() {
        final EntityDescriptor descriptor = (EntityDescriptor) unmarshallElement(singleElementUnknownAttributesFile);
        assert descriptor!=null;
        final AttributeMap attributes = descriptor.getUnknownAttributes();

        Assert.assertEquals(attributes.entrySet().size(), unknownAttributeNames.length);
        for (int i = 0; i < unknownAttributeNames.length; i++) {
            Assert.assertEquals(attributes.get(unknownAttributeNames[i]), unknownAttributeValues[i]);
        }
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        final EntityDescriptor descriptor = (EntityDescriptor) unmarshallElement(childElementsFile);
        assert descriptor!=null;
        Assert.assertNotNull(descriptor.getExtensions(), "Extensions child");
        Assert.assertNotNull(descriptor.getSignature(), "Signature child");
        Assert.assertEquals(descriptor.getRoleDescriptors(IDPSSODescriptor.DEFAULT_ELEMENT_NAME).size(), 2,
                "IDPSSODescriptor count");
        Assert.assertNotNull(descriptor.getIDPSSODescriptor("foo"), "IDPSSODescriptor (protocol)");
        Assert.assertNull(descriptor.getIDPSSODescriptor("bar"), "IDPSSODescriptor (protocol)");
        Assert.assertEquals(descriptor.getRoleDescriptors(IDPSSODescriptor.DEFAULT_ELEMENT_NAME, "foo").size(), 1,
                "IDPSSODescriptor (protocol) count");
        
        Assert.assertEquals(descriptor.getRoleDescriptors(SPSSODescriptor.DEFAULT_ELEMENT_NAME).size(), 3,
                "SPSSODescriptor count");
        Assert.assertEquals(descriptor.getRoleDescriptors(SPSSODescriptor.DEFAULT_ELEMENT_NAME, "foo").size(), 1,
                "SPSSODescriptor (protocol) count");
        Assert.assertNotNull(descriptor.getSPSSODescriptor("foo"), "SPPSSODescriptor (protocol)");
        Assert.assertNull(descriptor.getSPSSODescriptor("bar"), "SPPSSODescriptor (protocol)");
        
        Assert.assertEquals(descriptor.getRoleDescriptors(AuthnAuthorityDescriptor.DEFAULT_ELEMENT_NAME).size(), 2,
                "AuthnAuthorityDescriptor count");
        Assert.assertEquals(descriptor.getRoleDescriptors(AuthnAuthorityDescriptor.DEFAULT_ELEMENT_NAME, "foo").size(), 1,
                "AuthnAuthorityDescriptor count");
        Assert.assertNotNull(descriptor.getAuthnAuthorityDescriptor("foo"), "AuthnAuthorityDescriptor (protocol)");
        Assert.assertNull(descriptor.getAuthnAuthorityDescriptor("bar"), "AuthnAuthorityDescriptor (protocol)");
        
        Assert.assertEquals(descriptor.getRoleDescriptors(AttributeAuthorityDescriptor.DEFAULT_ELEMENT_NAME).size(), 1,
                "AttributeAuthorityDescriptor count");
        Assert.assertEquals(descriptor.getRoleDescriptors(AttributeAuthorityDescriptor.DEFAULT_ELEMENT_NAME, "foo").size(), 1,
                "AttributeAuthorityDescriptor (protocol) count");
        Assert.assertNotNull(descriptor.getAttributeAuthorityDescriptor("foo"), "AttributeAuthorityDescriptor (protocol)");
        Assert.assertNull(descriptor.getAttributeAuthorityDescriptor("bar"), "AttributeAuthorityDescriptor (protocol)");

        
        Assert.assertEquals(descriptor.getRoleDescriptors(PDPDescriptor.DEFAULT_ELEMENT_NAME).size(), 2,
                "PDPDescriptor count");
        Assert.assertEquals(descriptor.getRoleDescriptors(PDPDescriptor.DEFAULT_ELEMENT_NAME, "foo").size(), 1,
                "PDPDescriptor (protocol) count");
        Assert.assertNotNull(descriptor.getPDPDescriptor("foo"), "PDPDescriptor (protocol)");
        Assert.assertNull(descriptor.getPDPDescriptor("bar"), "PDPDescriptor (protocol)");
        
        Assert.assertNotNull(descriptor.getAffiliationDescriptor(), "AffiliationDescriptor ");
        Assert.assertNotNull(descriptor.getOrganization(), "Organization ");
        Assert.assertEquals(descriptor.getContactPersons().size(), 1, "ContactPerson count");
        Assert.assertEquals(descriptor.getAdditionalMetadataLocations().size(), 3, "AdditionalMetadataLocation count");
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        final QName qname =
                new QName(SAMLConstants.SAML20MD_NS, EntityDescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        final EntityDescriptor descriptor = (EntityDescriptor) buildXMLObject(qname);

        StringBuilder bigString = new StringBuilder();
        for (int i = 0; i < 2000; i++ ) {
            bigString.append('x');
        }
        try {
            descriptor.setEntityID(bigString.toString());
            Assert.fail();
        }
        catch (IllegalArgumentException e) {
        }
        
        descriptor.setEntityID(expectedEntityID);

        assertXMLEquals(expectedDOM, descriptor);
    }

    /**
     * Test marshalling of attribute IDness.
     *
     * @throws MarshallingException
     * @throws XMLParserException
     * */
    @Test
    public void testAttributeIDnessMarshall() throws MarshallingException, XMLParserException {
        final XMLObject target = buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);

        ((EntityDescriptor)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }


    @Test public void testSingleElementUnknownAttributesMarshall() {
        final EntityDescriptor descriptor = (new EntityDescriptorBuilder()).buildObject();

        for (int i = 0; i < unknownAttributeNames.length; i++) {
            descriptor.getUnknownAttributes().put(unknownAttributeNames[i], unknownAttributeValues[i]);
        }
        assertXMLEquals(expectedUnknownAttributesDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesMarshall() {
        final QName qname =
                new QName(SAMLConstants.SAML20MD_NS, EntityDescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        final EntityDescriptor descriptor = (EntityDescriptor) buildXMLObject(qname);

        descriptor.setEntityID(expectedEntityID);
        descriptor.setID(expectedID);
        descriptor.setValidUntil(expectedValidUntil);
        descriptor.setCacheDuration(expectedCacheDuration);

        assertXMLEquals(expectedOptionalAttributesDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        final QName qname =
                new QName(SAMLConstants.SAML20MD_NS, EntityDescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        EntityDescriptor descriptor = (EntityDescriptor) buildXMLObject(qname);
        descriptor.setID(expectedID);
        descriptor.setEntityID(expectedEntityID);

        final QName extensionsQName =
                new QName(SAMLConstants.SAML20MD_NS, Extensions.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        descriptor.setExtensions((Extensions) buildXMLObject(extensionsQName));

        descriptor.setSignature(buildSignatureSkeleton());

        final QName idpSSOQName =
                new QName(SAMLConstants.SAML20MD_NS, IDPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        final QName spSSOQName =
                new QName(SAMLConstants.SAML20MD_NS, SPSSODescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        final QName authnAuthQName =
                new QName(SAMLConstants.SAML20MD_NS, AuthnAuthorityDescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        final QName pdpQName =
                new QName(SAMLConstants.SAML20MD_NS, PDPDescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        QName affilQName =
                new QName(SAMLConstants.SAML20MD_NS, AffiliationDescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        RoleDescriptor desc = (RoleDescriptor) buildXMLObject(idpSSOQName);
        desc.addSupportedProtocol("foo");
        descriptor.getRoleDescriptors(IDPSSODescriptor.DEFAULT_ELEMENT_NAME).add(desc);
        
        desc = (RoleDescriptor) buildXMLObject(spSSOQName);
        desc.addSupportedProtocol("foo");
        descriptor.getRoleDescriptors(SPSSODescriptor.DEFAULT_ELEMENT_NAME).add(desc);

        descriptor.getRoleDescriptors(SPSSODescriptor.DEFAULT_ELEMENT_NAME).add(
                (SPSSODescriptor) buildXMLObject(spSSOQName));
        
        desc =(RoleDescriptor) buildXMLObject(authnAuthQName);
        desc.addSupportedProtocol("foo");
        descriptor.getRoleDescriptors(AuthnAuthorityDescriptor.DEFAULT_ELEMENT_NAME).add(desc);
        
        desc =(RoleDescriptor) buildXMLObject(pdpQName);
        desc.addSupportedProtocol("foo");
        descriptor.getRoleDescriptors(PDPDescriptor.DEFAULT_ELEMENT_NAME).add(desc);
        descriptor.getRoleDescriptors(IDPSSODescriptor.DEFAULT_ELEMENT_NAME).add(
                (IDPSSODescriptor) buildXMLObject(idpSSOQName));
        
        desc =(RoleDescriptor) buildXMLObject(AttributeAuthorityDescriptor.DEFAULT_ELEMENT_NAME);
        desc.addSupportedProtocol("foo");        
        descriptor.getRoleDescriptors(AttributeAuthorityDescriptor.DEFAULT_ELEMENT_NAME).add(desc);

        descriptor.getRoleDescriptors(SPSSODescriptor.DEFAULT_ELEMENT_NAME).add(
                (SPSSODescriptor) buildXMLObject(spSSOQName));
        descriptor.getRoleDescriptors(AuthnAuthorityDescriptor.DEFAULT_ELEMENT_NAME).add(
                (AuthnAuthorityDescriptor) buildXMLObject(authnAuthQName));
        descriptor.getRoleDescriptors(PDPDescriptor.DEFAULT_ELEMENT_NAME).add((PDPDescriptor) buildXMLObject(pdpQName));
        descriptor.setAffiliationDescriptor((AffiliationDescriptor) buildXMLObject(affilQName));

        final QName orgQName =
                new QName(SAMLConstants.SAML20MD_NS, Organization.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        descriptor.setOrganization((Organization) buildXMLObject(orgQName));

        final QName contactQName =
                new QName(SAMLConstants.SAML20MD_NS, ContactPerson.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        descriptor.getContactPersons().add((ContactPerson) buildXMLObject(contactQName));

        final QName addMDQName =
                new QName(SAMLConstants.SAML20MD_NS, AdditionalMetadataLocation.DEFAULT_ELEMENT_LOCAL_NAME,
                        SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 3; i++) {
            descriptor.getAdditionalMetadataLocations().add((AdditionalMetadataLocation) buildXMLObject(addMDQName));
        }

        assertXMLEquals(expectedChildElementsDOM, descriptor);
    }

    /**
     * Build a Signature skeleton to use in marshalling unit tests.
     * 
     * @return minimally populated Signature element
     */
    private Signature buildSignatureSkeleton() {
        final Signature signature = (Signature) buildXMLObject(Signature.DEFAULT_ELEMENT_NAME);
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        return signature;
    }

}