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
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 *
 */
@SuppressWarnings({"null", "javadoc"})
public class LogoutResponseTest extends StatusResponseTestBase {

    /**
     * Constructor
     *
     */
    public LogoutResponseTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/LogoutResponse.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/LogoutResponseOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/LogoutResponseChildElements.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20P_NS, LogoutResponse.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        LogoutResponse resp = (LogoutResponse) buildXMLObject(qname);
        
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
        final XMLObject target = buildXMLObject(LogoutResponse.DEFAULT_ELEMENT_NAME);

        ((LogoutResponse)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20P_NS, LogoutResponse.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        final LogoutResponse resp = (LogoutResponse) buildXMLObject(qname);
        
        super.populateRequiredAttributes(resp);
        super.populateOptionalAttributes(resp);
        
        assertXMLEquals(expectedOptionalAttributesDOM, resp);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20P_NS, LogoutResponse.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        final LogoutResponse resp = (LogoutResponse) buildXMLObject(qname);
        
        super.populateChildElements(resp);
        
        assertXMLEquals(expectedChildElementsDOM, resp);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final LogoutResponse resp = (LogoutResponse) unmarshallElement(singleElementFile);
        
        super.helperTestSingleElementUnmarshall(resp);
    }
 
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final LogoutResponse resp = (LogoutResponse) unmarshallElement(singleElementOptionalAttributesFile);

        super.helperTestSingleElementOptionalAttributesUnmarshall(resp);
    }
 
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final LogoutResponse resp = (LogoutResponse) unmarshallElement(childElementsFile);
        
        super.helperTestChildElementsUnmarshall(resp);
    }
    
}
