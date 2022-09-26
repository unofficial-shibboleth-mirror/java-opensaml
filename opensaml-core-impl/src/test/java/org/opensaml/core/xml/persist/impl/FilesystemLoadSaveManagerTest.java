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

package org.opensaml.core.xml.persist.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLRuntimeException;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.persist.FilesystemLoadSaveManager;
import org.opensaml.core.xml.persist.impl.PassthroughSourceStrategy;
import org.opensaml.core.xml.persist.impl.SegmentingIntermediateDirectoryStrategy;
import org.opensaml.core.xml.util.XMLObjectSource;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.util.concurrent.Uninterruptibles;

import net.shibboleth.shared.collection.Pair;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

public class FilesystemLoadSaveManagerTest extends XMLObjectBaseTestCase {
    
    private Logger log = LoggerFactory.getLogger(FilesystemLoadSaveManagerTest.class);
    
    private File baseDir;
    
    private FilesystemLoadSaveManager<SimpleXMLObject> manager;
    
    private Function<String, List<String>> intermediateDirectoryStrategy;
    
    @BeforeMethod
    public void setUp() throws IOException {
        baseDir = new File(System.getProperty("java.io.tmpdir"), "load-save-manager-test");
        baseDir.deleteOnExit();
        log.debug("Using base directory: {}", baseDir.getAbsolutePath());
        resetBaseDir();
        if (!baseDir.exists()) {
            Assert.assertTrue(baseDir.mkdirs());
        }
        
        manager = new FilesystemLoadSaveManager<>(baseDir);
        
        intermediateDirectoryStrategy = new SegmentingIntermediateDirectoryStrategy(1, 2, new PassthroughSourceStrategy());
    }
    
    @AfterMethod
    public void tearDown() throws IOException {
        resetBaseDir();
    }
    
    @Test
    public void emptyDir() throws IOException {
        testState(Collections.emptySet());
    }
    
    @DataProvider
    public Object[][] saveLoadUpdateRemoveParams() {
        return new Object[][] {
                new Object[] { Boolean.FALSE},
                new Object[] { Boolean.TRUE },
        };
    }
    
    @Test(dataProvider="saveLoadUpdateRemoveParams")
    public void saveLoadUpdateRemove(Boolean buildWithObjectSourceByteArray) throws IOException {
        testState(Collections.emptySet());
        
        Assert.assertNull(manager.load("bogus"));
        
        manager.save("foo", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME, buildWithObjectSourceByteArray));
        testState(Collections.singleton("foo"));
        
