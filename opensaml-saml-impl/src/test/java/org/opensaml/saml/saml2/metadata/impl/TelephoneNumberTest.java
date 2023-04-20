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
import org.opensaml.saml.saml2.metadata.TelephoneNumber;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.TelephoneNumber}.
 */
@SuppressWarnings({"null", "javadoc"})
public class TelephoneNumberTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected telephone number */
    protected String expectedNumber;
    
    /**
     * Constructor
     */
    public TelephoneNumberTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/TelephoneNumber.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedNumber = "888.100.1212";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final TelephoneNumber number = (TelephoneNumber) unmarshallElement(singleElementFile);
        assert number!=null;
        Assert.assertEquals(number.getValue(), expectedNumber, "Telephone number was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final TelephoneNumber number = (new TelephoneNumberBuilder()).buildObject();
        
        number.setValue(expectedNumber);

        assertXMLEquals(expectedDOM, number);
    }
}