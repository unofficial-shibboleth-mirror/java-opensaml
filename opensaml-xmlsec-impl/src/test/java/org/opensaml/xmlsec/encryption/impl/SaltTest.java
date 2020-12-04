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
import org.opensaml.xmlsec.encryption.OtherSource;
import org.opensaml.xmlsec.encryption.Salt;
import org.opensaml.xmlsec.encryption.Specified;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class SaltTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     *
     */
    public SaltTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/Salt.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/SaltChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Salt salt = (Salt) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(salt, "Salt");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        Salt salt = (Salt) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(salt, "Salt");
        Assert.assertNotNull(salt.getSpecified());
        Assert.assertNotNull(salt.getOtherSource());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        Salt salt = (Salt) buildXMLObject(Salt.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, salt);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        Salt salt = (Salt) buildXMLObject(Salt.DEFAULT_ELEMENT_NAME);
        
        salt.setSpecified(buildXMLObject(Specified.DEFAULT_ELEMENT_NAME));
        salt.setOtherSource(buildXMLObject(OtherSource.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, salt);
    }

}
