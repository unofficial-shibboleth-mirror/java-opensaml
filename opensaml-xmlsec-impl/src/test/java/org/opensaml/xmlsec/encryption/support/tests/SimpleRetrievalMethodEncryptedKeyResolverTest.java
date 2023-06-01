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

package org.opensaml.xmlsec.encryption.support.tests;

import java.util.ArrayList;
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
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.SimpleRetrievalMethodEncryptedKeyResolver;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Test the encrypted key resolver which dereferences RetrievalMethods.
 */
public class SimpleRetrievalMethodEncryptedKeyResolverTest extends XMLObjectBaseTestCase {
    
    /** The resolver instance to be tested. */
    private SimpleRetrievalMethodEncryptedKeyResolver resolver;
    
    /** No recipients specified to resolver, one EncryptedKey in instance. */
    @Test
    public void testSingleEKNoRecipient() {
        final String filename =  "/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverSingle.xml";
        final SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        assert sxo != null;
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        final EncryptedData encData = sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        assert encData != null;
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertFalse(keyInfo.getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new SimpleRetrievalMethodEncryptedKeyResolver();
        
        List<EncryptedKey> resolved = generateList(encData, resolver, null);
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
    }
    
    /** One recipients specified to resolver, one EncryptedKey in instance. */
    @Test
    public void testSingleEKWithRecipient() {
        String filename =  "/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverSingle.xml";
        final SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        assert sxo != null;
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        final EncryptedData encData = sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        assert encData != null;
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertFalse(keyInfo.getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new SimpleRetrievalMethodEncryptedKeyResolver();
        
        List<EncryptedKey> resolved = generateList(encData, resolver, CollectionSupport.singleton("foo"));
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
    }
    
    /** One recipients specified to resolver, RetrievalMethod has Transforms, so should fail. */
    @Test
    public void testSingleEKWithTransform() {
        String filename =  
            "/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverSingleWithTransforms.xml";
        final SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        assert sxo != null;
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        final EncryptedData encData = sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        assert encData != null;
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertFalse(keyInfo.getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new SimpleRetrievalMethodEncryptedKeyResolver();
        
        List<EncryptedKey> resolved = generateList(encData, resolver, CollectionSupport.singleton("foo"));
        Assert.assertEquals(resolved.size(), 0, "Incorrect number of resolved EncryptedKeys found");
    }
    
    /** One recipients specified to resolver, three EncryptedKeys in instance, 
     * two RetrievalMethod references. */
    @Test
    public void testMultiEKWithOneRecipient() {
        String filename =  "/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverMultiple.xml";
        final SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        assert sxo != null;
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        final EncryptedData encData = sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        assert encData != null;
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertFalse(keyInfo.getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new SimpleRetrievalMethodEncryptedKeyResolver();
        
        List<EncryptedKey> resolved = generateList(encData, resolver, CollectionSupport.singleton("foo"));
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
    }
    
    /** Two recipients specified to resolver, three EncryptedKeys in instance, 
     * two RetrievalMethod references. */
    @Test
    public void testMultiEKWithTwoRecipients() {
        String filename =  "/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverMultiple.xml";
        final SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        assert sxo != null;
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        final EncryptedData encData = sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        assert encData != null;
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertFalse(keyInfo.getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new SimpleRetrievalMethodEncryptedKeyResolver();
        
        List<EncryptedKey> resolved = generateList(encData, resolver, CollectionSupport.setOf("foo", "baz"));
        Assert.assertEquals(resolved.size(), 2, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(1) == allKeys.get(2), "Unexpected EncryptedKey instance found");
    }
    
    /** Multi recipient specified to resolver via ctor and method args. */
    @Test
    @SuppressWarnings("deprecation")
    public void testMultiRecipientsCtorAndArgs() {
        String filename =  "/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverMultiple.xml";
        final SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        assert sxo != null;
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        final EncryptedData encData = sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        assert encData != null;
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertFalse(keyInfo.getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new SimpleRetrievalMethodEncryptedKeyResolver(CollectionSupport.singleton("foo"));
        
        List<EncryptedKey> resolved = generateList(encData, resolver, CollectionSupport.singleton("baz"));
        Assert.assertEquals(resolved.size(), 2, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(1) == allKeys.get(2), "Unexpected EncryptedKey instance found");
    }
    
    /**
     * Extract all the EncryptedKey's from the SimpleXMLObject.
     * 
     * @param sxo the mock object to process
     * @return a list of EncryptedKey elements
     */
    @Nonnull private List<EncryptedKey> getEncryptedKeys(@Nonnull final SignableSimpleXMLObject sxo) {
        List<EncryptedKey> allKeys = new ArrayList<>();
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
