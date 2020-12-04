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
import org.opensaml.xmlsec.encryption.MasterKeyName;

/**
 *
 */
public class MasterKeyNameTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedStringContent;

    /**
     * Constructor
     *
     */
    public MasterKeyNameTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/MasterKeyName.xml";
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedStringContent = "someKeyName";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        MasterKeyName mkn = (MasterKeyName) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(mkn, "MasterKeyName");
        Assert.assertEquals(expectedStringContent, mkn.getValue(), "MasterKeyName value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        MasterKeyName mkn = (MasterKeyName) buildXMLObject(MasterKeyName.DEFAULT_ELEMENT_NAME);
        mkn.setValue(expectedStringContent);
        
        assertXMLEquals(expectedDOM, mkn);
    }

}
