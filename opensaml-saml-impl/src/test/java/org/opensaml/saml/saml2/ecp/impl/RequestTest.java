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

package org.opensaml.saml.saml2.ecp.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.IDPList;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.ecp.Request;

/**
 * Test case for creating, marshalling, and unmarshalling {@link Request}.
 */
public class RequestTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedProviderName;
    
    private Boolean expectedPassive;
    
    private String expectedSOAP11Actor;
    
    private Boolean expectedSOAP11MustUnderstand;
    
    /**
     * Constructor.
     */
    public RequestTest() {
        singleElementFile = "/org/opensaml/saml/saml2/ecp/impl/Request.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/ecp/impl/RequestOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/ecp/impl/RequestChildElements.xml";
    }

    /**
     * Test set up.
     */
    @BeforeMethod
    protected void setUp() {
        expectedProviderName = "https://provider.example.org";
        expectedSOAP11Actor = "https://soap11actor.example.org";
        expectedSOAP11MustUnderstand = true;
        expectedPassive = true;
    }



    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final Request request = (Request) unmarshallElement(singleElementFile);
        
        assert request != null;
        
        Assert.assertEquals(request.isSOAP11MustUnderstand(), expectedSOAP11MustUnderstand, "SOAP mustUnderstand had unxpected value");
        Assert.assertEquals(request.getSOAP11Actor(), expectedSOAP11Actor, "SOAP actor had unxpected value");
    }
 
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final Request request = (Request) unmarshallElement(singleElementOptionalAttributesFile);
        
        assert request != null;
        
        Assert.assertEquals(request.isSOAP11MustUnderstand(), expectedSOAP11MustUnderstand, "SOAP mustUnderstand had unxpected value");
        Assert.assertEquals(request.getSOAP11Actor(), expectedSOAP11Actor, "SOAP actor had unxpected value");
        
        Assert.assertEquals(request.isPassive(), expectedPassive, "IsPassive had unexpected value");
        Assert.assertEquals(request.getProviderName(), expectedProviderName, "ProviderName had unexpected value");
    }
   
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final Request request = (Request) unmarshallElement(childElementsFile);
        
        assert request != null;
        
        Assert.assertEquals(request.isSOAP11MustUnderstand(), expectedSOAP11MustUnderstand, "SOAP mustUnderstand had unxpected value");
        Assert.assertEquals(request.getSOAP11Actor(), expectedSOAP11Actor, "SOAP actor had unxpected value");
        
        Assert.assertNotNull(request.getIssuer(), "Issuer was null");
        Assert.assertNotNull(request.getIDPList(), "IDPList was null");
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final Request request = (Request) buildXMLObject(Request.DEFAULT_ELEMENT_NAME);
        
        request.setSOAP11Actor(expectedSOAP11Actor);
        request.setSOAP11MustUnderstand(expectedSOAP11MustUnderstand);
        
        assertXMLEquals(expectedDOM, request);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final Request request = (Request) buildXMLObject(Request.DEFAULT_ELEMENT_NAME);
        
        request.setSOAP11Actor(expectedSOAP11Actor);
        request.setSOAP11MustUnderstand(expectedSOAP11MustUnderstand);
        request.setProviderName(expectedProviderName);
        request.setPassive(expectedPassive);
        
        assertXMLEquals(expectedOptionalAttributesDOM, request);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final Request request = (Request) buildXMLObject(Request.DEFAULT_ELEMENT_NAME);
        
        request.setSOAP11Actor(expectedSOAP11Actor);
        request.setSOAP11MustUnderstand(expectedSOAP11MustUnderstand);
        
        request.setIssuer((Issuer) buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME));
        request.setIDPList((IDPList) buildXMLObject(IDPList.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, request);
    }

}