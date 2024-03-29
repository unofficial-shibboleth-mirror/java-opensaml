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
import org.opensaml.saml.saml2.core.Attribute;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.AttributeImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class AttributeTest extends XMLObjectProviderBaseTestCase {

    /** Expected Name attribute value */
    protected String expectedName;

    /** Expected NameFormat attribute value */
    protected String expectedNameFormat;

    /** Expected FriendlyName attribute value */
    protected String expectedFriendlyName;

    /**
     * Constructor
     */
    public AttributeTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/Attribute.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/AttributeOptionalAttributes.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedName = "attribName";
        expectedNameFormat = "urn:string";
        expectedFriendlyName = "Attribute Name";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final Attribute attribute = (Attribute) unmarshallElement(singleElementFile);
        assert attribute!=null;

        final String name = attribute.getName();
        Assert.assertEquals(name, expectedName, "Name was " + name + ", expected " + expectedName);

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final Attribute attribute = (Attribute) unmarshallElement(singleElementOptionalAttributesFile);
        assert attribute!=null;

        final String name = attribute.getName();
        Assert.assertEquals(name, expectedName, "Name was " + name + ", expected " + expectedName);

        final String nameFormat = attribute.getNameFormat();
        Assert.assertEquals(nameFormat, expectedNameFormat,
                "NameFormat was " + nameFormat + ", expected " + expectedNameFormat);

        final String friendlyName = attribute.getFriendlyName();
        Assert.assertEquals(friendlyName, expectedFriendlyName,
                "FriendlyName was " + friendlyName + ", expected " + expectedFriendlyName);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20_NS, Attribute.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final Attribute attribute = (Attribute) buildXMLObject(qname);

        attribute.setName(expectedName);

        assertXMLEquals(expectedDOM, attribute);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20_NS, Attribute.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final Attribute attribute = (Attribute) buildXMLObject(qname);

        attribute.setName(expectedName);
        attribute.setNameFormat(expectedNameFormat);
        attribute.setFriendlyName(expectedFriendlyName);

        assertXMLEquals(expectedOptionalAttributesDOM, attribute);
    }
}