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

import java.time.Instant;

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.KeyInfoConfirmationDataType;
import org.opensaml.xmlsec.signature.KeyInfo;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.KeyInfoConfirmationDataTypeImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class KeyInfoConfirmationDataTypeTest extends XMLObjectProviderBaseTestCase {

    /** Expected NotBefore value. */
    private Instant expectedNotBefore;

    /** Expected NotOnOrAfter value. */
    private Instant expectedNotOnOrAfter;

    /** Expected Recipient value. */
    private String expectedRecipient;

    /** Expected InResponseTo value. */
    private String expectedInResponseTo;

    /** Expected Address value. */
    private String expectedAddress;
    
    /** Expected xsi:type value. */
    private QName expectedType;
    
    /** Expected number of KeyInfo child elements. */
    private int expectedNumKeyInfoChildren;

    /** Constructor. */
    public KeyInfoConfirmationDataTypeTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/KeyInfoConfirmationDataType.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/KeyInfoConfirmationDataTypeOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/KeyInfoConfirmationDataTypeChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedNotBefore = Instant.parse("1984-08-26T10:01:30.043Z");
        expectedNotOnOrAfter = Instant.parse("1984-08-26T10:11:30.043Z");
        expectedRecipient = "recipient";
        expectedInResponseTo = "inresponse";
        expectedAddress = "address";
        expectedType = new QName(SAMLConstants.SAML20_NS, "KeyInfoConfirmationDataType", SAMLConstants.SAML20_PREFIX);
        expectedNumKeyInfoChildren = 3;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final KeyInfoConfirmationDataType kicd = (KeyInfoConfirmationDataType) unmarshallElement(singleElementFile);
        Assert.assertNotNull(kicd, "Object was null");
        assert kicd !=null;
        Assert.assertEquals(kicd.getSchemaType(), expectedType, "Object xsi:type was not the expected value");

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final KeyInfoConfirmationDataType kicd = (KeyInfoConfirmationDataType) unmarshallElement(singleElementOptionalAttributesFile);
        assert kicd !=null;
        final Instant notBefore = kicd.getNotBefore();
        Assert.assertEquals(notBefore, expectedNotBefore, "NotBefore was " + notBefore + ", expected " + expectedNotBefore);

        final Instant notOnOrAfter = kicd.getNotOnOrAfter();
        Assert.assertEquals(notOnOrAfter, expectedNotOnOrAfter,
                "NotOnOrAfter was " + notOnOrAfter + ", expected " + expectedNotOnOrAfter);

        final String recipient = kicd.getRecipient();
        Assert.assertEquals(recipient, expectedRecipient, "Recipient was " + recipient + ", expected " + expectedRecipient);

        final String inResponseTo = kicd.getInResponseTo();
        Assert.assertEquals(inResponseTo, expectedInResponseTo,
                "InResponseTo was " + inResponseTo + ", expected " + expectedInResponseTo);

        final String address = kicd.getAddress();
        Assert.assertEquals(address, expectedAddress, "Address was " + address + ", expected " + expectedAddress);
        
        Assert.assertEquals(kicd.getSchemaType(), expectedType, "Object xsi:type was not the expected value");
    }
    
    @Test
    public void testChildElementsUnmarshall() {
        KeyInfoConfirmationDataType kicd = (KeyInfoConfirmationDataType) unmarshallElement(childElementsFile);
        assert kicd !=null;
        Assert.assertEquals(kicd.getKeyInfos().size(), 3, "Unexpected number of KeyInfo children");
        Assert.assertEquals(kicd.getUnknownXMLObjects(KeyInfo.DEFAULT_ELEMENT_NAME).size(), 3, "Unexpected number of KeyInfo children");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        KeyInfoConfirmationDataType kicd = buildXMLObject();

        assertXMLEquals(expectedDOM, kicd);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        KeyInfoConfirmationDataType kicd = buildXMLObject();

        kicd.setNotBefore(expectedNotBefore);
        kicd.setNotOnOrAfter(expectedNotOnOrAfter);
        kicd.setRecipient(expectedRecipient);
        kicd.setInResponseTo(expectedInResponseTo);
        kicd.setAddress(expectedAddress);

        assertXMLEquals(expectedOptionalAttributesDOM, kicd);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        KeyInfoConfirmationDataType kicd = buildXMLObject();
        
        for (int i=0; i<expectedNumKeyInfoChildren; i++) {
            KeyInfo keyinfo = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
            kicd.getKeyInfos().add(keyinfo);
        }
        
        assertXMLEquals(expectedChildElementsDOM, kicd);
    }
    
    public KeyInfoConfirmationDataType buildXMLObject() {
        SAMLObjectBuilder<KeyInfoConfirmationDataType> builder = (SAMLObjectBuilder<KeyInfoConfirmationDataType>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<KeyInfoConfirmationDataType>ensureBuilder(
                        KeyInfoConfirmationDataType.TYPE_NAME);
        
        if(builder == null){
            Assert.fail("Unable to retrieve builder for object QName " + KeyInfoConfirmationDataType.TYPE_NAME);
        }
        return builder.buildObject();
    }

}