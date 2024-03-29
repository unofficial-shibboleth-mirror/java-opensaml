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
import org.opensaml.saml.saml2.core.AuthnQuery;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 *
 */
@SuppressWarnings({"null", "javadoc"})
public class AuthnQueryTest extends SubjectQueryTestBase {
    
    /** Expected SessionIndex attribute value */
    private String expectedSessionIndex;

    /**
     * Constructor
     *
     */
    public AuthnQueryTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/AuthnQuery.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/AuthnQueryOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/AuthnQueryChildElements.xml";
    }
    

    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        expectedSessionIndex = "session12345";
    }



    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20P_NS, AuthnQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        final AuthnQuery query = (AuthnQuery) buildXMLObject(qname);
        
        super.populateRequiredAttributes(query);
        
        assertXMLEquals(expectedDOM, query);
    }
    
    /**
     * Test marshalling of attribute IDness.
     *
     * @throws MarshallingException
     * @throws XMLParserException
     * */
    @Test
    public void testAttributeIDnessMarshall() throws MarshallingException, XMLParserException {
        final XMLObject target = buildXMLObject(AuthnQuery.DEFAULT_ELEMENT_NAME);

        ((AuthnQuery)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20P_NS, AuthnQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        final AuthnQuery query = (AuthnQuery) buildXMLObject(qname);
        
        super.populateRequiredAttributes(query);
        super.populateOptionalAttributes(query);
        query.setSessionIndex(expectedSessionIndex);
        
        assertXMLEquals(expectedOptionalAttributesDOM, query);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20P_NS, AuthnQuery.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        final AuthnQuery query = (AuthnQuery) buildXMLObject(qname);
        
        super.populateChildElements(query);
        
        final QName requestedAuthnContextQName = new QName(SAMLConstants.SAML20P_NS, RequestedAuthnContext.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        query.setRequestedAuthnContext((RequestedAuthnContext) buildXMLObject(requestedAuthnContextQName));
        
        assertXMLEquals(expectedChildElementsDOM, query);
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final  AuthnQuery query = (AuthnQuery) unmarshallElement(singleElementFile);
        assert query!=null;
        Assert.assertNotNull(query, "AuthnQuery");
        Assert.assertNull(query.getSessionIndex(), "SessionIndex");
        super.helperTestSingleElementUnmarshall(query);

    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final AuthnQuery query = (AuthnQuery) unmarshallElement(singleElementOptionalAttributesFile);
        assert query!=null;
        super.helperTestSingleElementOptionalAttributesUnmarshall(query);
        Assert.assertEquals(query.getSessionIndex(), expectedSessionIndex, "Unmarshalled SessionIndex was not the expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final AuthnQuery query = (AuthnQuery) unmarshallElement(childElementsFile);
        assert query!=null;
        super.helperTestChildElementsUnmarshall(query);
        Assert.assertNotNull(query.getRequestedAuthnContext(), "RequestedAuthnContext");
    }
}