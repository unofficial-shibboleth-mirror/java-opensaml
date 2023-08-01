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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.xml.ParserPool;
import net.shibboleth.shared.xml.QNameSupport;
import net.shibboleth.shared.xml.SerializeSupport;
import net.shibboleth.shared.xml.XMLParserException;

/**
 * Base test case class for tests that operate on XMLObjects.
 */
public abstract class XMLObjectBaseTestCase extends OpenSAMLInitBaseTestCase {

    /** Logger */
    @Nonnull private final Logger log = LoggerFactory.getLogger(XMLObjectBaseTestCase.class);

    /** Parser pool */
    protected static ParserPool parserPool;

    /** XMLObject builder factory */
    protected static XMLObjectBuilderFactory builderFactory;

    /** XMLObject marshaller factory */
    protected static MarshallerFactory marshallerFactory;

    /** XMLObject unmarshaller factory */
    protected static UnmarshallerFactory unmarshallerFactory;

    /** QName for SimpleXMLObject */
    @Nonnull protected static QName simpleXMLObjectQName =
            new QName(SimpleXMLObject.NAMESPACE, SimpleXMLObject.LOCAL_NAME);

    @BeforeClass
	protected void initXMLObjectSupport() throws Exception {
        try {
            parserPool = XMLObjectProviderRegistrySupport.getParserPool();
            builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
            marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
            unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
        } catch (Exception e) {
            log.error("Can not initialize XMLObjectBaseTestCase: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Asserts a given XMLObject is equal to an expected DOM. The XMLObject is marshalled and the resulting DOM object
     * is compared against the expected DOM object for equality.
     * 
     * @param expectedDOM the expected DOM
     * @param xmlObject the XMLObject to be marshalled and compared against the expected DOM
     */
    protected void assertXMLEquals(Document expectedDOM, @Nonnull XMLObject xmlObject) {
        assertXMLEquals("Marshalled DOM was not the same as the expected DOM", expectedDOM, xmlObject);
    }

    /**
     * Asserts a given XMLObject is equal to an expected DOM. The XMLObject is marshalled and the resulting DOM object
     * is compared against the expected DOM object for equality.
     * 
     * @param failMessage the message to display if the DOMs are not equal
     * @param expectedDOM the expected DOM
     * @param xmlObject the XMLObject to be marshalled and compared against the expected DOM
     */
    protected void assertXMLEquals(String failMessage, Document expectedDOM, XMLObject xmlObject) {
        assert xmlObject!=null;
        final Marshaller marshaller = marshallerFactory.getMarshaller(xmlObject);
        if (marshaller == null) {
            Assert.fail("Unable to locate marshaller for " + xmlObject.getElementQName()
                    + " can not perform equality check assertion");
        }
        assert marshaller!= null;

        try {
            final Element generatedDOM = marshaller.marshall(xmlObject, parserPool.newDocument());
            if (log.isDebugEnabled()) {
                log.debug("Marshalled DOM was " + SerializeSupport.nodeToString(generatedDOM));
            }
            final Diff diff = DiffBuilder.compare(expectedDOM).withTest(generatedDOM.getOwnerDocument())
                    .ignoreWhitespace()
                    .checkForIdentical()
                    .build();
            Assert.assertFalse(diff.hasDifferences(), failMessage);
        } catch (final Exception e) {
            Assert.fail("Marshalling failed with the following error: " + e);
        }
    }

    /**
     * Builds the requested XMLObject.
     * 
     * @param name name of the XMLObject
     * @param <T> type of the result
     * 
     * @return the built XMLObject
     */
    @Nonnull protected <T extends XMLObject> T buildXMLObject(@Nonnull final QName name) {
        final XMLObjectBuilder<T> builder = getBuilder(name);
        if (builder == null) {
            Assert.fail("no builder registered for: " + name);
        }
        final T wsObj = builder.buildObject(name);
        Assert.assertNotNull(wsObj);
        return wsObj;
    }
    
    /**
     * Unmarshalls an element file into its XMLObject.
     * 
     * @param elementFile the element file to unmarshall
     * @param <T> expected type
     * 
     * @return the XMLObject from the file
     */
    @Nullable protected <T extends XMLObject> T unmarshallElement(@Nonnull final String elementFile) {
        try {
            return unmarshallElement(elementFile, false);
        } catch (XMLParserException | UnmarshallingException e) {
            // Won't happen due to flag being passed
            Assert.fail("Unable to parse or unmarshall element file " + elementFile + ": " + e);
            return null;
        }
    }
    
    /**
     * Unmarshalls an element file into its XMLObject.
     * 
     * @param elementFile the element file to unmarshall
     * @param propagateErrors if true, checked exceptions will be thrown, if false then they cause assertion of test failure
     * @param <T> expected type
     * 
     * @return the XMLObject from the file
     * 
     * @throws XMLParserException ...
     * @throws UnmarshallingException ...
     */
    @Nullable protected <T extends XMLObject> T unmarshallElement(@Nonnull final String elementFile, boolean propagateErrors) 
            throws XMLParserException, UnmarshallingException {
        try {
            final Document doc = parseXMLDocument(elementFile);
            final Element element = doc.getDocumentElement();
            final Unmarshaller unmarshaller = getUnmarshaller(element);
            final T object = (T) unmarshaller.unmarshall(element);
            Assert.assertNotNull(object);
            return object;
        } catch (final XMLParserException e) {
            if (propagateErrors) {
                throw e;
            }
            Assert.fail("Unable to parse element file " + elementFile);
        } catch (final UnmarshallingException e) {
            if (propagateErrors) {
                throw e;
            }
            Assert.fail("Unmarshalling failed when parsing element file " + elementFile + ": " + e);
        }

        return null;
    }
    
    /**
     * For convenience when testing, pretty-print the specified DOM node to a file, or to 
     * the console if filename is null.
     * 
     * @param node node to print
     * @param filename name of file to print to
     */
    protected void printXML(@Nonnull final Node node, @Nonnull final String filename) {
        try {
            SerializeSupport.writeNode(node, new FileOutputStream(new File(filename)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * For convenience when testing, pretty-print the specified XMLObject to a file, or to 
     * the console if filename is null.
     * 
     * @param xmlObject {@link XMLObject} to print
     * @param filename name of file to print to
     */
    protected void printXML(@Nonnull final XMLObject xmlObject, @Nonnull final String filename) {
        Element elem = null;
        try {
            final Marshaller marshaller = marshallerFactory.getMarshaller(xmlObject);
            assert marshaller!= null;
            elem = marshaller.marshall(xmlObject);
        } catch (MarshallingException e) {
            e.printStackTrace();
        }
        assert elem != null;
        printXML(elem, filename);
    }

    /**
     * Lookup the XMLObjectBuilder for a QName.
     * 
     * @param qname the QName for which to find the builder
     * @param <T> type of result for the {@link XMLObjectBuilder}
     * 
     * @return the XMLObjectBuilder
     */
    @Nonnull protected <T extends XMLObject> XMLObjectBuilder<T> getBuilder(@Nonnull final QName qname) {
        return builderFactory.ensureBuilder(qname);
    }

    /**
     * Lookup the marshaller for a QName
     * 
     * @param qname the QName for which to find the marshaller
     * @return the marshaller
     */
    @Nonnull protected Marshaller getMarshaller(@Nonnull final QName qname) {
        return marshallerFactory.ensureMarshaller(qname);
    }

    /**
     * Lookup the marshaller for an XMLObject.
     * 
     * @param xmlObject the XMLObject for which to find the marshaller
     * @return the marshaller
     */
    @Nonnull protected Marshaller getMarshaller(@Nonnull final XMLObject xmlObject) {
        return marshallerFactory.ensureMarshaller(xmlObject);
    }

    /**
     * Lookup the unmarshaller for a QName.
     * 
     * @param qname the QName for which to find the unmarshaller
     * @return the unmarshaller
     */
    @Nonnull protected Unmarshaller getUnmarshaller(@Nonnull final QName qname) {
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(qname);
        if (unmarshaller == null) {
            Assert.fail("no unmarshaller registered for " + qname);
        }
        assert unmarshaller!=null;
        return unmarshaller;
    }

    /**
     * Lookup the unmarshaller for an XMLObject.
     * 
     * @param xmlObject the XMLObject for which to find the unmarshaller
     * @return the unmarshaller
     */
    @Nonnull protected Unmarshaller getUnmarshaller(@Nonnull final XMLObject xmlObject) {
        return getUnmarshaller(xmlObject.getElementQName());
    }

    /**
     * Lookup the unmarshaller for a DOM Element.
     * 
     * @param element the Element for which to find the unmarshaller
     * @return the unmarshaller
     */
    @Nonnull protected Unmarshaller getUnmarshaller(@Nonnull final Element element) {
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
        if (unmarshaller == null) {
            Assert.fail("no unmarshaller registered for " + QNameSupport.getNodeQName(element));
        }
        assert unmarshaller!=null;
        return unmarshaller;
    }

    /**
     * Parse an XML file as a classpath resource.
     * 
     * @param xmlFilename the file to parse 
     * @return the parsed Document
     * @throws XMLParserException if parsing did not succeed
     */
    @Nonnull protected Document parseXMLDocument(@Nonnull final String xmlFilename) throws XMLParserException {
        final InputStream is = getClass().getResourceAsStream(xmlFilename);
        assert is != null;
        Document doc = parserPool.parse(is);
        return doc;
    }
    
}