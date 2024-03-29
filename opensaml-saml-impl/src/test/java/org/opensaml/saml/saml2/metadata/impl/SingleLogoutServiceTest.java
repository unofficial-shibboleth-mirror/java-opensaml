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

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.SingleLogoutServiceImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class SingleLogoutServiceTest extends XMLObjectProviderBaseTestCase {
    
    protected String expectedBinding;
    protected String expectedLocation;
    protected String expectedResponseLocation;
    
    /** Unknown Attributes */
    protected QName[] unknownAttributeNames = { new QName("urn:foo:bar", "bar", "foo") };
    /** Unknown Attribute Values */
    protected String[] unknownAttributeValues = {"fred"};

    /**
     * Constructor
     */
    public SingleLogoutServiceTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/SingleLogoutService.xml";
        childElementsFile = "/org/opensaml/saml/saml2/metadata/impl/SingleLogoutServiceChildElements.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/metadata/impl/SingleLogoutServiceOptionalAttributes.xml";
        singleElementUnknownAttributesFile = "/org/opensaml/saml/saml2/metadata/impl/SingleLogoutServiceUnknownAttributes.xml";
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
        final SingleLogoutService service = (SingleLogoutService) unmarshallElement(singleElementFile);
        assert service!=null;
        Assert.assertEquals(service.getBinding(), expectedBinding, "Binding URI was not expected value");
        Assert.assertEquals(service.getLocation(), expectedLocation, "Location was not expected value");
    }
    
    /** {@inheritDoc} */
    @Test public void testSingleElementUnknownAttributesUnmarshall() {
        final SingleLogoutService service = (SingleLogoutService) unmarshallElement(singleElementUnknownAttributesFile);
        assert service!=null;
        final AttributeMap attributes = service.getUnknownAttributes();
        Assert.assertEquals(attributes.entrySet().size(), unknownAttributeNames.length);
        for (int i = 0; i < unknownAttributeNames.length; i++) {
            Assert.assertEquals(attributes.get(unknownAttributeNames[i]), unknownAttributeValues[i]);
        }
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final SingleLogoutService service = (SingleLogoutService) unmarshallElement(singleElementOptionalAttributesFile);
        assert service!=null;
        Assert.assertEquals(service.getBinding(), expectedBinding, "Binding URI was not expected value");
        Assert.assertEquals(service.getLocation(), expectedLocation, "Location was not expected value");
        Assert.assertEquals(service.getResponseLocation(), expectedResponseLocation, "ResponseLocation was not expected value");;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final SingleLogoutService service = (SingleLogoutService) buildXMLObject(SingleLogoutService.DEFAULT_ELEMENT_NAME);
        
        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);

        assertXMLEquals(expectedDOM, service);
    }
    
    @Test
    public void testSingleElementUnknownAttributesMarshall() {
        final SingleLogoutService service = (new SingleLogoutServiceBuilder()).buildObject();

        for (int i = 0; i < unknownAttributeNames.length; i++) {
            service.getUnknownAttributes().put(unknownAttributeNames[i], unknownAttributeValues[i]);
        }
        assertXMLEquals(expectedUnknownAttributesDOM, service);
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final SingleLogoutService service = (SingleLogoutService) buildXMLObject(SingleLogoutService.DEFAULT_ELEMENT_NAME);
        
        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);
        service.setResponseLocation(expectedResponseLocation);

        assertXMLEquals(expectedOptionalAttributesDOM, service);
    }
    
    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        final SingleLogoutService service = (SingleLogoutService) unmarshallElement(childElementsFile);
        assert service!=null;
        Assert.assertEquals(service.getUnknownXMLObjects().size(), 1);
    }
    
    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        SingleLogoutService service = (new SingleLogoutServiceBuilder()).buildObject();
        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);


        XMLObject obj = builderFactory.ensureBuilder(XSAny.TYPE_NAME).buildObject(new QName("http://example.org/", "bar", "foo"));
        
        service.getUnknownXMLObjects().add(obj);

        assertXMLEquals(expectedChildElementsDOM, service);    
    }
}