/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.metadata.AssertionIDRequestService;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.AssertionIDRequestServiceImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class AssertionIDRequestServiceTest extends XMLObjectProviderBaseTestCase {
    
    protected String expectedBinding;
    protected String expectedLocation;
    protected String expectedResponseLocation;
    
    /**
     * Constructor
     */
    public AssertionIDRequestServiceTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/AssertionIDRequestService.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/metadata/impl/AssertionIDRequestServiceOptionalAttributes.xml";
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
        AssertionIDRequestService service = (AssertionIDRequestService) unmarshallElement(singleElementFile);
        assert service!=null;
        
        Assert.assertEquals(service.getBinding(), expectedBinding, "Binding URI was not expected value");
        Assert.assertEquals(service.getLocation(), expectedLocation, "Location was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AssertionIDRequestService service = (AssertionIDRequestService) unmarshallElement(singleElementOptionalAttributesFile);
        assert service!=null;
        Assert.assertEquals(service.getBinding(), expectedBinding, "Binding URI was not expected value");
        Assert.assertEquals(service.getLocation(), expectedLocation, "Location was not expected value");
        Assert.assertEquals(service.getResponseLocation(), expectedResponseLocation, "ResponseLocation was not expected value");
        Assert.assertEquals(service.getUnknownXMLObjects(AssertionIDRequestService.DEFAULT_ELEMENT_NAME).size(), 0);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        AssertionIDRequestService service = (AssertionIDRequestService) buildXMLObject(AssertionIDRequestService.DEFAULT_ELEMENT_NAME);
        
        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);

        assertXMLEquals(expectedDOM, service);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        AssertionIDRequestService service = (new AssertionIDRequestServiceBuilder()).buildObject();
        
        service.setBinding(expectedBinding);
        service.setLocation(expectedLocation);
        service.setResponseLocation(expectedResponseLocation);

        assertXMLEquals(expectedOptionalAttributesDOM, service);
    }
}