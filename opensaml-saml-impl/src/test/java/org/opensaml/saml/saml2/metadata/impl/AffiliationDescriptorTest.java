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

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.AffiliateMember;
import org.opensaml.saml.saml2.metadata.AffiliationDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.AffiliationDescriptorImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class AffiliationDescriptorTest extends XMLObjectProviderBaseTestCase {

    /** Expected affiliationOwnerID value */
    protected String expectedOwnerID;

    /** Expceted ID value */
    protected String expectedID;

    /** Expected cacheDuration value. */
    protected Duration expectedCacheDuration;

    /** Expected validUntil value */
    protected Instant expectedValidUntil;

    /** Unknown Attributes */
    protected QName[] unknownAttributeNames = { new QName("urn:foo:bar", "bar", "foo") };
    /** Unknown Attribute Values */
    protected String[] unknownAttributeValues = {"fred"};

    /**
     * Constructor
     */
    public AffiliationDescriptorTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/AffiliationDescriptor.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/metadata/impl/AffiliationDescriptorOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/metadata/impl/AffiliationDescriptorChildElements.xml";
        singleElementUnknownAttributesFile = "/org/opensaml/saml/saml2/metadata/impl/AffiliationDescriptorUnknownAttributes.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedOwnerID = "urn:example.org";
        expectedID = "id";
        expectedCacheDuration = Duration.ofSeconds(90);
        expectedValidUntil = Instant.parse("2005-12-07T10:21:00Z");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final AffiliationDescriptor descriptor = (AffiliationDescriptor) unmarshallElement(singleElementFile);
        assert descriptor!=null;

        final String ownerId = descriptor.getOwnerID();
        Assert.assertEquals(ownerId,
                expectedOwnerID, "entityID attribute has a value of " + ownerId + ", expected a value of " + expectedOwnerID);

        final Duration duration = descriptor.getCacheDuration();
        Assert.assertNull(duration, "cacheDuration attribute has a value of " + duration + ", expected no value");

        final Instant validUntil = descriptor.getValidUntil();
        Assert.assertNull(validUntil, "validUntil attribute has a value of " + validUntil + ", expected no value");
        
        Assert.assertTrue(descriptor.isValid());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final AffiliationDescriptor descriptor = (AffiliationDescriptor) unmarshallElement(singleElementOptionalAttributesFile);
        assert descriptor!=null;
        final String ownerId = descriptor.getOwnerID();
        Assert.assertEquals(ownerId,
                expectedOwnerID, "entityID attribute has a value of " + ownerId + ", expected a value of " + expectedOwnerID);

        final String id = descriptor.getID();
        Assert.assertEquals(id, expectedID, "ID attribute has a value of " + id + ", expected a value of " + expectedID);

        final Duration duration = descriptor.getCacheDuration();
        Assert.assertEquals(duration, expectedCacheDuration, "cacheDuration attribute has a value of " + duration + ", expected a value of "
                        + expectedCacheDuration);

        final Instant validUntil = descriptor.getValidUntil();
        Assert.assertEquals(expectedValidUntil
                .compareTo(validUntil), 0, "validUntil attribute value did not match expected value");
        Assert.assertFalse(descriptor.isValid());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnknownAttributesUnmarshall() {
        final AffiliationDescriptor descriptor = (AffiliationDescriptor) unmarshallElement(singleElementUnknownAttributesFile);
        assert descriptor!=null;
        final  AttributeMap attributes = descriptor.getUnknownAttributes();

        Assert.assertEquals(attributes.entrySet().size(), unknownAttributeNames.length);
        for (int i = 0; i < unknownAttributeNames.length; i++) {
            Assert.assertEquals(attributes.get(unknownAttributeNames[i]), unknownAttributeValues[i]);
        }
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final AffiliationDescriptor descriptor = (AffiliationDescriptor) unmarshallElement(childElementsFile);
        assert descriptor!=null;
        Assert.assertNotNull(descriptor.getExtensions(), "Extensions");
        Assert.assertNotNull(descriptor.getSignature(), "Signature");
        Assert.assertEquals(descriptor.getKeyDescriptors().size(), 1, "KeyDescriptor count");
        Assert.assertEquals(descriptor.getMembers().size(), 3, "Affiliate Member count ");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20MD_NS, AffiliationDescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        final AffiliationDescriptor descriptor = (AffiliationDescriptor) buildXMLObject(qname);

        descriptor.setOwnerID(expectedOwnerID);

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
        final XMLObject target = buildXMLObject(AffiliationDescriptor.DEFAULT_ELEMENT_NAME);

        ((AffiliationDescriptor)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final AffiliationDescriptor descriptor = (new AffiliationDescriptorBuilder()).buildObject();

        descriptor.setOwnerID(expectedOwnerID);
        descriptor.setID(expectedID);
        descriptor.setValidUntil(expectedValidUntil);
        descriptor.setCacheDuration(expectedCacheDuration);

        assertXMLEquals(expectedOptionalAttributesDOM, descriptor);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnknownAttributesMarshall() {
        final AffiliationDescriptor descriptor = (new AffiliationDescriptorBuilder()).buildObject();

        for (int i = 0; i < unknownAttributeNames.length; i++) {
            descriptor.getUnknownAttributes().put(unknownAttributeNames[i], unknownAttributeValues[i]);
        }
        assertXMLEquals(expectedUnknownAttributesDOM, descriptor);
    }

    @Test
    public void testChildElementsMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20MD_NS, AffiliationDescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        final AffiliationDescriptor descriptor = (AffiliationDescriptor) buildXMLObject(qname);

        StringBuilder bigString = new StringBuilder();
        for (int i=0; i < 1026; i++) {
            bigString.append('s');
        }
        try {
            descriptor.setOwnerID(bigString.toString());
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        
        descriptor.setOwnerID(expectedOwnerID);
        descriptor.setID(expectedID);
        
        descriptor.setSignature( buildSignatureSkeleton() );

        descriptor.setExtensions((Extensions) buildXMLObject(Extensions.DEFAULT_ELEMENT_NAME));

        final QName affilMemberQName = new QName(SAMLConstants.SAML20MD_NS, AffiliateMember.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        descriptor.getMembers().add((AffiliateMember) buildXMLObject(affilMemberQName));
        descriptor.getMembers().add((AffiliateMember) buildXMLObject(affilMemberQName));
        descriptor.getMembers().add((AffiliateMember) buildXMLObject(affilMemberQName));
        descriptor.getKeyDescriptors().add((new KeyDescriptorBuilder().buildObject()));

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