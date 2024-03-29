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

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.ecp.RequestAuthenticated;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * Test case for creating, marshalling, and unmarshalling {@link RequestAuthenticated}.
 */
public class RequestAuthenticatedTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedSOAP11Actor;
    
    private Boolean expectedSOAP11MustUnderstand;
    
    /**
     * Constructor.
     */
    public RequestAuthenticatedTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml2/ecp/impl/RequestAuthenticated.xml";
    }
 
    /**
     * Test set up.
     */
    @BeforeMethod
    protected void setUp() {
        expectedSOAP11Actor = "https://soap11actor.example.org";
        expectedSOAP11MustUnderstand = true;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final RequestAuthenticated ra = (RequestAuthenticated) unmarshallElement(singleElementFile);
        
        assert ra != null;
        
        Assert.assertEquals(expectedSOAP11MustUnderstand, ra.isSOAP11MustUnderstand(), 
                "SOAP mustUnderstand had unxpected value");
        Assert.assertEquals(expectedSOAP11Actor, ra.getSOAP11Actor(), "SOAP actor had unxpected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final RequestAuthenticated ra = (RequestAuthenticated) buildXMLObject(RequestAuthenticated.DEFAULT_ELEMENT_NAME);
        
        ra.setSOAP11Actor(expectedSOAP11Actor);
        ra.setSOAP11MustUnderstand(expectedSOAP11MustUnderstand);
        
        assertXMLEquals(expectedDOM, ra);
    }

}