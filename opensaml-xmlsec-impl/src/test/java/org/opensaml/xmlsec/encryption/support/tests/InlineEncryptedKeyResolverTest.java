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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.InlineEncryptedKeyResolver;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Test the inline encrypted key resolver.
 */
@SuppressWarnings({"javadoc", "null"})
public class InlineEncryptedKeyResolverTest extends XMLObjectBaseTestCase {
    
    /** The resolver instance to be tested. */
    private InlineEncryptedKeyResolver resolver;

    /** No recipients specified to resolver, one inline EncryptedKey in instance. */
    @Test
    public void  testSingleEKNoRecipients() {
        final String filename = "/org/opensaml/xmlsec/encryption/support/InlineEncryptedKeyResolverSingle.xml";
        final EncryptedData encData = (EncryptedData) unmarshallElement(filename);
        assert encData != null;
        
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        final List<EncryptedKey> allKeys = keyInfo.getEncryptedKeys();
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new InlineEncryptedKeyResolver();
        
        final List<EncryptedKey> resolved = generateList(encData, resolver, null);
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
    }
    
    /** One recipient specified to resolver, one matching inline EncryptedKey in instance. */
    @Test
    public void  testSingleEKOneRecipientWithMatch() {
        final String filename = "/org/opensaml/xmlsec/encryption/support/InlineEncryptedKeyResolverSingle.xml";
        final EncryptedData encData = (EncryptedData) unmarshallElement(filename);
        assert encData != null;
        
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        final List<EncryptedKey> allKeys = keyInfo.getEncryptedKeys();
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new InlineEncryptedKeyResolver();
        
        List<EncryptedKey> resolved = generateList(encData, resolver, CollectionSupport.singleton("foo"));
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
    }
    
    /** One recipient specified to resolver, zero matching inline EncryptedKey in instance. */
    @Test
    public void  testSingleEKOneRecipientNoMatch() {
        final String filename = "/org/opensaml/xmlsec/encryption/support/InlineEncryptedKeyResolverSingle.xml";
        final EncryptedData encData = (EncryptedData) unmarshallElement(filename);
        assert encData != null;
        
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        final List<EncryptedKey> allKeys = keyInfo.getEncryptedKeys();
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new InlineEncryptedKeyResolver();
        
        List<EncryptedKey> resolved = generateList(encData, resolver, CollectionSupport.singleton("bar"));
        Assert.assertEquals(resolved.size(), 0, "Incorrect number of resolved EncryptedKeys found");
    }
    
    /** No recipients specified to resolver. */
    @Test
    public void  testMultiEKNoRecipients() {
        final String filename = "/org/opensaml/xmlsec/encryption/support/InlineEncryptedKeyResolverMultiple.xml";
        final EncryptedData encData = (EncryptedData) unmarshallElement(filename);
        assert encData != null;

        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        final List<EncryptedKey> allKeys = keyInfo.getEncryptedKeys();
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new InlineEncryptedKeyResolver();
        
        List<EncryptedKey> resolved = generateList(encData, resolver, null);
        Assert.assertEquals(resolved.size(), 4, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(1) == allKeys.get(1), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(2) == allKeys.get(2), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(3) == allKeys.get(3), "Unexpected EncryptedKey instance found");
    }
    
    /** One recipient specified to resolver, one matching and one recipient-less 
     *  inline EncryptedKey in instance. */
    @Test
    public void  testMultiEKOneRecipientWithMatch() {
        final String filename = "/org/opensaml/xmlsec/encryption/support/InlineEncryptedKeyResolverMultiple.xml";
        final EncryptedData encData = (EncryptedData) unmarshallElement(filename);
        assert encData != null;
        
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        final List<EncryptedKey> allKeys = keyInfo.getEncryptedKeys();
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new InlineEncryptedKeyResolver();
        
        List<EncryptedKey> resolved = generateList(encData, resolver, CollectionSupport.singleton("foo"));
        Assert.assertEquals(resolved.size(), 2, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(1) == allKeys.get(2), "Unexpected EncryptedKey instance found");
    }
    
    /** Multi recipient specified to resolver, several matching inline EncryptedKey in instance. */
    @Test
    public void  testMultiEKOneRecipientWithMatches() {
        final String filename = "/org/opensaml/xmlsec/encryption/support/InlineEncryptedKeyResolverMultiple.xml";
        final EncryptedData encData = (EncryptedData) unmarshallElement(filename);
        assert encData != null;
        
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        final List<EncryptedKey> allKeys = keyInfo.getEncryptedKeys();
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new InlineEncryptedKeyResolver();
        
        List<EncryptedKey> resolved = generateList(encData, resolver, CollectionSupport.setOf("foo", "baz"));
        Assert.assertEquals(resolved.size(), 3, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(1) == allKeys.get(2), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(2) == allKeys.get(3), "Unexpected EncryptedKey instance found");
    }
    
    /** Multi recipient specified to resolver via ctor and method args. */
    @Test
    @SuppressWarnings("deprecation")
    public void  testMultiRecipientsCtorAndArgs() {
        final String filename = "/org/opensaml/xmlsec/encryption/support/InlineEncryptedKeyResolverMultiple.xml";
        final EncryptedData encData = (EncryptedData) unmarshallElement(filename);
        assert encData != null;
        
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        final List<EncryptedKey> allKeys = keyInfo.getEncryptedKeys();
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new InlineEncryptedKeyResolver(CollectionSupport.singleton("foo"));
        
        List<EncryptedKey> resolved = generateList(encData, resolver, CollectionSupport.singleton("baz"));
        Assert.assertEquals(resolved.size(), 3, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(1) == allKeys.get(2), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(2) == allKeys.get(3), "Unexpected EncryptedKey instance found");
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
