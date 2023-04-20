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

package org.opensaml.saml.security.impl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.impl.CollectionCredentialResolver;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.EncryptionParametersResolver;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.RSAOAEPParameters;
import org.opensaml.xmlsec.impl.BasicEncryptionConfiguration;
import org.opensaml.xmlsec.impl.BasicEncryptionParametersResolver;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.collection.Pair;

@SuppressWarnings({"null", "javadoc"})
public class InlineSelfEncryptionParametersStrategyTest extends OpenSAMLInitBaseTestCase {
    
    private Credential cred1, cred2;
    
    private List<Credential> resolverCreds;
    
    private CollectionCredentialResolver credResolver;
    
    private EncryptionParametersResolver paramsResolver;
    
    private ProfileRequestContext prc;
    
    @BeforeClass
    public void classSetUp() throws NoSuchAlgorithmException, NoSuchProviderException {
        cred1 = AlgorithmSupport.generateKeyPairAndCredential(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, 1024, true);
        cred2 = AlgorithmSupport.generateKeyPairAndCredential(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, 1024, true);
        
        paramsResolver = new BasicEncryptionParametersResolver();
    }
    
    @BeforeMethod
    public void setUp() {
        resolverCreds = new ArrayList<>();
        credResolver = new CollectionCredentialResolver(resolverCreds);
        
        prc = new RequestContextBuilder().buildProfileRequestContext();
    }
    
    @Test
    public void testNoCreds() {
        final InlineSelfEncryptionParametersStrategy strategy = new InlineSelfEncryptionParametersStrategy(credResolver, paramsResolver);
        final List<EncryptionParameters> encParameters = strategy.apply(new Pair<ProfileRequestContext, EncryptionParameters>(prc, null));
        assert encParameters != null;
        Assert.assertTrue(encParameters.isEmpty());
    }
    
    @Test
    public void testSingleCred() {
        resolverCreds.add(cred1);
        
        final InlineSelfEncryptionParametersStrategy strategy = new InlineSelfEncryptionParametersStrategy(credResolver, paramsResolver);
        final List<EncryptionParameters> encParameters = strategy.apply(new Pair<ProfileRequestContext, EncryptionParameters>(prc, null));
        
        assert encParameters != null;
        Assert.assertEquals(encParameters.size(), 1);
        Assert.assertSame(encParameters.get(0).getKeyTransportEncryptionCredential(), cred1);
        Assert.assertEquals(encParameters.get(0).getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        
        final RSAOAEPParameters oaep = encParameters.get(0).getRSAOAEPParameters();
        assert oaep != null;
        Assert.assertEquals(oaep.getDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA1);
        Assert.assertEquals(oaep.getMaskGenerationFunction(), EncryptionConstants.ALGO_ID_MGF1_SHA1);
    }
    
    @Test
    public void testMultipleCreds() {
        resolverCreds.add(cred1);
        resolverCreds.add(cred2);
        
        final InlineSelfEncryptionParametersStrategy strategy = new InlineSelfEncryptionParametersStrategy(credResolver, paramsResolver);
        final List<EncryptionParameters> encParameters = strategy.apply(new Pair<ProfileRequestContext, EncryptionParameters>(prc, null));
        
        assert encParameters != null;
        Assert.assertEquals(encParameters.size(), 2);
        Assert.assertSame(encParameters.get(0).getKeyTransportEncryptionCredential(), cred1);
        Assert.assertEquals(encParameters.get(0).getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        
        final RSAOAEPParameters oaep1 = encParameters.get(0).getRSAOAEPParameters();
        assert oaep1 != null;
        Assert.assertEquals(oaep1.getDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA1);
        
        Assert.assertSame(encParameters.get(1).getKeyTransportEncryptionCredential(), cred2);
        Assert.assertEquals(encParameters.get(1).getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        
        final RSAOAEPParameters oaep2 = encParameters.get(0).getRSAOAEPParameters();
        assert oaep2 != null;
        Assert.assertEquals(oaep2.getDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA1);
    }
    
    @Test
    public void testConfigLookup() {
        resolverCreds.add(cred1);
        
        final Function<ProfileRequestContext, List<EncryptionConfiguration>> configStrategy = new Function<>() {
            public List<EncryptionConfiguration> apply(@Nullable ProfileRequestContext input) {
                final BasicEncryptionConfiguration selfConfig = new BasicEncryptionConfiguration();
                final RSAOAEPParameters rsaParams = new RSAOAEPParameters();
                rsaParams.setDigestMethod(EncryptionConstants.ALGO_ID_DIGEST_SHA256);
                selfConfig.setRSAOAEPParameters(rsaParams);
                selfConfig.setRSAOAEPParametersMerge(true);
                return CollectionSupport.listOf(selfConfig, SecurityConfigurationSupport.getGlobalEncryptionConfiguration());
        }};
        
        final InlineSelfEncryptionParametersStrategy strategy = new InlineSelfEncryptionParametersStrategy(credResolver, paramsResolver, configStrategy);
        final List<EncryptionParameters> encParameters = strategy.apply(new Pair<ProfileRequestContext, EncryptionParameters>(prc, null));
        
        assert encParameters != null;
        Assert.assertEquals(encParameters.size(), 1);
        Assert.assertSame(encParameters.get(0).getKeyTransportEncryptionCredential(), cred1);
        Assert.assertEquals(encParameters.get(0).getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        
        final RSAOAEPParameters oaep = encParameters.get(0).getRSAOAEPParameters();
        assert oaep != null;
        Assert.assertEquals(oaep.getDigestMethod(), EncryptionConstants.ALGO_ID_DIGEST_SHA256);
    }
    

}