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

package org.opensaml.core.xml.tests;

import org.testng.annotations.Test;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.w3c.dom.Document;

import net.shibboleth.shared.xml.XMLParserException;

/**
 * Test support for attributes from the XML Schema Instance namespace.
 */
@SuppressWarnings("javadoc")
public class XMLObjectXSIAttribsTest extends XMLObjectBaseTestCase {
    
    /** QName for SimpleXMLObject. */
    private QName simpleXMLObjectQName;

    /** Constructor. */
    public XMLObjectXSIAttribsTest() {
        simpleXMLObjectQName = new QName(SimpleXMLObject.NAMESPACE, SimpleXMLObject.LOCAL_NAME);
    }
    
    @Test
    public void testUnmarshallNoNil() throws XMLParserException, UnmarshallingException {
        final String documentLocation = "/org/opensaml/core/xml/SimpleXMLObjectWithAttribute.xml";
        final Document document = parserPool.parse(XMLObjectXSIAttribsTest.class.getResourceAsStream(documentLocation));

        final Unmarshaller unmarshaller = unmarshallerFactory.ensureUnmarshaller(document.getDocumentElement());
        final SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        Assert.assertNull(sxObject.isNilXSBoolean());
        final Boolean nil = sxObject.isNil();
        Assert.assertTrue(nil != null && !nil, "Expected isNil() false");
    }
    
    @Test
    public void testUnmarshallNil() throws XMLParserException, UnmarshallingException {
        String documentLocation = "/org/opensaml/core/xml/SimpleXMLObjectNil.xml";
        Document document = parserPool.parse(XMLObjectXSIAttribsTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.ensureUnmarshaller(document.getDocumentElement());
        SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        Assert.assertNotNull(sxObject.isNilXSBoolean());
        final Boolean nil = sxObject.isNil();
        Assert.assertTrue(nil != null && nil, "Expected isNil() true");
    }
    
    @Test
    public void testMarshallNil() throws XMLParserException {
        final String expectedDocumentLocation = "/org/opensaml/core/xml/SimpleXMLObjectNil.xml";
        final Document expectedDocument = parserPool.parse(XMLObjectXSIAttribsTest.class
                .getResourceAsStream(expectedDocumentLocation));

        final XMLObjectBuilder<SimpleXMLObject> sxoBuilder =
                (XMLObjectBuilder<SimpleXMLObject>) builderFactory.<SimpleXMLObject>ensureBuilder(simpleXMLObjectQName);
        final SimpleXMLObject sxObject = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME);
        sxObject.setNil(true);

        assertXMLEquals(expectedDocument, sxObject);
    }
    
    @Test
    public void testUnmarshallSchemaLocation() throws XMLParserException, UnmarshallingException {
        final String expectedValue = "http://www.example.com/Test http://www.example.com/Test.xsd";
        final String documentLocation = "/org/opensaml/core/xml/SimpleXMLObjectSchemaLocation.xml";
        final Document document = parserPool.parse(XMLObjectXSIAttribsTest.class.getResourceAsStream(documentLocation));

        final Unmarshaller unmarshaller = unmarshallerFactory.ensureUnmarshaller(document.getDocumentElement());
        final SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        Assert.assertEquals(sxObject.getSchemaLocation(), expectedValue, "Incorrect xsi:schemaLocation value");
    }
    
    @Test
    public void testMarshallSchemaLocation() throws XMLParserException {
        final String expectedValue = "http://www.example.com/Test http://www.example.com/Test.xsd";
        final String expectedDocumentLocation = "/org/opensaml/core/xml/SimpleXMLObjectSchemaLocation.xml";
        final Document expectedDocument = parserPool.parse(XMLObjectXSIAttribsTest.class
                .getResourceAsStream(expectedDocumentLocation));

        final XMLObjectBuilder<SimpleXMLObject> sxoBuilder =
                (XMLObjectBuilder<SimpleXMLObject>) builderFactory.<SimpleXMLObject>ensureBuilder(simpleXMLObjectQName);
        final SimpleXMLObject sxObject = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME);
        sxObject.setSchemaLocation(expectedValue);

        assertXMLEquals(expectedDocument, sxObject);
    }
    
    @Test
    public void testUnmarshallNoNamespaceSchemaLocation() throws XMLParserException, UnmarshallingException {
        final String expectedValue = "http://www.example.com/Test.xsd";
        final String documentLocation = "/org/opensaml/core/xml/SimpleXMLObjectNoNamespaceSchemaLocation.xml";
        final Document document = parserPool.parse(XMLObjectXSIAttribsTest.class.getResourceAsStream(documentLocation));

        final Unmarshaller unmarshaller = unmarshallerFactory.ensureUnmarshaller(document.getDocumentElement());
        final SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        Assert.assertEquals(sxObject.getNoNamespaceSchemaLocation(), expectedValue, "Incorrect xsi:noNamespaceSchemaLocation value");
    }
    
    @Test
    public void testMarshallNoNamespaceSchemaLocation() throws XMLParserException {
        final String expectedValue = "http://www.example.com/Test.xsd";
        final String expectedDocumentLocation = "/org/opensaml/core/xml/SimpleXMLObjectNoNamespaceSchemaLocation.xml";
        final Document expectedDocument = parserPool.parse(XMLObjectXSIAttribsTest.class
                .getResourceAsStream(expectedDocumentLocation));

        final XMLObjectBuilder<SimpleXMLObject> sxoBuilder =
                (XMLObjectBuilder<SimpleXMLObject>) builderFactory.<SimpleXMLObject>ensureBuilder(simpleXMLObjectQName);
        final SimpleXMLObject sxObject = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME);
        sxObject.setNoNamespaceSchemaLocation(expectedValue);

        assertXMLEquals(expectedDocument, sxObject);
    }
    

}
