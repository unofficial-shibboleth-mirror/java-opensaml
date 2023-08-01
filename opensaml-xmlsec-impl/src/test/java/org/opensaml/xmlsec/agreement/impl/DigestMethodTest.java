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

package org.opensaml.xmlsec.agreement.impl;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.component.UnmodifiableComponentException;

@SuppressWarnings("javadoc")
public class DigestMethodTest extends XMLObjectBaseTestCase {
    
    @Test
    public void basic() throws ComponentInitializationException {
        DigestMethod digest = new DigestMethod();
        digest.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        digest.initialize();
        Assert.assertEquals(digest.getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        
        try {
            digest.setAlgorithm("foo");
            Assert.fail("Modify of initialzied component should have failed");
        } catch (UnmodifiableComponentException e) {
            // expected
        }
        
        DigestMethod cloned  = digest.clone();
        Assert.assertTrue(cloned.isInitialized());
        Assert.assertEquals(cloned.getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        
        XMLObject xmlObject = digest.buildXMLObject();
        Assert.assertNotNull(xmlObject);
        Assert.assertTrue(org.opensaml.xmlsec.signature.DigestMethod.class.isInstance(xmlObject));
        org.opensaml.xmlsec.signature.DigestMethod xmlDigest = org.opensaml.xmlsec.signature.DigestMethod.class.cast(xmlObject);
        Assert.assertEquals(xmlDigest.getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        
    }
    
    @Test
    public void fromXMLObject() throws Exception {
        org.opensaml.xmlsec.signature.DigestMethod xmlObject = buildXMLObject(org.opensaml.xmlsec.signature.DigestMethod.DEFAULT_ELEMENT_NAME);
        xmlObject.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        
        DigestMethod parameter = DigestMethod.fromXMLObject(xmlObject);
        Assert.assertNotNull(parameter);
        Assert.assertTrue(parameter.isInitialized());
        Assert.assertEquals(parameter.getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        
        xmlObject.setAlgorithm(null);
        
        
        try {
            DigestMethod.fromXMLObject(xmlObject);
            Assert.fail("Should have failed invalid XMLObject");
        } catch (ComponentInitializationException e) {
            //expected
        }
    }
    
    @Test(expectedExceptions = ComponentInitializationException.class)
    public void missingValue() throws ComponentInitializationException {
        DigestMethod digest = new DigestMethod();
        digest.initialize();
    }
    
}
