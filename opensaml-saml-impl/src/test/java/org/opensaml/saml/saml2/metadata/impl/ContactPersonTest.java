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

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.Company;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.ContactPersonTypeEnumeration;
import org.opensaml.saml.saml2.metadata.EmailAddress;
import org.opensaml.saml.saml2.metadata.GivenName;
import org.opensaml.saml.saml2.metadata.SurName;
import org.opensaml.saml.saml2.metadata.TelephoneNumber;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.ContactPersonImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class ContactPersonTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected company name */
    protected ContactPersonTypeEnumeration expectedPersonType;
    
    /** Count of EmailAddress subelements */
    protected int emailAddressCount = 2;
    
    /** Count of TelephoneNumber subelements */
    protected int telephoneNumberCount = 3;
    
    /** Unknown Attributes */
    protected QName[] unknownAttributeNames = { new QName("urn:foo:bar", "bar", "foo") };
    /** Unknown Attribute Values */
    protected String[] unknownAttributeValues = {"fred"};

    /**
     * Constructor
     */
    public ContactPersonTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/ContactPerson.xml";
        childElementsFile = "/org/opensaml/saml/saml2/metadata/impl/ContactPersonChildElements.xml";
        singleElementUnknownAttributesFile = "/org/opensaml/saml/saml2/metadata/impl/ContactPersonUnknownAttributes.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedPersonType = ContactPersonTypeEnumeration.TECHNICAL;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final ContactPerson person = (ContactPerson) unmarshallElement(singleElementFile);
        assert person!=null;
        Assert.assertEquals(person.getType(), expectedPersonType, "Contact type was not expected value");
    }
    
    /** {@inheritDoc} */
    @Test public void testSingleElementUnknownAttributesUnmarshall() {
        final ContactPerson person = (ContactPerson) unmarshallElement(singleElementUnknownAttributesFile);
        assert person!=null;
        AttributeMap attributes = person.getUnknownAttributes();

        Assert.assertEquals(attributes.entrySet().size(), unknownAttributeNames.length);
        for (int i = 0; i < unknownAttributeNames.length; i++) {
            Assert.assertEquals(attributes.get(unknownAttributeNames[i]), unknownAttributeValues[i]);
        }
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall()
    {
        final ContactPerson person = (ContactPerson) unmarshallElement(childElementsFile);
        assert person!=null;
        Assert.assertNotNull(person.getExtensions(), "Extension Element not present");
        Assert.assertNotNull(person.getCompany(), "Company Element not present");
        Assert.assertNotNull(person.getSurName(), "SurName not present");
        Assert.assertNotNull(person.getGivenName(), "GivenName not present");
        Assert.assertEquals(person.getEmailAddresses().size(), emailAddressCount, "Email address count");
        Assert.assertEquals(person.getTelephoneNumbers().size(), telephoneNumberCount, "Telephone Number count");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final ContactPerson person = (ContactPerson) buildXMLObject(ContactPerson.DEFAULT_ELEMENT_NAME);
        
        person.setType(expectedPersonType);

        assertXMLEquals(expectedDOM, person);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnknownAttributesMarshall() {
        final ContactPerson person = (ContactPerson) buildXMLObject(ContactPerson.DEFAULT_ELEMENT_NAME);
        person.setType(ContactPersonTypeEnumeration.OTHER);

        for (int i = 0; i < unknownAttributeNames.length; i++) {
            person.getUnknownAttributes().put(unknownAttributeNames[i], unknownAttributeValues[i]);
        }
        assertXMLEquals(expectedUnknownAttributesDOM, person);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall()
    {
        final ContactPerson person = (new ContactPersonBuilder()).buildObject();
        
        person.setType(expectedPersonType);

        final  QName extensionsQName = new QName(SAMLConstants.SAML20MD_NS, Extensions.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        person.setExtensions((Extensions) buildXMLObject(extensionsQName));
        
        final QName companuQName = new QName(SAMLConstants.SAML20MD_NS, Company.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        person.setCompany((Company) buildXMLObject(companuQName));
        
        final QName givenNameQName = new QName(SAMLConstants.SAML20MD_NS, GivenName.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        person.setGivenName((GivenName) buildXMLObject(givenNameQName));
        
        final QName surnameQName = new QName(SAMLConstants.SAML20MD_NS, SurName.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        person.setSurName((SurName) buildXMLObject(surnameQName));
        
        final QName teleQName = new QName(SAMLConstants.SAML20MD_NS, TelephoneNumber.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < telephoneNumberCount; i++) {
            person.getTelephoneNumbers().add((TelephoneNumber) buildXMLObject(teleQName));
        }
        
        final QName emailQName = new QName(SAMLConstants.SAML20MD_NS, EmailAddress.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < emailAddressCount; i++) {
            person.getEmailAddresses().add((EmailAddress) buildXMLObject(emailQName));
        }
        
        assertXMLEquals(expectedChildElementsDOM, person);
    }
}