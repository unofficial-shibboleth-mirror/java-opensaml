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

package org.opensaml.xmlsec.config.impl;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.xmlsec.agreement.KeyAgreementProcessorRegistry;
import org.opensaml.xmlsec.agreement.KeyAgreementSupport;
import org.opensaml.xmlsec.agreement.impl.DHWithExplicitKDFKeyAgreementProcessor;
import org.opensaml.xmlsec.agreement.impl.DHWithLegacyKDFKeyAgreementProcessor;
import org.opensaml.xmlsec.agreement.impl.ECDHKeyAgreementProcessor;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;

@SuppressWarnings("javadoc")
public class GlobalKeyAgreementProcessorRegistryTest extends OpenSAMLInitBaseTestCase {
    
    @Test
    public void basic() {
       final KeyAgreementProcessorRegistry registry = KeyAgreementSupport.getGlobalProcessorRegistry(); 
       assert registry != null;
       
       Assert.assertEquals(registry.getRegisteredAlgorithms().size(), 3);
       
       Assert.assertEquals(registry.getRegisteredAlgorithms(), CollectionSupport.setOf(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES,
               EncryptionConstants.ALGO_ID_KEYAGREEMENT_DH, EncryptionConstants.ALGO_ID_KEYAGREEMENT_DH_EXPLICIT_KDF));
       
       Assert.assertNotNull(registry.getProcessor(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES));
       Assert.assertTrue(ECDHKeyAgreementProcessor.class.isInstance(
               registry.getProcessor(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES)));
       
       Assert.assertNotNull(registry.getProcessor(EncryptionConstants.ALGO_ID_KEYAGREEMENT_DH));
       Assert.assertTrue(DHWithLegacyKDFKeyAgreementProcessor.class.isInstance(
               registry.getProcessor(EncryptionConstants.ALGO_ID_KEYAGREEMENT_DH)));
       
       Assert.assertNotNull(registry.getProcessor(EncryptionConstants.ALGO_ID_KEYAGREEMENT_DH_EXPLICIT_KDF));
       Assert.assertTrue(DHWithExplicitKDFKeyAgreementProcessor.class.isInstance(
               registry.getProcessor(EncryptionConstants.ALGO_ID_KEYAGREEMENT_DH_EXPLICIT_KDF)));
    }

}
