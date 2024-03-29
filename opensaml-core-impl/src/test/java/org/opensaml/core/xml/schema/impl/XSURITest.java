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
import org.opensaml.core.xml.schema.XSURI;
import org.w3c.dom.Document;

import net.shibboleth.shared.xml.XMLParserException;

/**
 * Unit test for {@link XSURI}
 */
public class XSURITest extends XMLObjectBaseTestCase {
    
    private String testDocumentLocation;
    private QName expectedXMLObjectQName;
    private String expectedValue;
    
    @BeforeMethod
    protected void setUp() throws Exception{
        testDocumentLocation = "/org/opensaml/core/xml/schema/impl/xsURI.xml";
        expectedXMLObjectQName = new QName("urn:oasis:names:tc:SAML:2.0:assertion", "AttributeValue", "saml");
        expectedValue = "urn:test:foo:bar:baz";
    }

    /**
     * Tests Marshalling a URI type.
     * 
     * @throws MarshallingException ...
     * @throws XMLParserException ...
     */
    @Test
    public void testMarshall() throws MarshallingException, XMLParserException{
        XMLObjectBuilder<XSURI> uriBuilder = builderFactory.ensureBuilder(XSURI.TYPE_NAME);
        XSURI xsURI = uriBuilder.buildObject(expectedXMLObjectQName, XSURI.TYPE_NAME);
        xsURI.setURI(expectedValue);
        
        Marshaller marshaller = marshallerFactory.ensureMarshaller(xsURI);
        marshaller.marshall(xsURI);
        
        Document document = parserPool.parse(XSURITest.class.getResourceAsStream(testDocumentLocation));
        assertXMLEquals("Marshalled XSURI does not match example document", document, xsURI);
    }
    
    /**
     * Tests Marshalling a URI type.
     * 
     * @throws XMLParserException ...
     * @throws UnmarshallingException ...
     */
    @Test
    public void testUnmarshall() throws XMLParserException, UnmarshallingException{
        Document document = parserPool.parse(XSURITest.class.getResourceAsStream(testDocumentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.ensureUnmarshaller(document.getDocumentElement());
        XSURI xsURI = (XSURI) unmarshaller.unmarshall(document.getDocumentElement());
        
        Assert.assertEquals(xsURI.getElementQName(), expectedXMLObjectQName, "Unexpected XSURI QName");
        Assert.assertEquals(xsURI.getSchemaType(), XSURI.TYPE_NAME, "Unexpected XSURI schema type");
        Assert.assertEquals(expectedValue, xsURI.getURI(), "Unexpected value of XSURI");
    }
}
