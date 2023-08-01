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

package org.opensaml.xmlsec.encryption.impl;


import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.xmlsec.encryption.EncryptionProperty;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class EncryptionPropertyTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedTarget;
    private String expectedID;
    private int expectedNumUnknownChildren;
    
    private QName expectedAttribName1;
    private QName expectedAttribName2;
    
    private String expectedAttribValue1;
    private String expectedAttribValue2;
    
    
    /**
     * Constructor
     *
     */
    public EncryptionPropertyTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/EncryptionProperty.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/xmlsec/encryption/impl/EncryptionPropertyOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/EncryptionPropertyChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedTarget = "urn:string:foo";
        expectedID = "someID";
        expectedNumUnknownChildren = 2;
        
        expectedAttribName1 = new QName("urn:namespace:foo", "bar", "foo");
        expectedAttribValue1 = "abc";
        
        expectedAttribName2 = new QName("urn:namespace:foo", "baz", "foo");
        expectedAttribValue2 = "123";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final EncryptionProperty ep = (EncryptionProperty) unmarshallElement(singleElementFile);
        
        assert ep != null;
        Assert.assertNull(ep.getTarget(), "Target attribute");
        Assert.assertNull(ep.getID(), "Id attribute");
        Assert.assertEquals(ep.getUnknownXMLObjects().size(), 0, "Unknown children");
        Assert.assertNull(ep.getUnknownAttributes().get(expectedAttribName1), "Unknown attribute 1");
        Assert.assertNull(ep.getUnknownAttributes().get(expectedAttribName2), "Unknown attribute 2");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final EncryptionProperty ep = (EncryptionProperty) unmarshallElement(singleElementOptionalAttributesFile);
        
        assert ep != null;
        Assert.assertEquals(ep.getTarget(), expectedTarget, "Target attribute");
        Assert.assertEquals(ep.getID(), expectedID, "Id attribute");
        Assert.assertEquals(ep.getUnknownXMLObjects().size(), 0, "Unknown children");
        Assert.assertEquals(ep.getUnknownAttributes().get(expectedAttribName1), expectedAttribValue1, "Unknown attribute 1");
        Assert.assertEquals(ep.getUnknownAttributes().get(expectedAttribName2), expectedAttribValue2, "Unknown attribute 2");
        
        Assert.assertEquals(ep.resolveID(expectedID), ep, "ID lookup failed");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final EncryptionProperty ep = (EncryptionProperty) unmarshallElement(childElementsFile);
        
        assert ep != null;
        Assert.assertNull(ep.getTarget(), "Target attribute");
        Assert.assertNull(ep.getID(), "Id attribute");
        Assert.assertEquals(ep.getUnknownXMLObjects().size(), expectedNumUnknownChildren, "Unknown children");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final EncryptionProperty ep = (EncryptionProperty) buildXMLObject(EncryptionProperty.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, ep);
    }

    /**
     * Test marshalling of attribute IDness.
     *
     * @throws MarshallingException
     * @throws XMLParserException
     * */
    @Test
    public void testAttributeIDnessMarshall() throws MarshallingException, XMLParserException {
        final XMLObject target = buildXMLObject(EncryptionProperty.DEFAULT_ELEMENT_NAME);

        ((EncryptionProperty)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final EncryptionProperty ep = (EncryptionProperty) buildXMLObject(EncryptionProperty.DEFAULT_ELEMENT_NAME);
        
        ep.setTarget(expectedTarget);
        ep.setID(expectedID);
        ep.getUnknownAttributes().put(expectedAttribName1, expectedAttribValue1);
        ep.getUnknownAttributes().put(expectedAttribName2, expectedAttribValue2);
        
        assertXMLEquals(expectedOptionalAttributesDOM, ep);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final EncryptionProperty ep = (EncryptionProperty) buildXMLObject(EncryptionProperty.DEFAULT_ELEMENT_NAME);
        
        ep.getUnknownXMLObjects().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        ep.getUnknownXMLObjects().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, ep);
    }

}
