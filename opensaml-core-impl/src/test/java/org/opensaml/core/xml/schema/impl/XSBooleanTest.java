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
import org.opensaml.core.xml.schema.XSBoolean;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.w3c.dom.Document;

import net.shibboleth.shared.xml.XMLParserException;

/**
 * Unit test for {@link XSBoolean}
 */
public class XSBooleanTest extends XMLObjectBaseTestCase {
    
    private String testDocumentLocation;
    private QName expectedXMLObjectQName;
    private XSBooleanValue expectedValue;
    
    @BeforeMethod
    protected void setUp() throws Exception{
        testDocumentLocation = "/org/opensaml/core/xml/schema/impl/xsBoolean.xml";
        expectedXMLObjectQName = new QName("urn:example.org:foo", "bar", "foo");
        expectedValue = XSBooleanValue.valueOf("true");
    }

    /**
     * Tests Marshalling a boolean type.
     * @throws MarshallingException ...
     * @throws XMLParserException ...
     */
    @Test
    public void testMarshall() throws MarshallingException, XMLParserException{
        XMLObjectBuilder<XSBoolean> xsbBuilder = builderFactory.ensureBuilder(XSBoolean.TYPE_NAME);
        XSBoolean xsBoolean = xsbBuilder.buildObject(expectedXMLObjectQName, XSBoolean.TYPE_NAME);
        xsBoolean.setValue(expectedValue);
        
        Marshaller marshaller = marshallerFactory.ensureMarshaller(xsBoolean);
        marshaller.marshall(xsBoolean);
        
        Document document = parserPool.parse(XSBooleanTest.class.getResourceAsStream(testDocumentLocation));
        assertXMLEquals("Marshalled XSBoolean does not match example document", document, xsBoolean);
    }
    
    /**
     * Tests Unmarshalling a boolean type.
     * 
     * @throws XMLParserException ...
     * @throws UnmarshallingException ... 
     */
    @Test
    public void testUnmarshall() throws XMLParserException, UnmarshallingException{
        Document document = parserPool.parse(XSBooleanTest.class.getResourceAsStream(testDocumentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.ensureUnmarshaller(document.getDocumentElement());
        XSBoolean xsBoolean = (XSBoolean) unmarshaller.unmarshall(document.getDocumentElement());
        
        Assert.assertEquals(xsBoolean.getElementQName(), expectedXMLObjectQName, "Unexpected XSBoolean QName");
        Assert.assertEquals(xsBoolean.getSchemaType(), XSBoolean.TYPE_NAME, "Unexpected XSBoolean schema type");
        Assert.assertEquals(expectedValue, xsBoolean.getValue(), "Unexpected value of XSBoolean");
    }
}
