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

import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCriterion;
import org.opensaml.xmlsec.keyinfo.impl.provider.ECKeyValueProvider;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;


/**
 * Test resolution of credentials from RSAKeyValue child of KeyInfo.
 */
@SuppressWarnings({"javadoc", "null"})
public class ECKeyValueTest extends XMLObjectBaseTestCase {
    
    private KeyInfoCredentialResolver resolver;
    
    private String keyInfoFile;
    
    private ECPublicKey pubKey;
    
    /** Curve name: secp256r1, OID: 1.2.840.10045.3.1.7 */
    private final String ecBase64 =
            "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEBM0jGYrvVMpbVTT728+RfDLL0tPg" +
            "swfUSUXfrXKwAGOmrSbF1KHsErZdXhnEC1VSmm9kTd8VzIi4OihEVMoU+w==";

    @BeforeMethod
    protected void setUp() throws Exception {
        List<KeyInfoProvider> providers = new ArrayList<>();
        providers.add(new ECKeyValueProvider());
        resolver = new BasicProviderKeyInfoCredentialResolver(providers);
        keyInfoFile = "/org/opensaml/xmlsec/keyinfo/impl/ECKeyValue.xml";
        pubKey = KeySupport.buildJavaECPublicKey(ecBase64);
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
