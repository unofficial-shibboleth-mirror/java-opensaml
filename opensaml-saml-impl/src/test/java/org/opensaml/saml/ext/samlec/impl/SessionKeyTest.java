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

package org.opensaml.saml.ext.samlec.impl;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.samlec.EncType;
import org.opensaml.saml.ext.samlec.SessionKey;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * Test case for creating, marshalling, and unmarshalling {@link SessionKey}.
 */
public class SessionKeyTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedAlg;
    
    private String expectedSOAP11Actor;
    
    private Boolean expectedSOAP11MustUnderstand;
    
    /** Constructor. */
    public SessionKeyTest() {
        singleElementFile = "/org/opensaml/saml/ext/samlec/impl/SessionKey.xml";
        childElementsFile = "/org/opensaml/saml/ext/samlec/impl/SessionKeyChildElements.xml";
    }
 
    /**
     * Test set up.
     * 
     * @throws Exception
     */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAlg = "http://myalgorithm.example.com";
        expectedSOAP11Actor = "https://soap11actor.example.org";
        expectedSOAP11MustUnderstand = true;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final SessionKey key = (SessionKey) unmarshallElement(singleElementFile);
        assert key != null;
        
        Assert.assertEquals(expectedSOAP11MustUnderstand, key.isSOAP11MustUnderstand(),
                "SOAP mustUnderstand had unxpected value");
        Assert.assertEquals(expectedSOAP11Actor, key.getSOAP11Actor(), "SOAP actor had unxpected value");
        Assert.assertEquals(expectedAlg, key.getAlgorithm(),"Algorithm had unexpected value");
    }
 
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final SessionKey key = (SessionKey) unmarshallElement(childElementsFile);
        assert key != null;
        
        Assert.assertEquals(expectedSOAP11MustUnderstand, key.isSOAP11MustUnderstand(),
                "SOAP mustUnderstand had unxpected value");
        Assert.assertEquals(expectedSOAP11Actor, key.getSOAP11Actor(), "SOAP actor had unxpected value");
        Assert.assertEquals(2, key.getEncTypes().size(), "Wrong number of EncTypes");
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final SessionKey key = (SessionKey) buildXMLObject(SessionKey.DEFAULT_ELEMENT_NAME);
        
        key.setSOAP11Actor(expectedSOAP11Actor);
        key.setSOAP11MustUnderstand(expectedSOAP11MustUnderstand);
        key.setAlgorithm(expectedAlg);
        
        assertXMLEquals(expectedDOM, key);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final SessionKey key = (SessionKey) buildXMLObject(SessionKey.DEFAULT_ELEMENT_NAME);
        
        key.setSOAP11Actor(expectedSOAP11Actor);
        key.setSOAP11MustUnderstand(expectedSOAP11MustUnderstand);
        
        key.getEncTypes().add((EncType) buildXMLObject(EncType.DEFAULT_ELEMENT_NAME));
        key.getEncTypes().add((EncType) buildXMLObject(EncType.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, key);
    }

}