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

package org.opensaml.core.config.provider;

import java.io.File;
import java.io.IOException;

import org.opensaml.core.config.ConfigurationProperties;
import org.opensaml.core.config.ConfigurationPropertiesSource;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.io.Files;

/**
 * Test {@link AbstractFilesystemConfigurationPropertiesSource}.
 */
public class FilesystemConfigurationPropertiesSourceTest {
    
    /** The source test to test. */
    private ConfigurationPropertiesSource source;
    
    /** Main file with test data. */
    private File mainFile;
    
    /** Actual target file test runs against. */
    private File targetFile;
    
    /**
     * Constructor.
     *  
     * @throws IOException ...
     */
    public FilesystemConfigurationPropertiesSourceTest() throws IOException {
        mainFile = new File("src/test/resources/opensaml-config.properties");
        targetFile = File.createTempFile("opensaml-config.properties", "");
        System.out.println(mainFile.getAbsolutePath());
        System.out.println(targetFile.getAbsolutePath()); 
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        Files.copy(mainFile, targetFile);
    }

    @AfterMethod
    protected void tearDown() throws Exception {
        if (targetFile.exists()) {
            targetFile.delete();
        }
    }
    
    /**
     *  Test basic retrieval of properties from properties source.
     */
    @Test
    public void testSource() {
        source = new TestFilesystemConfigurationPropertiesSource();
        final ConfigurationProperties props = source.getProperties();
        assert props != null;
        
        Assert.assertEquals(props.getProperty("opensaml.config.partitionName"), "myapp", "Incorrect property value");
        Assert.assertEquals(props.getProperty("opensaml.initializer.foo.flag"), "true", "Incorrect property value");
    }
    
    /**
     * Test class which supplies the location of the test resource.
     */
    public class TestFilesystemConfigurationPropertiesSource 
        extends AbstractFilesystemConfigurationPropertiesSource {

        /** {@inheritDoc} */
        protected String getFilename() {
            return targetFile.getAbsolutePath();
        }
    }

}
