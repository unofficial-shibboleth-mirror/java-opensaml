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

import java.time.Instant;

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.AssertionArtifact;
import org.opensaml.saml.saml1.core.AssertionIDReference;
import org.opensaml.saml.saml1.core.AttributeQuery;
import org.opensaml.saml.saml1.core.Query;
import org.opensaml.saml.saml1.core.Request;
import org.opensaml.xmlsec.signature.Signature;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.XMLParserException;

/**
 * Test in and around the {@link org.opensaml.saml.saml1.core.Request} interface
 */
@SuppressWarnings({"null", "javadoc"})
public class RequestTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    private final String expectedID;
    
    private final Instant expectedIssueInstant;

    private final int expectedMinorVersion;
    
    public RequestTest() {
        expectedID = "ident";
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleRequest.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml1/impl/singleRequestAttributes.xml";
        expectedIssueInstant = Instant.parse("1970-01-01T00:00:00.100Z");
        expectedMinorVersion = 1;
        qname = Request.DEFAULT_ELEMENT_NAME;
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final Request request = (Request) unmarshallElement(singleElementFile);
        assert request != null;
        String id = request.getID();
        Assert.assertNull(id, "ID attribute has value " + id + "expected no value");
        
        Instant date = request.getIssueInstant();
        Assert.assertNull(date, "IssueInstant attribute has a value of " + date + ", expected no value");

        Assert.assertNull(request.getQuery(), "Query has value");
        Assert.assertEquals(request.getAssertionArtifacts().size(), 0, "AssertionArtifact present");
        Assert.assertEquals(request.getAssertionIDReferences().size(), 0, "AssertionIDReferences present");
        Assert.assertNull(request.getIssueInstant(), "IssueInstance has value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final Request request = (Request) unmarshallElement(singleElementOptionalAttributesFile);
        assert request != null;
        Assert.assertEquals(request.getID(), expectedID, "ID");
        final SAMLVersion version = request.getVersion();
        assert version!=null;
        Assert.assertEquals(version.getMinorVersion(), expectedMinorVersion, "MinorVersion");
        Assert.assertEquals(request.getIssueInstant(), expectedIssueInstant, "IssueInstant");
        
    }
    
    /**
     * Test a few Requests with children 
     */
    @Test
    public void testSingleElementChildrenUnmarshall() {
        Request request; 
        
        request = (Request) unmarshallElement("/org/opensaml/saml/saml1/impl/RequestWithAssertionArtifact.xml");
        assert request != null;
        Assert.assertNull(request.getQuery(), "Query is not null");
        Assert.assertEquals(request.getAssertionIDReferences().size(), 0, "AssertionId count");
        Assert.assertEquals(request.getAssertionArtifacts().size(), 2, "AssertionArtifact count");
        
        request = (Request) unmarshallElement("/org/opensaml/saml/saml1/impl/RequestWithQuery.xml");
        assert request != null;
        Assert.assertNotNull(request.getQuery(), "Query is null");
        Assert.assertEquals(request.getAssertionIDReferences().size(), 0, "AssertionId count");
        Assert.assertEquals(request.getAssertionArtifacts().size(), 0, "AssertionArtifact count");
        
        request = (Request) unmarshallElement("/org/opensaml/saml/saml1/impl/RequestWithAssertionIDReference.xml");
        assert request != null;
        Assert.assertNull(request.getQuery(), "Query is not null");
        Assert.assertNotNull(request.getAssertionIDReferences(), "AssertionId");
        Assert.assertEquals(request.getAssertionIDReferences().size(), 3, "AssertionId count");
        Assert.assertEquals(request.getAssertionArtifacts().size(), 0, "AssertionArtifact count");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /**
     * Test marshalling of attribute IDness.
     *
     * @throws MarshallingException
     * @throws XMLParserException
     * */
    @Test
    public void testAttributeIDnessMarshall() throws MarshallingException, XMLParserException {
        XMLObject target = buildXMLObject(Request.DEFAULT_ELEMENT_NAME);

        ((Request)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        
        Request request = (Request) buildXMLObject(qname);

        request.setID(expectedID);
        request.setIssueInstant(expectedIssueInstant);
        assertXMLEquals(expectedOptionalAttributesDOM, request);
    }

    /**
     * Test a few Requests with children 
     */
    @Test
    public void testSingleElementChildrenMarshall() {
        QName oqname;
        Request request; 
        Document dom;
                
        
        try {
            dom = parserPool.parse(this.getClass().getResourceAsStream("/org/opensaml/saml/saml1/impl/RequestWithAssertionArtifact.xml")); request = (Request) buildXMLObject(qname); 
            oqname = AssertionArtifact.DEFAULT_ELEMENT_NAME;
            request.getAssertionArtifacts().add((AssertionArtifact) buildXMLObject(oqname));
            request.getAssertionArtifacts().add((AssertionArtifact) buildXMLObject(oqname));
            assertXMLEquals(dom, request);
          
            dom = parserPool.parse(this.getClass().getResourceAsStream("/org/opensaml/saml/saml1/impl/RequestWithAssertionIDReference.xml"));
            request = (Request) buildXMLObject(qname); 
            oqname = AssertionIDReference.DEFAULT_ELEMENT_NAME;
            request.getAssertionIDReferences().add((AssertionIDReference) buildXMLObject(oqname));
            request.getAssertionIDReferences().add((AssertionIDReference) buildXMLObject(oqname));
            request.getAssertionIDReferences().add((AssertionIDReference) buildXMLObject(oqname));
            assertXMLEquals(dom, request);

            dom = parserPool.parse(this.getClass().getResourceAsStream("/org/opensaml/saml/saml1/impl/RequestWithQuery.xml"));
            request = (Request) buildXMLObject(qname); 
            oqname = AttributeQuery.DEFAULT_ELEMENT_NAME;
            request.setQuery((AttributeQuery) buildXMLObject(oqname));
            assertXMLEquals(dom, request);

        } catch (XMLParserException e) {
            Assert.fail(e.toString());
        }
    }
    
    @Test
    public void testSignatureUnmarshall() {
        final Request request = (Request) unmarshallElement("/org/opensaml/saml/saml1/impl/RequestWithSignature.xml");
        assert request != null;
        final Signature sig = request.getSignature();
        assert sig != null;
        Assert.assertNotNull(sig.getKeyInfo(), "KeyInfo was null");
    }
    
    @Test
    public void testDOMIDResolutionUnmarshall() {
        final Request request = (Request) unmarshallElement("/org/opensaml/saml/saml1/impl/RequestWithSignature.xml");
        assert request != null;
        assert request != null;
        final Signature sig = request.getSignature();
        assert sig != null;
        final Element elem = sig.getDOM();
        assert elem != null;
        final Document document = elem.getOwnerDocument();
        final Element idElem = request.getDOM();
        assert idElem != null;
        Assert.assertNotNull(document.getElementById(expectedID), "DOM ID resolution returned null");
        Assert.assertTrue(idElem.isSameNode(document.getElementById(expectedID)), "DOM elements were not equal");
    }

    @Test
    public void testDOMIDResolutionMarshall() throws MarshallingException {
        final Request request = (Request) buildXMLObject(Request.DEFAULT_ELEMENT_NAME);
        assert request != null;
        request.setID(expectedID);
        request.setQuery((AttributeQuery) buildXMLObject(AttributeQuery.DEFAULT_ELEMENT_NAME));
        
        final Marshaller marshaller = marshallerFactory.getMarshaller(request);
        assert marshaller!=null;
        marshaller.marshall(request);
        
        final Query query = request.getQuery();
        assert query != null;
        final Element elem = query.getDOM();
        assert elem != null;
        final Document document = elem.getOwnerDocument();
        final Element idElem = request.getDOM();
        assert idElem != null;
        
        Assert.assertNotNull(document.getElementById(expectedID), "DOM ID resolution returned null");
        Assert.assertTrue(idElem.isSameNode(document.getElementById(expectedID)), "DOM elements were not equal");
    }

}
