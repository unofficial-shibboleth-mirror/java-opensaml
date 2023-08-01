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

package org.opensaml.saml.metadata.resolver.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.io.Files;

@SuppressWarnings("javadoc")
public class FilesystemMetadataResolverTest extends XMLObjectBaseTestCase {

    private FilesystemMetadataResolver metadataProvider;
    
    private File mdFile;

    private String entityID;

    private CriteriaSet criteriaSet;

    @BeforeMethod
    protected void setUp() throws Exception {
        entityID = "urn:mace:incommon:washington.edu";

        URL mdURL = FilesystemMetadataResolverTest.class
                .getResource("/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        mdFile = new File(mdURL.toURI());

        criteriaSet = new CriteriaSet(new EntityIdCriterion(entityID));
    }

    @Test
    public void testInactive() throws Exception {
        
        metadataProvider = new FilesystemMetadataResolver(mdFile);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");
        metadataProvider.setActivationCondition(PredicateSupport.alwaysFalse());
        metadataProvider.initialize();

        final Boolean flag = metadataProvider.wasLastRefreshSuccess();
        Assert.assertTrue(flag != null && flag);
        Assert.assertNull(metadataProvider.getLastFailureCause());
        
        EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        Assert.assertNull(descriptor, "Retrieved entity descriptor was not null");
    }
    
    /**
     * Tests the {@link HTTPMetadataResolver#lookupEntityID(String)} method.
     * 
     * @throws ResolverException ...
     */
    @Test
    public void testGetEntityDescriptor() throws ResolverException {
        try {
            metadataProvider = new FilesystemMetadataResolver(mdFile);
            metadataProvider.setParserPool(parserPool);
            metadataProvider.setId("test");
            metadataProvider.initialize();
            
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertTrue(flag != null && flag);
            Assert.assertNull(metadataProvider.getLastFailureCause());
        } catch (ComponentInitializationException e) {
            Assert.fail("Valid metdata failed init");
        }
        
        final EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        assert descriptor != null;
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");
    }
    
    /**
     * Tests failure mode of an invalid metadata file that does not exist.
     * 
     * @throws ResolverException ...
     * @throws ComponentInitializationException ...
     */
    @Test
    public void testNonexistentMetadataFile() throws ResolverException, ComponentInitializationException {
        try {
            metadataProvider = new FilesystemMetadataResolver(new File("I-Dont-Exist.xml"));
            metadataProvider.setParserPool(parserPool);
            metadataProvider.setId("test");
            metadataProvider.initialize();
            Assert.fail("Init should have thrown");
        } catch (final ComponentInitializationException e) {
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertFalse(flag != null && flag);
            Assert.assertNotNull(metadataProvider.getLastFailureCause());
            Assert.assertTrue(ResolverException.class.isInstance(metadataProvider.getLastFailureCause()));
        }
    }
    
    /**
     * Tests failure mode of an invalid metadata file that is actually a directory.
     * 
     * @throws IOException ...
     * @throws ResolverException ...
     * @throws ComponentInitializationException ...
     */
    @Test
    public void testInvalidMetadataFile() throws IOException, ResolverException, ComponentInitializationException {
        File targetFile = new File(System.getProperty("java.io.tmpdir"), "filesystem-md-provider-test");
        if (targetFile.exists()) {
            Assert.assertTrue(targetFile.delete());
        }
        Assert.assertTrue(targetFile.mkdir());
        Assert.assertTrue(targetFile.exists());
        Assert.assertTrue(targetFile.isDirectory());
        
        try {
            metadataProvider = new FilesystemMetadataResolver(targetFile);
            metadataProvider.setParserPool(parserPool);
            metadataProvider.setId("test");
            metadataProvider.initialize();
            Assert.fail("Init should have thrown");
        } catch (final ComponentInitializationException e) {
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertFalse(flag != null && flag);
            Assert.assertNotNull(metadataProvider.getLastFailureCause());
            Assert.assertTrue(ResolverException.class.isInstance(metadataProvider.getLastFailureCause()));
        } finally {
            targetFile.delete();
        }
    }
    
    /**
     * Tests failure mode of an invalid metadata file that is unreadable.
     * 
     * @throws IOException ...
     * @throws ResolverException ...
     * @throws ComponentInitializationException ...
     */
    @Test
    public void testUnreadableMetadataFile() throws IOException, ResolverException, ComponentInitializationException {
        File targetFile = File.createTempFile("filesystem-md-provider-test", "xml");
        Assert.assertTrue(targetFile.exists());
        Assert.assertTrue(targetFile.isFile());
        Assert.assertTrue(targetFile.canRead());
        
        
        try {
            metadataProvider = new FilesystemMetadataResolver(targetFile);
            metadataProvider.setParserPool(parserPool);
            metadataProvider.setId("test");
            metadataProvider.initialize();
        } catch (final ComponentInitializationException e) {
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertFalse(flag != null && flag);
            Assert.assertNotNull(metadataProvider.getLastFailureCause());
            Assert.assertTrue(ResolverException.class.isInstance(metadataProvider.getLastFailureCause()));
        } finally {
            targetFile.delete();
        }
    }
    
    /**
     * Tests failure mode of a metadata file which disappears after initial creation of the provider.
     * 
     * @throws IOException ...
     * @throws ResolverException ...
     */
    @Test
    public void testDisappearingMetadataFile() throws IOException, ResolverException {
        File targetFile = new File(System.getProperty("java.io.tmpdir"), "filesystem-md-provider-disappearing.xml");
        if (targetFile.exists()) {
            Assert.assertTrue(targetFile.delete());
        }
        Files.copy(mdFile, targetFile);
        Assert.assertTrue(targetFile.exists());
        Assert.assertTrue(targetFile.canRead());
        
        try {
            metadataProvider = new FilesystemMetadataResolver(targetFile);
            metadataProvider.setParserPool(parserPool);
            metadataProvider.setId("test");
            metadataProvider.initialize();
            
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertTrue(flag != null && flag);
            Assert.assertNull(metadataProvider.getLastFailureCause());
        } catch (final ComponentInitializationException e) {
            Assert.fail("Filesystem metadata provider init failed with file: " + targetFile.getAbsolutePath());
        }
        
        Assert.assertTrue(targetFile.delete());
        
        try {
            metadataProvider.refresh();
            Assert.fail("Refresh should have thrown");
        } catch (final ResolverException e) {
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertFalse(flag != null && flag);
            Assert.assertNotNull(metadataProvider.getLastFailureCause());
            Assert.assertTrue(ResolverException.class.isInstance(metadataProvider.getLastFailureCause()));
        }
    }
    
    /**
     * Tests failfast init of false, with graceful recovery when file later appears.
     * 
     * @throws IOException ...
     * @throws InterruptedException ...
     */
    @Test
    public void testRecoveryFromNoFailFast() throws IOException, InterruptedException {
        File targetFile = new File(System.getProperty("java.io.tmpdir"), "filesystem-md-provider-failfast.xml");
        if (targetFile.exists()) {
            Assert.assertTrue(targetFile.delete());
        }
        
        try {
            metadataProvider = new FilesystemMetadataResolver(targetFile);
            metadataProvider.setFailFastInitialization(false);
            metadataProvider.setParserPool(parserPool);
            metadataProvider.setId("test");
            metadataProvider.initialize();
            
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertFalse(flag != null && flag);
            Assert.assertNotNull(metadataProvider.getLastFailureCause());
            Assert.assertTrue(ResolverException.class.isInstance(metadataProvider.getLastFailureCause()));
        } catch (final ComponentInitializationException | ResolverException e) {
            Assert.fail("Filesystem metadata provider init failed with non-existent file and fail fast = false");
        }
        
        // Test that things don't blow up when initialized, no fail fast, but have no data.
        try {
            EntityDescriptor entity = metadataProvider.resolveSingle(criteriaSet);
            Assert.assertNull(entity, "Retrieved entity descriptor was not null"); 
        } catch (ResolverException e) {
            Assert.fail("Metadata provider behaved non-gracefully when initialized with fail fast = false");
        }
        
        // Filesystem timestamp may only have 1-second precision, so need to sleep for a couple of seconds just 
        // to make sure that the new copied file's timestamp is later than the Jodatime lastRefresh time
        // in the metadata provider.
        Thread.sleep(2000);
        
        Files.copy(mdFile, targetFile);
        Assert.assertTrue(targetFile.exists());
        Assert.assertTrue(targetFile.canRead());
        
        try {
            metadataProvider.refresh();
            
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertTrue(flag != null && flag);
            Assert.assertNull(metadataProvider.getLastFailureCause());
            
            EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
            Assert.assertNotNull(descriptor, "Retrieved entity descriptor was null");
        } catch (ResolverException e) {
            Assert.fail("Filesystem metadata provider refresh failed recovery from initial init failure");
        }
    }
    
    @Test
    public void testExpiredMetadataWithValidRequiredAndNoFailFast() throws URISyntaxException, ResolverException {
        URL mdURL = FilesystemMetadataResolverTest.class
                .getResource("/org/opensaml/saml/saml2/metadata/simple-metadata-expired.xml");
        File targetFile = new File(mdURL.toURI());
        
        try {
            metadataProvider = new FilesystemMetadataResolver(targetFile);
            metadataProvider.setFailFastInitialization(false);
            metadataProvider.setRequireValidMetadata(true);
            metadataProvider.setId("test");
            metadataProvider.setParserPool(parserPool);
            metadataProvider.initialize();
            
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertFalse(flag != null && flag);
            Assert.assertNotNull(metadataProvider.getLastFailureCause());
            Assert.assertTrue(ResolverException.class.isInstance(metadataProvider.getLastFailureCause()));
        } catch (ComponentInitializationException | ResolverException e) {
            Assert.fail("Filesystem metadata provider init failed with expired file and fail fast = false");
        }
        
        EntityDescriptor entity = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp.example.org")));
        Assert.assertNull(entity);
    }
    
}