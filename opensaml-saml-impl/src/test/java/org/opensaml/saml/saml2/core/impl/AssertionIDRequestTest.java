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
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AssertionIDRef;
import org.opensaml.saml.saml2.core.AssertionIDRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 *
 */
@SuppressWarnings({"null", "javadoc"})
public class AssertionIDRequestTest extends RequestTestBase {
    
    private int expectedNumAssertionIDRefs;

    /**
     * Constructor
     *
     */
    public AssertionIDRequestTest() {
        super();
        
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/AssertionIDRequest.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/AssertionIDRequestOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/AssertionIDRequestChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        expectedNumAssertionIDRefs = 3;
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, AssertionIDRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        AssertionIDRequest req = (AssertionIDRequest) buildXMLObject(qname);
        
        super.populateRequiredAttributes(req);
        
        assertXMLEquals(expectedDOM, req);
    }
    
    /**
     * Test marshalling of attribute IDness.
     *
     * @throws MarshallingException
     * @throws XMLParserException
     * */
    @Test
    public void testAttributeIDnessMarshall() throws MarshallingException, XMLParserException {
        final XMLObject target = buildXMLObject(AssertionIDRequest.DEFAULT_ELEMENT_NAME);

        ((AssertionIDRequest)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20P_NS, AssertionIDRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        final AssertionIDRequest req = (AssertionIDRequest) buildXMLObject(qname);
        
        super.populateRequiredAttributes(req);
        super.populateOptionalAttributes(req);
        
        assertXMLEquals(expectedOptionalAttributesDOM, req);
    }


    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final AssertionIDRequestBuilder builder = (AssertionIDRequestBuilder) builderFactory.getBuilder(AssertionIDRequest.DEFAULT_ELEMENT_NAME);
        assert builder!= null;
        final AssertionIDRequest req = builder.buildObject();
        
        super.populateChildElements(req);
        
        QName assertionIDRefQName = new QName(SAMLConstants.SAML20_NS, AssertionIDRef.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        for (int i = 0; i< expectedNumAssertionIDRefs; i++)
            req.getAssertionIDRefs().add((AssertionIDRef) buildXMLObject(assertionIDRefQName));
        
        assertXMLEquals(expectedChildElementsDOM, req);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final AssertionIDRequest req = (AssertionIDRequest) unmarshallElement(singleElementFile);
        
        super.helperTestSingleElementUnmarshall(req);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final AssertionIDRequest req = (AssertionIDRequest) unmarshallElement(singleElementOptionalAttributesFile);
        
        super.helperTestSingleElementOptionalAttributesUnmarshall(req);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final AssertionIDRequest req = (AssertionIDRequest) unmarshallElement(childElementsFile);
        assert req != null;       
        super.helperTestChildElementsUnmarshall(req);
        Assert.assertEquals(req.getAssertionIDRefs().size(), expectedNumAssertionIDRefs, "AssertionIDRef count");
    }
}