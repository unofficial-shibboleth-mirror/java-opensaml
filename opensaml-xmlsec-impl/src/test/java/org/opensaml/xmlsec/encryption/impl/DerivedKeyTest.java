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


import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.encryption.DerivedKey;
import org.opensaml.xmlsec.encryption.DerivedKeyName;
import org.opensaml.xmlsec.encryption.KeyDerivationMethod;
import org.opensaml.xmlsec.encryption.MasterKeyName;
import org.opensaml.xmlsec.encryption.ReferenceList;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class DerivedKeyTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedId;
    private String expectedRecipient;
    private String expectedType;
    
    /**
     * Constructor
     *
     */
    public DerivedKeyTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/DerivedKey.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/xmlsec/encryption/impl/DerivedKeyOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/DerivedKeyChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedId = "abc123";
        expectedRecipient = "theRecipient";
        expectedType = "urn:string:foo";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final DerivedKey dk = (DerivedKey) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(dk);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final DerivedKey dk = (DerivedKey) unmarshallElement(childElementsFile);
        assert dk != null;
        
        Assert.assertNotNull(dk.getKeyDerivationMethod());
        Assert.assertNotNull(dk.getReferenceList());
        Assert.assertNotNull(dk.getDerivedKeyName());
        Assert.assertNotNull(dk.getMasterKeyName());
        Assert.assertNotNull(dk);
    }
    
    /** {@inheritDoc} */
    public void testSingleElementOptionalAttributesUnmarshall() {
        final DerivedKey dk = (DerivedKey) unmarshallElement(singleElementOptionalAttributesFile);
        
        assert dk != null;
        Assert.assertEquals(dk.getId(), expectedId);
        Assert.assertEquals(dk.getRecipient(), expectedRecipient);
        Assert.assertEquals(dk.getType(), expectedType);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final DerivedKey dk = (DerivedKey) buildXMLObject(DerivedKey.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, dk);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final DerivedKey dk = (DerivedKey) buildXMLObject(DerivedKey.DEFAULT_ELEMENT_NAME);
        
        dk.setKeyDerivationMethod(buildXMLObject(KeyDerivationMethod.DEFAULT_ELEMENT_NAME));
        dk.setReferenceList(buildXMLObject(ReferenceList.DEFAULT_ELEMENT_NAME));
        dk.setDerivedKeyName(buildXMLObject(DerivedKeyName.DEFAULT_ELEMENT_NAME));
        dk.setMasterKeyName(buildXMLObject(MasterKeyName.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, dk);
    }

    /** {@inheritDoc} */
    public void testSingleElementOptionalAttributesMarshall() {
        final DerivedKey dk = (DerivedKey) buildXMLObject(DerivedKey.DEFAULT_ELEMENT_NAME);
        
        dk.setId(expectedId);
        dk.setRecipient(expectedRecipient);
        dk.setType(expectedType);
        
        assertXMLEquals(expectedOptionalAttributesDOM, dk);
    }

}
