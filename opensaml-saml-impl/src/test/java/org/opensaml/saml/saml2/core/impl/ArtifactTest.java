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

/**
 * 
 */
package org.opensaml.saml.saml2.core.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.Artifact;

/**
 *
 */
@SuppressWarnings({"null", "javadoc"})
public class ArtifactTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected element content*/
    private String expectedContent;

    /**
     * Constructor
     *
     */
    public ArtifactTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/Artifact.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedContent = "abc123";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final Artifact artifact = (Artifact) unmarshallElement(singleElementFile);
        assert artifact != null;
        Assert.assertEquals(artifact.getValue(), expectedContent, "Artifact content is not the expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final Artifact artifact = (Artifact) buildXMLObject(Artifact.DEFAULT_ELEMENT_NAME);
        
        artifact.setValue(expectedContent);
        
        assertXMLEquals(expectedDOM, artifact);
    }
}