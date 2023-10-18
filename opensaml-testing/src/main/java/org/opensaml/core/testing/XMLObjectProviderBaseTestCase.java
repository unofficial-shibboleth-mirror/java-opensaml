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

package org.opensaml.core.testing;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.ElementSupport;
import net.shibboleth.shared.xml.ParserPool;
import net.shibboleth.shared.xml.XMLParserException;

/**
 * Base test case for {@link org.opensaml.core.xml.XMLObject}s in XMLTooling for which we need a full set
 * of object provider tests, i.e marshalling and unmarshalling of single elements; with optional
 * attributes; and with child elements.
 */
@SuppressWarnings("null")
public abstract class XMLObjectProviderBaseTestCase extends XMLObjectBaseTestCase {

    /** Location of file containing a single element with NO optional attributes. */
    protected String singleElementFile;

    /** Location of file containing a single element with all optional attributes. */
    protected String singleElementOptionalAttributesFile;

    /** Location of file containing a single element with some unknown attributes. */
    protected String singleElementUnknownAttributesFile;

    /** Location of file containing a single element with child elements. */
    protected String childElementsFile;
    
    /** Location of file containing some kind of invalid content. */
    protected String invalidFile;

    /** The expected result of a marshalled single element with no optional attributes. */
    protected Document expectedDOM;

    /** The expected result of a marshalled single element with all optional attributes. */
    protected Document expectedOptionalAttributesDOM;

    /** The expected result of a marshalled single element some unknown attributes. */
    protected Document expectedUnknownAttributesDOM;

    /** The expected result of a marshalled single element with child elements. */
    protected Document expectedChildElementsDOM;
    
    /** The result of parsing the invalid file. */
    protected Document invalidDOM;

    /**
     * Init testing fields. 
     * 
     * @throws Exception on error
     */
    @BeforeClass
    protected void initXMLObjectProviderTestingSupprt() throws Exception {
        if (singleElementFile != null) {
            expectedDOM = parserPool.parse(XMLObjectProviderBaseTestCase.class
                    .getResourceAsStream(singleElementFile));
        }

        if (singleElementOptionalAttributesFile != null) {
            expectedOptionalAttributesDOM = parserPool.parse(XMLObjectProviderBaseTestCase.class
                    .getResourceAsStream(singleElementOptionalAttributesFile));
        }

        if (childElementsFile != null) {
            expectedChildElementsDOM = parserPool.parse(XMLObjectProviderBaseTestCase.class
                    .getResourceAsStream(childElementsFile));
        }
        
        if (singleElementUnknownAttributesFile != null) {
            expectedUnknownAttributesDOM = parserPool.parse(XMLObjectProviderBaseTestCase.class
                    .getResourceAsStream(singleElementUnknownAttributesFile));
        }
        
        if (invalidFile != null) {
            invalidDOM = parserPool.parse(XMLObjectProviderBaseTestCase.class
                    .getResourceAsStream(invalidFile));
        }
    }

    /**
     * Tests unmarshalling a document that contains a single element (no children) with no optional attributes.
     */
    public abstract void testSingleElementUnmarshall();

    /**
     * Tests unmarshalling a document that contains a single element (no children) with all that element's optional
     * attributes.
     */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        Assert.assertNull(singleElementOptionalAttributesFile,
                "No testSingleElementOptionalAttributesUnmarshall present");
    }

    /**
     * Tests unmarshalling a document that contains a single element (no children) with all that element's optional
     * attributes.
     */
    @Test
    public void testSingleElementUnknownAttributesUnmarshall() {
        Assert.assertNull(singleElementUnknownAttributesFile,
                "No testSingleElementUnknownAttributesUnmarshall present");
    }

    /**
     * Tests unmarshalling a document that contains a single element with children.
     */
    @Test
    public void testChildElementsUnmarshall() {
        Assert.assertNull(childElementsFile, "No testSingleElementChildElementsUnmarshall present");
    }

    /**
     * Tests marshalling the contents of a single element, with no optional attributes, to a DOM document.
     */
    public abstract void testSingleElementMarshall();

    /**
     * Tests marshalling the contents of a single element, with all optional attributes, to a DOM document.
     */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        Assert.assertNull(expectedOptionalAttributesDOM, "No testSingleElementOptionalAttributesMarshall");
    }

    /**
     * Tests marshalling the contents of a single element, some unknown attributes, to a DOM document.
     */
    @Test
    public void testSingleElementUnknownAttributesMarshall() {
        Assert.assertNull(expectedUnknownAttributesDOM, "No testSingleUnknownAttributesMarshall");
    }

    /**
     * Tests marshalling the contents of a single element with child elements to a DOM document.
     */
    @Test
    public void testChildElementsMarshall() {
        Assert.assertNull(expectedChildElementsDOM, "No testSingleElementChildElementsMarshall");
    }

    /**
     * Test marshalling of attribute IDness.
     * 
     * @param target target object
     * @param idValue ID value
     *
     * @throws MarshallingException
     * @throws XMLParserException
     * */
    public void testAttributeIDnessMarshall(@Nonnull final XMLObject target, final String idValue)
            throws MarshallingException, XMLParserException {
        // Test marshall of newly constructed object
        final Marshaller marshaller = XMLObjectSupport.getMarshaller(target);
        assert marshaller!=null;
        Element origDOM = marshaller.marshall(target);
        Element resolvedDOM = origDOM.getOwnerDocument().getElementById(idValue);
        Assert.assertNotNull(resolvedDOM);
        Assert.assertTrue(origDOM.isSameNode(resolvedDOM));

        // Remarshall existing DOM into new Document
        final ParserPool parserPool =  XMLObjectProviderRegistrySupport.getParserPool();
        assert parserPool!=null;
        Document newDocument = parserPool.newDocument();
        origDOM = marshaller.marshall(target, newDocument);
        resolvedDOM = newDocument.getElementById(idValue);
        Assert.assertNotNull(resolvedDOM);
        Assert.assertTrue(origDOM.isSameNode(resolvedDOM));

        // Remarshall existing DOM as child of new parent Element in new Document
        newDocument = parserPool.newDocument();
        final Element parent = ElementSupport.constructElement(newDocument, "urn:test:foo", "Foo", "foo");
        ElementSupport.setDocumentElement(newDocument, parent);
        origDOM = marshaller.marshall(target, parent);
        resolvedDOM = newDocument.getElementById(idValue);
        Assert.assertNotNull(resolvedDOM);
        Assert.assertTrue(origDOM.isSameNode(resolvedDOM));
    }
    
}