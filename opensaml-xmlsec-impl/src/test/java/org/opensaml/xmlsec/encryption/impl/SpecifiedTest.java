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

package org.opensaml.xmlsec.encryption.impl;


import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.encryption.Specified;

/**
 *
 */
public class SpecifiedTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedStringContent;

    /**
     * Constructor
     *
     */
    public SpecifiedTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/Specified.xml";
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedStringContent = "someBase64==";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Specified specified = (Specified) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(specified, "Specified");
        Assert.assertEquals(expectedStringContent, specified.getValue(), "Specified value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        Specified specified = (Specified) buildXMLObject(Specified.DEFAULT_ELEMENT_NAME);
        specified.setValue(expectedStringContent);
        
        assertXMLEquals(expectedDOM, specified);
    }

}
