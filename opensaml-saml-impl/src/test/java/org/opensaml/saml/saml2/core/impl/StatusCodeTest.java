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

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.StatusCode;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.StatusCodeImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class StatusCodeTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected Value attribute value*/
    private String expectedValue;

    /**
     * Constructor
     *
     */
    public StatusCodeTest() {
       singleElementFile = "/org/opensaml/saml/saml2/core/impl/StatusCode.xml";
       childElementsFile = "/org/opensaml/saml/saml2/core/impl/StatusCodeChildElements.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedValue = "urn:string";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        StatusCode statusCode = (StatusCode) buildXMLObject(StatusCode.DEFAULT_ELEMENT_NAME);
        
        statusCode.setValue(expectedValue);
        
        assertXMLEquals(expectedDOM, statusCode);

    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        StatusCode statusCode = (StatusCode) buildXMLObject(StatusCode.DEFAULT_ELEMENT_NAME);
        
        QName statusCodeQName = new QName(SAMLConstants.SAML20P_NS, StatusCode.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
        statusCode.setStatusCode((StatusCode) buildXMLObject(statusCodeQName));
        
        assertXMLEquals(expectedChildElementsDOM, statusCode);
    }



    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final StatusCode statusCode = (StatusCode) unmarshallElement(singleElementFile);
        assert statusCode !=null;
        Assert.assertEquals(statusCode.getValue(), expectedValue, "Unmarshalled status code URI value was not the expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final StatusCode statusCode = (StatusCode) unmarshallElement(childElementsFile);
        assert statusCode !=null;        
        Assert.assertNotNull(statusCode.getStatusCode());
    }
}