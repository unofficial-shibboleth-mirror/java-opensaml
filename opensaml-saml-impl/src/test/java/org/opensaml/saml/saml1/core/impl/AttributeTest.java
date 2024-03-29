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

/**
 * 
 */

package org.opensaml.saml.saml1.core.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Attribute;
import org.opensaml.saml.saml1.core.AttributeValue;

/**
 * 
 */
@SuppressWarnings({"null", "javadoc"})
public class AttributeTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    /** Value from test file */
    private final String expectedAttributeName;

    /** Value from test file */
    private final String expectedAttributeNamespace;

    /**
     * Constructor
     */
    public AttributeTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleAttribute.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml1/impl/singleAttributeAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml1/impl/AttributeWithChildren.xml";
        expectedAttributeName = "AttributeName";
        expectedAttributeNamespace = "namespace";
        qname = new QName(SAMLConstants.SAML1_NS, Attribute.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final Attribute attribute = (Attribute) unmarshallElement(singleElementFile);
        assert attribute!=null;

        Assert.assertNull(attribute.getAttributeName(), "AttributeName");
        Assert.assertNull(attribute.getAttributeNamespace(), "AttributeNamespace");
        Assert.assertEquals(attribute.getAttributeValues().size(), 0, "<AttributeValue> subelement found");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        Attribute attribute = (Attribute) unmarshallElement(singleElementOptionalAttributesFile);
        assert attribute!=null;

        Assert.assertEquals(attribute.getAttributeName(), expectedAttributeName, "AttributeName");
        Assert.assertEquals(attribute.getAttributeNamespace(), expectedAttributeNamespace, "AttributeNamespace");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final Attribute attribute = (Attribute) unmarshallElement(childElementsFile);
        assert attribute!=null;

        Assert.assertNotNull(attribute.getAttributeValues(), "<AttributeValue> subelement not found");
        Assert.assertEquals(attribute.getAttributeValues().size(), 4, "Number of <AttributeValue> subelement not found");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final Attribute attribute = (Attribute) buildXMLObject(qname);

        attribute.setAttributeName(expectedAttributeName);
        attribute.setAttributeNamespace(expectedAttributeNamespace);
        assertXMLEquals(expectedOptionalAttributesDOM, attribute);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        Attribute attribute = (Attribute) buildXMLObject(qname);

        final XMLObjectBuilder<XSString> attributeValueBuilder = builderFactory.ensureBuilder(XSString.TYPE_NAME);
        
        attribute.getAttributeValues().add(attributeValueBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME)); 
        attribute.getAttributeValues().add(attributeValueBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME)); 
        attribute.getAttributeValues().add(attributeValueBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME)); 
        attribute.getAttributeValues().add(attributeValueBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME)); 

        assertXMLEquals(expectedChildElementsDOM, attribute);
    }
}