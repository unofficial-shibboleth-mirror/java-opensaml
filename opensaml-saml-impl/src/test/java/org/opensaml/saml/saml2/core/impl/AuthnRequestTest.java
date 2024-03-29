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
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.Scoping;
import org.opensaml.saml.saml2.core.Subject;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 * Unit test for {@link AuthnRequest}.
 */
@SuppressWarnings({"null", "javadoc"})
public class AuthnRequestTest extends RequestTestBase {
    
            
        /** Expected ForceAuthn attribute */    
        private XSBooleanValue expectedForceAuthn;
        
        /** Expected IsPassive attribute */    
        private XSBooleanValue expectedIsPassive;
        
        /** Expected ProtocolBinding attribute */    
        private String expectedProtocolBinding;
        
        /** Expected AssertionConsumerServiceIndex attribute */    
        private Integer expectedAssertionConsumerServiceIndex;
        
        /** Expected AssertionConsumerServiceURL attribute */    
        private String expectedAssertionConsumerServiceURL;
        
        /** Expected AttributeConsumingServiceIndex attribute */    
        private Integer expectedAttributeConsumingServiceIndex;
        
        /** Expected ProviderName attribute */    
        private String expectedProviderName;

    /**
     * Constructor
     *
     */
    public AuthnRequestTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/AuthnRequest.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/AuthnRequestOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/AuthnRequestChildElements.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        
        expectedForceAuthn = new XSBooleanValue(Boolean.TRUE, false);
        expectedIsPassive = new XSBooleanValue(Boolean.TRUE, false);
        expectedProtocolBinding = "urn:string:protocol-binding";
        expectedAssertionConsumerServiceIndex = Integer.valueOf(3);
        expectedAssertionConsumerServiceURL = "http://sp.example.org/acs";
        expectedAttributeConsumingServiceIndex = Integer.valueOf(2);
        expectedProviderName = "Example Org";
    }



    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, AuthnRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        AuthnRequest req = (AuthnRequest) buildXMLObject(qname);
        
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
        final XMLObject target = buildXMLObject(AuthnRequest.DEFAULT_ELEMENT_NAME);

        ((AuthnRequest)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20P_NS, AuthnRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        final AuthnRequest req = (AuthnRequest) buildXMLObject(qname);
        
        super.populateRequiredAttributes(req);
        super.populateOptionalAttributes(req);
        
        req.setForceAuthn(expectedForceAuthn);
        req.setIsPassive(expectedIsPassive);
        req.setProtocolBinding(expectedProtocolBinding);
        req.setAssertionConsumerServiceIndex(expectedAssertionConsumerServiceIndex);
        req.setAssertionConsumerServiceURL(expectedAssertionConsumerServiceURL);
        req.setAttributeConsumingServiceIndex(expectedAttributeConsumingServiceIndex);
        req.setProviderName(expectedProviderName);
        
        assertXMLEquals(expectedOptionalAttributesDOM, req);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20P_NS, AuthnRequest.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        AuthnRequest req = (AuthnRequest) buildXMLObject(qname);
        
        super.populateChildElements(req);
        
        QName subjectQName = new QName(SAMLConstants.SAML20_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        req.setSubject((Subject) buildXMLObject(subjectQName));
        
        QName nameIDPolicyQName = new QName(SAMLConstants.SAML20P_NS, NameIDPolicy.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        req.setNameIDPolicy((NameIDPolicy) buildXMLObject(nameIDPolicyQName));
        
        QName conditionsQName = new QName(SAMLConstants.SAML20_NS, Conditions.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        req.setConditions((Conditions) buildXMLObject(conditionsQName));
        
        QName requestedAuthnContextQName = new QName(SAMLConstants.SAML20P_NS, RequestedAuthnContext.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        req.setRequestedAuthnContext((RequestedAuthnContext) buildXMLObject(requestedAuthnContextQName));
        
        QName scopingQName = new QName(SAMLConstants.SAML20P_NS, Scoping.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        req.setScoping((Scoping) buildXMLObject(scopingQName));
        
        assertXMLEquals(expectedChildElementsDOM, req);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AuthnRequest req = (AuthnRequest) unmarshallElement(singleElementFile);
        assert req != null;
        Assert.assertEquals(req.isForceAuthn(), Boolean.FALSE, "ForceAuthn (empty) was not default value");
        Assert.assertEquals(req.isPassive(), Boolean.FALSE, "IsPassive (empty) was not default value");
        Assert.assertNull(req.getProtocolBinding(), "ProtocolBinding was not null");
        Assert.assertNull(req.getAssertionConsumerServiceIndex(), "AssertionConsumerServiceIndex was not null");
        Assert.assertNull(req.getAssertionConsumerServiceURL(), "AssertionConsumerServiceURL was not null");
        Assert.assertNull(req.getAttributeConsumingServiceIndex(), "AttributeConsumingServiceIndex was not null");
        Assert.assertNull(req.getProviderName(), "ProviderName was not null");
        
        super.helperTestSingleElementUnmarshall(req);

    }
 
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final AuthnRequest req = (AuthnRequest) unmarshallElement(singleElementOptionalAttributesFile);
        assert req != null;
        
        Assert.assertEquals(req.isForceAuthnXSBoolean(), expectedForceAuthn, "Unmarshalled ForceAuthn was not the expected value");
        Assert.assertEquals(req.isPassiveXSBoolean(), expectedIsPassive, "Unmarshalled IsPassive was not the expected value");
        Assert.assertEquals(req.getProtocolBinding(), expectedProtocolBinding, "Unmarshalled ProtocolBinding was not the expected value");
        Assert.assertEquals(req.getAssertionConsumerServiceIndex(), expectedAssertionConsumerServiceIndex, "Unmarshalled AssertionConsumerServiceIndex was not the expected value");
        Assert.assertEquals(req.getAssertionConsumerServiceURL(), expectedAssertionConsumerServiceURL, "Unmarshalled AssertionConsumerServiceURL was not the expected value");
        Assert.assertEquals(req.getAttributeConsumingServiceIndex(), expectedAttributeConsumingServiceIndex, "Unmarshalled AttributeConsumingServiceIndex was not the expected value");
        Assert.assertEquals(req.getProviderName(), expectedProviderName, "Unmarshalled ProviderName was not the expected value");
        
        super.helperTestSingleElementOptionalAttributesUnmarshall(req);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final AuthnRequest req = (AuthnRequest) unmarshallElement(childElementsFile);
        assert req != null;
        
        Assert.assertNotNull(req.getSubject(), "Subject was null");
        Assert.assertNotNull(req.getNameIDPolicy(), "NameIDPolicy was null");
        Assert.assertNotNull(req.getConditions(), "Conditions was null");
        Assert.assertNotNull(req.getRequestedAuthnContext(), "RequestedAuthnContext was null");
        Assert.assertNotNull(req.getScoping(), "Scoping was null");
        
        super.helperTestChildElementsUnmarshall(req);
    }
    
    /**
     * Test the proper behavior of the XSBooleanValue attributes.
     */
    @Test
    public void testXSBooleanAttributes() {
        final AuthnRequest req = (AuthnRequest) buildXMLObject(AuthnRequest.DEFAULT_ELEMENT_NAME);
        assert req != null;
        
        // ForceAuthn attribute
        req.setForceAuthn(Boolean.TRUE);
        Assert.assertEquals(req.isForceAuthn(), Boolean.TRUE, "Unexpected value for boolean attribute found");
        Assert.assertNotNull(req.isForceAuthnXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(req.isForceAuthnXSBoolean(), new XSBooleanValue(Boolean.TRUE, false),
                "XSBooleanValue was unexpected value");
        XSBooleanValue bool = req.isForceAuthnXSBoolean();
        assert bool != null;
        Assert.assertEquals(bool.toString(), "true", "XSBooleanValue string was unexpected value");
        
        req.setForceAuthn(Boolean.FALSE);
        Assert.assertEquals(req.isForceAuthn(), Boolean.FALSE, "Unexpected value for boolean attribute found");
        Assert.assertNotNull(req.isForceAuthnXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(req.isForceAuthnXSBoolean(), new XSBooleanValue(Boolean.FALSE, false),
                "XSBooleanValue was unexpected value");
        bool = req.isForceAuthnXSBoolean();
        assert bool != null;
        Assert.assertEquals(bool .toString(), "false", "XSBooleanValue string was unexpected value");
        
        req.setForceAuthn((Boolean) null);
        Assert.assertEquals(req.isForceAuthn(), Boolean.FALSE, "Unexpected default value for boolean attribute found");
        Assert.assertNull(req.isForceAuthnXSBoolean(), "XSBooleanValue was not null");
        
        
        // IsPassive attribute
        req.setIsPassive(Boolean.TRUE);
        Assert.assertEquals(req.isPassive(), Boolean.TRUE, "Unexpected value for boolean attribute found");
        Assert.assertNotNull(req.isPassiveXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(req.isPassiveXSBoolean(), new XSBooleanValue(Boolean.TRUE, false),
                "XSBooleanValue was unexpected value");
        bool = req.isPassiveXSBoolean();
        assert bool != null;
        Assert.assertEquals(bool.toString(), "true", "XSBooleanValue string was unexpected value");
        
        req.setIsPassive(Boolean.FALSE);
        Assert.assertEquals(req.isPassive(), Boolean.FALSE, "Unexpected value for boolean attribute found");
        Assert.assertNotNull(req.isPassiveXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(req.isPassiveXSBoolean(), new XSBooleanValue(Boolean.FALSE, false),
                "XSBooleanValue was unexpected value");
        bool =  req.isPassiveXSBoolean(); 
        assert bool != null;
        Assert.assertEquals(bool.toString(), "false", "XSBooleanValue string was unexpected value");
        
        req.setIsPassive((Boolean) null);
        Assert.assertEquals(req.isPassive(), Boolean.FALSE, "Unexpected default value for boolean attribute found");
        Assert.assertNull(req.isPassiveXSBoolean(), "XSBooleanValue was not null");
    }
    
    @Test(expectedExceptions=UnmarshallingException.class)
    public void testBadSAMLVersion() throws XMLParserException, UnmarshallingException {
        unmarshallElement("/org/opensaml/saml/saml2/core/impl/AuthnRequestBadSAMLVersion.xml", true);
    }
    
}