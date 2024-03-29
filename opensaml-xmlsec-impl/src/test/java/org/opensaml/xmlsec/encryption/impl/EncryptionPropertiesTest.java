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


import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.xmlsec.encryption.EncryptionProperties;
import org.opensaml.xmlsec.encryption.EncryptionProperty;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class EncryptionPropertiesTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedID;
    private int expectedNumEncProps;
    
    /**
     * Constructor
     *
     */
    public EncryptionPropertiesTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/EncryptionProperties.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/xmlsec/encryption/impl/EncryptionPropertiesOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/EncryptionPropertiesChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedID = "someID";
        expectedNumEncProps = 3;
        
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final EncryptionProperties ep = (EncryptionProperties) unmarshallElement(singleElementFile);
        
        assert ep != null;
        Assert.assertNull(ep.getID(), "Id attribute");
        Assert.assertEquals(ep.getEncryptionProperties().size(), 0, "# of EncryptionProperty children");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final EncryptionProperties ep = (EncryptionProperties) unmarshallElement(singleElementOptionalAttributesFile);
        
        assert ep != null;
        Assert.assertEquals(ep.getID(), expectedID, "Id attribute");
        Assert.assertEquals(ep.getEncryptionProperties().size(), 0, "# of EncryptionProperty children");
        
        Assert.assertEquals(ep.resolveID(expectedID), ep, "ID lookup failed");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final EncryptionProperties ep = (EncryptionProperties) unmarshallElement(childElementsFile);
        
        assert ep != null;
        Assert.assertNull(ep.getID(), "Id attribute");
        Assert.assertEquals(ep.getEncryptionProperties().size(), expectedNumEncProps, "# of EncryptionProperty children");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final EncryptionProperties ep = (EncryptionProperties) buildXMLObject(EncryptionProperties.DEFAULT_ELEMENT_NAME);
        
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
        final XMLObject target = buildXMLObject(EncryptionProperties.DEFAULT_ELEMENT_NAME);

        ((EncryptionProperties)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final EncryptionProperties ep = (EncryptionProperties) buildXMLObject(EncryptionProperties.DEFAULT_ELEMENT_NAME);
        
        ep.setID(expectedID);
        
        assertXMLEquals(expectedOptionalAttributesDOM, ep);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final EncryptionProperties ep = (EncryptionProperties) buildXMLObject(EncryptionProperties.DEFAULT_ELEMENT_NAME);
        
        ep.getEncryptionProperties().add((EncryptionProperty) buildXMLObject(EncryptionProperty.DEFAULT_ELEMENT_NAME));
        ep.getEncryptionProperties().add((EncryptionProperty) buildXMLObject(EncryptionProperty.DEFAULT_ELEMENT_NAME));
        ep.getEncryptionProperties().add((EncryptionProperty) buildXMLObject(EncryptionProperty.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, ep);
    }

}
