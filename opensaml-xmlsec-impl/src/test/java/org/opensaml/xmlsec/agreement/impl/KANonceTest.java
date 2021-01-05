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

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.UnmodifiableComponentException;

/**
 *
 */
public class KANonceTest extends XMLObjectBaseTestCase  {
    
    @Test
    public void basic() throws ComponentInitializationException {
        KANonce nonce = new KANonce();
        nonce.setValue("someBase64==   ");
        nonce.initialize();
        Assert.assertEquals(nonce.getValue(), "someBase64==");
        
        try {
            nonce.setValue("foo");
            Assert.fail("Modify of initialzied component should have failed");
        } catch (UnmodifiableComponentException e) {
            // expected
        }
        
        KANonce cloned  = nonce.clone();
        Assert.assertTrue(cloned.isInitialized());
        Assert.assertEquals(cloned.getValue(), "someBase64==");
        
        XMLObject xmlObject = nonce.buildXMLObject();
        Assert.assertNotNull(xmlObject);
        Assert.assertTrue(org.opensaml.xmlsec.encryption.KANonce.class.isInstance(xmlObject));
        org.opensaml.xmlsec.encryption.KANonce xmlNonce = org.opensaml.xmlsec.encryption.KANonce.class.cast(xmlObject);
        Assert.assertEquals(xmlNonce.getValue(), "someBase64==");
        
    }
    
    @Test
    public void fromXMLObject() throws Exception {
        org.opensaml.xmlsec.encryption.KANonce xmlObject = buildXMLObject(org.opensaml.xmlsec.encryption.KANonce.DEFAULT_ELEMENT_NAME);
        xmlObject.setValue("someBase64==");
        
        KANonce parameter = KANonce.fromXMLObject(xmlObject);
        Assert.assertNotNull(parameter);
        Assert.assertTrue(parameter.isInitialized());
        Assert.assertEquals(parameter.getValue(), "someBase64==");
        
        xmlObject.setValue(null);
        
        
        try {
            KANonce.fromXMLObject(xmlObject);
            Assert.fail("Should have failed invalid XMLObject");
        } catch (ComponentInitializationException e) {
            //expected
        }
    }
    
    @Test(expectedExceptions = ComponentInitializationException.class)
    public void missingValue() throws ComponentInitializationException {
        KANonce nonce = new KANonce();
        nonce.initialize();
    }
    
}
