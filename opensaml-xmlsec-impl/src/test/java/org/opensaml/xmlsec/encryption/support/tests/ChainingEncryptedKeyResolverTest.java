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

package org.opensaml.xmlsec.encryption.support.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.support.ChainingEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.InlineEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.SimpleRetrievalMethodEncryptedKeyResolver;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Test the encrypted key resolver which dereferences RetrievalMethods.
 */
@SuppressWarnings({"javadoc", "null"})
public class ChainingEncryptedKeyResolverTest extends XMLObjectBaseTestCase {
    
    /** The resolver instance to be tested. */
    private ChainingEncryptedKeyResolver resolver;
    
    private List<EncryptedKeyResolver> resolverChain;
    
    private Set<String> recipients;
    
    
    @BeforeMethod
    protected void setUp() throws Exception {
        EncryptedKeyResolver inline = new InlineEncryptedKeyResolver();
        EncryptedKeyResolver rm = new SimpleRetrievalMethodEncryptedKeyResolver();
        resolverChain = Arrays.asList(inline, rm);
        //resolver = new ChainingEncryptedKeyResolver(resolverChain, recipients);
        
        recipients = new HashSet<>();
    }
    
    /** Test error case of empty resolver chain. */
    @Test(expectedExceptions=IllegalStateException.class)
    public void testEmptyChain() {
        String filename =  "/org/opensaml/xmlsec/encryption/support/ChainingEncryptedKeyResolverSingleInline.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        assert sxo != null;
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        final EncryptedData encData = sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        assert encData != null;
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertFalse(keyInfo.getEncryptedKeys().isEmpty());
        Assert.assertTrue(keyInfo.getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new ChainingEncryptedKeyResolver(new ArrayList<EncryptedKeyResolver>());
        
        generateList(encData, resolver, null);
    }
    
    /** One recipient specified to resolver, EncryptedKey in instance inline. */
    @Test
    public void testSingleEKInline() {
        String filename =  "/org/opensaml/xmlsec/encryption/support/ChainingEncryptedKeyResolverSingleInline.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        assert sxo != null;
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        final EncryptedData encData = sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        assert encData != null;
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertFalse(keyInfo.getEncryptedKeys().isEmpty());
        Assert.assertTrue(keyInfo.getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        recipients.add("foo");
        
        resolver = new ChainingEncryptedKeyResolver(resolverChain);
        
        List<EncryptedKey> resolved = generateList(encData, resolver, recipients);
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
    }
    
    /** One recipient specified to resolver, EncryptedKey in instance via RetrievalMethod . */
    @Test
    public void testSingleEKRetrievalMethod() {
        String filename =  "/org/opensaml/xmlsec/encryption/support/ChainingEncryptedKeyResolverSingleRetrievalMethod.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        assert sxo != null;
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        final EncryptedData encData = sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        assert encData != null;
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertTrue(keyInfo.getEncryptedKeys().isEmpty());
        Assert.assertFalse(keyInfo.getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        recipients.add("foo");
        
        resolver = new ChainingEncryptedKeyResolver(resolverChain);
        
        List<EncryptedKey> resolved = generateList(encData, resolver, recipients);
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
    }
    
    /** One recipient specified to resolver, EncryptedKeys in instance inline and via RetrievalMethod . */
    @Test
    public void testMultiEKWithOneRecipient() {
        String filename =  "/org/opensaml/xmlsec/encryption/support/ChainingEncryptedKeyResolverMultiple.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        assert sxo != null;
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        final EncryptedData encData = sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        assert encData != null;
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertFalse(keyInfo.getEncryptedKeys().isEmpty());
        Assert.assertFalse(keyInfo.getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        recipients.add("foo");
        
        resolver = new ChainingEncryptedKeyResolver(resolverChain);
        
        List<EncryptedKey> resolved = generateList(encData, resolver, recipients);
        Assert.assertEquals(resolved.size(), 2, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(1) == allKeys.get(3), "Unexpected EncryptedKey instance found");
    }
    
    /** Two recipients specified to resolver, EncryptedKeys in instance inline and via RetrievalMethod . */
    @Test
    public void testMultiEKWithTwoRecipients() {
        String filename =  "/org/opensaml/xmlsec/encryption/support/ChainingEncryptedKeyResolverMultiple.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        assert sxo != null;
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        final EncryptedData encData = sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        assert encData != null;
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertFalse(keyInfo.getEncryptedKeys().isEmpty());
        Assert.assertFalse(keyInfo.getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        recipients.add("foo");
        recipients.add("baz");
        
        resolver = new ChainingEncryptedKeyResolver(resolverChain);
        
        List<EncryptedKey> resolved = generateList(encData, resolver, recipients);
        Assert.assertEquals(resolved.size(), 4, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(1) == allKeys.get(2), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(2) == allKeys.get(3), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(3) == allKeys.get(5), "Unexpected EncryptedKey instance found");
    }
    
    /** Multi recipient specified to resolver via ctor and method args. */
    @Test
    @SuppressWarnings("deprecation")
    public void testMultiRecipientsCtorAndArgs() {
        String filename =  "/org/opensaml/xmlsec/encryption/support/ChainingEncryptedKeyResolverMultiple.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        assert sxo != null;
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        final EncryptedData encData = sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        assert encData != null;
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertFalse(keyInfo.getEncryptedKeys().isEmpty());
        Assert.assertFalse(keyInfo.getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        recipients.add("baz");
        
        resolver = new ChainingEncryptedKeyResolver(resolverChain, CollectionSupport.singleton("foo"));
        
        List<EncryptedKey> resolved = generateList(encData, resolver, recipients);
        Assert.assertEquals(resolved.size(), 4, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(1) == allKeys.get(2), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(2) == allKeys.get(3), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(3) == allKeys.get(5), "Unexpected EncryptedKey instance found");
    }
    
    /**
     * Extract all the EncryptedKey's from the SignableSimpleXMLObject.
     * 
     * @param sxo the mock object to process
     * @return a list of EncryptedKey elements
     */
    private List<EncryptedKey> getEncryptedKeys(@Nonnull final SignableSimpleXMLObject sxo) {
        List<EncryptedKey> allKeys = new ArrayList<>();
        
        final EncryptedData edata = sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        assert edata != null;
        final KeyInfo keyInfo = edata.getKeyInfo();
        assert keyInfo != null;
        allKeys.addAll(keyInfo.getEncryptedKeys());
        for (XMLObject xmlObject : sxo.getUnknownXMLObjects()) {
           if (xmlObject instanceof EncryptedKey)  {
               allKeys.add((EncryptedKey) xmlObject);
           }
        }
        return allKeys;
    }
    
    /**
     * Resolve EncryptedKeys and put them in an ordered list.
     * 
     * @param encData the EncryptedData context
     * @param ekResolver the resolver to test
     * @param recipients the valid recipients for resolution
     * @return list of resolved EncryptedKeys
     */
    @Nonnull private List<EncryptedKey> generateList(@Nonnull final EncryptedData encData,
            @Nonnull final EncryptedKeyResolver ekResolver, @Nullable final Set<String> recipients) {
        
        return StreamSupport.stream(ekResolver.resolve(encData, recipients).spliterator(), false)
                .collect(CollectionSupport.nonnullCollector(Collectors.toList())).get();
    }


}