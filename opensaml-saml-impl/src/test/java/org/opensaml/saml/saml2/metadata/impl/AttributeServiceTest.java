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
package org.opensaml.saml.saml2.metadata.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.AttributeService;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.AttributeServiceImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class AttributeServiceTest extends XMLObjectProviderBaseTestCase {
    
    protected String expectedBinding;
    protected String expectedLocation;
    protected String expectedResponseLocation;
    
    /**
     * Constructor
     */
    public AttributeServiceTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/AttributeService.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/metadata/impl/AttributeServiceOptionalAttributes.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedBinding = "urn:binding:foo";
        expectedLocation = "example.org";
        expectedResponseLocation = "example.org/response";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final AttributeService service = (AttributeService) unmarshallElement(singleElementFile);
        assert service!=null;        
        Assert.assertEquals(service.getBinding(), expectedBinding, "Binding URI was not expected value");
        Assert.assertEquals(service.getLocation(), expectedLocation, "Location was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final AttributeService service = (AttributeService) unmarshallElement(singleElementOptionalAttributesFile);
        assert service!=null;                
        Assert.assertEquals(service.getBinding(), expectedBinding, "Binding URI was not expected value");
        Assert.assertEquals(service.getLocation(), expectedLocation, "Location was not expected value");
        Assert.assertEquals(service.getResponseLocation(), expectedResponseLocation, "ResponseLocation was not expected value");;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final AttributeService service = (new AttributeServiceBuilder()).buildObject();
        
        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);

        assertXMLEquals(expectedDOM, service);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20MD_NS, AttributeService.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        final AttributeService service = (AttributeService) buildXMLObject(qname);
        
        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);
        service.setResponseLocation(expectedResponseLocation);

        assertXMLEquals(expectedOptionalAttributesDOM, service);
    }
}