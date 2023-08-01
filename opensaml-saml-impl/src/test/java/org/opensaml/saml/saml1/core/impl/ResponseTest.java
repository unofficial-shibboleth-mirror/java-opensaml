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
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Status;
import org.opensaml.xmlsec.signature.Signature;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.XMLParserException;

/**
 * Test class for org.opensaml.saml.saml1.core.Response
 */
@SuppressWarnings({"null", "javadoc"})
public class ResponseTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    /** Representation of IssueInstant in test file. */
    private final String expectedID;

    /** Representation of IssueInstant in test file. */
    private final Instant expectedIssueInstant;

    /** Representation of InResponseTo in test file. */
    private final String expectedInResponseTo;

    /** Representation of MinorVersion in test file. */
    private final int expectedMinorVersion;

    /** Representation of Recipient in test file. */
    private final String expectedRecipient;

    /**
     * Constructor
     */
    public ResponseTest() {
        expectedID = "ident";
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleResponse.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml1/impl/singleResponseAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml1/impl/ResponseWithChildren.xml";
        expectedIssueInstant = Instant.parse("1970-01-01T00:00:00.100Z");

        expectedInResponseTo="inresponseto";
        expectedMinorVersion=1;
        expectedRecipient="recipient";
        
        qname = Response.DEFAULT_ELEMENT_NAME;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {

        final Response response = (Response) unmarshallElement(singleElementFile);
        assert response != null;

        String id = response.getID();
        Assert.assertNull(id, "ID attribute has value " + id + "expected no value");
       
        Assert.assertNull(response.getIssueInstant(), "IssueInstant attribute has a value of " + response.getIssueInstant() 
                        + ", expected no value");

        Assert.assertEquals(response.getAssertions().size(), 0, "Assertion elements count");

        Status status;
        status = response.getStatus();
        Assert.assertNull(status, "Status element has a value of " + status + ", expected no value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final Response response;
        response = (Response) unmarshallElement(singleElementOptionalAttributesFile);
        assert response != null;

        Assert.assertEquals(response.getID(), expectedID, "ID");
        Assert.assertEquals(response.getIssueInstant(), expectedIssueInstant, "IssueInstant attribute ");

        String string = response.getInResponseTo();
        Assert.assertEquals(string, expectedInResponseTo, "InResponseTo attribute ");

        string = response.getRecipient();
        Assert.assertEquals(string, expectedRecipient, "Recipient attribute ");

        final SAMLVersion version =response.getVersion();
        assert version!=null;
        int i = version.getMinorVersion();
        Assert.assertEquals(i, expectedMinorVersion, "MinorVersion attribute ");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final Response response = (Response) unmarshallElement(childElementsFile);
        assert response != null;

        Assert.assertEquals(response.getAssertions().size(), 1, "No Assertion elements count");

        Status status;
        status = response.getStatus();
        Assert.assertNotNull(status, "No Status element found");
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
        XMLObject target = buildXMLObject(Response.DEFAULT_ELEMENT_NAME);

        ((Response)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        Response response = (Response) buildXMLObject(qname);

        response.setID(expectedID);
        response.setInResponseTo(expectedInResponseTo);
        response.setIssueInstant(expectedIssueInstant);
        response.setRecipient(expectedRecipient);

        assertXMLEquals(expectedOptionalAttributesDOM, response);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        Response response = (Response) buildXMLObject(qname);

        response.getAssertions().add((Assertion) buildXMLObject(Assertion.DEFAULT_ELEMENT_NAME));
        response.setStatus((Status)buildXMLObject(Status.DEFAULT_ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, response);

    }
    
    @Test
    public void testSignatureUnmarshall() {
        final Response response = (Response) unmarshallElement("/org/opensaml/saml/saml1/impl/ResponseWithSignature.xml");
        assert response != null;

        Assert.assertNotNull(response, "Response was null");
        final Signature sig = response.getSignature();
        assert sig != null;
        Assert.assertNotNull(sig.getKeyInfo(), "KeyInfo was null");
    }
    
    @Test
    public void testDOMIDResolutionUnmarshall() {
        final Response response = (Response) unmarshallElement("/org/opensaml/saml/saml1/impl/ResponseWithSignature.xml");
        assert response != null;
        Assert.assertNotNull(response, "Response was null");
        final Signature sig = response.getSignature();
        assert sig != null;
        final Element elem = sig.getDOM();
        assert elem != null;
        final Document document = elem.getOwnerDocument();
        final Element idElem = response.getDOM();
        
        Assert.assertNotNull(document.getElementById(expectedID), "DOM ID resolution returned null");
        assert idElem != null;
        Assert.assertTrue(idElem.isSameNode(document.getElementById(expectedID)), "DOM elements were not equal");
    }

    @Test
    public void testDOMIDResolutionMarshall() throws MarshallingException {
        final Response response = (Response) buildXMLObject(Response.DEFAULT_ELEMENT_NAME);
        response.setID(expectedID);
        response.setStatus((Status) buildXMLObject(Status.DEFAULT_ELEMENT_NAME));
        
        final Marshaller marshaller = marshallerFactory.getMarshaller(response);
        assert marshaller!=null;
        marshaller.marshall(response);
        
        final Status status =response.getStatus();
        assert status != null;
        final Element elem = status.getDOM();
        assert elem != null;
        final Document document = elem.getOwnerDocument();
        final Element idElem = response.getDOM();
        assert idElem != null;
        
        Assert.assertNotNull(document.getElementById(expectedID), "DOM ID resolution returned null");
        Assert.assertTrue(idElem.isSameNode(document.getElementById(expectedID)), "DOM elements were not equal");
    }

}
