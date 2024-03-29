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

import java.time.Instant;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.SessionIndex;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 *
 */
@SuppressWarnings({"null", "javadoc"})
public class LogoutRequestTest extends RequestTestBase {
    
    /** Expected Reason attribute value */
    private String expectedReason;
    
    /** Expected NotOnOrAfter attribute value */
    private Instant expectedNotOnOrAfter;
    
    /** Expected number of SessionIndex child elements */
    private int expectedNumSessionIndexes;

    /**
     * Constructor
     *
     */
    public LogoutRequestTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/LogoutRequest.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/LogoutRequestOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/LogoutRequestChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        expectedReason = "urn:string:reason";
        expectedNotOnOrAfter = Instant.parse("2006-02-21T20:45:00.000Z");
        expectedNumSessionIndexes = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, LogoutRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        LogoutRequest req = (LogoutRequest) buildXMLObject(qname);
        
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
        XMLObject target = buildXMLObject(LogoutRequest.DEFAULT_ELEMENT_NAME);

        ((LogoutRequest)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, LogoutRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        LogoutRequest req = (LogoutRequest) buildXMLObject(qname);
        
        super.populateRequiredAttributes(req);
        super.populateOptionalAttributes(req);
        req.setReason(expectedReason);
        req.setNotOnOrAfter(expectedNotOnOrAfter);
        
        assertXMLEquals(expectedOptionalAttributesDOM, req);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, LogoutRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        LogoutRequest req = (LogoutRequest) buildXMLObject(qname);
        
        super.populateChildElements(req);
        
        QName nameIDQName = new QName(SAMLConstants.SAML20_NS, NameID.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        req.setNameID((NameID) buildXMLObject(nameIDQName));
        
        QName sessionIndexQName = new QName(SAMLConstants.SAML20P_NS, SessionIndex.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        for (int i=0; i<expectedNumSessionIndexes; i++){
            req.getSessionIndexes().add((SessionIndex) buildXMLObject(sessionIndexQName));
        }
        
        assertXMLEquals(expectedChildElementsDOM, req);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        LogoutRequest req = (LogoutRequest) unmarshallElement(singleElementFile);
        assert req!=null;
        Assert.assertNull(req.getReason(), "Reason was not null");
        Assert.assertNull(req.getNotOnOrAfter(), "NotOnOrAfter was not null");
        super.helperTestSingleElementUnmarshall(req);
    }
 
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        LogoutRequest req = (LogoutRequest) unmarshallElement(singleElementOptionalAttributesFile);
        assert req!=null;
        
        Assert.assertEquals(req.getReason(), expectedReason, "Unmarshalled Reason attribute was not the expectecd value");
        Assert.assertEquals(expectedNotOnOrAfter.compareTo(req.getNotOnOrAfter()), 0, "Unmarshalled NotOnOrAfter attribute was not the expectecd value");
        super.helperTestSingleElementOptionalAttributesUnmarshall(req);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        LogoutRequest req = (LogoutRequest) unmarshallElement(childElementsFile);
        assert req!=null;
        
        Assert.assertNotNull(req.getNameID(), "Identifier was null");
        Assert.assertEquals(req.getSessionIndexes().size(), expectedNumSessionIndexes, "Number of unmarshalled SessionIndexes was not the expected value");
        super.helperTestChildElementsUnmarshall(req);
    }
    
}
