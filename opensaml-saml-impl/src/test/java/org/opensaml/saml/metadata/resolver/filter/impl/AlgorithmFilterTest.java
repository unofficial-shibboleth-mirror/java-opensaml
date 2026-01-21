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

package org.opensaml.saml.metadata.resolver.filter.impl;

import static org.testng.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolverTest;
import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.xmlsec.encryption.MGF;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class AlgorithmFilterTest extends XMLObjectBaseTestCase implements Predicate<EntityDescriptor> {
    
    private FilesystemMetadataResolver metadataProvider;
    
    private File mdFile;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        URL mdURL = FilesystemMetadataResolverTest.class
                .getResource("/org/opensaml/saml/metadata/resolver/filter/impl/EntityDescriptorWithAlgorithms.xml");
        mdFile = new File(mdURL.toURI());

        metadataProvider = new FilesystemMetadataResolver(mdFile);
        metadataProvider.setParserPool(parserPool);
    }
    
    @Test
    public void test() throws ComponentInitializationException, ResolverException {
        
        final DigestMethod digest1 = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digest1.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA256);

        final DigestMethod digest2 = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digest2.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA512);

        final DigestMethod digest3 = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digest3.setAlgorithm("foo");

        final SigningMethod signing1 = buildXMLObject(SigningMethod.DEFAULT_ELEMENT_NAME);
        signing1.setAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);

        final SigningMethod signing2 = buildXMLObject(SigningMethod.DEFAULT_ELEMENT_NAME);
        signing2.setAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512);

        final SigningMethod signing3 = buildXMLObject(SigningMethod.DEFAULT_ELEMENT_NAME);
        signing3.setAlgorithm("foo");

        final EncryptionMethod enc1 = buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME);
        enc1.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);

        final org.opensaml.xmlsec.signature.DigestMethod embeddedDigest =
                buildXMLObject(org.opensaml.xmlsec.signature.DigestMethod.DEFAULT_ELEMENT_NAME);
        embeddedDigest.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        enc1.getUnknownXMLObjects().add(embeddedDigest);
        
        final MGF mgf = buildXMLObject(MGF.DEFAULT_ELEMENT_NAME);
        mgf.setAlgorithm(EncryptionConstants.ALGO_ID_MGF1_SHA256);
        enc1.getUnknownXMLObjects().add(mgf);

        final EncryptionMethod enc2 = buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME);
        enc2.setAlgorithm("foo");

        final EncryptionMethod enc3 = buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME);
        enc3.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);

        final Collection<XMLObject> algs =
                CollectionSupport.listOf(digest1, digest2, signing1, signing2, enc1, digest3, signing3, enc2, enc3);
        
        final AlgorithmFilter filter = new AlgorithmFilter();
        filter.setRules(CollectionSupport.singletonMap(this, algs));
        filter.initialize();
        
        metadataProvider.setMetadataFilter(filter);
        metadataProvider.setId("test");
        metadataProvider.initialize();

        EntityIdCriterion crit = new EntityIdCriterion("https://foo.example.org/sp");
        EntityDescriptor entity = metadataProvider.resolveSingle(new CriteriaSet(crit));
        assert entity != null;
        Extensions exts = entity.getExtensions();
        assert exts != null;
        
        List<XMLObject> extElements = exts.getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME);
        assertEquals(extElements.size(), 3);
        
        Iterator<XMLObject> digests = extElements.iterator();
        assertEquals(((DigestMethod) digests.next()).getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        assertEquals(((DigestMethod) digests.next()).getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA512);
        assertEquals(((DigestMethod) digests.next()).getAlgorithm(), "foo");

        extElements = exts.getUnknownXMLObjects(SigningMethod.DEFAULT_ELEMENT_NAME);
        assertEquals(extElements.size(), 3);
        
        Iterator<XMLObject> signings = extElements.iterator();
        assertEquals(((SigningMethod) signings.next()).getAlgorithm(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        assertEquals(((SigningMethod) signings.next()).getAlgorithm(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512);
        assertEquals(((SigningMethod) signings.next()).getAlgorithm(), "foo");

        for (final RoleDescriptor role : entity.getRoleDescriptors()) {
            exts = role.getExtensions();
            Assert.assertEquals(exts.getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME).size(), 1);
            Assert.assertEquals(exts.getUnknownXMLObjects(SigningMethod.DEFAULT_ELEMENT_NAME).size(), 1);
            
            for (final KeyDescriptor key : role.getKeyDescriptors()) {
                final List<EncryptionMethod> methods = key.getEncryptionMethods();
                assertEquals(methods.size(), 3);
                assertEquals(methods.get(0).getAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
                assertEquals(methods.get(1).getAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);
                assertEquals(methods.get(2).getAlgorithm(), "foo");
                
                final List<XMLObject> encDigests = methods.get(1).getUnknownXMLObjects(
                        org.opensaml.xmlsec.signature.DigestMethod.DEFAULT_ELEMENT_NAME);
                assertEquals(encDigests.size(), 1);
                assertEquals(((org.opensaml.xmlsec.signature.DigestMethod) encDigests.get(0)).getAlgorithm(),
                        SignatureConstants.ALGO_ID_DIGEST_SHA256);

                final List<XMLObject> mgfs = methods.get(1).getUnknownXMLObjects(MGF.DEFAULT_ELEMENT_NAME);
                assertEquals(mgfs.size(), 1);
                assertEquals(((MGF) mgfs.get(0)).getAlgorithm(), EncryptionConstants.ALGO_ID_MGF1_SHA256);
            }
        }
    }
    
    @Test
    public void testWithRemoval() throws ComponentInitializationException, ResolverException {
        
        final DigestMethod digest2 = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digest2.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA512);

        final DigestMethod digest3 = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digest3.setAlgorithm("foo");

        final SigningMethod signing2 = buildXMLObject(SigningMethod.DEFAULT_ELEMENT_NAME);
        signing2.setAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512);

        final SigningMethod signing3 = buildXMLObject(SigningMethod.DEFAULT_ELEMENT_NAME);
        signing3.setAlgorithm("foo");

        final EncryptionMethod enc1 = buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME);
        enc1.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);

        final org.opensaml.xmlsec.signature.DigestMethod embeddedDigest =
                buildXMLObject(org.opensaml.xmlsec.signature.DigestMethod.DEFAULT_ELEMENT_NAME);
        embeddedDigest.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        enc1.getUnknownXMLObjects().add(embeddedDigest);
        
        final MGF mgf = buildXMLObject(MGF.DEFAULT_ELEMENT_NAME);
        mgf.setAlgorithm(EncryptionConstants.ALGO_ID_MGF1_SHA256);
        enc1.getUnknownXMLObjects().add(mgf);

        final EncryptionMethod enc2 = buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME);
        enc2.setAlgorithm("foo");

        final Collection<XMLObject> algs =
                CollectionSupport.listOf(digest2, signing2, enc1, digest3, signing3, enc2);
        
        final AlgorithmFilter filter = new AlgorithmFilter();
        filter.setRemoveExistingDigestMethods(true);
        filter.setRemoveExistingSigningMethods(true);
        filter.setRemoveExistingEncryptionMethods(true);
        filter.setRules(CollectionSupport.singletonMap(this, algs));
        filter.initialize();
        
        metadataProvider.setMetadataFilter(filter);
        metadataProvider.setId("test");
        metadataProvider.initialize();

        EntityIdCriterion crit = new EntityIdCriterion("https://foo.example.org/sp");
        EntityDescriptor entity = metadataProvider.resolveSingle(new CriteriaSet(crit));
        assert entity != null;
        Extensions exts = entity.getExtensions();
        assert exts != null;
        
        List<XMLObject> extElements = exts.getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME);
        assertEquals(extElements.size(), 2);
        
        Iterator<XMLObject> digests = extElements.iterator();
        assertEquals(((DigestMethod) digests.next()).getAlgorithm(), SignatureConstants.ALGO_ID_DIGEST_SHA512);
        assertEquals(((DigestMethod) digests.next()).getAlgorithm(), "foo");

        extElements = exts.getUnknownXMLObjects(SigningMethod.DEFAULT_ELEMENT_NAME);
        assertEquals(extElements.size(), 2);
        
        Iterator<XMLObject> signings = extElements.iterator();
        assertEquals(((SigningMethod) signings.next()).getAlgorithm(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512);
        assertEquals(((SigningMethod) signings.next()).getAlgorithm(), "foo");

        for (final RoleDescriptor role : entity.getRoleDescriptors()) {
            exts = role.getExtensions();
            if (exts != null) {
                Assert.assertTrue(exts.getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME).isEmpty());
                Assert.assertTrue(exts.getUnknownXMLObjects(SigningMethod.DEFAULT_ELEMENT_NAME).isEmpty());
            }

            for (final KeyDescriptor key : role.getKeyDescriptors()) {
                final List<EncryptionMethod> methods = key.getEncryptionMethods();
                assertEquals(methods.size(), 2);
                assertEquals(methods.get(0).getAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);
                assertEquals(methods.get(1).getAlgorithm(), "foo");
                
                final List<XMLObject> encDigests = methods.get(0).getUnknownXMLObjects(
                        org.opensaml.xmlsec.signature.DigestMethod.DEFAULT_ELEMENT_NAME);
                assertEquals(encDigests.size(), 1);
                assertEquals(((org.opensaml.xmlsec.signature.DigestMethod) encDigests.get(0)).getAlgorithm(),
                        SignatureConstants.ALGO_ID_DIGEST_SHA256);

                final List<XMLObject> mgfs = methods.get(0).getUnknownXMLObjects(MGF.DEFAULT_ELEMENT_NAME);
                assertEquals(mgfs.size(), 1);
                assertEquals(((MGF) mgfs.get(0)).getAlgorithm(), EncryptionConstants.ALGO_ID_MGF1_SHA256);
            }
        }
    }

    @Test
    public void testRemovalOnly() throws ComponentInitializationException, ResolverException {
        
        final AlgorithmFilter filter = new AlgorithmFilter();
        filter.setRemoveExistingDigestMethods(true);
        filter.setRemoveExistingSigningMethods(true);
        filter.setRemoveExistingEncryptionMethods(true);
        filter.setRules(CollectionSupport.singletonMap(this, CollectionSupport.emptyList()));
        filter.initialize();
        
        metadataProvider.setMetadataFilter(filter);
        metadataProvider.setId("test");
        metadataProvider.initialize();

        EntityIdCriterion crit = new EntityIdCriterion("https://foo.example.org/sp");
        EntityDescriptor entity = metadataProvider.resolveSingle(new CriteriaSet(crit));
        assert entity != null;
        Extensions exts = entity.getExtensions();
        if (exts != null) {
            Assert.assertTrue(exts.getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME).isEmpty());
            Assert.assertTrue(exts.getUnknownXMLObjects(SigningMethod.DEFAULT_ELEMENT_NAME).isEmpty());
        }
        
        for (final RoleDescriptor role : entity.getRoleDescriptors()) {
            exts = entity.getExtensions();
            if (exts != null) {
                Assert.assertTrue(exts.getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME).isEmpty());
                Assert.assertTrue(exts.getUnknownXMLObjects(SigningMethod.DEFAULT_ELEMENT_NAME).isEmpty());
            }
            for (final KeyDescriptor key : role.getKeyDescriptors()) {
                Assert.assertTrue(key.getEncryptionMethods().isEmpty());
            }
        }
    }
    
    /** {@inheritDoc} */
    public boolean test(final EntityDescriptor input) {
        return input != null && "https://foo.example.org/sp".equals(input.getEntityID());
    }

}