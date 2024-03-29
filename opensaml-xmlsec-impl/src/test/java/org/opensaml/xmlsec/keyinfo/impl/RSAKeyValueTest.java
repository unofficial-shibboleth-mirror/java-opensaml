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

package org.opensaml.xmlsec.keyinfo.impl;

import org.testng.annotations.Test;

import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCriterion;
import org.opensaml.xmlsec.keyinfo.impl.provider.RSAKeyValueProvider;
import org.opensaml.xmlsec.signature.KeyInfo;


/**
 * Test resolution of credentials from RSAKeyValue child of KeyInfo.
 */
@SuppressWarnings({"javadoc", "null"})
public class RSAKeyValueTest extends XMLObjectBaseTestCase {
    
    private KeyInfoCredentialResolver resolver;
    
    private String keyInfoFile;
    
    private RSAPublicKey pubKey;
    
    private final String rsaBase64 = 
    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw/WnsbA8frhQ+8EoPgMr" +
    "QjpINjt20U/MvsvmoAgQnAgEF4OYt9Vj9/2YvMO4NvX1fNDFzoYRyOMrypF7skAP" +
    "cITUhdcPSEpI4nsf5yFZLziK/tQ26RsccE7WhpGB8eHu9tfseelgyioorvmt+JCo" +
    "P15c5rYUuIfVC+eEsYolw344q6N61OACHETuySL0a1+GFu3WoISXte1pQIst7HKv" +
    "BbHH41HEWAxT6e0hlD5PyKL4lBJadGHXg8Zz4r2jV2n6+Ox7raEWmtVCGFxsAoCR" +
    "alu6nvs2++5Nnb4C1SE640esfYhfeMd5JYfsTNMaQ8sZLpsWdglAGpa/Q87K19LI" +
    "wwIDAQAB";

    @BeforeMethod
    protected void setUp() throws Exception {
        List<KeyInfoProvider> providers = new ArrayList<>();
        providers.add(new RSAKeyValueProvider());
        resolver = new BasicProviderKeyInfoCredentialResolver(providers);
        keyInfoFile = "/org/opensaml/xmlsec/keyinfo/impl/RSAKeyValue.xml";
        pubKey = KeySupport.buildJavaRSAPublicKey(rsaBase64);
    }
    
    /**
     * Test basic credential resolution.
     * 
     * @throws ResolverException on error resolving credentials
     */
    @Test
    public void testCredResolution() throws ResolverException {
        KeyInfo keyInfo = (KeyInfo) unmarshallElement(keyInfoFile);
        CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo) );
        Iterator<Credential> iter = resolver.resolve(criteriaSet).iterator();
        
        Assert.assertTrue(iter.hasNext(), "No credentials were found");
        
        Credential credential = iter.next();
        Assert.assertNotNull(credential, "Credential was null");
        Assert.assertFalse(iter.hasNext(), "Too many credentials returned");
        Assert.assertTrue(credential instanceof BasicCredential, "Credential is not of the expected type");
        
        
        Assert.assertNotNull(credential.getPublicKey(), "Public key was null");
        Assert.assertEquals(credential.getPublicKey(), pubKey, "Expected public key value not found");
        
        Assert.assertEquals(credential.getKeyNames().size(), 2, "Wrong number of key names");
        Assert.assertTrue(credential.getKeyNames().contains("Foo"), "Expected key name value not found");
        Assert.assertTrue(credential.getKeyNames().contains("Bar"), "Expected key name value not found");
    }
    

}
