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

package org.opensaml.core.xml.schema.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSBase64Binary;
import org.w3c.dom.Document;

import net.shibboleth.shared.xml.XMLParserException;

/**
 * Unit test for {@link XSBase64Binary}
 */
public class XSBase64BinaryTest extends XMLObjectBaseTestCase {
    
    private String testDocumentLocation;
    private QName expectedXMLObjectQName;
    private String expectedValue;
    
    @BeforeMethod
    protected void setUp() throws Exception{
        testDocumentLocation = "/org/opensaml/core/xml/schema/impl/xsBase64Binary.xml";
        expectedXMLObjectQName = new QName("urn:example.org:foo", "bar", "foo");
        expectedValue = "abcdABCDE===";
    }

    /**
     * Tests Marshalling a base64Binary type.
     * @throws MarshallingException ...
     * @throws XMLParserException ...
     */
    @Test
    public void testMarshall() throws MarshallingException, XMLParserException{
        XMLObjectBuilder<XSBase64Binary> xsb64bBuilder = builderFactory.ensureBuilder(XSBase64Binary.TYPE_NAME);
        XSBase64Binary xsb64b = xsb64bBuilder.buildObject(expectedXMLObjectQName, XSBase64Binary.TYPE_NAME);
        xsb64b.setValue(expectedValue);
        
        Marshaller marshaller = marshallerFactory.ensureMarshaller(xsb64b);
        marshaller.marshall(xsb64b);
        
        Document document = parserPool.parse(XSBase64BinaryTest.class.getResourceAsStream(testDocumentLocation));
        assertXMLEquals("Marshalled XSBase64Binary does not match example document", document, xsb64b);
    }
    
    /**
     * Tests Marshalling a base64Binary type.
     * 
     * @throws XMLParserException ...
     * @throws UnmarshallingException ... 
     */
    @Test
    public void testUnmarshall() throws XMLParserException, UnmarshallingException{
        Document document = parserPool.parse(XSBase64BinaryTest.class.getResourceAsStream(testDocumentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.ensureUnmarshaller(document.getDocumentElement());
        XSBase64Binary xsb64b = (XSBase64Binary) unmarshaller.unmarshall(document.getDocumentElement());
        
        Assert.assertEquals(xsb64b.getElementQName(), expectedXMLObjectQName, "Unexpected XSBase64Binary QName");
        Assert.assertEquals(xsb64b.getSchemaType(), XSBase64Binary.TYPE_NAME, "Unexpected XSBase64Binary schema type");
        Assert.assertEquals(expectedValue, xsb64b.getValue(), "Unexpected value of XSBase64Binary");
    }
}