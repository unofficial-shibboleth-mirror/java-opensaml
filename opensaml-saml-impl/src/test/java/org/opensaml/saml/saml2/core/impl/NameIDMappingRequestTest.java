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
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDMappingRequest;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 *
 */
@SuppressWarnings({"null", "javadoc"})
public class NameIDMappingRequestTest extends RequestTestBase {

    /**
     * Constructor
     *
     */
    public NameIDMappingRequestTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/NameIDMappingRequest.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/NameIDMappingRequestOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/NameIDMappingRequestChildElements.xml";
    }    
    
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, NameIDMappingRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        NameIDMappingRequest req = (NameIDMappingRequest) buildXMLObject(qname);
        
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
        XMLObject target = buildXMLObject(NameIDMappingRequest.DEFAULT_ELEMENT_NAME);

        ((NameIDMappingRequest)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, NameIDMappingRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        NameIDMappingRequest req = (NameIDMappingRequest) buildXMLObject(qname);
        
        super.populateRequiredAttributes(req);
        super.populateOptionalAttributes(req);
        
        assertXMLEquals(expectedOptionalAttributesDOM, req);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, NameIDMappingRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        NameIDMappingRequest req = (NameIDMappingRequest) buildXMLObject(qname);
        
        super.populateChildElements(req);
        
        QName nameIDQName = new QName(SAMLConstants.SAML20_NS, NameID.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        req.setNameID((NameID) buildXMLObject(nameIDQName));
        
        QName nameIDPolicyQName = new QName(SAMLConstants.SAML20P_NS, NameIDPolicy.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        req.setNameIDPolicy((NameIDPolicy) buildXMLObject(nameIDPolicyQName));
        
        assertXMLEquals(expectedChildElementsDOM, req);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        NameIDMappingRequest req = (NameIDMappingRequest) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(req, "NameIDMappingRequest was null");
        super.helperTestSingleElementUnmarshall(req);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        NameIDMappingRequest req = (NameIDMappingRequest) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertNotNull(req, "NameIDMappingRequest was null");
        super.helperTestSingleElementOptionalAttributesUnmarshall(req);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        NameIDMappingRequest req = (NameIDMappingRequest) unmarshallElement(childElementsFile);
        assert req!=null;
        Assert.assertNotNull(req.getNameID(), "Identifier was null");
        Assert.assertNotNull(req.getNameIDPolicy(), "NameIDPolicy was null");
        super.helperTestChildElementsUnmarshall(req);
    }

}
