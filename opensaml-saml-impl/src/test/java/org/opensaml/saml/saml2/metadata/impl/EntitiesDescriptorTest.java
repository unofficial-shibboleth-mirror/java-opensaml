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
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.EntitiesDescriptorImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class EntitiesDescriptorTest extends XMLObjectProviderBaseTestCase {

    /** Expected Name attribute value */
    protected String expectedName;

    /** Expected ID attribute value */
    protected String expectedID;

    /** Expected cacheDuration value in miliseconds */
    protected Duration expectedCacheDuration;

    /** Expected validUntil value */
    protected Instant expectedValidUntil;

    /** Expected number of child EntitiesDescriptors */
    protected int expectedEntitiesDescriptorsCount;

    /** Expected number of child EntityDescriptors */
    protected int expectedEntityDescriptorsCount;

    /**
     * Constructor
     */
    public EntitiesDescriptorTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/EntitiesDescriptor.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/metadata/impl/EntitiesDescriptorOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/metadata/impl/EntitiesDescriptorChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedID = "id";
        expectedName = "eDescName";
        expectedCacheDuration = Duration.ofSeconds(90);
        expectedValidUntil = Instant.parse("2005-12-07T10:21:00.000Z");
        expectedEntitiesDescriptorsCount = 3;
        expectedEntityDescriptorsCount = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final EntitiesDescriptor entitiesDescriptorObj = (EntitiesDescriptor) unmarshallElement(singleElementFile);
        assert entitiesDescriptorObj!=null;
        final String name = entitiesDescriptorObj.getName();
        Assert.assertNull(name, "Name attribute has a value of " + name + ", expected no value");

        final Duration duration = entitiesDescriptorObj.getCacheDuration();
        Assert.assertNull(duration, "cacheDuration attribute has a value of " + duration + ", expected no value");

        final Instant validUntil = entitiesDescriptorObj.getValidUntil();
        Assert.assertNull(validUntil, "validUntil attribute has a value of " + validUntil + ", expected no value");
        Assert.assertTrue(entitiesDescriptorObj.isValid());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final EntitiesDescriptor entitiesDescriptorObj = (EntitiesDescriptor) unmarshallElement(singleElementOptionalAttributesFile);
        assert entitiesDescriptorObj!=null;
        final String name = entitiesDescriptorObj.getName();
        Assert.assertEquals(name, expectedName,
                "Name attribute has a value of " + name + ", expected a value of " + expectedName);

        final String id = entitiesDescriptorObj.getID();
        Assert.assertEquals(id, expectedID, "ID attriubte has a value of " + id + ", expected a value of " + expectedID);

        final Duration duration = entitiesDescriptorObj.getCacheDuration();
        Assert.assertEquals(duration, expectedCacheDuration, "cacheDuration attribute has a value of " + duration + ", expected a value of "
                        + expectedCacheDuration);

        final Instant validUntil = entitiesDescriptorObj.getValidUntil();
        Assert.assertEquals(expectedValidUntil
                .compareTo(validUntil), 0, "validUntil attribute value did not match expected value");
        Assert.assertFalse(entitiesDescriptorObj.isValid());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final EntitiesDescriptor entitiesDescriptor = (EntitiesDescriptor) unmarshallElement(childElementsFile);
        assert entitiesDescriptor!=null;
        Assert.assertNotNull(entitiesDescriptor.getSignature(), "Signature");
        Assert.assertNotNull(entitiesDescriptor.getExtensions(), "Extensions");
        Assert.assertEquals(entitiesDescriptor
                .getEntitiesDescriptors().size(), expectedEntitiesDescriptorsCount, "Entities Descriptor child elements");
        Assert.assertEquals(entitiesDescriptor
                .getEntityDescriptors().size(), expectedEntityDescriptorsCount, "Entity Descriptor child elements");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, EntitiesDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        EntitiesDescriptor entitiesDescriptor = (EntitiesDescriptor) buildXMLObject(qname);

        assertXMLEquals(expectedDOM, entitiesDescriptor);
    }

    /**
     * Test marshalling of attribute IDness.
     *
     * @throws MarshallingException
     * @throws XMLParserException
     * */
    @Test
    public void testAttributeIDnessMarshall() throws MarshallingException, XMLParserException {
        XMLObject target = buildXMLObject(EntitiesDescriptor.DEFAULT_ELEMENT_NAME);

        ((EntitiesDescriptor)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, EntitiesDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        EntitiesDescriptor entitiesDescriptor = (EntitiesDescriptor) buildXMLObject(qname);

        entitiesDescriptor.setName(expectedName);
        entitiesDescriptor.setID(expectedID);
        entitiesDescriptor.setCacheDuration(expectedCacheDuration);
        entitiesDescriptor.setValidUntil(expectedValidUntil);

        assertXMLEquals(expectedOptionalAttributesDOM, entitiesDescriptor);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, EntitiesDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        EntitiesDescriptor entitiesDescriptor = (EntitiesDescriptor) buildXMLObject(qname);
        entitiesDescriptor.setID(expectedID);
        
        entitiesDescriptor.setSignature( buildSignatureSkeleton() );

        QName extensionsQName = new QName(SAMLConstants.SAML20MD_NS, Extensions.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        entitiesDescriptor.setExtensions((Extensions) buildXMLObject(extensionsQName));
        
        QName entitiesDescriptorQName = new QName(SAMLConstants.SAML20MD_NS, EntitiesDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        QName entityDescriptorQName = new QName(SAMLConstants.SAML20MD_NS, EntityDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        entitiesDescriptor.getEntitiesDescriptors().add((EntitiesDescriptor) buildXMLObject(entitiesDescriptorQName));
        entitiesDescriptor.getEntityDescriptors().add((EntityDescriptor) buildXMLObject(entityDescriptorQName));
        entitiesDescriptor.getEntitiesDescriptors().add((EntitiesDescriptor) buildXMLObject(entitiesDescriptorQName));
        entitiesDescriptor.getEntityDescriptors().add((EntityDescriptor) buildXMLObject(entityDescriptorQName));
        entitiesDescriptor.getEntitiesDescriptors().add((EntitiesDescriptor) buildXMLObject(entitiesDescriptorQName));
        assertXMLEquals(expectedChildElementsDOM, entitiesDescriptor);
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