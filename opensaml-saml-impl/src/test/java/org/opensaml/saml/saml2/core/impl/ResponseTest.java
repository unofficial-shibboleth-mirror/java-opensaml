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
package org.opensaml.saml.saml2.core.impl;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.ResponseImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class ResponseTest extends StatusResponseTestBase {
    
    /** Expected number of Assertion child elements. */
    private int expectedNumAssertions;
    
    /** Expected number of EncryptedAssertion child elements. */
    private int expectedNumEncryptedAssertions;


    /**
     * Constructor.
     *
     */
    public ResponseTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/Response.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/ResponseOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/ResponseChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        expectedNumAssertions = 3;
        expectedNumEncryptedAssertions = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final QName qname = 
            new QName(SAMLConstants.SAML20P_NS, Response.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        final Response resp = (Response) buildXMLObject(qname);
        
        super.populateRequiredAttributes(resp);
        
        assertXMLEquals(expectedDOM, resp);
    }
    
    /**
     * Test marshalling of attribute IDness.
     *
     * @throws MarshallingException
     * @throws XMLParserException
     * */
    @Test
    public void testAttributeIDnessMarshall() throws MarshallingException, XMLParserException {
        final XMLObject target = buildXMLObject(Response.DEFAULT_ELEMENT_NAME);

        ((Response)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final QName qname = 
            new QName(SAMLConstants.SAML20P_NS, Response.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        final Response resp = (Response) buildXMLObject(qname);
        
        super.populateRequiredAttributes(resp);
        super.populateOptionalAttributes(resp);
        
        assertXMLEquals(expectedOptionalAttributesDOM, resp);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final Response resp = (Response) buildXMLObject(Response.DEFAULT_ELEMENT_NAME);
        
        super.populateChildElements(resp);
        
        resp.getAssertions().add((Assertion) buildXMLObject(Assertion.DEFAULT_ELEMENT_NAME));
        resp.getAssertions().add((Assertion) buildXMLObject(Assertion.DEFAULT_ELEMENT_NAME));
        resp.getEncryptedAssertions().add((EncryptedAssertion) buildXMLObject(EncryptedAssertion.DEFAULT_ELEMENT_NAME));
        resp.getEncryptedAssertions().add((EncryptedAssertion) buildXMLObject(EncryptedAssertion.DEFAULT_ELEMENT_NAME));
        resp.getAssertions().add((Assertion) buildXMLObject(Assertion.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, resp);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final Response resp = (Response) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(resp, "Response was null");
        super.helperTestSingleElementUnmarshall(resp);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final Response resp = (Response) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertNotNull(resp, "Response was null");
        super.helperTestSingleElementOptionalAttributesUnmarshall(resp);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final Response resp = (Response) unmarshallElement(childElementsFile);
        assert resp!=null;
        Assert.assertEquals(resp.getAssertions().size(), expectedNumAssertions, "Assertion count");
        Assert.assertEquals(resp.getEncryptedAssertions().size(), expectedNumEncryptedAssertions, "EncryptedAssertion count");
        super.helperTestChildElementsUnmarshall(resp);
    }
    
    @Test(expectedExceptions=UnmarshallingException.class)
    public void testBadSAMLVersion() throws XMLParserException, UnmarshallingException {
        unmarshallElement("/org/opensaml/saml/saml2/core/impl/ResponseBadSAMLVersion.xml", true);
    }

}