        manager.save("bar", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME, buildWithObjectSourceByteArray));
        manager.save("baz", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME, buildWithObjectSourceByteArray));
        testState(Set.of("foo", "bar", "baz"));
        
        // Duplicate with overwrite
        manager.save("bar", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME, buildWithObjectSourceByteArray), true);
        testState(Set.of("foo", "bar", "baz"));
        
        // Duplicate without overwrite
        try {
            manager.save("bar", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME, buildWithObjectSourceByteArray), false);
            Assert.fail("Should have failed on duplicate save without overwrite");
        } catch (IOException e) {
            // expected, do nothing
        }
        testState(Set.of("foo", "bar", "baz"));
        
        // Test again. Since checkModifyTime=false, we should get back data even though unmodified
        testState(Set.of("foo", "bar", "baz"));
        
        Assert.assertTrue(manager.updateKey("foo", "foo2"));
        testState(Set.of("foo2", "bar", "baz"));
        
        // Doesn't exist anymore
        Assert.assertFalse(manager.updateKey("foo", "foo2"));
        testState(Set.of("foo2", "bar", "baz"));
        
        // Can't update to an existing name
        try {
            manager.updateKey("bar", "baz");
            Assert.fail("updateKey should have failed to due existing new key name");
        } catch (IOException e) {
            // expected, do nothing
        }
        testState(Set.of("foo2", "bar", "baz"));
        
        // Doesn't exist anymore
        Assert.assertFalse(manager.remove("foo"));
        testState(Set.of("foo2", "bar", "baz"));
        
        Assert.assertTrue(manager.remove("foo2"));
        testState(Set.of("bar", "baz"));
        
        Assert.assertTrue(manager.remove("bar"));
        Assert.assertTrue(manager.remove("baz"));
        testState(Collections.emptySet());
    }
    
    @Test(dataProvider="saveLoadUpdateRemoveParams")
    public void saveLoadUpdateRemoveWithIntermediateDirs(Boolean buildWithObjectSourceByteArray) throws IOException {
        manager = new FilesystemLoadSaveManager<>(baseDir, intermediateDirectoryStrategy);
        
        testState(Collections.emptySet());
        
        Assert.assertNull(manager.load("bogus"));
        
        Assert.assertFalse(new File(parentPath(baseDir, "fo"), "foo").exists());
        manager.save("foo", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME, buildWithObjectSourceByteArray));
        testState(Collections.singleton("foo"));
        Assert.assertTrue(new File(parentPath(baseDir, "fo"), "foo").exists());
        
        Assert.assertFalse(new File(parentPath(baseDir, "ba"), "bar").exists());
        Assert.assertFalse(new File(parentPath(baseDir, "ba"), "baz").exists());
        manager.save("bar", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME, buildWithObjectSourceByteArray));
        manager.save("baz", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME, buildWithObjectSourceByteArray));
        testState(Set.of("foo", "bar", "baz"));
        Assert.assertTrue(new File(parentPath(baseDir, "ba"), "bar").exists());
        Assert.assertTrue(new File(parentPath(baseDir, "ba"), "baz").exists());
        
        // Duplicate with overwrite
        manager.save("bar", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME, buildWithObjectSourceByteArray), true);
        testState(Set.of("foo", "bar", "baz"));
        
        // Duplicate without overwrite
        try {
            manager.save("bar", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME, buildWithObjectSourceByteArray), false);
            Assert.fail("Should have failed on duplicate save without overwrite");
        } catch (IOException e) {
            // expected, do nothing
        }
        testState(Set.of("foo", "bar", "baz"));
        
        // Test again. Since checkModifyTime=false, we should get back data even though unmodified
        testState(Set.of("foo", "bar", "baz"));
        
        Assert.assertFalse(new File(parentPath(baseDir, "fo"), "foo2").exists());
        Assert.assertTrue(manager.updateKey("foo", "foo2"));
        testState(Set.of("foo2", "bar", "baz"));
        Assert.assertTrue(new File(parentPath(baseDir, "fo"), "foo2").exists());
        
        // Doesn't exist anymore
        Assert.assertFalse(manager.updateKey("foo", "foo2"));
        testState(Set.of("foo2", "bar", "baz"));
        
        // Can't update to an existing name
        try {
            manager.updateKey("bar", "baz");
            Assert.fail("updateKey should have failed to due existing new key name");
        } catch (IOException e) {
            // expected, do nothing
        }
        testState(Set.of("foo2", "bar", "baz"));
        
        // Doesn't exist anymore
        Assert.assertFalse(manager.remove("foo"));
        testState(Set.of("foo2", "bar", "baz"));
        Assert.assertFalse(new File(parentPath(baseDir, "fo"), "foo").exists());
        
        Assert.assertTrue(new File(parentPath(baseDir, "fo"), "foo2").exists());
        Assert.assertTrue(manager.remove("foo2"));
        testState(Set.of("bar", "baz"));
        Assert.assertFalse(new File(parentPath(baseDir, "fo"), "foo2").exists());
        
        Assert.assertTrue(new File(parentPath(baseDir, "ba"), "bar").exists());
        Assert.assertTrue(new File(parentPath(baseDir, "ba"), "baz").exists());
        Assert.assertTrue(manager.remove("bar"));
        Assert.assertTrue(manager.remove("baz"));
        testState(Collections.emptySet());
        Assert.assertFalse(new File(parentPath(baseDir, "ba"), "bar").exists());
        Assert.assertFalse(new File(parentPath(baseDir, "ba"), "baz").exists());
    }
    
    @Test
    public void checkCheckModifyTimeTracking() throws IOException {
        manager = new FilesystemLoadSaveManager<>(baseDir, true);
        
        Assert.assertNull(manager.load("foo"));
        Assert.assertNull(manager.getLoadLastModified("foo"));
        
        manager.save("foo", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME, true));
        
        Assert.assertNotNull(manager.load("foo"));
        Instant initialCachedModified = manager.getLoadLastModified("foo");
        Assert.assertNotNull(initialCachedModified);
        
        // Hasn't changed
        Assert.assertNull(manager.load("foo"));
        Assert.assertEquals(manager.getLoadLastModified("foo"), initialCachedModified);
        
        // We have to sleep a little to get an updated timestamp when we save a new one, 
        // since filesystem mtime granularity is only seconds.
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
        
        // Change it
        manager.save("foo", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME, true), true);
        
        Assert.assertNotNull(manager.load("foo"));
        Instant updatedCachedModified = manager.getLoadLastModified("foo");
        Assert.assertNotNull(updatedCachedModified);
        Assert.assertNotEquals(updatedCachedModified, initialCachedModified);
        
        // Hasn't changed (again)
        Assert.assertNull(manager.load("foo"));
        Assert.assertEquals(manager.getLoadLastModified("foo"), updatedCachedModified);
        
        // Test update of key
        manager.updateKey("foo", "bar");
        Assert.assertNull(manager.load("foo"));
        Assert.assertNull(manager.load("bar"));
        Assert.assertNull(manager.getLoadLastModified("foo"));
        Assert.assertNotNull(manager.getLoadLastModified("bar"));
        Assert.assertEquals(manager.getLoadLastModified("bar"), updatedCachedModified);
        
        // Test removal of key
        manager.remove("bar");
        Assert.assertNull(manager.getLoadLastModified("bar"));
    }

    @Test
    public void buildTargetFileFromKey() throws IOException {
        File target = manager.buildFile("abc");
        Assert.assertEquals(target, new File(baseDir, "abc"));
    }
    
    @Test
    public void buildTargetFileFromKeyWithIntermediateDirs() throws IOException {
        manager = new FilesystemLoadSaveManager<>(baseDir, intermediateDirectoryStrategy);
        File target = manager.buildFile("abc");
        Assert.assertEquals(target, new File(parentPath(baseDir, "ab"), "abc"));
    }
    
    @Test(expectedExceptions=IOException.class)
    public void targetExistsButIsNotAFile() throws IOException {
        File target = new File(baseDir, "abc");
        Assert.assertFalse(target.exists());
        target.mkdir();
        try {
            manager.buildFile("abc");
        } finally {
            if (target.exists()) {
                Files.delete(target.toPath());
            }
        }
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void targetKeyIsNull() throws IOException {
        manager.buildFile(null);
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void targetKeyIsEmpty() throws IOException {
        manager.buildFile("  ");
    }
    
    @Test
    public void ctorCreateDirectory() throws IOException {
        resetBaseDir();
        Assert.assertFalse(baseDir.exists());
        new FilesystemLoadSaveManager<>(baseDir);
        Assert.assertTrue(baseDir.exists());
    }
    
    @Test
    public void ctorPathTrimming() throws IOException {
        new FilesystemLoadSaveManager<>(String.format("    %s     ", baseDir.getAbsolutePath()));
        File target = manager.buildFile("abc");
        Assert.assertEquals(target.getParentFile(), baseDir);
        Assert.assertEquals(target.getParent(), baseDir.getAbsolutePath());
        Assert.assertFalse(target.getParent().startsWith(" "));
        Assert.assertFalse(target.getParent().endsWith(" "));
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void ctorEmptyPathString() {
        new FilesystemLoadSaveManager<>("  ");
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void ctorNullFile() {
        new FilesystemLoadSaveManager<>((File)null);
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void ctorRelativeDir() {
        new FilesystemLoadSaveManager<>("my/relative/dir");
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void ctorBaseDirPathExistsButNotADirectory() throws IOException {
        resetBaseDir();
        Files.createFile(baseDir.toPath());
        new FilesystemLoadSaveManager<>(baseDir);
    }
    
    @Test
    public void iterator() throws IOException {
        Iterator<Pair<String,SimpleXMLObject>> iterator = null;
        
        iterator = manager.listAll().iterator();
        Assert.assertFalse(iterator.hasNext());
        try {
            iterator.next();
            Assert.fail("Should have failed due to no more elements");
        } catch (NoSuchElementException e) {
            //expected, do nothing
        }
        
        manager.save("foo", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        iterator = manager.listAll().iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertNotNull(iterator.next());
        Assert.assertFalse(iterator.hasNext());
        try {
            iterator.next();
            Assert.fail("Should have failed due to no more elements");
        } catch (NoSuchElementException e) {
            //expected, do nothing
        }
        
        manager.save("bar", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        manager.save("baz", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        iterator = manager.listAll().iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertNotNull(iterator.next());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertNotNull(iterator.next());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertNotNull(iterator.next());
        Assert.assertFalse(iterator.hasNext());
        try {
            iterator.next();
            Assert.fail("Should have failed due to no more elements");
        } catch (NoSuchElementException e) {
            //expected, do nothing
        }
        
        manager.remove("foo");
        iterator = manager.listAll().iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertNotNull(iterator.next());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertNotNull(iterator.next());
        Assert.assertFalse(iterator.hasNext());
        try {
            iterator.next();
            Assert.fail("Should have failed due to no more elements");
        } catch (NoSuchElementException e) {
            //expected, do nothing
        }
        
        manager.remove("bar");
        manager.remove("baz");
        iterator = manager.listAll().iterator();
        Assert.assertFalse(iterator.hasNext());
        try {
            iterator.next();
            Assert.fail("Should have failed due to no more elements");
        } catch (NoSuchElementException e) {
            //expected, do nothing
        }
        
        // Test when file is removed after iterator is created
        manager.save("foo", (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        Assert.assertTrue(manager.exists("foo"));
        Assert.assertNotNull(manager.load("foo"));
        iterator = manager.listAll().iterator();
        manager.remove("foo");
        Assert.assertFalse(iterator.hasNext());
        
    }
    
    
    
    // Helpers
    
    private File parentPath(File base, String ... dirs) {
        File parentPath = base;
        for (String dir : dirs) {
            parentPath = new File(parentPath, dir);
        }
        return parentPath;
    }
    
    private void testState(Set<String> expectedKeys) throws IOException {
        Assert.assertEquals(manager.listKeys().isEmpty(), expectedKeys.isEmpty() ? true : false);
        Assert.assertEquals(manager.listKeys(), expectedKeys);
        for (String expectedKey : expectedKeys) {
            Assert.assertTrue(manager.exists(expectedKey));
            SimpleXMLObject sxo = manager.load(expectedKey);
            Assert.assertNotNull(sxo);
            Assert.assertEquals(sxo.getObjectMetadata().get(XMLObjectSource.class).size(), 1);
        }
        
        Assert.assertEquals(manager.listAll().iterator().hasNext(), expectedKeys.isEmpty() ? false: true);
        
        int sawCount = 0;
        for (Pair<String,SimpleXMLObject> entry : manager.listAll()) {
            sawCount++;
            Assert.assertTrue(expectedKeys.contains(entry.getFirst()));
            Assert.assertNotNull(entry.getSecond());
        }
        Assert.assertEquals(sawCount, expectedKeys.size());
    }
    
    private void resetBaseDir() throws IOException {
        if (baseDir.exists()) {
            if (baseDir.isDirectory()) {
                Files.walk(baseDir.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            } else {
                baseDir.delete();
            }
        }
    }
    
    // It's hard to actually test that we're writing the existing byte[], but by doing this
    // we can at least visually inspect the logs for save() ops and see that it logs as expected.
    protected <T extends XMLObject> T buildXMLObject(QName name, boolean withObjectSource) {
        T xmlObject = super.buildXMLObject(name);
        if (withObjectSource) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                XMLObjectSupport.marshallToOutputStream(xmlObject, baos);
                xmlObject.getObjectMetadata().put(new XMLObjectSource(baos.toByteArray()));
            } catch (MarshallingException | IOException e) {
                throw new XMLRuntimeException("Error marshalling XMLObject", e);
            }
        }
        return xmlObject;
    }

    
}
