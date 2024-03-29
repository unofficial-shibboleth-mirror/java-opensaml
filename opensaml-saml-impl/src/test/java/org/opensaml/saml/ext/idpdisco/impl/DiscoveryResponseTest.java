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

package org.opensaml.saml.ext.idpdisco.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.ext.idpdisco.DiscoveryResponse;

/**
 * Test case for creating, marshalling, and unmarshalling {@link DiscoveryResponseImpl}.
 */
public class DiscoveryResponseTest extends XMLObjectProviderBaseTestCase {

    protected String expectedBinding;

    protected String expectedLocation;

    protected String expectedResponseLocation;

    protected Integer expectedIndex;

    protected XSBooleanValue expectedIsDefault;

    /**
     * Constructor
     */
    public DiscoveryResponseTest() {
        singleElementFile = "/org/opensaml/saml/ext/idpdisco/DiscoveryResponse.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/ext/idpdisco/DiscoveryResponseOptionalAttributes.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedBinding = "urn:binding:foo";
        expectedLocation = "example.org";
        expectedResponseLocation = "example.org/response";
        expectedIndex = Integer.valueOf(3);
        expectedIsDefault = new XSBooleanValue(Boolean.TRUE, false);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final DiscoveryResponse service = (DiscoveryResponse) unmarshallElement(singleElementFile);
        assert service != null;
        Assert.assertEquals(service.getBinding(), expectedBinding, "Binding URI was not expected value");
        Assert.assertEquals(service.getLocation(), expectedLocation, "Location was not expected value");
        Assert.assertEquals(service.getIndex(), expectedIndex, "Index was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final DiscoveryResponse service = (DiscoveryResponse) unmarshallElement(singleElementOptionalAttributesFile);
        assert service != null;
        Assert.assertEquals(service.getBinding(), expectedBinding, "Binding URI was not expected value");
        Assert.assertEquals(service.getLocation(), expectedLocation, "Location was not expected value");
        Assert.assertEquals(service.getIndex(), expectedIndex, "Index was not expected value");
        Assert.assertEquals(service.getResponseLocation(), expectedResponseLocation, "ResponseLocation was not expected value");
        Assert.assertEquals(service.isDefaultXSBoolean(), expectedIsDefault, "isDefault was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final DiscoveryResponse service = (DiscoveryResponse) buildXMLObject(DiscoveryResponse.DEFAULT_ELEMENT_NAME);

        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);
        service.setIndex(expectedIndex);

        assertXMLEquals(expectedDOM, service);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final DiscoveryResponse service = (DiscoveryResponse) buildXMLObject(DiscoveryResponse.DEFAULT_ELEMENT_NAME);

        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);
        service.setIndex(expectedIndex);
        service.setResponseLocation(expectedResponseLocation);
        service.setIsDefault(expectedIsDefault);

        assertXMLEquals(expectedOptionalAttributesDOM, service);
    }

    /**
     * Test the proper behavior of the XSBooleanValue attributes.
     */
    @Test
    public void testXSBooleanAttributes() {
        final DiscoveryResponse acs = (DiscoveryResponse) buildXMLObject(DiscoveryResponse.DEFAULT_ELEMENT_NAME);

        // isDefault attribute
        acs.setIsDefault(Boolean.TRUE);
        Assert.assertEquals(acs.isDefault(), Boolean.TRUE, "Unexpected value for boolean attribute found");
        XSBooleanValue xsbool = acs.isDefaultXSBoolean();
        assert xsbool != null;
        Assert.assertEquals(xsbool, new XSBooleanValue(Boolean.TRUE, false), "XSBooleanValue was unexpected value");
        Assert.assertEquals(xsbool.toString(), "true", "XSBooleanValue string was unexpected value");

        acs.setIsDefault(Boolean.FALSE);
        Assert.assertEquals(acs.isDefault(), Boolean.FALSE, "Unexpected value for boolean attribute found");
        xsbool = acs.isDefaultXSBoolean();
        assert xsbool != null;
        Assert.assertEquals(xsbool, new XSBooleanValue(Boolean.FALSE, false), "XSBooleanValue was unexpected value");
        Assert.assertEquals(xsbool.toString(), "false", "XSBooleanValue string was unexpected value");

        acs.setIsDefault((Boolean) null);
        Assert.assertEquals(acs.isDefault(), Boolean.FALSE, "Unexpected default value for boolean attribute found");
        Assert.assertNull(acs.isDefaultXSBoolean(), "XSBooleanValue was not null");
    }

}