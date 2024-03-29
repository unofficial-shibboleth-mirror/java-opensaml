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
import org.opensaml.saml.saml2.ecp.Response;

/**
 * Test case for creating, marshalling, and unmarshalling {@link Response}.
 */
public class ResponseTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedACSURL;
    
    private String expectedSOAP11Actor;
    
    private Boolean expectedSOAP11MustUnderstand;
    
    /**
     * Constructor.
     */
    public ResponseTest() {
        singleElementFile = "/org/opensaml/saml/saml2/ecp/impl/Response.xml";
    }

    /**
     * Test set up.
     */
    @BeforeMethod
    protected void setUp() {
        expectedACSURL = "https://sp.example.org/acs";
        expectedSOAP11Actor = "https://soap11actor.example.org";
        expectedSOAP11MustUnderstand = true;
    }



    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final Response response = (Response) unmarshallElement(singleElementFile);
        
        assert response != null;
        
        Assert.assertEquals(response.isSOAP11MustUnderstand(), expectedSOAP11MustUnderstand, "SOAP mustUnderstand had unxpected value");
        Assert.assertEquals(response.getSOAP11Actor(), expectedSOAP11Actor, "SOAP actor had unxpected value");
        Assert.assertEquals(response.getAssertionConsumerServiceURL(), expectedACSURL, "ACS URL had unexpected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final Response response = (Response) buildXMLObject(Response.DEFAULT_ELEMENT_NAME);
        
        response.setSOAP11Actor(expectedSOAP11Actor);
        response.setSOAP11MustUnderstand(expectedSOAP11MustUnderstand);
        response.setAssertionConsumerServiceURL(expectedACSURL);
        
        assertXMLEquals(expectedDOM, response);
    }

}