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

package org.opensaml.xmlsec.agreement.impl;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

/**
 *
 */
public class KANonceTest extends OpenSAMLInitBaseTestCase  {
    
    @Test
    public void basic() {
        KANonce nonce = new KANonce("   someBase64==   ");
        Assert.assertEquals(nonce.getValue(), "someBase64==");
        
        XMLObject xmlObject = nonce.buildXMLObject();
        Assert.assertNotNull(xmlObject);
        Assert.assertTrue(org.opensaml.xmlsec.encryption.KANonce.class.isInstance(xmlObject));
        org.opensaml.xmlsec.encryption.KANonce xmlNonce = org.opensaml.xmlsec.encryption.KANonce.class.cast(xmlObject);
        Assert.assertEquals(xmlNonce.getValue(), "someBase64==");
        
        try {
            new KANonce("    ");
            Assert.fail("KANonce accepted illegal empty value");
        } catch (ConstraintViolationException e) {
            // expected, do nothing
        }
    }
    
}
