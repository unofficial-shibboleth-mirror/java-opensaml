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
import org.opensaml.xmlsec.encryption.IterationCount;
import org.opensaml.xmlsec.encryption.KeyLength;
import org.opensaml.xmlsec.encryption.PBKDF2Params;
import org.opensaml.xmlsec.encryption.PRF;
import org.opensaml.xmlsec.encryption.Salt;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class PBKDF2ParamsTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     *
     */
    public PBKDF2ParamsTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/PBKDF2Params.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/PBKDF2ParamsChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final PBKDF2Params params = (PBKDF2Params) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(params, "PBKDF2Params");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final PBKDF2Params params = (PBKDF2Params) unmarshallElement(childElementsFile);
        
        assert params != null;
        Assert.assertNotNull(params.getSalt());
        Assert.assertNotNull(params.getIterationCount());
        Assert.assertNotNull(params.getKeyLength());
        Assert.assertNotNull(params.getPRF());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final PBKDF2Params params = (PBKDF2Params) buildXMLObject(PBKDF2Params.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, params);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final PBKDF2Params params = (PBKDF2Params) buildXMLObject(PBKDF2Params.DEFAULT_ELEMENT_NAME);
        
        params.setSalt(buildXMLObject(Salt.DEFAULT_ELEMENT_NAME));
        params.setIterationCount(buildXMLObject(IterationCount.DEFAULT_ELEMENT_NAME));
        params.setKeyLength(buildXMLObject(KeyLength.DEFAULT_ELEMENT_NAME));
        params.setPRF(buildXMLObject(PRF.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, params);
    }

}
